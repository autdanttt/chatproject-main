package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    @JsonProperty("from_user_id")
    private Long fromUserId;
    @JsonProperty("to_user_id")
    private Long toUserId;
    @JsonProperty("message_type")
    private MessageType messageType;
    @JsonProperty("content")
    private String content;

    @Override
    public String toString() {
        return "MessageRequest{" +
                "fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", messageType=" + messageType +
                ", content='" + content + '\'' +
                '}';
    }
}
