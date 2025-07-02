package com.forcy.chatapp.user;

import com.forcy.chatapp.auth.AuthUserDTO;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.media.AssetService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @PostMapping("/avatar")
    public ResponseEntity<?> updateAvatar(@RequestPart("image") MultipartFile file) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));

        String image = userService.updateAvatar(user.getId(),file);

        Map<String, String> map = new HashMap<>();
        map.put("avatar_url", image);
        return ResponseEntity.ok().body(map);
    }

    @GetMapping("/listother")
    public ResponseEntity<?> getListOhters() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        return ResponseEntity.ok(userService.getAllUserExcept(user.getId()));
    }
}
