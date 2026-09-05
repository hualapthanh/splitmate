package com.pm.budgetservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public NewTopic budgetEventsTopic() {
        return TopicBuilder.name(kafkaProperties.getTopics().getBudgetEvents())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
