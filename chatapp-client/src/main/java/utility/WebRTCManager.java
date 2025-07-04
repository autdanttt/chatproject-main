package utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import dev.onvoid.webrtc.*;
import dev.onvoid.webrtc.media.MediaDevices;
import dev.onvoid.webrtc.media.MediaStreamTrack;
import dev.onvoid.webrtc.media.audio.*;
import dev.onvoid.webrtc.media.video.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import payload.CandidatePayload;
import payload.IceCandidate;
import payload.SdpPayload;
import view.VideoPanel;
import view.login.TokenManager;
import view.main.UserToken;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WebRTCManager {
    private final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WebRTCManager.class);
    private static final Logger logger = Logger.getLogger(WebRTCManager.class.getName());
    private PeerConnectionFactory factory;
    private RTCPeerConnection peerConnection;
//    private String jwtToken;

    private VideoPanel localPanel;
    private VideoPanel remotePanel;

    private VideoDeviceSource videoSource;
    private AudioDeviceModule audioDeviceModule;
    private VideoTrack videoTrack;
    private AudioTrack audioTrack;

    // Media (remote)
    private SourceDataLine speaker; // dùng để phát audio từ peer
    private VideoTrack remoteVideoTrack;

    private VideoTrackSink remoteVideoSink;
    private AudioTrackSink remoteAudioSink;

    private VideoTrackSink localVideoSink;


    private RTCRtpSender videoSender;
    private RTCRtpSender audioSender;

    private List<RTCRtpTransceiver> transceivers = new ArrayList<>();
