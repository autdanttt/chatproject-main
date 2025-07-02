package view.main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UserToken {
    private String jwtToken;
    private Long userId;
    private String email;
    private String fullName;
    private String avatarUrl;

}
