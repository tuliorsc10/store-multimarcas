package com.tulio.store_multimarcas.configuration.rabbitmq;

import com.seuprojeto.common.consumer.RabbitQueuesEnum;
import com.seuprojeto.common.consumer.dto.MessageWrapper;
import com.tulio.store_multimarcas.configuration.MessageProducer;
import com.tulio.store_multimarcas.configuration.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "messaging.type", havingValue = "rabbitmq")
public class RabbitMqProducer implements MessageProducer<Object> {

    private final RabbitTemplate rabbitTemplate;
    private final MessageProperties messageProperties;

    public RabbitMqProducer(RabbitTemplate rabbitTemplate, MessageProperties messageProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageProperties = messageProperties;
    }

    @Override
    public void send(String destination, MessageWrapper<Object> message) {
        rabbitTemplate.convertAndSend(
                messageProperties.getRabbit().getExchange(),
                RabbitQueuesEnum.getRoutingKey(destination),
                message
        );
    }
}
