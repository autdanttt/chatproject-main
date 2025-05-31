package com.forcy.chatapp.security.jwt;

import com.forcy.chatapp.entity.Role;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    JwtUtility jwtUtil;

    @Autowired @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver exceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(!hasAuthorizationBearer(request)){
            filterChain.doFilter(request, response);
            return;
        }

        String token = getBearerToken(request);
        LOGGER.info("Token: "+ token);
        try {
            Claims claims = jwtUtil.validateAccessToken(token);

            LOGGER.info("ROLE TOKEN" + claims.get("roles"));
            UserDetails userDetails = getUserDetails(claims);

            setAuthenticationContext(userDetails, request);

            filterChain.doFilter(request, response);

            clearAuthenticationContext();
        } catch (JwtValidationException e) {
            LOGGER.error(e.getMessage(), e);
            exceptionResolver.resolveException(request, response, null, e);
        }
    }
    private void clearAuthenticationContext() {
        SecurityContextHolder.clearContext();
    }
    private void setAuthenticationContext(UserDetails userDetails, HttpServletRequest request) {
        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        LOGGER.info("User Authorites: " + authentication.getAuthorities());

        LOGGER.info("Role" + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }
    private UserDetails getUserDetails(Claims claims) {
        String subject = (String) claims.get(Claims.SUBJECT);
        String[] array = subject.split(",");

        Long userId = Long.valueOf(array[0]);
        String username = array[1];

        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        String roles = (String) claims.get("roles");
        roles = roles.replace("[", "").replace("]", "");
        String[] rolesName = roles.split(",");
        for (String aRoleName : rolesName){
            user.addRole(new Role(aRoleName));
        }

        LOGGER.info("User parsed from JWT: "+ user.getId() + ", " + user.getUsername() + ", ");
        LOGGER.info("ROLE: "+ user.getRoles());
        return new CustomUserDetails(user);
    }

    private String getBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String[] array = header.split(" ");
        if(array.length == 2){
            return array[1];
        }
        return null;
    }
    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        LOGGER.info("Authorization Header: "+ header);
        if(ObjectUtils.isEmpty(header) || !header.startsWith("Bearer")){
            return false;
        }
        return true;
    }
}
