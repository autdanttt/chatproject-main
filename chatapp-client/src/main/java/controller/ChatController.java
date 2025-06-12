package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.*;
import org.glassfish.grizzly.http.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import utility.EmojiConverter;
import view.ChatView;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ChatController{
    private final static String url = "ws://localhost:10000/chat";

    private final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private ChatView view;
    private String username;
    private Long userId;
    private String jwtToken;
    private StompSession stompSession;
    private Long currentChatId;
//    private ChatStompClient chatClient;


    public ChatController(ChatView view,Long userId, String username, String jwtToken) {
        this.view = view;
        this.userId = userId;
        this.username = username;
        this.jwtToken = jwtToken;
        view.setEmojiSelectedListener(this::sendEmoji);
        initializeListeners();
        setupWebSocket();
        loadChats();
    }

    private void setupWebSocket() {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JSR310Module());
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(converter);


        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        logger.info("Connecting with headers: " + headers);


        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                stompSession = session;
                logger.info("Connected to WebSocket server: {}", connectedHeaders);


                session.subscribe("/user/queue/messages", new StompFrameHandler() {

                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return MessageResponse.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        MessageResponse message = (MessageResponse) payload;
                        if (message != null && currentChatId != null && message.getChatId() != null && message.getChatId().equals(currentChatId)) {
                            String display = "[" + message.getFromUserId() + "]: " + message.getContent();
                            logger.info("Received message for chat {}: {}", currentChatId, display);

                            String content = message.getContent();

                            MessageType messageType = message.getMessageType().equals("TEXT") ? MessageType.TEXT : MessageType.EMOJI;

                            SwingUtilities.invokeLater(() -> view.addMessage(content, message.getFromUserId(), message.getFromUserId().equals(userId),
                                    message.getSentAt() != null ? message.getSentAt().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A",messageType));
                        } else {
                            logger.warn("Received null or irrelevant message for chatId: {}", currentChatId);
                        }

                    }
                });
                ReadyMessage readyMessage = new ReadyMessage();
                readyMessage.setSender(username);
                readyMessage.setType("READY");
                session.send("/app/ready", readyMessage);
                logger.info("Sent ready message for user: {}", username);
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                logger.error("Session exception: {}", exception.getMessage(), exception);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(view.getFrame(), "Error: " + exception.getMessage()));
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                logger.error("Transport error: {}", exception.getMessage(), exception);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(view.getFrame(), "Transport error: " + exception.getMessage()));
            }
        };


        if (sessionHandler == null) {
            logger.error("StompSessionHandler cannot be null");
            throw new IllegalStateException("StompSessionHandler cannot be null");
        }
        logger.info("Connecting to {}", url);
        logger.info("🔐 JWT Token: {}", jwtToken);

        CompletableFuture<StompSession> future = stompClient.connectAsync(url, (WebSocketHttpHeaders) null, headers, sessionHandler);
        future.whenComplete((stompSession, throwable) -> {
            if (throwable != null) {
                logger.error("Connection failed: {}", throwable.getMessage(), throwable);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(view.getFrame(), "Failed to connect: " + throwable.getMessage()));
            } else {
                logger.info("Connection succeeded: {}", stompSession.getSessionId());
            }
        });


    }


    private void displayMessage(MessageResponse messageResponse) {

    }

    private void updateChatList(ChatResponse chatResponse) {

    }

    private void loadChats() {
        try {
            String url = "http://localhost:10000/chats"; // Không cần ?username nữa
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken); // Giữ nguyên

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                ObjectMapper objectMapper = new ObjectMapper();

                objectMapper.registerModule(new JavaTimeModule()); // Bắt buộc
                objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // Sử dụng ISO format
                ChatResponse[] chats = objectMapper.readValue(conn.getInputStream(), ChatResponse[].class);
                view.getChatListModel().clear();
                for (ChatResponse chat : chats) {

                    view.getChatListModel().addElement(new ChatItem(
                            chat.getChatId(),
                            chat.getOtherUserId(),
                            chat.getOtherUsername(),
                            chat.getLastMessage(),
                            chat.getLastMessageTime()
                    ));
                }
                System.out.println("User " + getUsername() + " loaded chats successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view.getFrame(), "Error loading chats: " + e.getMessage());
        }
    }

    private void initializeListeners() {
        view.getSearchButton().addActionListener(e->searchUsers());
        view.getSendButton().addActionListener(e->sendMessage());
        view.getChatList().addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()) {
                ChatItem selectedChat = view.getChatList().getSelectedValue();
                if(selectedChat != null) {
                    currentChatId = selectedChat.getChatId();
                    view.getChatPanel().setVisible(true);
                    view.clearMessages();
                    loadChatHistory(currentChatId);
                }
            }
        });

        view.getSendButton().addActionListener(e->sendMessage());
    }

    private void loadChatHistory(Long chatId) {
        logger.info("Chat id: " + chatId);
        view.clearMessages();
        try{
            String url = "http://localhost:10000/chats/" + chatId + "/messages";
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            logger.info("Load by URL: " + url);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

                MessageResponse[] messages = objectMapper.readValue(conn.getInputStream(), MessageResponse[].class);
                logger.info("Loaded " + messages.length + " messages");

                for (int i = 0; i < messages.length; i++) {
                    MessageResponse message = messages[i];
                    try {
                        logger.info("Processing message " + i + ": " + message);
                        boolean isSentByMe = message.getFromUserId() != null && message.getFromUserId().equals(userId);
                        String time = message.getSentAt() != null
                                ? message.getSentAt().format(DateTimeFormatter.ofPattern("HH:mm"))
                                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

                        MessageType messageType = message.getMessageType().equals("TEXT") ? MessageType.TEXT : MessageType.EMOJI;
                        view.addMessage(message.getContent(), message.getFromUserId(), isSentByMe, time, messageType);
                    } catch (Exception e) {
                        logger.error("Error processing message " + i + ": " + e.getMessage());
                    }
                }
            }

        }catch (Exception e){
            JOptionPane.showMessageDialog(view.getFrame(), "Error loading chats: " + e.getMessage());

        }

    }

    private void loadChatMessages() {
//        Long chatId = view.getSelectedChatId();
//        if(chatId == null) return;

//        try{
//            String url = "http://localhost:8080/chats/" + chatId + "/messages";
//
//        }
//
    }

    private void sendMessage() {
        String messageContent = view.getMessageInput().getText().trim();

        if(!messageContent.isEmpty() && currentChatId != null && stompSession != null && userId != null) {
            try{
                Long toUserId = getOtherUserId(currentChatId);
                if(toUserId == null) {
                    JOptionPane.showMessageDialog(view.getFrame(), "Cannot determine recipient for chatId: " + currentChatId);
                    return;
                }
                MessageRequest messageRequest = new MessageRequest();
                messageRequest.setFromUserId(userId);
                messageRequest.setToUserId(toUserId);
                messageRequest.setMessageType(MessageType.TEXT);
                messageRequest.setContent(messageContent);
                messageRequest.setMessageType(MessageType.TEXT);

                ObjectMapper objectMapper = new ObjectMapper();
//                String json = objectMapper.writeValueAsString(messageRequest);
                String url = "http://localhost:10000/messages";
//                stompSession.send(url, json.getBytes());
//                logger.info("Sent message: " + json);
                RestTemplate restTemplate = new RestTemplate();
                String json = objectMapper.writeValueAsString(messageRequest);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + jwtToken);
                HttpEntity<String> entity = new HttpEntity<>(json, headers);

                String response = restTemplate.postForObject(url, entity, String.class);

                // Ghi log nếu cần
                logger.info("Sent message: " + json);
                logger.info("Response: " + response);


                SwingUtilities.invokeLater(()->{
                    view.addMessage(messageContent, userId, true, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), MessageType.TEXT);
                    view.getMessageInput().setText("");
                });
            }catch (Exception e){
                logger.error("Error sending message: " + e.getMessage());
                JOptionPane.showMessageDialog(view.getFrame(), "Error sending message: " + e.getMessage());
            }
        }else {
            JOptionPane.showMessageDialog(view.getFrame(), "Please select a chat or enter a valid message.");
        }

    }

    private Long getOtherUserId(Long chatId) {
        Enumeration<ChatItem> elements = view.getChatListModel().elements();
        Iterator<ChatItem> iterator = Collections.list(elements).iterator();

        while(iterator.hasNext()) {
            ChatItem item = iterator.next();
            if(item.getChatId().equals(chatId)) {
                Long otherUserId = item.getOtherUserId();

                if(otherUserId != null) {
                    return otherUserId;
                }else {
                    logger.warn("No other user id found for chat " + chatId);
                    return null;
                }
            }
        }
        logger.warn("No chat found with chat id " + chatId);
        return null;

    }

    private void searchUsers() {

    }

    public String getUsername() {
        return username;
    }


    public void sendEmoji(File file) {


        if( currentChatId != null && stompSession != null && userId != null) {
            try{
                Long toUserId = getOtherUserId(currentChatId);
                if(toUserId == null) {
                    JOptionPane.showMessageDialog(view.getFrame(), "Cannot determine recipient for chatId: " + currentChatId);
                    return;
                }
                MessageRequest messageRequest = new MessageRequest();
                messageRequest.setFromUserId(userId);
                messageRequest.setToUserId(toUserId);
                messageRequest.setMessageType(MessageType.EMOJI);

                String messageContent = file.getName();
                messageRequest.setContent(messageContent);

                ObjectMapper objectMapper = new ObjectMapper();
//                String json = objectMapper.writeValueAsString(messageRequest);
                String url = "http://localhost:10000/messages";
//                stompSession.send(url, json.getBytes());
//                logger.info("Sent message: " + json);
                RestTemplate restTemplate = new RestTemplate();
                String json = objectMapper.writeValueAsString(messageRequest);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + jwtToken);
                HttpEntity<String> entity = new HttpEntity<>(json, headers);

                String response = restTemplate.postForObject(url, entity, String.class);

                // Ghi log nếu cần
                logger.info("Sent message: " + json);
                logger.info("Response: " + response);


                SwingUtilities.invokeLater(()->{
                    view.addMessage(messageContent, userId, true, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), MessageType.EMOJI);

                });
            }catch (Exception e){
                logger.error("Error sending message: " + e.getMessage());
                JOptionPane.showMessageDialog(view.getFrame(), "Error sending message: " + e.getMessage());
            }
        }else {
            JOptionPane.showMessageDialog(view.getFrame(), "Please select a chat or enter a valid message.");
        }
    }
}
