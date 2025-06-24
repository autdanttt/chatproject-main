package com.forcy.chatapp.auth;

import com.forcy.chatapp.entity.RefreshToken;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.security.jwt.JwtUtility;
import com.forcy.chatapp.token.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TokenService {
    private final Logger log = LoggerFactory.getLogger(TokenService.class);
    @Value("${app.security.jwt.refresh-token.expiration}")
    private int refreshTokenExpiration;
    @Autowired
    RefreshTokenRepository refreshTokenRepo;
    @Autowired
    JwtUtility jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    public AuthResponse generateToken(User user){
        String accessToken = jwtUtil.generateAccessToken(user);
        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);

        String randomUUID = UUID.randomUUID().toString();
        response.setRefreshToken(randomUUID);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(passwordEncoder.encode(randomUUID));

        long refreshTokenExpirationInMillis =System.currentTimeMillis() + refreshTokenExpiration * 60000;
        refreshToken.setExpiryTime(new Date(refreshTokenExpirationInMillis));
        refreshTokenRepo.save(refreshToken);

        return response;
    }
    public AuthResponse refreshTokens(RefreshTokenRequest  request) throws RefreshTokenExpiredException {

        String rawRefreshToken = request.getRefreshToken();
        log.info("Refresh token: {}", rawRefreshToken);

        List<RefreshToken> listRefreshToken = refreshTokenRepo.findByUsername(request.getUsername());
        log.info("Refresh token found: {}", listRefreshToken);

        RefreshToken foundRefreshToken = null;

        for (RefreshToken token : listRefreshToken){
            if(passwordEncoder.matches(rawRefreshToken, token.getToken())){
                log.info("Matched refresh token: " + token.getToken());

                foundRefreshToken = token;
            }
        }
        if (foundRefreshToken == null){
            throw new RefreshTokenNotFoundException("Refresh token not found");
        }

        Date currentTime = new Date();

        if(foundRefreshToken.getExpiryTime().before(currentTime)){
            throw new RefreshTokenExpiredException();
        }

        AuthResponse response = generateToken(foundRefreshToken.getUser());
        refreshTokenRepo.delete(foundRefreshToken);
        return response;
    }
}
