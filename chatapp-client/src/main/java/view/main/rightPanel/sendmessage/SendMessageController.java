package view.main.rightPanel.sendmessage;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import model.MessageResponse;
import model.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import di.BaseController;
import view.ApiResult;
import view.ErrorDTO;
import view.login.TokenManager;
import view.main.UserToken;
import view.main.leftPanel.chatlist.ChatSelectedEvent;
import view.main.rightPanel.components.FooterPanel;

import javax.swing.*;
import java.io.File;
import java.util.Map;

public class SendMessageController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageController.class);
    private FooterPanel footerPanel;
    private final SendMessageService sendMessageService;
    private String jwtToken;
    private Long userId;
    private Long otherUserId;
    private Long chatId;

    @Inject
    public SendMessageController(FooterPanel footerPanel, SendMessageService sendMessageService, EventBus eventBus) {
        this.footerPanel = footerPanel;
        this.sendMessageService = sendMessageService;
        footerPanel.setEmojiSelectedListener(this::sendEmoji);
        footerPanel.setImageSelectedListener(this::sendImage);
        eventBus.register(this);

        initializeListeners();
    }

    private void sendImage(File file) {
        ResponseEntity<Map> responseUpload = sendMessageService.sendFile(jwtToken, userId, file);

        if(responseUpload.getStatusCode().is2xxSuccessful()) {

            String messageContent = (String) responseUpload.getBody().get("mediaId");

           if(!messageContent.isEmpty()   && chatId != null && otherUserId != null){
               ApiResult<MessageResponse> result = sendMessageService.sendTextMessage(jwtToken, userId, otherUserId, messageContent, MessageType.IMAGE);

               LOGGER.info("Result: " + result);
               if (result.isSuccess()) {
                   MessageResponse message = result.getData();
                   LOGGER.info("Message Response: " + message);
                   eventBus.post(message);
               } else {
                   ErrorDTO error = result.getError();
                   LOGGER.info("Error: " + error);
               }
           }else {
               JOptionPane.showMessageDialog(null, "Please select a chat or enter a valid image.");
           }

        }else {
            JOptionPane.showMessageDialog(null, "Error sending file " + responseUpload.getStatusCode());
        }

        
    }

    private void sendEmoji(File file) {

        LOGGER.info("Sending text message");
        String content = file.getName();
        LOGGER.info("Text message: " + content);

        if(!content.isEmpty()   && chatId != null && otherUserId != null){
            ApiResult<MessageResponse> result = sendMessageService.sendTextMessage(TokenManager.getAccessToken(), userId, otherUserId, content, MessageType.EMOJI);
            LOGGER.info("Result: " + result);
            if (result.isSuccess()) {
                MessageResponse message = result.getData();
                LOGGER.info("Message Response: " + message);
                eventBus.post(message);
            } else {
                ErrorDTO error = result.getError();
                LOGGER.info("Error: " + error);
            }
        }else {
            JOptionPane.showMessageDialog(null, "Please select a chat or enter a valid emoji.");
        }
        footerPanel.getTextField().setText("");

    }

    @Subscribe
    public void onJwtToken(UserToken userToken) {
        LOGGER.info("Received JWT token: " + TokenManager.getAccessToken());
        this.jwtToken = TokenManager.getAccessToken();
        this.userId = userToken.getUserId();
    }

    @Subscribe
    public void onChatSelected(ChatSelectedEvent event) {
        LOGGER.info("Received chat selected: " + event.getChatId());
        this.chatId = event.getChatId();
        this.otherUserId = event.getUserId();
    }

    private void initializeListeners() {
        footerPanel.addSendButtonListener(e-> sendTextMessage());
    }

    private void sendTextMessage() {
        LOGGER.info("Sending text message");
        String content = footerPanel.getTextField().getText().trim();
        LOGGER.info("Text message: " + content);

        if(!content.isEmpty() && chatId != null && otherUserId != null) {
            ApiResult<MessageResponse> result = sendMessageService.sendTextMessage(TokenManager.getAccessToken(), userId, otherUserId, content, MessageType.TEXT);
            LOGGER.info("Result: " + result);
            if (result.isSuccess()) {
                MessageResponse message = result.getData();
                LOGGER.info("Message Response: " + message);
                eventBus.post(message);
            } else {
                ErrorDTO error = result.getError();
                LOGGER.info("Error: " + error);
            }
        }else {
            JOptionPane.showMessageDialog(null, "Please select a chat or enter a valid message.");

        }
        footerPanel.getTextField().setText("");
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
