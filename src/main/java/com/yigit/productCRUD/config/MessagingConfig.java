package com.yigit.productCRUD.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Messaging config.
 */
@Configuration
public class MessagingConfig {

    /**
     * The constant QUEUE.
     */
    public static final String QUEUE = "ykucukcinar_queue";
    /**
     * The constant EXCHANGE.
     */
    public static final String EXCHANGE = "ykucukcinar_exchange";
    /**
     * The constant ROUTING_KEY.
     */
    public static final String ROUTING_KEY = "ykucukcinar_routingKey";

    /**
     * Queue queue.
     *
     * @return the queue
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE);
    }

    /**
     * Exchange topic exchange.
     *
     * @return the topic exchange
     */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    /**
     * Binding binding.
     *
     * @param queue    the queue
     * @param exchange the exchange
     * @return the binding
     */
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    /**
     * Converter message converter.
     *
     * @return the message converter
     */
    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Template amqp template.
     *
     * @param connectionFactory the connection factory
     * @return the amqp template
     */
    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
