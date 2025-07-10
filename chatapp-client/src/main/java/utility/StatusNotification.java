package utility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusNotification {
    private String email;
    private String status; // "online" hoặc "offline"
    private String last; // chỉ dùng nếu offline
}
