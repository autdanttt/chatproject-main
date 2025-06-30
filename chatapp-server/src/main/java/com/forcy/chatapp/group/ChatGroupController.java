package com.forcy.chatapp.group;

import com.forcy.chatapp.entity.Message;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.group.dto.ChatGroupDTO;
import com.forcy.chatapp.group.dto.CreateGroupRequest;
import com.forcy.chatapp.group.dto.GroupItemDTO;
import com.forcy.chatapp.group.dto.UpdateGroupRequest;
import com.forcy.chatapp.message.MessageResponse;
import com.forcy.chatapp.message.MessageService;
import com.forcy.chatapp.user.UserNotFoundException;
import com.forcy.chatapp.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody @Valid CreateGroupRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        ChatGroupDTO chatGroup = chatGroupService.createGroup(request);

        return new ResponseEntity<>(chatGroup, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroup(@PathVariable("id") Long groupId, @RequestBody @Valid UpdateGroupRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));


        ChatGroupDTO chatGroupDTO = chatGroupService.updateGroupName(groupId, user.getId(), request.getName());

        return new ResponseEntity<>(chatGroupDTO, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable("id") Long groupId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        chatGroupService.deleteGroup(groupId, user.getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyGroups() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        List<GroupItemDTO> list = chatGroupService.getGroupsForSideBar(user.getId());
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }


    @GetMapping("/{groupId}/messages")
    public ResponseEntity<?> getGroupMessages(@PathVariable("groupId") Long groupId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
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
        messageResponse.setFromUserName(message.getUser().getUsername());
        messageResponse.setToUserId(null);
        messageResponse.setDeliveredAt(null);
        return messageResponse;
    }
}
