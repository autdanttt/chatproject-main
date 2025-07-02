package com.forcy.chatapp.security.jwt;

import com.forcy.chatapp.entity.Role;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    @Autowired
    private JwtUtility jwtUtility;

    public Authentication getAuthentication(String token) throws JwtValidationException {
        Claims claims  = jwtUtility.validateAccessToken(token);

        if(claims == null) return null;


        UserDetails userDetails = getUserDetails(claims);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    }
    private UserDetails getUserDetails(Claims claims) {
        String subject = (String) claims.get(Claims.SUBJECT);
        String[] array = subject.split(",");


        Long userId = Long.valueOf(array[0]);
        String email = array[1];

        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        String roles = (String) claims.get("roles");
        roles = roles.replace("[", "").replace("]", "");
        String[] rolesName = roles.split(",");
        for (String aRoleName : rolesName){
            user.addRole(new Role(aRoleName));
        }

        return new CustomUserDetails(user);
    }
}
