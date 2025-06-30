package utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import event.MessageSeenEvent;
import model.ReadyMessage;
import dev.onvoid.webrtc.RTCIceCandidate;
import dev.onvoid.webrtc.RTCSdpType;
import dev.onvoid.webrtc.RTCSessionDescription;
import model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import payload.CandidatePayload;
import payload.IceCandidate;
import payload.SdpPayload;
import view.ApiResult;
import view.ErrorDTO;
import view.main.UserToken;
import view.main.leftPanel.chatlist.ChatSelectedEvent;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebSocketClientManager {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientManager.class);
    private final static String url = "ws://localhost:10000/chat";
    private StandardWebSocketClient webSocketClient;
    private StompSession stompSession;
    private final EventBus eventBus;
    private WebRTCManager webRTCManager;
    private Long currentUserId;
    private final Map<Long, Long> pendingSeenPrivate = new ConcurrentHashMap<>();
    private final Map<Long, Long> pendingSeenGroup = new ConcurrentHashMap<>();
    private Long chatId;
    private boolean isGroup;


    @Inject
    public WebSocketClientManager(WebRTCManager webRTCManager,EventBus eventBus) {
        this.webRTCManager = webRTCManager;
        this.eventBus = eventBus;
        eventBus.register(this);
        startSeenSenderTimer();
    }

    @Subscribe
    public void onJwtToken(UserToken userToken) {
        currentUserId = userToken.getUserId();
    }
    @Subscribe
    public void onSelectedEvent(ChatSelectedEvent chatSelectedEvent) {
        chatId = chatSelectedEvent.getChatId();
        if (chatSelectedEvent.getType().equals("GROUP")) {
            pendingSeenGroup.put(chatId,0L);
            isGroup = true;
        }else {
            pendingSeenPrivate.put(chatId,0L);
            isGroup = false;
        }
    }

    @Subscribe
    public void onMessageSeenEvent(MessageSeenEvent messageSeenEvent) {
        logger.info("Message seen from controller: {}", messageSeenEvent.getMessageId());
        updatePendingSeen(messageSeenEvent.getMessageId());
    }

    public void updatePendingSeen(Long messageId) {
        if(isGroup) {
            pendingSeenGroup.put(chatId,messageId);
            logger.info("Message seen from group: {}", pendingSeenGroup.get(chatId));
            logger.info("Size " + pendingSeenGroup.size());
        }else {
            pendingSeenPrivate.put(chatId,messageId);
            logger.info("Message seen for chat: {}",pendingSeenPrivate.get(chatId));
            logger.info("Size " + pendingSeenPrivate.size());
        }
    }

    private void startSeenSenderTimer() {
        Timer timer = new Timer("SeenSender", true);

        logger.info("Pending seen private: {}", pendingSeenPrivate);
        logger.info("Pending seen group: {}", pendingSeenGroup);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Running seen sender timer");
                for (Map.Entry<Long, Long> entry : pendingSeenPrivate.entrySet()) {
                    Long chatId = entry.getKey();
                    Long messageId = entry.getValue();
                    logger.info("Seen sender timer: chatId={}, messageId={}", chatId, messageId);
                    sendSeenEvent(currentUserId, chatId, null, messageId);
                }

                for (Map.Entry<Long, Long> entry : pendingSeenGroup.entrySet()) {
                    Long groupId = entry.getKey();
                    Long messageId = entry.getValue();
                    logger.info("Seen sender timer: groupId={}, messageId={}", groupId, messageId);
                    sendSeenEvent(currentUserId, null, groupId, messageId);
                }
            }
        }, 0, 3000); // Gửi mỗi 3 giây
    }


    public ApiResult<String> setupWebSocket(String jwtToken, String username) {
        try {
            webSocketClient = new StandardWebSocketClient();
            WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JSR310Module());
            MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
            converter.setObjectMapper(objectMapper);
            stompClient.setMessageConverter(converter);

            StompHeaders headers = new StompHeaders();
            headers.add("Authorization", "Bearer " + jwtToken);

            StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    stompSession = session;

                    session.subscribe("/user/queue/messages", new StompFrameHandler() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                            return MessageResponse.class;
                        }
                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                            MessageResponse message = (MessageResponse) payload;
                            if (message != null) {
                                logger.info("Message received: {}", message.getContent());
                                eventBus.post(message);
                            } else {
                                logger.warn("Received null message");
                            }
                        }
                    });

                    // Gửi ready
                    ReadyMessage readyMessage = new ReadyMessage();
                    readyMessage.setSender(username);
                    readyMessage.setMessage("READY");
                    session.send("/app/ready", readyMessage);

                    subscribeToSdp();
                    subscribeToCandidate();
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    logger.error("Transport error: {}", exception.getMessage(), exception);
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    logger.error("Session exception: {}", exception.getMessage(), exception);
                }
            };

            CompletableFuture<StompSession> future = stompClient.connectAsync(url, (WebSocketHttpHeaders) null, headers, sessionHandler);

            try {
                future.get(3, TimeUnit.SECONDS); // đợi 3 giây
                return ApiResult.ok("WebSocket connected ✅");
            } catch (TimeoutException e) {
                logger.error("WebSocket connection timeout", e);
                return ApiResult.fail(new ErrorDTO(new Date(), 408,url, List.of(e.getMessage())));
            } catch (Exception e) {
                logger.error("WebSocket connection failed", e);
                return ApiResult.fail(new ErrorDTO(new Date(), 500, url, List.of(e.getMessage())));
            }

        } catch (Exception ex) {
            logger.error("Exception during WebSocket setup", ex);
            return ApiResult.fail(new ErrorDTO(new Date(), 500, url, List.of(ex.getMessage())));
        }
    }
    private void subscribeToSdp() {
        stompSession.subscribe("/user/queue/sdp", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return SdpPayload.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                SdpPayload sdpPayload = (SdpPayload) payload;

                if("offer".equals(sdpPayload.getType())) {
                    webRTCManager.handleIncomingOffer(new RTCSessionDescription(RTCSdpType.OFFER, sdpPayload.getSdp()), sdpPayload.getFromUserId());
                }else {
                    webRTCManager.handleAnswer(new RTCSessionDescription(RTCSdpType.ANSWER, sdpPayload.getSdp()));
                }

            }
        });

    }


    private void subscribeToCandidate() {
        stompSession.subscribe("/user/queue/candidate", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return CandidatePayload.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                CandidatePayload candidatePayload = (CandidatePayload) payload;
                IceCandidate candidate = candidatePayload.getCandidate();

                RTCIceCandidate rtcIceCandidate = new RTCIceCandidate(
                        candidate.getSdpMid(),
                        candidate.getSdpMLineIndex(),
                        candidate.getCandidate()
                );

                webRTCManager.handleCandidate(rtcIceCandidate);

            }
        });
    }

    public void sendSeenEvent(Long userId, Long chatId, Long groupId, Long messageId){
        if(stompSession != null && stompSession.isConnected()){
            Map<String, Object> payload = new HashMap<>();
            payload.put("user_id", userId);
            payload.put("chat_id", chatId);
            payload.put("group_id", groupId);
            payload.put("message_id", messageId);

            stompSession.send("/app/seen", payload);
            logger.info("Sent seen event");
        }else {
            logger.info("Can't send seen event since stomp session is not connected");
        }



    }
