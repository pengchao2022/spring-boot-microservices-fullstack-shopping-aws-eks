package com.ecommerce.order.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // 订单相关队列和交换器
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_STATUS_QUEUE = "order.status.queue";
    public static final String ORDER_CANCELLED_QUEUE = "order.cancelled.queue";
    public static final String ORDER_EXCHANGE = "order.exchange";
    
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }
    
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(ORDER_CREATED_QUEUE, true);
    }
    
    @Bean
    public Queue orderStatusQueue() {
        return new Queue(ORDER_STATUS_QUEUE, true);
    }
    
    @Bean
    public Queue orderCancelledQueue() {
        return new Queue(ORDER_CANCELLED_QUEUE, true);
    }
    
    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder.bind(orderCreatedQueue())
                .to(orderExchange())
                .with("order.created");
    }
    
    @Bean
    public Binding orderStatusBinding() {
        return BindingBuilder.bind(orderStatusQueue())
                .to(orderExchange())
                .with("order.status.*");
    }
    
    @Bean
    public Binding orderCancelledBinding() {
        return BindingBuilder.bind(orderCancelledQueue())
                .to(orderExchange())
                .with("order.cancelled");
    }
}
