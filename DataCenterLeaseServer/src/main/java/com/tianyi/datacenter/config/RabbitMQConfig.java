package com.tianyi.datacenter.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by tianxujin on 2019/9/4 15:01
 */
@Configuration
public class RabbitMQConfig {

    public final static String QUEUE_NAME = "RQ_TYInspect_Video";

    public final static String QUEUE_RESULT = "RQ_TYInspect_Video_Result";

    public final static String EXCHANGE_NAME = "EX_BUS";

    public final static String EXCHANGE_L2 = "EX_BUS_L2";

    public final static String BINDING_KEY = "TYHelmet.File.Video.Upload";

    public final static String BINDING_RESULT = "TYHelmet.File.Video.Recognition.Level2.Inspect.Video";

    //声明队列
    @Bean
    public Queue queue() {
        //Queue(String name, boolean durable, boolean exclusive, boolean autoDelete)
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Queue queue2() {
        //Queue(String name, boolean durable, boolean exclusive, boolean autoDelete)
        return new Queue(QUEUE_RESULT);
    }

    // 创建一个 topic 类型的交换器
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public TopicExchange exchangel2() {
        return new TopicExchange(EXCHANGE_L2, true, false);
    }

    // 使用路由键（routingKey）把队列（Queue）绑定到交换器（Exchange）
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(BINDING_KEY);
    }
    @Bean
    public Binding binding2() {
        return BindingBuilder.bind(queue2()).to(exchangel2()).with(BINDING_RESULT);
    }

}
