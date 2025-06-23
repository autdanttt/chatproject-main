package com.forcy.chatapp.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterDTO {
    @Length(min= 5, max= 20, message = "Username must be in between 5-20 characters")
    @NotNull
    private String username;
    @Length(min = 5, max = 20, message = "Password must be in between 5-20 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private String password;
    @Pattern(
            regexp = "^(0)(3[2-9]|5[6|8|9]|7[0-9]|8[1-9]|9[0-9])\\d{7}$",
            message = "Phone number invalid"
    )
    @Length(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
    @NotNull
    private String phoneNumber;

    public UserRegisterDTO(String username, String password, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}
