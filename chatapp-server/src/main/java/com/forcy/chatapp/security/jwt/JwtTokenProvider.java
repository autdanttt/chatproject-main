package com.forcy.chatapp.security.jwt;

import com.forcy.chatapp.entity.Role;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtTokenProvider {
    private final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Autowired
    private JwtUtility jwtUtility;

    public Authentication getAuthentication(String token) throws JwtValidationException {
        Claims claims  = jwtUtility.validateAccessToken(token);


        if(claims == null) return null;


        log.info("claims: {}", claims);
        UserDetails userDetails = getUserDetails(claims);
        log.info("userDetails: {}", userDetails);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    }
    private UserDetails getUserDetails(Claims claims) {
        log.info("claims in getUserDetails: {}", claims);
        String subject = (String) claims.get(Claims.SUBJECT);
        String[] array = subject.split(",");


        Long userId = Long.valueOf(array[0]);
        String username = array[1];

        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        log.info("user: {}", user);

        List<String> roles = claims.get("roles", List.class); // ✅ dùng đúng List
        log.info("roles from claims: {}", roles);

        for (String roleName : roles) {
            user.addRole(new Role(roleName.trim())); // trim để tránh lỗi nếu có khoảng trắng
            log.info("addRole: {}", roleName);
        }

        log.info("user: {}", user);

        return new CustomUserDetails(user);
    }
}
