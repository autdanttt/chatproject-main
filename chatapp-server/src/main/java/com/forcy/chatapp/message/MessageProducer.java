package com.forcy.chatapp.message;

import com.forcy.chatapp.entity.ChatGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    @Autowired private KafkaTemplate<String, MessageResponse> kafkaTemplate;


    public void sendPrivateMessage(MessageResponse response) {
        kafkaTemplate.send("chat-messages", response);
    }

    public void sendGroupMessage(MessageResponse response) {
        kafkaTemplate.send("group-messages", response);
    }
}
