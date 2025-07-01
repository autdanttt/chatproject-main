package com.forcy.chatapp.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
    @NotNull(message = "Email cannot be null")
    @Email
    private String email;
    @Length(min = 5, max = 30, message = "Full name must be between 5-30 characters")
    @NotNull
    private String fullName;
    @NotNull(message = "Password cannot be null")
    @Length(min = 8, max = 64, message = "Password must be between 8 - 64")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String avatarUrl;
    @Valid
    private Set<RoleDTO> roles = new HashSet<>();

    public void addRole(RoleDTO role) {
        this.getRoles().add(role);
    }
}
