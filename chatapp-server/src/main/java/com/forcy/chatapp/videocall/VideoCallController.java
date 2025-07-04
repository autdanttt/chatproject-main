package com.forcy.chatapp.videocall;

import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.payload.CallRequestPayload;
import com.forcy.chatapp.payload.CallResponsePayload;
import com.forcy.chatapp.payload.CandidatePayload;
import com.forcy.chatapp.payload.SdpPayload;
import com.forcy.chatapp.user.UserNotFoundException;
import com.forcy.chatapp.user.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/video-call")
public class VideoCallController {
    private Logger logger = LoggerFactory.getLogger(VideoCallController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/call-request")
    public ResponseEntity<?> sendCallRequest(@RequestBody @Valid CallRequestPayload callRequest){
        logger.info("Call request: " + callRequest.getToUserId());
        String fromEmailUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User fromUser = userRepository.findByEmail(fromEmailUser)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + fromEmailUser));

        User toUser = userRepository.findById(callRequest.getToUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found: " +callRequest.getToUserId()));

        CallRequestPayload responsePayload = new CallRequestPayload();
        responsePayload.setFromUserId(fromUser.getId());
        responsePayload.setToUserId(toUser.getId());
        responsePayload.setCallerName(fromUser.getFullName());
        messagingTemplate.convertAndSendToUser(toUser.getEmail(), "/queue/call-request", responsePayload);
        logger.info("Send payload to user id: " + toUser.getEmail());
        Map<String, String> map = new HashMap<>();
        map.put("message", "Call request sent to " + toUser.getFullName());

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/call-response")
    public ResponseEntity<?> sendCallResponse(@RequestBody @Valid CallResponsePayload callResponse){
        logger.info("Call response: " + callResponse.getToUserId());

        String fromEmailUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User fromUser = userRepository.findByEmail(fromEmailUser)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + fromEmailUser));

        User toUser = userRepository.findById(callResponse.getToUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found: " +callResponse.getToUserId()));

        CallResponsePayload responsePayload = new CallResponsePayload();
        responsePayload.setToUserId(toUser.getId());
        responsePayload.setFromUserId(fromUser.getId());
        responsePayload.setAccepted(callResponse.isAccepted());

        messagingTemplate.convertAndSendToUser(toUser.getEmail(), "/queue/call-response", responsePayload);
        logger.info("Send response to user id: " + toUser.getEmail());

        Map<String, String> map = new HashMap<>();
        map.put("message", "Call response sent to " + toUser.getFullName());

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/sdp")
    public ResponseEntity<?> sendSdp(@RequestBody SdpPayload sdpPayload) {
        String fromEmailUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User fromUser = userRepository.findByEmail(fromEmailUser)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + fromEmailUser));

        User toUser = userRepository.findById(sdpPayload.getToUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + sdpPayload.getToUserId()));

//        Map<String, Object> payload = new HashMap<>();
//        payload.put("fromUserId", fromUser.getId());
//        payload.put("type", sdpPayload.getType());
//        payload.put("sdp", sdpPayload.getSdp());

        SdpPayload responsePayload = new SdpPayload();
        responsePayload.setToUserId(toUser.getId());
        responsePayload.setFromUserId(fromUser.getId());
        responsePayload.setSdp(sdpPayload.getSdp());
        responsePayload.setType(sdpPayload.getType());

        messagingTemplate.convertAndSendToUser(toUser.getEmail(), "/queue/sdp", responsePayload);

        return ResponseEntity.ok("SDP sent to " + toUser.getEmail());

    }


    @PostMapping("/candidate")
    public ResponseEntity<?> handleCandidate(@RequestBody CandidatePayload candidatePayload) {
        String fromEmailUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User fromUser = userRepository.findByEmail(fromEmailUser)
                .orElseThrow(() -> new UserNotFoundException("User not found: " +fromEmailUser));
        User toUser = userRepository.findById(candidatePayload.getToUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + candidatePayload.getToUserId()));


//        Map<String, Object> payload = new HashMap<>();
//
//        payload.put("type", "candidate");
//        payload.put("candidate", candidatePayload.getCandidate());
//        payload.put("fromUserId",fromUser.getId());

        CandidatePayload responsePayload = new CandidatePayload();
        responsePayload.setToUserId(toUser.getId());
        responsePayload.setType("candidate");
        responsePayload.setCandidate(candidatePayload.getCandidate());

        messagingTemplate.convertAndSendToUser(toUser.getEmail(), "/queue/candidate",responsePayload);

        return ResponseEntity.ok("Candidate sent to " + toUser.getEmail());

    }
}
