package view.main.rightPanel.message;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import event.LastMessageEvent;
import event.MessageSeenEvent;
import model.MessageResponse;
import model.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import di.BaseController;
import view.login.TokenManager;
import view.main.UserToken;
import event.ChatSelectedEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessagePanel messagePanel;
    private final MessageService messageService;
    private String jwtToken;
    private Long userId;

    @Inject
    public MessageController(MessagePanel messagePanel, MessageService messageService,EventBus eventBus) {
        this.messagePanel = messagePanel;
        this.messageService = messageService;
        eventBus.register(this);
    }

    @Subscribe
    public void onGetJwtToken(UserToken userToken) {
        this.jwtToken = TokenManager.getAccessToken();
        this.userId = userToken.getUserId();
    }

    @Subscribe
    public void onChatSelectedEvent(ChatSelectedEvent event) {
        Long chatId = event.getChatId();
        addListMessage(chatId, userId, event.getType());
    }
    
    @Subscribe
    public void addMessage(MessageResponse response) {
        addMessagePanel(response);
    }

    @Subscribe
    public void updateSeen(LastMessageEvent event) {
        Long messageId = event.getLastVisibleId();
        eventBus.post( new MessageSeenEvent(messageId));
    }
    private void addMessagePanel(MessageResponse message) {
        boolean isSentByMe = message.getFromUserId() != null && message.getFromUserId().equals(userId);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date sentAt = message.getSentAt();
        String time = sentAt != null
                ? formatter.format(sentAt)
                : formatter.format(new Date());
        MessageType messageType;
        if (message.getMessageType().equals("TEXT")){
            messageType = MessageType.TEXT;
        } else if (message.getMessageType().equals("EMOJI")) {
            messageType = MessageType.EMOJI;
        }else {
            messageType = MessageType.IMAGE;
        }
        messagePanel.addMessage(message.getMessageId(),message.getContent(),message.getFromFullName(),isSentByMe, time, messageType);
    }

    private void addListMessage(Long chatId, Long userId,String type) {
        messagePanel.clearMessages();
        MessageResponse[] messages;
        if(type.equals("GROUP")){
            messages = messageService.getGroupMessageByGroupId(chatId,TokenManager.getAccessToken());
        }else {
            messages = messageService.getMessageByChatId(chatId, TokenManager.getAccessToken());
        }
        for(int i = 0; i < messages.length; i++) {
            MessageResponse message = messages[i];
            boolean isSentByMe = message.getFromUserId() != null && message.getFromUserId().equals(userId);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date sentAt = message.getSentAt();
            String time = sentAt != null
                    ? formatter.format(sentAt)
                    : formatter.format(new Date());

            MessageType messageType;
            if (message.getMessageType().equals("TEXT")){
                messageType = MessageType.TEXT;
            } else if (message.getMessageType().equals("EMOJI")) {
                messageType = MessageType.EMOJI;
            }else {
                messageType = MessageType.IMAGE;
            }
            messagePanel.addMessage(message.getMessageId(),message.getContent(),message.getFromFullName(), isSentByMe, time, messageType);
        }
    }

    @Override
    protected void setupDependencies() {

    }

    @Override
    public void activate(Object... params) {

    }

    @Override
    public void deactivate() {

    }
}
