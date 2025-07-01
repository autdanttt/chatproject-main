package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    @JsonProperty("message_id")
    private Long messageId;      // Thêm để khớp với JSON
    @JsonProperty("from_user_id")
    private Long fromUserId;
    @JsonProperty("to_user_id")
    private Long toUserId;       // Thêm nếu cần (dùng để xác định chat 1-1)
    @JsonProperty("from_full_name")
    private String fromFullName;
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("group_id")
    private Long groupId;
    @JsonProperty("message_type")
    private String messageType;  // Thêm nếu cần xử lý loại tin nhắn
    @JsonProperty("content")
    private String content;
    @JsonProperty("sent_at")
    private Date sentAt;
    @JsonProperty("delivered_at")
    private Date deliveredAt;

    @Override
    public String toString() {
        return "MessageResponse{" +
                "content='" + content + '\'' +
                ", messageId=" + messageId +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                '}';
    }
}