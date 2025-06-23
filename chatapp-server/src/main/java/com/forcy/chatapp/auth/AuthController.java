package com.forcy.chatapp.auth;

import com.forcy.chatapp.entity.Role;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.security.CustomUserDetails;
import com.forcy.chatapp.user.UserService;
import com.forcy.chatapp.user.dto.UserRegisterDTO;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/oauth")
@Validated
public class AuthController{


    AuthenticationManager authenticationManager;
    TokenService tokenService;
    UserService userService;
    ModelMapper mapper;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, UserService userService, ModelMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @PostMapping("/token")
    public ResponseEntity<?> getAccessToken(@RequestBody @Valid AuthRequest request){
        String username = request.getUsername();
        String password = request.getPassword();
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            AuthResponse response = tokenService.generateToken(userDetails.getUser());

            return ResponseEntity.ok(response);
        }catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

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
        String username = request.getUsername();
        String password = request.getPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            User user = userService.getByUsername(username);

            AuthUserDTO loginDTO = new AuthUserDTO();
            loginDTO.setId(user.getId());
            loginDTO.setUsername(user.getUsername());
            loginDTO.setPhoneNumber(user.getPhoneNumber());
            List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
            loginDTO.setRoles(roles);
            AuthResponse response = tokenService.generateToken(userDetails.getUser());
//            HttpHeaders jwtHeader = new HttpHeaders();
//            jwtHeader.add("Jwt-Token", response.getAccessToken());
            response.setUser(loginDTO);

            return new ResponseEntity<>(response, OK);
        }catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDTO userDTO) {
        userService.registerUser(userDTO);

        return new ResponseEntity<>(Map.of("message", "Dang ki thanh cong"), HttpStatus.CREATED);
    }
//    private AuthUserDTO entity2DTO(User user){
//        return mapper.map(user, AuthUserDTO.class);
//    }
}
