package event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FullNameUpdateEvent {
    private final String fullName;
    private final String imageUrl;
}
