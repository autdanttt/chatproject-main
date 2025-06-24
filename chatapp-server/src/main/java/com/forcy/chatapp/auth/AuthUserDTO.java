package com.forcy.chatapp.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
public class AuthUserDTO {
    private Long id;
    @Length(min = 5, max = 128, message = "Username must be between 5 - 128")
    @NotNull(message = "Username cannot be null")
    private String username;
//    @NotNull(message = "Password cannot be null")
//    @Length(min = 8, max = 64, message = "Password must be between 8 - 64")
//    private String password;
    private String phoneNumber;
    @Valid
    private Set<RoleDTO> roles = new HashSet<>();

    public void addRole(RoleDTO role) {
        this.getRoles().add(role);
    }

}
