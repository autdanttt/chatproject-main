package com.forcy.chatapp.seen;

import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.MessageDelivery;
import com.forcy.chatapp.message.MessageDeliveryRepository;
import com.forcy.chatapp.message.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SeenService {

    private final Logger LOGGER = LoggerFactory.getLogger(SeenService.class);

    private final MessageDeliveryRepository messageDeliveryRepository;

    public SeenService(MessageDeliveryRepository messageDeliveryRepository) {
        this.messageDeliveryRepository = messageDeliveryRepository;
    }

    public void updateSeenAtForPrivateChat(SeenEvent event) {
        List<MessageDelivery> deliveries = messageDeliveryRepository
                .findPrivateMessagesToUpdateSeenAt(event.getUserId(), event.getChatId(), event.getMessageId());

        for (MessageDelivery delivery : deliveries) {
            if (delivery.getSeenAt() == null) {
                delivery.setSeenAt(new Date());
                messageDeliveryRepository.save(delivery);
                LOGGER.info("Message {} seen at {} for user {}", event.getMessageId(), delivery.getSeenAt().getTime(), event.getUserId());
            }
        }
    }

    public void updateSeenAtForGroup(SeenEvent event) {
        List<MessageDelivery> deliveries =
                messageDeliveryRepository.findGroupMessagesToUpdateSeenAt(
                        event.getUserId(), event.getGroupId(), event.getMessageId());

        for (MessageDelivery delivery : deliveries) {
            delivery.setSeenAt(new Date());
        }

        messageDeliveryRepository.saveAll(deliveries);
    }
}
