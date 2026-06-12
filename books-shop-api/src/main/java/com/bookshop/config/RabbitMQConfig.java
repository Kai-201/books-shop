package com.bookshop.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  // Spring 启动时加载我
public class RabbitMQConfig {

    // 常量：避免写死字符串，方便引用和改
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_PAY_QUEUE = "order.pay.queue";
    public static final String ORDER_PAY_KEY = "order.pay";

    @Bean  // 注册交换机
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
        // DirectExchange = 按路由键精准匹配，不是通配
    }

    @Bean  // 注册队列
    public Queue orderPayQueue() {
        return QueueBuilder.durable(ORDER_PAY_QUEUE).build();
        // durable = 服务器重启后队列还在，消息不丢
    }

    @Bean  // 绑定
    public Binding orderPayBinding() {
        return BindingBuilder
                .bind(orderPayQueue())      // 这个队列
                .to(orderExchange())        // 绑到这个交换机
                .with(ORDER_PAY_KEY);       // 路由键 = "order.pay"
    }
}
