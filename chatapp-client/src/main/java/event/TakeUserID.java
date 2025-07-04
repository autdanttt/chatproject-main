package event;

import view.login.UserLogin;

public class TakeUserID {
    private UserLogin userLogin;
    public TakeUserID(UserLogin userLogin) {
        this.userLogin = userLogin;
    }

    public UserLogin getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(UserLogin userLogin) {
        this.userLogin = userLogin;
    }
}
