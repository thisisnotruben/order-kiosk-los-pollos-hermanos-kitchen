package rarlog.me.kitchen.kitchen;

import java.util.List;

import lombok.RequiredArgsConstructor;
import rarlog.me.kitchen.consumer.RabbitMqConsumer;
import rarlog.me.kitchen.dto.Work;
import rarlog.me.kitchen.entity.CustomerOrder;
import rarlog.me.kitchen.entity.OrderItem;
import rarlog.me.kitchen.entity.OrderItemId;
import rarlog.me.kitchen.publisher.RabbitMqProducer;
import rarlog.me.kitchen.repository.CustomerOrderRepository;
import rarlog.me.kitchen.repository.OrderItemRepository;
import rarlog.me.kitchen.worker.Worker;

@RequiredArgsConstructor
public class Kitchen {

    private final CustomerOrderRepository customerOrderRepository;
    private final OrderItemRepository orderItemRepository;

    private final RabbitMqProducer messageProducer;
    private final RabbitMqConsumer messageConsumer;

    private final WorkerForce workerForce;

    public void prepareOrder(CustomerOrder customerOrder) {

        customerOrder.setStatus(CustomerOrder.Status.PREPARING.toString());
        customerOrderRepository.save(customerOrder);

        List<OrderItem> orderItems = orderItemRepository.findByIdOrderId(customerOrder.getId());
        for (OrderItem orderItem : orderItems) {
            for (int i = 0; i < orderItem.getQuantity(); i++) {

                Work work = new Work(customerOrder.getId(), orderItem.getId().getFoodId());
                messageProducer.sendMessage(work);

                Worker worker = workerForce.getWorker();
                if (worker != null) {
                    worker.prepareFoodItem(work);
                }
            }
        }
    }

    public void workerFinished(Worker worker, Work work) {

        Work moreWork = messageConsumer.consume();
        if (moreWork != null) {
            worker.prepareFoodItem(moreWork);
        }

        OrderItem orderItem = orderItemRepository.findById(new OrderItemId(work.getOrderId(), work.getFoodId())).get();
        orderItem.setQuantityMade(orderItem.getQuantityMade() + 1);

        if (orderItem.getQuantity() == orderItem.getQuantityMade()) {
            orderItem.setStatus(CustomerOrder.Status.READY.toString());

            List<OrderItem> orderItems = orderItemRepository.findByIdOrderId(work.getOrderId());
            int ordersReady = 0;

            for (OrderItem otherOrderItem : orderItems) {
                if (otherOrderItem.getStatus() == CustomerOrder.Status.READY.toString()) {
                    ordersReady++;
                }
            }

            if (ordersReady == orderItems.size()) {
                CustomerOrder customerOrder = customerOrderRepository.getReferenceById(work.getOrderId());
                customerOrder.setStatus(CustomerOrder.Status.READY.toString());
                // TODO
                // this where you notify customer that order is ready
            }
        }

        orderItemRepository.save(orderItem);
    }

}
