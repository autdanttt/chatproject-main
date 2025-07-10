package event;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LastMessageEvent {
    private Long lastVisibleId;
}
