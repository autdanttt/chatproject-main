package model;

import com.fasterxml.jackson.annotation.JsonProperty;
public class ReadyMessage {
    @JsonProperty("sender")
    private String sender;

    @JsonProperty("message")
    private String message;

    public ReadyMessage() {
    }

    public ReadyMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ReadyMessage{" +
                "sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}