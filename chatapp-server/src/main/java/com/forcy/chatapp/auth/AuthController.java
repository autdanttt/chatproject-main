package com.forcy.chatapp.auth;

import com.forcy.chatapp.entity.Role;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.security.CustomUserDetails;
import com.forcy.chatapp.security.jwt.JwtValidationException;
import com.forcy.chatapp.user.UserService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/oauth")
@Validated
public class AuthController{


    AuthenticationManager authenticationManager;
    TokenService tokenService;
    UserService userService;
    ModelMapper mapper;
    EmailService emailService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, UserService userService, ModelMapper mapper, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userService = userService;
        this.emailService = emailService;
        this.mapper = mapper;
    }

//    @PostMapping("/token")
//    public ResponseEntity<?> getAccessToken(@RequestBody @Valid AuthRequest request){
//        String username = request.getUsername();
//        String password = request.getPassword();
//        try {
//            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//
//            AuthResponse response = tokenService.generateToken(userDetails.getUser());
//
//            return ResponseEntity.ok(response);
//        }catch (BadCredentialsException ex) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenRequest request){

        try{
            AuthResponse response = tokenService.refreshTokens(request);

            RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse();
            refreshTokenResponse.setAccessToken(response.getAccessToken());
            refreshTokenResponse.setRefreshToken(response.getRefreshToken());

            return ResponseEntity.ok(refreshTokenResponse);
        } catch (RefreshTokenExpiredException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request){
        String email = request.getEmail();
        String password = request.getPassword();


        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            User user = userService.getByEmail(email);

            AuthUserDTO loginDTO = new AuthUserDTO();
            loginDTO.setId(user.getId());
            loginDTO.setEmail(user.getEmail());
            loginDTO.setFullName(user.getFullName());
            loginDTO.setAvatarUrl(user.getAvatarUrl());
            Set<RoleDTO> roleDTOS = new HashSet<>();
            for (Role role : user.getRoles()) {
                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setId(role.getId());
                roleDTO.setName(role.getName());
                roleDTO.setDescription(role.getDescription());
                roleDTOS.add(roleDTO);
            }

            loginDTO.setRoles(roleDTOS);

            AuthResponse response = tokenService.generateToken(userDetails.getUser());
            response.setUser(loginDTO);

            return new ResponseEntity<>(response, OK);
        }catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@RequestPart("user") @Valid UserRegisterDTO dto,
                                      @RequestPart("image") @Nullable MultipartFile multipartFile){

        userService.registerUser(dto, multipartFile);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Đăng ký thành công. Vui lòng kiểm tra email để xác thực.",
                        "email", dto.getEmail()
                ));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            emailService.verifyEmail(token);
            return ResponseEntity.ok("Xác thực tài khoản thành công!");
        } catch (JwtValidationException | IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
