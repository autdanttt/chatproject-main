package com.forcy.chatapp.user;

import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.media.AssetService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@Validated
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

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateInfo(@RequestPart(value = "user") @Valid UpdateUserRequest updateUserRequest, @RequestPart(value = "image", required = false) @Nullable MultipartFile image) {


        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));


        UserDTO userDTO = userService.updateUserInfo(updateUserRequest, image, user.getId());

        return ResponseEntity.ok().body(userDTO);
    }

//    @PostMapping(value = "/update-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> updateUserAvatar(@RequestPart("image") MultipartFile image) {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
//
//        // Gọi service xử lý ảnh
//        UpdateUserRequest dummyRequest = new UpdateUserRequest();
//        dummyRequest.setFullName(user.getFullName()); // giữ tên cũ
//
//        UserDTO userDTO = userService.updateUserInfo(dummyRequest, image, user.getId());
//        return ResponseEntity.ok(userDTO);
//    }
//
//
//    @PutMapping(value = "/update-json", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> updateUserJson(@RequestBody @Valid UpdateUserRequest updateUserRequest) {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
//
//        // ✅ Nếu không nhập tên mới thì giữ nguyên tên cũ
//        if (updateUserRequest.getFullName() == null || updateUserRequest.getFullName().trim().isEmpty()) {
//            updateUserRequest.setFullName(user.getFullName());
//        }
//
//        UserDTO userDTO = userService.updateUserInfo(updateUserRequest, null, user.getId());
//        return ResponseEntity.ok(userDTO);
//    }




    @GetMapping("/listother")
    public ResponseEntity<?> getListOthers() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        return ResponseEntity.ok(userService.getAllUserExcept(user.getId()));
    }
}
