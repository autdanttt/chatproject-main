package service;

public class RegisterServiceImpl implements RegisterService {
    @Override
    public boolean register(String username, String password) {
        if(!username.equals("admin") && !password.equals("123456")) {
            return true;
        }else {
            return false;
        }
    }
}
