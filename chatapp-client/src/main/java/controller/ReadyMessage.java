package controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReadyMessage {
    @JsonProperty("sender")
    private String sender;

    @JsonProperty("type")
    private String type;

    public ReadyMessage() {
    }

    public ReadyMessage(String sender, String type) {
        this.sender = sender;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "ReadyMessage{" +
                "sender='" + sender + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
