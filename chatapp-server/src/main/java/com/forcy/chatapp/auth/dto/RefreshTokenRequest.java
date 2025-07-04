package com.forcy.chatapp.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class RefreshTokenRequest {
    @NotNull
    @Email
    private String email;
    @NotNull @Length(min = 36, max = 50)
    private String refreshToken;

}
