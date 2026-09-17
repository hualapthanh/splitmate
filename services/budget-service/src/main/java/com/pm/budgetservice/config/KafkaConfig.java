package com.pm.budgetservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Value("${spring.kafka.bootstrap-servers:127.0.0.1:9092}")
    private String bootstrapServers;

    @Bean
    public org.springframework.kafka.core.ProducerFactory<String, Object> producerFactory() {
        java.util.Map<String, Object> configProps = new java.util.HashMap<>();
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonSerializer.class);
        return new org.springframework.kafka.core.DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate() {
        return new org.springframework.kafka.core.KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic budgetEventsTopic() {
        return TopicBuilder.name(kafkaProperties.getTopics().getBudgetEvents())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
