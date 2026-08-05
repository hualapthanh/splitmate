package com.pm.authservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
    private Topic topic = new Topic();

    @Data
    public static class Topic {
        private String userRegistered = "user-registered-topic";
        private String passwordChanged = "password-changed-topic";
    }
}
