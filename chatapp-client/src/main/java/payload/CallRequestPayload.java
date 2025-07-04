package payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CallRequestPayload {
    @JsonProperty("to_user_id")
    private Long toUserId;
    @JsonProperty("from_user_id")
    private Long fromUserId;
    @JsonProperty("caller_name")
    private String callerName;
}
