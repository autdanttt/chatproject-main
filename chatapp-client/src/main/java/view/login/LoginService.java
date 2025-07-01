package view.login;

import java.io.IOException;

public interface LoginService{
    public UserLogin authenticate(String email, String password) throws IOException;
}
