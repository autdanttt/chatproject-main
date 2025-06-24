package com.forcy.chatapp.group;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UpdateGroupRequest {
    @NotNull
    @Length(min = 2, max = 30, message = "Name must be between 2 - 30")
    private String name;
    @NotNull
    private Long userId;

}
