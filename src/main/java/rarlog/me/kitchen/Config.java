package rarlog.me.kitchen;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import rarlog.me.kitchen.kitchen.Worker;
import rarlog.me.kitchen.queue.RabbitMqProducer;
import rarlog.me.kitchen.repository.FoodItemRepository;

@Configuration
public class Config {

    public final static String EXCHANGE_NAME = "${rabbitmq.exchange.name}";
    public final static String QUEUE_FOOD_NAME = "${rabbitmq.queueFood.name}";
    public final static String ROUTING_KEY_FOOD_NAME = "${rabbitmq.routingFood.key}";
    public final static String QUEUE_WORKER_NAME = "${rabbitmq.queueWorker.name}";
    public final static String ROUTING_KEY_WORKER_NAME = "${rabbitmq.routingWorker.key}";

    @Value(EXCHANGE_NAME)
    private String exchange;

    @Value(QUEUE_FOOD_NAME)
    private String queueFood;

    @Value(ROUTING_KEY_FOOD_NAME)
    private String routingKeyFood;

    @Value(QUEUE_WORKER_NAME)
    private String queueWorker;

    @Value(ROUTING_KEY_WORKER_NAME)
    private String routingKeyWorker;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue queueFood() {
        return new Queue(queueFood);
    }

    @Bean
    public Queue queueWorker() {
        return new Queue(queueWorker);
    }

    @Bean
    public Binding bindingFood() {
        return BindingBuilder
                .bind(queueFood())
                .to(exchange())
                .with(routingKeyFood);
    }

    @Bean
    public Binding bindingWorker() {
        return BindingBuilder
                .bind(queueWorker())
                .to(exchange())
                .with(routingKeyWorker);
    }

    @Bean
    public MessageConverter converter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

    @Bean
    List<Worker> workers(@Value("${workforce.amount}") int amount, FoodItemRepository foodItemRepository,
            RabbitMqProducer rabbitMqProducer) {
        List<Worker> workers = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            workers.add(new Worker(foodItemRepository, rabbitMqProducer));
        }
        return workers;
    }

}
