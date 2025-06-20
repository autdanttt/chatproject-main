package view.login;

import java.io.IOException;

public interface LoginService{
    public UserLogin authenticate(String username, String password) throws IOException;
}
