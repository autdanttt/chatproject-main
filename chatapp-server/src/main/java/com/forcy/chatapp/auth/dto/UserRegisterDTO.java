package com.forcy.chatapp.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
public class UserRegisterDTO {
    @NotNull(message = "Email cannot be null")
    @Email
    private String email;
    @NotNull
    @Length(min = 5, max = 30, message =  "Full name must not be null")
    private String fullName;
    @NotNull(message = "Password cannot be null")
    @Length(min = 8, max = 64, message = "Password must be between 8 - 64")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}

