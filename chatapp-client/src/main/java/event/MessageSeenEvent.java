package event;

public class MessageSeenEvent {
     private Long messageId;
     public MessageSeenEvent(Long messageId) {
         this.messageId = messageId;
     }
     public Long getMessageId() {
         return messageId;
     }
}
