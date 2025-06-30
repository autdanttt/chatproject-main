package com.forcy.chatapp.group.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Setter
public class CreateGroupRequest {
    @NotNull
    @Length(min = 2, max = 30, message = "Name must be between 2 - 30")
    private String name;
    @NotNull
    private Long creatorId;
    @NotNull
    private List<Long> memberIds; // danh sách user ID (có thể bao gồm hoặc không gồm creator)
}
