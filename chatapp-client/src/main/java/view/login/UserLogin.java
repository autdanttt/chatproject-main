package view.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin {
    private int statusCode;
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String email;
    private String fullName;
    private String avatarUrl;
    private Set<RoleDTO> roles;

    public void addRole(RoleDTO role) {
        this.getRoles().add(role);
    }
}
