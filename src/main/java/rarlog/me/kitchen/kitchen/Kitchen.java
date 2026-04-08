package rarlog.me.kitchen.kitchen;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import rarlog.me.kitchen.Config;
import rarlog.me.kitchen.dto.WorkDto;
import rarlog.me.kitchen.entity.CustomerOrder;
import rarlog.me.kitchen.entity.OrderItem;
import rarlog.me.kitchen.entity.OrderItemId;
import rarlog.me.kitchen.queue.RabbitMqConsumer;
import rarlog.me.kitchen.queue.RabbitMqProducer;
import rarlog.me.kitchen.repository.CustomerOrderRepository;
import rarlog.me.kitchen.repository.OrderItemRepository;

@Component
@RequiredArgsConstructor
public class Kitchen {

    private final CustomerOrderRepository customerOrderRepository;
    private final OrderItemRepository orderItemRepository;

    private final RabbitMqProducer messageProducer;
    private final RabbitMqConsumer messageConsumer;

    private final List<Worker> workers;

    private static final Logger LOGGER = LoggerFactory.getLogger(Kitchen.class);

    public void prepareOrder(CustomerOrder customerOrder) {
        LOGGER.info(String.format("Kitchen preparing order: %s", customerOrder));

        customerOrder.setStatus(CustomerOrder.Status.PREPARING.toString());
        customerOrderRepository.save(customerOrder);

        List<OrderItem> orderItems = orderItemRepository.findByIdOrderId(customerOrder.getId());
        for (OrderItem orderItem : orderItems) {
            for (int i = 0; i < orderItem.getQuantity(); i++) {

                WorkDto work = new WorkDto(customerOrder.getId(), orderItem.getId().getFoodId());
                messageProducer.sendFoodMessage(work);

                Worker worker = getWorker();
                if (worker != null) {
                    worker.prepareFoodItem(work);
                }
            }
        }
    }

    @RabbitListener(queues = { Config.QUEUE_WORKER_NAME })
    public void workerFinished(WorkDto work) {

        WorkDto moreWork = messageConsumer.consumeFoodMessage();
        Worker worker = getWorker();
        if (moreWork != null && worker != null) {
            worker.prepareFoodItem(moreWork);
        }

        OrderItem orderItem = orderItemRepository.findById(new OrderItemId(work.getOrderId(), work.getFoodId())).get();

        if (orderItem.getQuantity() > orderItem.getQuantityMade()) {
            orderItem.setQuantityMade(orderItem.getQuantityMade() + 1);
            orderItemRepository.save(orderItem);
        }

        if (orderItem.getQuantity() == orderItem.getQuantityMade()) {
            orderItem.setStatus(CustomerOrder.Status.READY.toString());
            orderItemRepository.save(orderItem);

            List<OrderItem> orderItems = orderItemRepository.findByIdOrderId(work.getOrderId());
            int ordersReady = 0;

            for (OrderItem otherOrderItem : orderItems) {
                if (otherOrderItem.getStatus().equals(CustomerOrder.Status.READY.toString())) {
                    ordersReady++;
                }
            }

            if (ordersReady == orderItems.size()) {
                CustomerOrder customerOrder = customerOrderRepository.findById(work.getOrderId()).get();
                customerOrder.setStatus(CustomerOrder.Status.READY.toString());
                customerOrderRepository.save(customerOrder);
                // TODO
                // this where you notify customer that order is ready
            }
        }
    }

    public Worker getWorker() {
        for (Worker worker : workers) {
            if (worker.getStatus() == Worker.Status.IDLE) {
                return worker;
            }
        }
        return null;
    }

}
