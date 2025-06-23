package com.forcy.chatapp.user;

import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.user.dto.UserRegisterDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDTO userDTO) {
//        userService.registerUser(userDTO);
//
//        return new ResponseEntity<>(Map.of("message", "Dang ki thanh cong"), HttpStatus.CREATED);
//    }

}
