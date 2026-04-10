package com.tulio.store_multimarcas.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "messaging")
@Getter
@Setter
public class MessageProperties {

    private String type;
    private Rabbit rabbit;

    @Getter
    @Setter
    public static class Rabbit {
        private String exchange;
    }
}