//    private volatile boolean isCallActive = true;


    @Inject
    public WebRTCManager( EventBus eventBus) {
        eventBus.register(this);
    }

    @Subscribe
    public void onJwtToken(UserToken userToken) {
        LOGGER.info("Received JWT token: " + userToken.getJwtToken());
 //       this.jwtToken = userToken.getJwtToken();

    }


    public synchronized void endCall() {
        LOGGER.info("Starting call termination process at {}", System.currentTimeMillis());

        // Ngăn callback trong remoteVideoSink
        LOGGER.info("Setting isCallActive to false");
//        isCallActive = false;

        // Xóa sink video
        LOGGER.info("Removing remote video sink");
        if (remoteVideoTrack != null && remoteVideoSink != null) {
            try {
                remoteVideoTrack.removeSink(remoteVideoSink);
                LOGGER.info("remoteVideoSink removed");
            } catch (Exception e) {
                LOGGER.error("Error removing remoteVideoSink: {}", e.getMessage(), e);
            }
            remoteVideoSink = null;
        }

        LOGGER.info("Removing local video sink");
        if (videoTrack != null && localVideoSink != null) {
            try {
                videoTrack.removeSink(localVideoSink);
                LOGGER.info("localVideoSink removed");
            } catch (Exception e) {
                LOGGER.error("Error removing localVideoSink: {}", e.getMessage(), e);
            }
            localVideoSink = null;
        }

        // Xóa sink audio
        LOGGER.info("Removing audio sink");
        if (audioTrack != null && remoteAudioSink != null) {
            try {
                audioTrack.removeSink(remoteAudioSink);
                LOGGER.info("remoteAudioSink removed");
            } catch (Exception e) {
                LOGGER.error("Error removing remoteAudioSink: {}", e.getMessage(), e);
            }
            remoteAudioSink = null;
        }

        // Xóa sender khỏi PeerConnection
        LOGGER.info("Removing video sender");
        if (videoSender != null && peerConnection != null) {
            try {
                peerConnection.removeTrack(videoSender);
                LOGGER.info("videoSender removed");
            } catch (Exception e) {
                LOGGER.error("Error removing videoSender: {}", e.getMessage(), e);
            }
            videoSender = null;
        }

        LOGGER.info("Removing audio sender");
        if (audioSender != null && peerConnection != null) {
            try {
                peerConnection.removeTrack(audioSender);
                LOGGER.info("audioSender removed");
            } catch (Exception e) {
                LOGGER.error("Error removing audioSender: {}", e.getMessage(), e);
            }
            audioSender = null;
        }

        // Đóng PeerConnection
        LOGGER.info("Closing peerConnection");
        if (peerConnection != null) {
            try {
                peerConnection.close();
                LOGGER.info("peerConnection closed");
            } catch (Exception e) {
                LOGGER.error("Error closing peerConnection: {}", e.getMessage(), e);
            }
            peerConnection = null;
        }

        // Dừng loa
        LOGGER.info("Stopping speaker");
        if (speaker != null) {
            try {
                speaker.drain();
                speaker.stop();
                speaker.close();
                LOGGER.info("speaker stopped and closed");
            } catch (Exception e) {
                LOGGER.error("Error stopping speaker: {}", e.getMessage(), e);
            }
            speaker = null;
        }

        // Dừng nguồn video
        LOGGER.info("Stopping video source");
        if (videoSource != null) {
            try {
                videoSource.stop();
                LOGGER.info("videoSource stopped");
            } catch (Exception e) {
                LOGGER.error("Error stopping videoSource: {}", e.getMessage(), e);
            }
            videoSource = null;
        }

        // Dừng audio device
        LOGGER.info("Stopping audio device module");
        if (audioDeviceModule != null) {
            try {
                audioDeviceModule.stopRecording();
                audioDeviceModule.dispose();
                LOGGER.info("audioDeviceModule stopped and disposed");
            } catch (Exception e) {
                LOGGER.error("Error stopping audioDeviceModule: {}", e.getMessage(), e);
            }
            audioDeviceModule = null;
        }

        // Giải phóng factory
        LOGGER.info("Disposing factory");
        if (factory != null) {
            try {
                factory.dispose();
                LOGGER.info("factory disposed");
            } catch (Exception e) {
                LOGGER.error("Error disposing factory: {}", e.getMessage(), e);
            }
            factory = null;
        }

        // Làm sạch VideoPanel
        LOGGER.info("Clearing VideoPanels");
        if (localPanel != null) {
            try {
                LOGGER.info("localPanel cleared");
            } catch (Exception e) {
                LOGGER.error("Error clearing localPanel: {}", e.getMessage(), e);
            }
            localPanel = null;
        }
        if (remotePanel != null) {
            try {
                LOGGER.info("remotePanel cleared");
            } catch (Exception e) {
                LOGGER.error("Error clearing remotePanel: {}", e.getMessage(), e);
            }
            remotePanel = null;
        }

//        // Thông báo server
//        LOGGER.info("Notifying server of call termination");
//        try {
//            String url = "http://localhost:10000/video-call/end";
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("Authorization", "Bearer " + TokenManager.getAccessToken());
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//            restTemplate.postForObject(url, entity, String.class);
//            LOGGER.info("Server notified");
//        } catch (Exception e) {
//            LOGGER.error("Error notifying server: {}", e.getMessage(), e);
//        }

        LOGGER.info("Call termination completed at {}", System.currentTimeMillis());
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
//                            logger.info("Candidate Payload: " + payload);
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

//                        logger.info("Candidate payload {} " + payload);
//                        logger.info("Response from send candidate " + response);
//                        logger.info("Sent ICE candidate: " + candidate.sdp);
                }

                @Override
                public void onTrack(RTCRtpTransceiver transceiver) {
                    transceivers.add(transceiver);
                    MediaStreamTrack track = transceiver.getReceiver().getTrack();
                    if (track instanceof VideoTrack vt) {
                        remoteVideoTrack = vt;
                        remoteVideoTrack.addSink(remoteVideoSink = new VideoTrackSink() {
                            @Override
                            public void onVideoFrame(VideoFrame frame) {
//                                if (!isCallActive) {
//                                    LOGGER.info("Call is not active, skipping frame processing");
//                                    return;
//                                }
                                BufferedImage image = i420ToBufferedImage(frame.buffer);
                                SwingUtilities.invokeLater(() -> {
//                                    if (isCallActive) {
                                        remotePanel.updateImage(image);
//                                    }
                                });
                            }
                        });
                        logger.info("Received remote video track");
                    }
                    if(track instanceof AudioTrack at) {
                        audioTrack = at;
                        audioTrack.addSink(remoteAudioSink = new AudioTrackSink() {
                            @Override
                            public void onData(byte[] data, int bitsPerSample, int sampleRate, int channels, int frames) {
//                                if (!isCallActive) return;
                                try {
                                    if (speaker == null) {
                                        AudioFormat format = new AudioFormat(sampleRate, bitsPerSample, channels, true, false);
                                        speaker = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, format));
                                        speaker.open(format);
                                        speaker.start();
                                    }

                                    speaker.write(data, 0, data.length);
                                } catch (Exception e) {
                                    logger.info("Lỗi phát âm thanh remote" + e.getMessage());
                                }
                            }
                        });
                        logger.info("Received audio audio track");
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

    public void addMediaStream(VideoDevice camera, AudioDevice mic) {
        // Video
        if (camera != null) {
            videoSource = new VideoDeviceSource();
            videoSource.setVideoCaptureDevice(camera);
            videoSource.start();

            videoTrack = factory.createVideoTrack("video0", (VideoTrackSource) videoSource);
            videoTrack.addSink(localVideoSink = new VideoTrackSink() {
                @Override
                public void onVideoFrame(VideoFrame frame) {
                    VideoFrameBuffer frameBuffer = frame.buffer;
                    BufferedImage img = i420ToBufferedImage(frameBuffer);
                    SwingUtilities.invokeLater(() -> {
                        localPanel.updateImage(img);
                    });
                }
            });
            videoSender = peerConnection.addTrack(videoTrack, List.of("stream1"));

            logger.info("Local video track added to PeerConnection: {}"+ camera.getName());
        } else {
            logger.info("No camera selected");
        }

        // Audio
        if (mic != null) {
            audioDeviceModule = new AudioDeviceModule();
            audioDeviceModule.setRecordingDevice(mic);
            audioDeviceModule.initRecording();
            audioDeviceModule.startRecording();


            AudioOptions audioOptions = new AudioOptions();
            AudioTrackSource audioTrackSource = factory.createAudioSource(audioOptions);
            audioTrack = factory.createAudioTrack("audio0", (AudioTrackSource) audioTrackSource);

            audioSender = peerConnection.addTrack(audioTrack, List.of("stream1"));
            logger.info("Local audio track added to PeerConnection: {}"+ mic.getName());
        } else {
            logger.info("No microphone selected");
        }
    }

