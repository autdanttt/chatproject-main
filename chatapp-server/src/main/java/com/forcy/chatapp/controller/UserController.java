package com.forcy.chatapp.controller;

import com.forcy.chatapp.dto.UserDTO;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserDTO userDTO) {

        User user = userService.registerUser(userDTO.getName());

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

}
