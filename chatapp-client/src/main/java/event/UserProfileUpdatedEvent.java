package event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class UserProfileUpdatedEvent {
    private String fullName;
    private String avatarUrl;
}
