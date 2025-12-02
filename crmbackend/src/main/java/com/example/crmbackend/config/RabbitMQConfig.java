package com.example.crmbackend.config;

import com.example.crmbackend.service.ExportService;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration
 */
@Configuration
public class RabbitMQConfig {
    public static final String DELAY_QUEUE = "activity.delay.queue";
    public static final String REMINDER_QUEUE = "activity.reminder.queue";
    public static final String DLX_EXCHANGE = "activity.dlx.exchange";

    @Bean
    public Queue exportQueue() {
        return new Queue(ExportService.EXPORT_QUEUE, true);
    }

//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(new Jackson2JsonMessageConverter());
//        return template;
//    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", REMINDER_QUEUE)
                .build();
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue reminderQueue() {
        return QueueBuilder.durable(REMINDER_QUEUE).build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(reminderQueue())
                .to(dlxExchange())
                .with(REMINDER_QUEUE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

