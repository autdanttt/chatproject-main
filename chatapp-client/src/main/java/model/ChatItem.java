package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatItem {
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("other_user_id")
    private Long otherUserId;
    @JsonProperty("other_user_full_name")
    private String otherUserFullName;
    @JsonProperty("last_message")
    private String lastMessage;
    @JsonProperty("last_message_time")
    private Date lastMessageTime;
    @JsonProperty("image_url")
    private String avatarUrl;

    public String getFormattedTime() {
        return lastMessageTime != null
                ? new SimpleDateFormat("HH:mm").format(lastMessageTime)
                : "";
    }
}