package com.forcy.chatapp.videocall;

import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.payload.CandidatePayload;
import com.forcy.chatapp.payload.SdpPayload;
import com.forcy.chatapp.user.UserNotFoundException;
import com.forcy.chatapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/sdp")
    public ResponseEntity<?> sendSdp(@RequestBody SdpPayload sdpPayload) {
        String fromUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User fromUser = userRepository.findByUsername(fromUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + fromUsername));

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

        messagingTemplate.convertAndSendToUser(toUser.getUsername(), "/queue/sdp", responsePayload);

        return ResponseEntity.ok("SDP sent to " + toUser.getUsername());

    }


    @PostMapping("/candidate")
    public ResponseEntity<?> handleCandidate(@RequestBody CandidatePayload candidatePayload) {
        String fromUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User fromUser = userRepository.findByUsername(fromUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + fromUsername));
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


        messagingTemplate.convertAndSendToUser(toUser.getUsername(), "/queue/candidate",responsePayload);

        return ResponseEntity.ok("Candidate sent to " + toUser.getUsername());

    }
}
