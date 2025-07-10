package event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileUpdatedEvent {
    private String fullName;
    private String avatarUrl;
}
