package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatGroupDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("group_name")
    private String groupName;
    @JsonProperty("image")
    private String image;
    @JsonProperty("creator_id")
    private Long creatorId;
    @JsonProperty("member_ids")
    private List<Long> memberIds;
}