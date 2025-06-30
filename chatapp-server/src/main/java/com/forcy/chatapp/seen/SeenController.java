package com.forcy.chatapp.seen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class SeenController {

    @Autowired
    private SeenService seenService;

    @MessageMapping("/seen")
    public void handleSeen(@Payload SeenEvent seenEvent) {
        if (seenEvent.getGroupId() != null) {
            seenService.updateSeenAtForGroup(seenEvent);
        } else if (seenEvent.getChatId() != null) {
            seenService.updateSeenAtForPrivateChat(seenEvent);
        } else {
            throw new IllegalArgumentException("Either groupId or chatId must be provided");
        }
    }

}
