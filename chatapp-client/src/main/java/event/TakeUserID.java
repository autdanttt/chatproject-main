package event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import view.login.UserLogin;

@AllArgsConstructor
@Getter
public class TakeUserID {
    private UserLogin userLogin;
}
