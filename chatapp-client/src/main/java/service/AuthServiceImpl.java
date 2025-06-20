package service;

public class AuthServiceImpl implements AuthService {
    @Override
    public boolean authenticate(String username, String password) {
        if(username.equals("admin") && password.equals("123456")) {
            return true;
        }else {
            return false;
        }

    }
}
