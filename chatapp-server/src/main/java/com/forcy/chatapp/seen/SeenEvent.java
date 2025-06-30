package com.forcy.chatapp.seen;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeenEvent {
    private Long userId;
    private Long chatId;
    private Long groupId;
    private Long messageId;
}