//    public ConnectionResult setupWebSocket(String jwtToken, String username) {
//        webSocketClient = new StandardWebSocketClient();
//        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JSR310Module());
//        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//        converter.setObjectMapper(objectMapper);
//        stompClient.setMessageConverter(converter);
//
//
//        StompHeaders headers = new StompHeaders();
//        headers.add("Authorization", "Bearer " + jwtToken);
//        logger.info("Connecting with headers: " + headers);
//
//        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
//            @Override
//            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//                stompSession = session;
//                logger.info("Connected to WebSocket server: {}", connectedHeaders);
//
//                session.subscribe("/user/queue/messages", new StompFrameHandler() {
//
//                    @Override
//                    public Type getPayloadType(StompHeaders headers) {
//                        return MessageResponse.class;
//                    }
//
//                    @Override
//                    public void handleFrame(StompHeaders headers, Object payload) {
//                        MessageResponse message = (MessageResponse) payload;
////                        if (message != null && currentChatId != null && message.getChatId() != null && message.getChatId().equals(currentChatId)) {
////                            String display = "[" + message.getFromUserId() + "]: " + message.getContent();
////                            logger.info("Received message for chat {}: {}", currentChatId, display);
////
////                            String content = message.getContent();
////
////                            //  MessageType messageType = message.getMessageType().equals("TEXT") ? MessageType.TEXT : MessageType.EMOJI;
////
////                            MessageType messageType;
////                            if (message.getMessageType().equals("TEXT")){
////                                messageType = MessageType.TEXT;
////                            } else if (message.getMessageType().equals("EMOJI")) {
////                                messageType = MessageType.EMOJI;
////                            }else {
////                                messageType = MessageType.IMAGE;
////                            }
////
////                            SwingUtilities.invokeLater(() -> view.addMessage(content, message.getFromUserId(), message.getFromUserId().equals(userId),
////                                    message.getSentAt() != null ? message.getSentAt().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A",messageType));
////                        } else {
////                            logger.warn("Received null or irrelevant message for chatId: {}", currentChatId);
////                        }
//                        if(message != null){
//                            logger.info("Message received: {}", message.getContent());
//                            eventBus.post(message);
//
//                        }else {
//                            logger.warn("received null message from websocket");
//                        }
//
//                    }
//                });
//                ReadyMessage readyMessage = new ReadyMessage();
//                readyMessage.setSender(username);
//                readyMessage.setType("READY");
//                session.send("/app/ready", readyMessage);
//                logger.info("Sent ready message for user: {}", username);
//
//                subscribeToSdp();
//                subscribeToCandidate();
//            }
//
//            @Override
//            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
//                logger.error("Session exception: {}", exception.getMessage(), exception);
//            }
//
//            @Override
//            public void handleTransportError(StompSession session, Throwable exception) {
//                logger.error("Transport error: {}", exception.getMessage(), exception);
//            }
//        };
//
//
//        if (sessionHandler == null) {
//            logger.error("StompSessionHandler cannot be null");
//            throw new IllegalStateException("StompSessionHandler cannot be null");
//        }
//        logger.info("Connecting to {}", url);
//        logger.info("🔐 JWT Token: {}", jwtToken);
//
//        CompletableFuture<StompSession> future = stompClient.connectAsync(url, (WebSocketHttpHeaders) null, headers, sessionHandler);
//        future.whenComplete((stompSession, throwable) -> {
//            if (throwable != null) {
//                logger.error("Connection failed: {}", throwable.getMessage(), throwable);
//            } else {
//                logger.info("Connection succeeded: {}", stompSession.getSessionId());
//            }
//        });
//
//        return "kdfs1";
//    }




}
