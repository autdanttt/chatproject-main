import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ChatStompClient {
    private static final Logger logger = LoggerFactory.getLogger(ChatStompClient.class);

    private final String url;
    private final String username;
    private final String jwtToken;
    private final Consumer<String> messageConsumer;
    private StompSession session;

    public ChatStompClient(String url, String username, String jwtToken, Consumer<String> messageConsumer) {
        this.url = url;
        this.username = username;
        this.jwtToken = jwtToken;
        this.messageConsumer = messageConsumer;
        logger.info("Initialized ChatStompClient with URL: {}, username: {}", url, username);
    }

    public void connect() {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JSR310Module());
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(converter);

         StompHeaders headers = new StompHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        logger.info("Connecting with headers: {}", headers);

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                ChatStompClient.this.session = session;
                logger.info("Connected to WebSocket server: {}", connectedHeaders);

                session.subscribe("/topic/public", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return MessageResponse.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        MessageResponse message = (MessageResponse) payload;
                        String display = "[" + message.getFromUserId() + "]: " + message.getContent();
                        logger.info("Received public message: {}", display);
                        messageConsumer.accept(display);
                    }
                });

                session.subscribe("/user/queue/messages", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return MessageResponse.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        MessageResponse message = (MessageResponse) payload;
                        try {
                            if (message != null) {
                                String display = "[" + message.getFromUserId() + "]: " + message.getContent();
                                logger.info("Received message: {}", display);
                                messageConsumer.accept(display);
                            } else {
                                logger.warn("Received null message");
                            }
                        } catch (Exception e) {
                            logger.error("Error parsing message: {}", e.getMessage(), e);
                            messageConsumer.accept("Error parsing message: " + e.getMessage());
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
                messageConsumer.accept("Error: " + exception.getMessage());
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                logger.error("Transport error: {}", exception.getMessage(), exception);
                messageConsumer.accept("Transport error: " + exception.getMessage());
            }
        };

        if (sessionHandler == null) {
            logger.error("StompSessionHandler cannot be null");
            throw new IllegalStateException("StompSessionHandler cannot be null");
        }

        logger.info("🔗 Connecting to: {}", url);
        logger.info("🔐 JWT Token: {}", jwtToken);

        CompletableFuture<StompSession> future = stompClient.connectAsync(url, (WebSocketHttpHeaders) null, headers, sessionHandler);
        future.whenComplete((stompSession, throwable) -> {
            if (throwable != null) {
                logger.error("Connection failed: {}", throwable.getMessage(), throwable);
                messageConsumer.accept("Failed to connect: " + throwable.getMessage());
            } else {
                logger.info("Connection succeeded: {}", stompSession.getSessionId());
            }
        });
    }

    public void disconnect() {
        if (session != null) {
            session.disconnect();
            logger.info("Disconnected from WebSocket server");
        }
    }

    public void sendMessage(MessageResponse message) {
        if (session != null && session.isConnected()) {
            session.send("/app/chat", message);
            logger.info("Sent message: {}", message);
        } else {
            logger.warn("Cannot send message: session is not connected");
        }
    }
}