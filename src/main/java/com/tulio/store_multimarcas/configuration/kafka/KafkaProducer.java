package com.tulio.store_multimarcas.configuration.kafka;

import com.seuprojeto.common.consumer.dto.MessageWrapper;
import com.tulio.store_multimarcas.configuration.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "messaging.type", havingValue = "kafka")
public class KafkaProducer implements MessageProducer<Object> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessageWithCallback(String topic, Object message) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message sent successfully to topic: {} | Offset: {}",
                        topic, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message to topic: {}", topic, ex);
            }
        });
    }

    @Override
    public void send(String destination, MessageWrapper<Object> message) {
        try {
            SendResult<String, Object> result = kafkaTemplate.send(destination, message)
                    .get(30, TimeUnit.SECONDS);

            log.info("Message confirmed. Offset: {}", result.getRecordMetadata().offset());

        } catch (TimeoutException e) {
            log.error("Timeout while sending message", e);

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error sending message", e);
            Thread.currentThread().interrupt();
        }
    }
}
