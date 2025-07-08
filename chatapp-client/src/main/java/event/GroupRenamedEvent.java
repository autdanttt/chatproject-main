package event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupRenamedEvent {
//    private Long groupId;
    private String newGroupName;
}
