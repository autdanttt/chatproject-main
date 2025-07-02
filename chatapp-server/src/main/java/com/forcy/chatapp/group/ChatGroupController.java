package com.forcy.chatapp.group;

import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.group.dto.ChatGroupDTO;
import com.forcy.chatapp.group.dto.CreateGroupRequest;
import com.forcy.chatapp.group.dto.ChatGroupResponse;
import com.forcy.chatapp.group.dto.UpdateGroupRequest;
import com.forcy.chatapp.message.MessageResponse;
import com.forcy.chatapp.message.MessageService;
import com.forcy.chatapp.user.UserNotFoundException;
import com.forcy.chatapp.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@Validated
public class ChatGroupController {

    @Autowired
    private ChatGroupService chatGroupService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createGroup(@RequestPart("group") @Valid CreateGroupRequest request,
                                         @RequestPart("image") MultipartFile multipartFile) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found " + email));

        ChatGroupDTO chatGroup = chatGroupService.createGroup(request, multipartFile);

        return new ResponseEntity<>(chatGroup, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroup(@PathVariable("id") Long groupId, @RequestBody @Valid UpdateGroupRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found " + email));


        ChatGroupDTO chatGroupDTO = chatGroupService.updateGroupName(groupId, user.getId(), request.getName());

        return new ResponseEntity<>(chatGroupDTO, HttpStatus.OK);
    }

    @PostMapping("/image")
    public ResponseEntity<?> updateGroupImage(@RequestPart("group_id") Long groupId,@RequestPart("image") MultipartFile file) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found " + email));

        String image = chatGroupService.updateImage(groupId, user.getId(), file);


        Map<String, String> map = new HashMap<>();
        map.put("image", image);
        return ResponseEntity.ok().body(map);

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable("id") Long groupId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found " + email));

        chatGroupService.deleteGroup(groupId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyGroups() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found " + email));
        List<ChatGroupResponse> list = chatGroupService.getGroupsForSideBar(user.getId());
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{groupId}/messages")
    public ResponseEntity<?> getGroupMessages(@PathVariable("groupId") Long groupId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        chatGroupService.getGroupMember(groupId, user.getId());

        List<Message> messages = messageService.findGroupMessagesByGroupId(groupId);


        List<MessageResponse> responses = new ArrayList<>();
        for (Message message : messages) {
            MessageResponse messageResponse = getMessageResponse(groupId, message, user);

            responses.add(messageResponse);

        }

        if (messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(responses);
    }

    private static MessageResponse getMessageResponse(Long groupId, Message message, User user) {
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessageId(message.getId());
        messageResponse.setFromUserId(message.getUser().getId());
        messageResponse.setMessageType(message.getType());
        messageResponse.setContent(message.getContent());
        messageResponse.setGroupId(groupId);
        messageResponse.setSentAt(message.getSentAt());
        messageResponse.setFromFullName(message.getUser().getFullName());
        messageResponse.setToUserId(null);
        messageResponse.setDeliveredAt(null);
        return messageResponse;
    }
}
