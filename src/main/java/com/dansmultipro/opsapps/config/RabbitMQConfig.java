package com.dansmultipro.opsapps.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EMAIL_REGISTER_EX = "email.notification.exchange.regist";
    public static final String EMAIL_REGISTER_QUEUE = "email.notification.queue.regist";
    public static final String EMAIL_REGISTER_KEY = "email.notification.key.regist";

    public static final String EMAIL_CREATE_TRANSACTION_EX = "email.notification.exchange.transaction.create";
    public static final String EMAIL_CREATE_TRANSACTION_QUEUE = "email.notification.queue.transaction.create";
    public static final String EMAIL_CREATE_TRANSACTION_KEY = "email.notification.key.transaction.create";

    public static final String EMAIL_UPDATE_TRANSACTION_EX = "email.notification.exchange.transaction.update";
    public static final String EMAIL_UPDATE_TRANSACTION_QUEUE = "email.notification.queue.transaction.update";
    public static final String EMAIL_UPDATE_TRANSACTION_KEY = "email.notification.key.transaction.update";

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange registerExchange(){
        return new DirectExchange(EMAIL_REGISTER_EX);
    }

    @Bean
    public DirectExchange createExchange(){
        return new DirectExchange(EMAIL_CREATE_TRANSACTION_EX);
    }

    @Bean
    public DirectExchange updatedExchange() {
        return new DirectExchange(EMAIL_UPDATE_TRANSACTION_EX);
    }

    @Bean
    public Queue registerQueue() {
        return QueueBuilder.durable(EMAIL_REGISTER_QUEUE).build();
    }

    @Bean
    public Queue createQueue() {
        return QueueBuilder.durable(EMAIL_CREATE_TRANSACTION_QUEUE).build();
    }

    @Bean
    public Queue updatedQueue() {
        return QueueBuilder.durable(EMAIL_UPDATE_TRANSACTION_QUEUE).build();
    }

    @Bean
    public Binding registerBinding() {
        return BindingBuilder.bind(registerQueue())
                .to(registerExchange())
                .with(EMAIL_REGISTER_KEY);
    }

    @Bean
    public Binding successBinding() {
        return BindingBuilder.bind(createQueue())
                .to(createExchange())
                .with(EMAIL_CREATE_TRANSACTION_KEY);
    }

    @Bean
    public Binding updateBinding() {
        return BindingBuilder.bind(updatedQueue())
                .to(updatedExchange())
                .with(EMAIL_UPDATE_TRANSACTION_KEY);
    }
}
