package utility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;

@Getter
@AllArgsConstructor
public class OtherUserStatusEvent {
    private String email;
    private String status;
    private String type; // thêm type để phân biệt
}
