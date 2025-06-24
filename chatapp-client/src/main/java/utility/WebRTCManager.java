package utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import dev.onvoid.webrtc.*;
import dev.onvoid.webrtc.media.MediaDevices;
import dev.onvoid.webrtc.media.MediaStreamTrack;
import dev.onvoid.webrtc.media.audio.AudioDevice;
import dev.onvoid.webrtc.media.audio.AudioOptions;
import dev.onvoid.webrtc.media.audio.AudioTrack;
import dev.onvoid.webrtc.media.audio.AudioTrackSource;
import dev.onvoid.webrtc.media.video.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.client.RestTemplate;
import payload.CandidatePayload;
import payload.IceCandidate;
import payload.SdpPayload;
import view.MainVideoFrame;
import view.VideoPanel;
import view.login.TokenManager;
import view.main.UserToken;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Logger;

public class WebRTCManager {
    private final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WebRTCManager.class);
    private static final Logger logger = Logger.getLogger(WebRTCManager.class.getName());
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(WebRTCManager.class);
    private PeerConnectionFactory factory;
    private RTCPeerConnection peerConnection;
//    private String jwtToken;

    private VideoPanel localPanel;
    private VideoPanel remotePanel;


    @Inject
    public WebRTCManager( EventBus eventBus) {
        eventBus.register(this);
    }

    @Subscribe
    public void onJwtToken(UserToken userToken) {
        LOGGER.info("Received JWT token: " + userToken.getJwtToken());
 //       this.jwtToken = userToken.getJwtToken();

    }


    public void setVideoPanel(VideoPanel localPanel, VideoPanel remotePanel) {
        this.localPanel = localPanel;
        this.remotePanel = remotePanel;
    }

    public void initialize(Long toUserId) {
        try {
            factory = new PeerConnectionFactory();
            if (factory == null) {
                logger.warning("Error initializing WebRTC peer");
                return;
            }

            RTCIceServer stunServer = new RTCIceServer();
            stunServer.urls.add("stun:stun.l.google.com:19302");

            RTCConfiguration rtcConfig = new RTCConfiguration();
            rtcConfig.iceServers.add(stunServer);


            // Tạo observer
            PeerConnectionObserver observer = new PeerConnectionObserver() {

                @Override
                public void onIceCandidate(RTCIceCandidate candidate) {

                        IceCandidate iceCandidate = new IceCandidate(candidate.sdpMid, candidate.sdpMLineIndex, candidate.sdp);

                        CandidatePayload candidatePayload = new CandidatePayload(iceCandidate, toUserId);

                        ObjectMapper objectMapper = new ObjectMapper();
                        String payload;
                        try {
                            payload = objectMapper.writeValueAsString(candidatePayload);
                            logger.info("Candidate Payload: " + payload);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }


                        String url = "http://localhost:10000/video-call/candidate";

                        RestTemplate restTemplate = new RestTemplate();
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set("Authorization", "Bearer " + TokenManager.getAccessToken());
                        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

                        String response = restTemplate.postForObject(url, entity, String.class);

                        logger.info("Candidate payload {} " + payload);
                        logger.info("Response from send candidate " + response);
                        logger.info("Sent ICE candidate: " + candidate.sdp);
                }

                @Override
                public void onTrack(RTCRtpTransceiver transceiver) {
                    MediaStreamTrack track = transceiver.getReceiver().getTrack();
                    if (track instanceof VideoTrack remoteVideoTrack) {
                        remoteVideoTrack.addSink(new VideoTrackSink() {
                            @Override
                            public void onVideoFrame(VideoFrame frame) {
                                BufferedImage image = i420ToBufferedImage(frame.buffer);
                                SwingUtilities.invokeLater(() -> {
                                    remotePanel.updateImage(image);
                                });
                            }
                        });
                        logger.info("Received remote video track");
                    } else if (track instanceof AudioTrack audioTrack) {
                        logger.info("Received audio track: " + audioTrack);
                    }
                }

                // (Optional) override more observer methods like onConnectionStateChange if needed
            };

            peerConnection = factory.createPeerConnection(rtcConfig, observer);
            if (peerConnection == null) {
                logger.warning("Failed to create RTCPeerConnection");
            } else {
                logger.info("RTCPeerConnection created successfully");
            }

        } catch (Exception e) {
            logger.warning("RTC initialization failed: " + e.getMessage());
        }
    }

    public void addMediaStream(int cameraId){
        // Video
        List<VideoDevice> cams = MediaDevices.getVideoCaptureDevices();

        if (!cams.isEmpty()) {
            for (int i = 0; i < cams.size(); i++) {
                logger.warning("i : " + cams.get(i).toString());
            }
            VideoDeviceSource videoSource = new VideoDeviceSource();
            videoSource.setVideoCaptureDevice(cams.get(cameraId));
            //VIDEO START
            videoSource.start();

            VideoTrack vt = factory.createVideoTrack("video0", (VideoTrackSource) videoSource);

            vt.addSink(new VideoTrackSink() {
                @Override
                public void onVideoFrame(VideoFrame frame) {
                    logger.info("Received video frame " + frame);
                    VideoFrameBuffer frameBuffer = frame.buffer;
                    BufferedImage img = i420ToBufferedImage(frameBuffer);
                    SwingUtilities.invokeLater(() ->{
                        localPanel.updateImage(img);
                    });
                }
            });
            peerConnection.addTrack(vt, List.of("stream1"));
            logger.info("Local video track added to PeerConnection");
        }

        //Audio
        List<AudioDevice> mics = MediaDevices.getAudioCaptureDevices();
        if (!mics.isEmpty()) {
            AudioTrackSource audioTrackSource = factory.createAudioSource(new AudioOptions());
            AudioTrack audioTrack = factory.createAudioTrack("audio0",audioTrackSource);
            peerConnection.addTrack(audioTrack, List.of("stream1"));
        }
    }

    private BufferedImage i420ToBufferedImage(VideoFrameBuffer videoFrameBuffer) {
        I420Buffer i420Buffer = videoFrameBuffer.toI420();
        int width = i420Buffer.getWidth();
        int height = i420Buffer.getHeight();

        ByteBuffer yPlane = i420Buffer.getDataY();
        ByteBuffer uPlane = i420Buffer.getDataU();
        ByteBuffer vPlane = i420Buffer.getDataV();

        int yStride = i420Buffer.getStrideY();
        int uStride = i420Buffer.getStrideU();
        int vStride = i420Buffer.getStrideV();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int yIndex = y * yStride + x;
                int uIndex = (y / 2) * uStride + (x / 2);
                int vIndex = (y / 2) * vStride + (x / 2);

                int Y = yPlane.get(yIndex) & 0xFF;
                int U = uPlane.get(uIndex) & 0xFF;
                int V = vPlane.get(vIndex) & 0xFF;

                // Convert YUV to RGB
                int C = Y - 16;
                int D = U - 128;
                int E = V - 128;

                int R = clamp((298 * C + 409 * E + 128) >> 8);
                int G = clamp((298 * C - 100 * D - 208 * E + 128) >> 8);
                int B = clamp((298 * C + 516 * D + 128) >> 8);

                int rgb = (R << 16) | (G << 8) | B;
                image.setRGB(x, y, rgb);
            }
        }

        return image;
    }
    private static int clamp(int val) {
        return Math.max(0, Math.min(255, val));
    }

    public void createOffer(Long toUserId){
        if(peerConnection != null){
            peerConnection.createOffer(new RTCOfferOptions(
            ),new CreateSessionDescriptionObserver(){
                @Override
                public void onSuccess(RTCSessionDescription rtcSessionDescription) {
                    if(peerConnection != null){
                        peerConnection.setLocalDescription(rtcSessionDescription, new SetSessionDescriptionObserver() {
                            @Override
                            public void onSuccess() {
                                logger.info("Local offer SDP set successfully");
                                sendSdpToPeer(rtcSessionDescription, toUserId);
                            }

                            @Override
                            public void onFailure(String s) {
                                logger.info("Failed to set local offer SDP: {}" + s);

                            }
                        });
                    }
                }

                @Override
                public void onFailure(String s) {
                    logger.info("Failed to create answer SDP: {}" + s);
                }
            });
        }

    }

    private void sendSdpToPeer(RTCSessionDescription sdp, Long toUserId) {
            String type = sdp.sdpType == RTCSdpType.OFFER ? "offer" : "answer";

            SdpPayload sdpPayload = new SdpPayload(type, sdp.sdp,null, toUserId);
            ObjectMapper objectMapper = new ObjectMapper();
            String payload;

            try {
                payload = objectMapper.writeValueAsString(sdpPayload);
                logger.info("Sending SDP To Peer : " + payload);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            String url = "http://localhost:10000/video-call/sdp";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " +TokenManager.getAccessToken());
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            String response = restTemplate.postForObject(url, entity, String.class);

            logger.info("Sending SDP To Server : " +payload);

            logger.info("Received SDP To Server : " + response);


            logger.info("Sent SDP ({}): {}" + type + sdp.sdp);

    }

    public void handleAnswer(RTCSessionDescription sdp){
        RTCSessionDescription answer = new RTCSessionDescription(RTCSdpType.ANSWER, sdp.sdp);
        peerConnection.setRemoteDescription(answer, new SetSessionDescriptionObserver() {
            @Override
            public void onSuccess() {
                logger.info("Answer SDP set successfully");
            }

            @Override
            public void onFailure(String error) {
                logger.info("Failed to set remote SDP: {}" + error);

            }
        });
    }

    public void handleIncomingOffer(RTCSessionDescription sdp, Long fromUserId) {
        SwingUtilities.invokeLater(() -> {
            MainVideoFrame videoFrame = new MainVideoFrame();
            videoFrame.setVisible(true);

            if (peerConnection == null) {
                initialize(fromUserId); // Quan trọng
            }
            RTCSessionDescription offer = new RTCSessionDescription(RTCSdpType.OFFER, sdp.sdp);

            peerConnection.setRemoteDescription(offer, new SetSessionDescriptionObserver() {
                @Override
                public void onSuccess() {
                    logger.info("Remote offer SDP set successfully");

                    addMediaStream(0);
                    // 2. Gán panel vào WebRTCManager
                    setVideoPanel(videoFrame.remotePanel,videoFrame.localPanel);
                    peerConnection.createAnswer(new RTCAnswerOptions(), new CreateSessionDescriptionObserver() {

                        @Override
                        public void onSuccess(RTCSessionDescription description) {


                            peerConnection.setLocalDescription(description, new SetSessionDescriptionObserver() {

                                @Override
                                public void onSuccess() {
                                    logger.info("Local answer SDP set successfully");
                                    sendSdpToPeer(description, fromUserId);
                                }

                                @Override
                                public void onFailure(String error) {
                                    logger.info("Failed to set local answer SDP: {}" + error);

                                }
                            });

                        }

                        @Override
                        public void onFailure(String error) {
                            logger.info("Failed to create answer SDP: {}" + error);

                        }
                    });
                }

                @Override
                public void onFailure(String error) {
                    logger.info("Failed to set remote offer SDP: {}" + error);
                }
            });
        });

    }

    public void handleCandidate(RTCIceCandidate candidate){
        if(peerConnection != null){
            peerConnection.addIceCandidate(candidate);
            logger.info("Added remote ICE candidate: " + candidate.sdp);
        }else {
            logger.info("PeerConnection is null, cannot add ICE candidate");
        }
    }

    public void close(){
        if(peerConnection != null){
            peerConnection.close();
            peerConnection = null;
            logger.info("PeerConnection closed");
        }
    }

    public void testLocalVideoOnly() {
        try {
            factory = new PeerConnectionFactory();

            List<VideoDevice> cameras = MediaDevices.getVideoCaptureDevices();
            if (cameras.isEmpty()) {
                logger.warning("Không tìm thấy thiết bị camera");
                return;
            }

            VideoDeviceSource videoSource = new VideoDeviceSource();
            videoSource.setVideoCaptureDevice(cameras.get(0));
            videoSource.start();

            VideoTrack localTrack = factory.createVideoTrack("local_video", videoSource);
            localTrack.addSink(new VideoTrackSink() {
                @Override
                public void onVideoFrame(VideoFrame frame) {
                    logger.info("Video frame received: " + frame);
                    BufferedImage img = i420ToBufferedImage(frame.buffer);
                    SwingUtilities.invokeLater(() -> {
                        localPanel.updateImage(img); // bạn có JPanel hiển thị ở đây
                    });
                }
            });

            logger.info("Đang hiển thị video local từ webcam");

        } catch (Exception e) {
            logger.warning("Lỗi khi test local video: " + e.getMessage());
        }
    }

    public void setLocalPanel(VideoPanel localPanel) {
        this.localPanel = localPanel;
    }
}

