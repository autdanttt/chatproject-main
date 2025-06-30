package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;


public class ChatGroupResponse {
    @JsonProperty("group_id")
    private Long groupId;
    @JsonProperty("group_name")
    private String groupName;
    @JsonProperty("last_message_content")
    private String lastMessageContent;
    @JsonProperty("last_message_sender_name")
    private String lastMessageSenderName;
    @JsonProperty("last_message_time")
    private Date lastMessageTime;

    public ChatGroupResponse() {
    }

    public ChatGroupResponse(Long groupId, String groupName, String lastMessageContent, String lastMessageSenderName, Date lastMessageTime) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageSenderName = lastMessageSenderName;
        this.lastMessageTime = lastMessageTime;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public String getLastMessageSenderName() {
        return lastMessageSenderName;
    }

    public void setLastMessageSenderName(String lastMessageSenderName) {
        this.lastMessageSenderName = lastMessageSenderName;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    @Override
    public String toString() {
        return "ChatGroupResponse{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", lastMessageContent='" + lastMessageContent + '\'' +
                ", lastMessageSenderName='" + lastMessageSenderName + '\'' +
                ", lastMessageTime=" + lastMessageTime +
                '}';
    }
}
