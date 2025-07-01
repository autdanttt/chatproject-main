package model;

import lombok.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatItem {
    private Long chatId;
    private Long otherUserId;
    private String otherUserFullName;
    private String lastMessage;
    private Date lastMessageTime;
    private String avatarUrl;

    public String getFormattedTime() {
        return lastMessageTime != null
                ? new SimpleDateFormat("HH:mm").format(lastMessageTime)
                : "";

    }
}