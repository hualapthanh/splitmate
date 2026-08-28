package com.pm.groupservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
    private Topics topics = new Topics();

    @Data
    public static class Topics {
        private String groupEvents = "group-events-topic";
    }
}
