package utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
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

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebSocketClientManager {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientManager.class);
    private final static String url = "ws://localhost:10000/chat";
    private StandardWebSocketClient webSocketClient;
    private StompSession stompSession;
    private final EventBus eventBus;
    private WebRTCManager webRTCManager;

    @Inject
    public WebSocketClientManager(WebRTCManager webRTCManager,EventBus eventBus) {
        this.webRTCManager = webRTCManager;
        this.eventBus = eventBus;
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
            logger.info("Connecting with headers: {}", headers);

            StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    stompSession = session;
                    logger.info("✅ Connected to WebSocket server: {}", connectedHeaders);

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
                    readyMessage.setType("READY");
                    session.send("/app/ready", readyMessage);
                    logger.info("Sent READY message for user: {}", username);

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
