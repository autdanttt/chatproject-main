package utility;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HangupMessage {
    private String type;
    @JsonProperty("from_user_id")
    private Long fromUserId;
    @JsonProperty("to_user_id")
    private Long toUserId;
}
