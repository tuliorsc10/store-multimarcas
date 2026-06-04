package com.tulio.store_multimarcas.configuration.kafka;

import com.seuprojeto.common.consumer.TopicEnum;
import com.seuprojeto.common.consumer.dto.MessageWrapper;
import com.tulio.store_multimarcas.configuration.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "messaging.type", havingValue = "kafka")
public class KafkaProducer implements MessageProducer<Object> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

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

        public ConsumerRecord<String, Object> sendAndReply(
                String destination,
                Object message) {

            ProducerRecord<String, Object> record =
                    new ProducerRecord<>(destination, message);

            record.headers().add(
                    KafkaHeaders.REPLY_TOPIC,
                    (destination + "_REPLY").getBytes(StandardCharsets.UTF_8));

            try {

                RequestReplyFuture<String, Object, Object> future =
                        replyingKafkaTemplate.sendAndReceive(record);

                return future.get(10, TimeUnit.SECONDS);

            } catch (TimeoutException e) {

                throw new RuntimeException(
                        "Timeout aguardando resposta do Kafka", e);

            } catch (Exception e) {

                throw new RuntimeException(
                        "Erro ao executar request/reply", e);
            }
        }
}
