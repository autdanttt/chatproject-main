package com.forcy.chatapp.chat;

import com.forcy.chatapp.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {


    private Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
    @Autowired private MessageService messageService;

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void consume(MessageResponse response) {
        logger.info("📨 [KafkaConsumer] Nhận message từ topic: {}",response);
        messageService.deliverMessage(response);
    }
}
