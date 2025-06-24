package com.forcy.chatapp.group;

import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.group.dto.ChatGroupDTO;
import com.forcy.chatapp.group.dto.CreateGroupRequest;
import com.forcy.chatapp.group.dto.UpdateGroupRequest;
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@Validated
public class ChatGroupController {

    @Autowired
    private ChatGroupService chatGroupService;

    @Autowired
    private UserRepository userRepository;


    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody @Valid CreateGroupRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        ChatGroupDTO chatGroup = chatGroupService.createGroup(request);

        return new ResponseEntity<>(chatGroup, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroup(@PathVariable("id") Long groupId, @RequestBody @Valid UpdateGroupRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));


        ChatGroupDTO chatGroupDTO = chatGroupService.updateGroupName(groupId, user.getId(), request.getName());

        return new ResponseEntity<>(chatGroupDTO, HttpStatus.OK);
    }




}
