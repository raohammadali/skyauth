package com.skynetauth.auth_service.producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.skynetauth.auth_service.dto.dto.UserEventDto;
import com.skynetauth.auth_service.models.User;

@Service
public class UserEventProducer {

    private static final String TOPIC = "logs";

    @Autowired
    private KafkaTemplate<String, UserEventDto> kafkaTemplate;

    /**
     * Publishes a "user created" event to the configured Kafka topic.
     *
     * The event payload contains the user's first name, last name, and phone number and is sent to the "logs" topic.
     *
     * @param user the user whose data will be published as the event payload
     */
    public void sendUserCreatedEvent(User user) {
        System.out.println(String.format("Producing user created event for user: %s", user.getFirstName()));
        UserEventDto userEvent = new UserEventDto();
        userEvent.setFirstName(user.getFirstName());
        userEvent.setLastName(user.getLastName());
        userEvent.setPhone(user.getPhone());
        kafkaTemplate.send(TOPIC, userEvent);
        System.out.println(String.format("Producing user created event for user done"));
    }
}