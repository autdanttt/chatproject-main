package com.forcy.chatapp.user;

import com.forcy.chatapp.auth.AuthUserDTO;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.media.AssetService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private AssetService assetService;

//    @PostMapping("/register")
//    public ResponseEntity<User> register(@RequestBody @Valid AuthUserDTO authUserDTO) {
//        User user = userService.registerUser(authUserDTO);
//        return new ResponseEntity<>(user, HttpStatus.CREATED);
//    }


    @PostMapping("/avatar")
    public ResponseEntity<?> updateAvatar(@RequestParam("image") MultipartFile file) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        String image = userService.updateAvatar(user.getId(),file);

        Map<String, String> map = new HashMap<>();
        map.put("avatar_url", image);
        return ResponseEntity.ok().body(map);
    }
}
