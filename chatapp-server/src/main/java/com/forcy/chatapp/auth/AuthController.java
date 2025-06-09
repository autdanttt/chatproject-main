package com.forcy.chatapp.auth;

import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.security.CustomUserDetails;
import com.forcy.chatapp.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/oauth")
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
            return ResponseEntity.ok(response);
        } catch (RefreshTokenExpiredException | RefreshTokenNotFoundException e) {
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

            AuthUserDTO loginDTO = entity2DTO(user);
            AuthResponse response = tokenService.generateToken(userDetails.getUser());
            HttpHeaders jwtHeader = new HttpHeaders();
            jwtHeader.add("Jwt-Token", response.getAccessToken());


            return new ResponseEntity<>(loginDTO, jwtHeader, OK);
        }catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
    private AuthUserDTO entity2DTO(User user){
        return mapper.map(user, AuthUserDTO.class);
    }
}
