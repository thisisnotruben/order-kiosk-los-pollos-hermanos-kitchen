package rarlog.me.kitchen.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rarlog.me.kitchen.Config;
import rarlog.me.kitchen.dto.WorkDto;

@Service
@RequiredArgsConstructor
public class RabbitMqProducer {

    @Value(Config.EXCHANGE_NAME)
    private String exchange;

    @Value(Config.ROUTING_KEY_FOOD_NAME)
    private String routingKeyFood;

    @Value(Config.ROUTING_KEY_WORKER_NAME)
    private String routingKeyWorker;

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public void sendFoodMessage(WorkDto message) {
        LOGGER.info(String.format("Message food sent: %s", message));
        rabbitTemplate.convertAndSend(exchange, routingKeyFood, message);
    }

    public void sendWorkerMessage(WorkDto message) {
        LOGGER.info(String.format("Message worker sent: %s", message));
        rabbitTemplate.convertAndSend(exchange, routingKeyWorker, message);
    }

}
