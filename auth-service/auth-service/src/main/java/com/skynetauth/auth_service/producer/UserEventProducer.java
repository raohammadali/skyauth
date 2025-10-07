package com.skynetauth.auth_service.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.skynetauth.auth_service.dto.dto.UserEventDto;
import com.skynetauth.auth_service.mapper.UserEventMapper;
import com.skynetauth.auth_service.models.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserEventProducer {

    @Value("${kafka.topics.user-events}")
    private String userEventsTopic;

    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;


    public UserEventProducer(KafkaTemplate<String, UserEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    public void sendUserCreatedEvent(User user) {
        UserEventDto userEvent = new UserEventMapper().toDto(user);
        kafkaTemplate.send(userEventsTopic, userEvent)
        .whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to produce user created event for user: {}", user.getFirstName(), ex);
            } else {
                log.info("Successfully produced user created event for user: {} to partition: {}",
                user.getFirstName(), result.getRecordMetadata().partition());
            }
        });
    }
}