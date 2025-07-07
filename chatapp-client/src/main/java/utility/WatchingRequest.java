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
public class WatchingRequest {
    @JsonProperty("user_id")
    Long userId;
    @JsonProperty("other_user_id")
    Long otherUserId;
}
