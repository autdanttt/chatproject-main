package com.forcy.chatapp.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("avatar_url")
    private String avatarUrl;
}