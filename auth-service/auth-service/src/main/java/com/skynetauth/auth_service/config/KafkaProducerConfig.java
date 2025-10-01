package com.skynetauth.auth_service.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.skynetauth.auth_service.dto.dto.UserEventDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Create a ProducerFactory configured for producing UserEventDto messages to Kafka.
     *
     * Configures the bootstrap servers and sets the key serializer to StringSerializer and
     * the value serializer to JsonSerializer for serializing UserEventDto payloads.
     *
     * @return a ProducerFactory<String, UserEventDto> configured with string keys and JSON-serialized values
     */
    @Bean
    public ProducerFactory<String, UserEventDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Provides a KafkaTemplate for sending UserEventDto messages keyed by String.
     *
     * @return a KafkaTemplate<String, UserEventDto> for producing messages to Kafka topics
     */
    @Bean
    public KafkaTemplate<String, UserEventDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}