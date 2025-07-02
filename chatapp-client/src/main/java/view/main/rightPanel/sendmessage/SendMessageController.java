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
import view.main.rightPanel.components.FooterRightPanel;

import javax.swing.*;
import java.io.File;
import java.util.Map;

public class SendMessageController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageController.class);
    private FooterRightPanel footerRightPanel;
    private final SendMessageService sendMessageService;
    private String jwtToken;
    private Long userId;
    private Long otherUserId;
    private Long chatId;
    private String type;

    @Inject
    public SendMessageController(FooterRightPanel footerRightPanel, SendMessageService sendMessageService, EventBus eventBus) {
        this.footerRightPanel = footerRightPanel;
        this.sendMessageService = sendMessageService;

        footerRightPanel.setEmojiSelectedListener(this::sendEmoji);
        footerRightPanel.setImageSelectedListener(this::sendImage);
        eventBus.register(this);

        initializeListeners();
    }
    @Subscribe
    public void onJwtToken(UserToken userToken) {
        this.jwtToken = TokenManager.getAccessToken();
        this.userId = userToken.getUserId();
    }

    @Subscribe
    public void onChatSelected(ChatSelectedEvent event) {
        this.chatId = event.getChatId();
        this.otherUserId = event.getUserId();
        this.type = event.getType();
    }


    private void sendImage(File file) {
        ResponseEntity<Map> responseUpload = sendMessageService.sendFile(jwtToken, userId, file);

        if(responseUpload.getStatusCode().is2xxSuccessful()) {

            String messageContent = (String) responseUpload.getBody().get("mediaId");

           if(!messageContent.isEmpty()   && chatId != null){
               ApiResult<MessageResponse> result;
               if(type.equals("CHAT")) {
                   result = sendMessageService.sendTextMessage(TokenManager.getAccessToken(), userId, otherUserId, messageContent, MessageType.IMAGE);
               }else {
                   result = sendMessageService.sendTextGroupMessage(TokenManager.getAccessToken(), userId,chatId,messageContent, MessageType.IMAGE);
               }

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
        String content = file.getName();
        LOGGER.info("Text message: " + content);

        if(!content.isEmpty() && chatId != null){

            ApiResult<MessageResponse> result;
            if(type.equals("CHAT")) {
                result = sendMessageService.sendTextMessage(TokenManager.getAccessToken(), userId, otherUserId, content, MessageType.EMOJI);
            }else {
                result = sendMessageService.sendTextGroupMessage(TokenManager.getAccessToken(), userId,chatId,content, MessageType.EMOJI);
            }

            if (result.isSuccess()) {
                MessageResponse message = result.getData();
                eventBus.post(message);
            } else {
                ErrorDTO error = result.getError();
                LOGGER.info("Error: " + error);
            }
        }else {
            JOptionPane.showMessageDialog(null, "Please select a chat or enter a valid emoji.");
        }
        footerRightPanel.getTextField().setText("");

    }


    private void initializeListeners() {
        footerRightPanel.addSendButtonListener(e-> sendTextMessage());
    }

    private void sendTextMessage() {
        String content = footerRightPanel.getTextField().getText().trim();

        if(!content.isEmpty() && chatId != null) {
            ApiResult<MessageResponse> result;
            if(type.equals("CHAT")) {
                result = sendMessageService.sendTextMessage(TokenManager.getAccessToken(), userId, otherUserId, content, MessageType.TEXT);
            }else {
                result = sendMessageService.sendTextGroupMessage(TokenManager.getAccessToken(), userId,chatId,content, MessageType.TEXT);
            }

            if (result.isSuccess()) {
                MessageResponse message = result.getData();
                eventBus.post(message);
            } else {
                ErrorDTO error = result.getError();
                LOGGER.info("Error: " + error);
            }
        }else {
            JOptionPane.showMessageDialog(null, "Please select a chat or enter a valid message hihi.");

        }
        footerRightPanel.getTextField().setText("");
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
