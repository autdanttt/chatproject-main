package view.login;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import event.TakeUserID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import di.BaseController;

import javax.swing.*;
import java.io.IOException;


public class LoginController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;
    private LoginView loginView;

    @Inject
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected void setupDependencies() {
        this.loginView = new LoginView();

        loginView.addLoginButtonListener(e -> {
            String username = loginView.getUsername().trim();
            String password = loginView.getPassword().trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginView, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
                return;
            }

            try {
                UserLogin userLogin = loginService.authenticate(username, password);
                TokenManager.setUserId(userLogin.getUserId());
                if (userLogin.getStatusCode() == 200) {
                    navigator.navigateTo("MainChat", userLogin.getUserId(), userLogin.getEmail(), userLogin.getFullName(), userLogin.getAvatarUrl(), TokenManager.getAccessToken());
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        loginView.addSignupButtonListener(e -> {
            String username = loginView.getUsername();
            loginView.setVisible(false);
            navigator.navigateTo("Register", username);
        });
    }

    ;

    @Override
    public void activate(Object... params) {
        loginView.setVisible(true);

    }

    @Override
    public void deactivate() {
        loginView.setVisible(false);
    }
}
