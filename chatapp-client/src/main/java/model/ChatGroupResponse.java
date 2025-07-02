package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatGroupResponse {
    @JsonProperty("group_id")
    private Long groupId;
    @JsonProperty("group_name")
    private String groupName;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("last_message_content")
    private String lastMessageContent;
    @JsonProperty("last_message_sender_name")
    private String lastMessageSenderName;
    @JsonProperty("last_message_time")
    private Date lastMessageTime;
}
