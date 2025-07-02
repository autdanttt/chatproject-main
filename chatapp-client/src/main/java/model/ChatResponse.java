package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("other_user_id")
    private Long otherUserId;
    @JsonProperty("other_user_full_name")
    private String otherUserFullName;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("last_message")
    private String lastMessage;
    @JsonProperty("last_message_time")
    private Date lastMessageTime;

    @Override
    public String toString() {
        return "ChatResponse{" +
                "chatId=" + chatId +
                ", otherUsername='" + otherUsername + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastMessageTime=" + lastMessageTime +
                '}';
    }
}