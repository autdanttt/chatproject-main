package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessageRequest {
    @JsonProperty("from_user_id")
    private Long fromUserId;
    @JsonProperty("to_group_id")
    private Long toGroupId;
    @JsonProperty("message_type")
    private MessageType messageType;
    @JsonProperty("content")
    private String content;

}
