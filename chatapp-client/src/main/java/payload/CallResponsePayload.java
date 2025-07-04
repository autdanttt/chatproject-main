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
public class CallResponsePayload {
    @JsonProperty("to_user_id")
    private Long toUserId;
    @JsonProperty("from_user_id")
    private Long fromUserId;
    @JsonProperty("accepted")
    private boolean accepted;
}
