package com.forcy.chatapp.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
@Getter
@Setter
public class AuthRequest {
    @NotNull
    @Email
    private String email;
    @NotNull @Length(min = 5, max = 50)
    private String password;
}
