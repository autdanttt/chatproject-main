package com.forcy.chatapp;

import com.forcy.chatapp.entity.Role;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.security.jwt.JwtUtility;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class JwtUtilityTests {
    private static JwtUtility jwtUtil;
    private EntityManager entityManager;


    @BeforeAll
    static void setup(){
        jwtUtil = new JwtUtility();
        jwtUtil.setIssuerName("My Company");
        jwtUtil.setSecretKey("ABCDEFJHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxwyz!@#$%^&*()");
        jwtUtil.setAccessTokenExpiration(2);
    }

//    @Test
//    public void testValidateSuccess(){
//        User user = new User();
//        user.setId(1L);
//        user.setUsername("username");
//        user.setPassword("password");
//        user.setPhoneNumber("1234567890");
//        Role role = new Role("Role test");
//        role.setDescription("role description");
//        user.getRoles().add(role);
//        String token = jwtUtil.generateAccessToken(user);
//        assertThat(token).isNotNull();
//        assertDoesNotThrow(()->{
//            jwtUtil.validateAccessToken(token);
//        });
//
//    }
}
