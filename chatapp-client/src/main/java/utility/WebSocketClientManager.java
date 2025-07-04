package utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Provider;
import dev.onvoid.webrtc.media.audio.AudioDevice;
import dev.onvoid.webrtc.media.video.VideoDevice;
import event.MessageSeenEvent;
import model.ReadyMessage;
import dev.onvoid.webrtc.RTCIceCandidate;
import dev.onvoid.webrtc.RTCSdpType;
import dev.onvoid.webrtc.RTCSessionDescription;
import model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import payload.*;
import view.*;
import view.login.TokenManager;
import view.main.UserToken;
import event.ChatSelectedEvent;

import javax.swing.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Timer;
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
    private MainVideoFrame videoFrame;
    private Long currentUserId;
    private final Map<Long, Long> pendingSeenPrivate = new ConcurrentHashMap<>();
    private final Map<Long, Long> pendingSeenGroup = new ConcurrentHashMap<>();
    private Long chatId;
    private boolean isGroup;
    private Long otherUserId;
    private VideoDevice selectedCamera;
    private AudioDevice selectedMic;

    @Inject
    private Provider<MainVideoFrameController> videoFrameControllerProvider;


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
            otherUserId = chatSelectedEvent.getUserId();
        }
    }

    @Subscribe
    public void onCallEndedEvent(CallEndedEvent callEndedEvent) {
        logger.info("Received CallEndedEvent");
        sendHangupEvent(currentUserId, otherUserId);
        webRTCManager.endCall();
        videoFrame.closeFrame();
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
//                logger.info("Running seen sender timer");
                for (Map.Entry<Long, Long> entry : pendingSeenPrivate.entrySet()) {
                    logger.info("Pending seen private: {}",pendingSeenPrivate);
                    logger.info("Pending seen entry: {}",entry);
                    Long chatId = entry.getKey();
                    Long messageId = entry.getValue();
//                    logger.info("Seen sender timer: chatId={}, messageId={}", chatId, messageId);
                    sendSeenEvent(currentUserId, chatId, null, messageId);
                }

                for (Map.Entry<Long, Long> entry : pendingSeenGroup.entrySet()) {
                    Long groupId = entry.getKey();
                    Long messageId = entry.getValue();
//                    logger.info("Seen sender timer: groupId={}, messageId={}", groupId, messageId);
                    sendSeenEvent(currentUserId, null, groupId, messageId);
                }
            }
        }, 0, 3000); // Gửi mỗi 3 giây
    }


    public ApiResult<String> setupWebSocket(String jwtToken, String email) {
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


                    // Subscription cho call-request
                    logger.info("Subscribing to call request");
                    subscribeToCallRequest();
                    logger.info("Subscribing to call response");
                    subscribeToCallResponse();

                    // Gửi ready
                    ReadyMessage readyMessage = new ReadyMessage();
                    readyMessage.setSender(email);
                    readyMessage.setMessage("READY");
                    session.send("/app/ready", readyMessage);

                    subscribeToSdp();
                    subscribeToCandidate();

                    subscribeToEndedCall();
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

    private void subscribeToEndedCall() {
        stompSession.subscribe("/user/queue/video-call/peer", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return HangupMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                HangupMessage message = (HangupMessage) payload;
                logger.info("The call has been ended due to a peer"  + message.getFromUserId());
                webRTCManager.endCall();
                videoFrame.closeFrame();
            }
        });
    }

    private void subscribeToCallResponse() {
        stompSession.subscribe("/user/queue/call-response", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return CallResponsePayload.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                CallResponsePayload response = (CallResponsePayload) payload;
                logger.info("Call response received from {}: {}", response.getFromUserId(), response.isAccepted());
                SwingUtilities.invokeLater(() -> {
                    if (response.isAccepted()) {
                        //Hien thi chon phan cung
                        HardwareSelectionDialog hardwareSelectionDialog = new HardwareSelectionDialog(null);
                        hardwareSelectionDialog.setVisible(true);
                        if(hardwareSelectionDialog.isConfirmed()){
                            selectedCamera = hardwareSelectionDialog.getSelectedCamera();
                            selectedMic = hardwareSelectionDialog.getSelectedMic();

                            MainVideoFrameController mainVideoFrameController = videoFrameControllerProvider.get();
                            videoFrame = mainVideoFrameController.getMainVideoFrame();
                            // Khởi tạo WebRTC
//                            videoFrame = new MainVideoFrame();
                            videoFrame.setVisible(true);
                            webRTCManager.setVideoPanel(videoFrame.localPanel, videoFrame.remotePanel);
                            webRTCManager.initialize(response.getFromUserId());
                            webRTCManager.addMediaStream(selectedCamera, selectedMic);
                            webRTCManager.createOffer(response.getFromUserId());
                            videoFrame.showActiveState(); // Giả định MainVideoFrame có phương thức cập nhật trạng thái
                        }
                    } else {
                        // Đóng MainVideoFrame hoặc hiển thị thông báo từ chối
                        videoFrame.dispose(); // Đóng form
                        videoFrame = null;
                        JOptionPane.showMessageDialog(null,
                                "Call rejected by user " + response.getToUserId(),
                                "Call Rejected",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
        });
    }

    private void subscribeToCallRequest() {
        stompSession.subscribe("/user/queue/call-request", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return CallRequestPayload.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                CallRequestPayload callRequestPayload = (CallRequestPayload) payload;
                logger.info("Call request received from {}", callRequestPayload.getCallerName());
                showCallConfirmation(callRequestPayload.getFromUserId(), callRequestPayload.getCallerName());
            }
        });

    }

    private void showCallConfirmation(Long callerId, String callerName) {
        SwingUtilities.invokeLater(() -> {
            int response = JOptionPane.showConfirmDialog(
                    null,
                    "Incoming call from " + callerName + " (ID: " + callerId + "). Accept?",
                    "Incoming Call",
                    JOptionPane.YES_NO_OPTION
            );

            try {
                RestTemplate restTemplate = new RestTemplate();
                String url = "http://localhost:10000/video-call/call-response";

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + TokenManager.getAccessToken());

                // Sử dụng CallResponsePayload
                CallResponsePayload payload = new CallResponsePayload();
                payload.setFromUserId(TokenManager.getUserId());
                payload.setToUserId(callerId);
                payload.setAccepted(response == JOptionPane.YES_OPTION);

                HttpEntity<CallResponsePayload> request = new HttpEntity<>(payload, headers);
                ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, request, Map.class);

                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    if (response == JOptionPane.YES_OPTION) {

                        HardwareSelectionDialog hardwareSelectionDialog = new HardwareSelectionDialog(null);
                        hardwareSelectionDialog.setVisible(true);
                        if(hardwareSelectionDialog.isConfirmed()){
                            selectedCamera = hardwareSelectionDialog.getSelectedCamera();
                            selectedMic = hardwareSelectionDialog.getSelectedMic();

                            MainVideoFrameController mainVideoFrameController = videoFrameControllerProvider.get();
                            videoFrame = mainVideoFrameController.getMainVideoFrame();
                            videoFrame.setVisible(true);
                            webRTCManager.setVideoPanel(videoFrame.localPanel, videoFrame.remotePanel);
                            webRTCManager.initialize(callerId);
                            webRTCManager.addMediaStream(selectedCamera, selectedMic);
                            videoFrame.showActiveState();
                        }
                    }
                    // Hiển thị thông báo từ response
                    String message = (String) responseEntity.getBody().get("message");
                    JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error sending call response", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {

                } else {
                    logger.error("Error sending call response: {}", e.getMessage(), e);
                    JOptionPane.showMessageDialog(null, "Error sending call response: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                logger.error("Error sending call response: {}", e.getMessage(), e);
                JOptionPane.showMessageDialog(null, "Error sending call response", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
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
//            logger.info("Sent seen event");
        }else {
            logger.info("Can't send seen event since stomp session is not connected");
        }
    }

    public void sendHangupEvent(Long userId, Long otherUserId){
        if(stompSession != null && stompSession.isConnected()){
            HangupMessage hangupMessage = new HangupMessage();
            hangupMessage.setType("hangup");
            hangupMessage.setFromUserId(userId);
            hangupMessage.setToUserId(otherUserId);

            logger.info("Sent hangup event");
            stompSession.send("/app/video-call/hangup", hangupMessage);
        }else {
            logger.info("Can't send hangup event since stomp session is not connected");
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
