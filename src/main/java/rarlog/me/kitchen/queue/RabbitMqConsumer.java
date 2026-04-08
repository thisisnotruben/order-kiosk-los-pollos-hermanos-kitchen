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
public class RabbitMqConsumer {

    @Value(Config.QUEUE_FOOD_NAME)
    private String queueFood;

    @Value(Config.QUEUE_WORKER_NAME)
    private String queueWorker;

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqConsumer.class);

    private final RabbitTemplate rabbitTemplate;

    public WorkDto consumeFoodMessage() {
        WorkDto message = (WorkDto) rabbitTemplate.receiveAndConvert(queueFood);
        LOGGER.info(String.format("Consumed message: %s", message));
        return message;
    }

}
