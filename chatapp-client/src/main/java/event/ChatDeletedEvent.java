package event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatDeletedEvent {
    private Long chatId;
}
