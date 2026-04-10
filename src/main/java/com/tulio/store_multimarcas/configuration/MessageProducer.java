package com.tulio.store_multimarcas.configuration;

import com.seuprojeto.common.consumer.dto.MessageWrapper;

public interface MessageProducer<T> {
    void send(String destination, MessageWrapper<T> message);
}
