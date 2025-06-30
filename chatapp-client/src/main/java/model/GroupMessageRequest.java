package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupMessageRequest {
    @JsonProperty("from_user_id")
    private Long fromUserId;
    @JsonProperty("to_group_id")
    private Long toGroupId;
    @JsonProperty("message_type")
    private MessageType messageType;
    @JsonProperty("content")
    private String content;

    public GroupMessageRequest(Long fromUserId, Long toGroupId, MessageType messageType, String content) {
        this.fromUserId = fromUserId;
        this.toGroupId = toGroupId;
        this.messageType = messageType;
        this.content = content;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToGroupId() {
        return toGroupId;
    }

    public void setToGroupId(Long toGroupId) {
        this.toGroupId = toGroupId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public GroupMessageRequest() {
    }
}
