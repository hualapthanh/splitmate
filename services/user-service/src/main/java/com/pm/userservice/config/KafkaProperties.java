package com.pm.userservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
    private Topics topics = new Topics();
    private String consumerGroup = "splitmate-user-service-v1";

    @Data
    public static class Topics {
        private String userRegistered = "user-registered-topic";
        private String userRegisteredDlt = "user-registered-topic.DLT";
    }
}
