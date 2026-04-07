package rarlog.me.kitchen.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rarlog.me.kitchen.config.RabbitMqConfig;
import rarlog.me.kitchen.dto.Work;

@Service
@RequiredArgsConstructor
public class RabbitMqConsumer {

    @Value(RabbitMqConfig.QUEUE_NAME)
    private String queue;

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqConsumer.class);

    private final RabbitTemplate rabbitTemplate;

    public Work consume() {
        Work message = (Work) rabbitTemplate.receiveAndConvert(queue);
        LOGGER.info(String.format("Consumed message: %s", message));
        return message;
    }

}
