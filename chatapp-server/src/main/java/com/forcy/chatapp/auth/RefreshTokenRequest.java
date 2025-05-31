package com.forcy.chatapp.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class RefreshTokenRequest {
    @NotNull @Length(min = 5, max = 20)
    private String username;
    @NotNull @Length(min = 36, max = 50)
    private String refreshToken;

}
