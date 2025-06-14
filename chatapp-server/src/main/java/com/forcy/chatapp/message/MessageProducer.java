package com.forcy.chatapp.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    @Autowired private KafkaTemplate<String, MessageResponse> kafkaTemplate;


    public void send(MessageResponse response) {
        kafkaTemplate.send("chat-messages", response);
    }
}
