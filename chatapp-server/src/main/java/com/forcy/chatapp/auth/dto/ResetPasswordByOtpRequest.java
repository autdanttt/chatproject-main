package com.forcy.chatapp.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordByOtpRequest {
    @NotNull
    private String email;
    @NotNull
    @Length(min = 6, max = 6)
    private String token;
    @NotNull
    private String newPassword;
}
