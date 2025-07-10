package event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatSelectedEvent {
    private Long chatId;
    private Long userId;
    private String type;
}