//    public void addMediaStream(int cameraId){
//        // Video
//        List<VideoDevice> cams = MediaDevices.getVideoCaptureDevices();
//
//        if (!cams.isEmpty()) {
//            for (int i = 0; i < cams.size(); i++) {
//                logger.warning(i + ": " + cams.get(i).toString());
//            }
//            VideoDeviceSource videoSource = new VideoDeviceSource();
//            videoSource.setVideoCaptureDevice(cams.get(cameraId));
//            //VIDEO START
//            videoSource.start();
//
//            VideoTrack vt = factory.createVideoTrack("video0", (VideoTrackSource) videoSource);
//
//            vt.addSink(new VideoTrackSink() {
//                @Override
//                public void onVideoFrame(VideoFrame frame) {
////                    logger.info("Received video frame " + frame);
//                    VideoFrameBuffer frameBuffer = frame.buffer;
//                    BufferedImage img = i420ToBufferedImage(frameBuffer);
//                    SwingUtilities.invokeLater(() ->{
//                        localPanel.updateImage(img);
//                    });
//                }
//            });
//            peerConnection.addTrack(vt, List.of("stream1"));
//            logger.info("Local video track added to PeerConnection");
//        }
//
//        //Audio
//        List<AudioDevice> mics = MediaDevices.getAudioCaptureDevices();
//        if (!mics.isEmpty()) {
//            AudioTrackSource audioTrackSource = factory.createAudioSource(new AudioOptions());
//            AudioTrack audioTrack = factory.createAudioTrack("audio0",audioTrackSource);
//            peerConnection.addTrack(audioTrack, List.of("stream1"));
//        }
//    }

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
//                logger.info("Sending SDP To Peer : " + payload);
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

//            logger.info("Sending SDP To Server : " +payload);
//
//            logger.info("Received SDP To Server : " + response);


//            logger.info("Sent SDP ({}): {}" + type + sdp.sdp);

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
//            MainVideoFrame videoFrame = new MainVideoFrame();
//            videoFrame.setVisible(true);

            if (peerConnection == null) {
                initialize(fromUserId); // Quan trọng
            }
            RTCSessionDescription offer = new RTCSessionDescription(RTCSdpType.OFFER, sdp.sdp);

            peerConnection.setRemoteDescription(offer, new SetSessionDescriptionObserver() {
                @Override
                public void onSuccess() {
                    logger.info("Remote offer SDP set successfully");

//                    addMediaStream(0);
                    // 2. Gán panel vào WebRTCManager
//                    setVideoPanel(videoFrame.remotePanel,videoFrame.localPanel);
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
//            logger.info("Added remote ICE candidate: " + candidate.sdp);
        }else {
            logger.info("PeerConnection is null, cannot add ICE candidate");
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

