package com.forcy.chatapp.auth;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@EnableMethodSecurity
public class RoleTestController {
    @GetMapping("/public")
    public String publicEndpoint(){
        return "public";
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String userEndpoint(){
        return "user or admin";
    }


    // Chỉ ADMIN mới truy cập
    @GetMapping("/admin")
//    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "✅ Chỉ ADMIN mới vào được";
    }

    @GetMapping("/me")
    public List<String> getCurrentUserAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
