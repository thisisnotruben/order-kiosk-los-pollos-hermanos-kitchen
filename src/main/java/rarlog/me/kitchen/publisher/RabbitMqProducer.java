package rarlog.me.kitchen.publisher;

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
public class RabbitMqProducer {

    @Value(RabbitMqConfig.EXCHANGE_NAME)
    private String exchange;

    @Value(RabbitMqConfig.ROUTING_KEY_NAME)
    private String routingKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(Work message) {
        LOGGER.info(String.format("Message sent -> %s", message));
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

}
