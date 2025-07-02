package view.login;


import com.google.inject.Inject;
import di.BaseController;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.IOException;


public class LoginController extends BaseController {
    private final LoginService loginService;
    private LoginView loginView;

    @Inject
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected void setupDependencies() {
        this.loginView = new LoginView();

        loginView.addLoginButtonListener(e -> performLogin());

        loginView.getPasswordField().registerKeyboardAction(e -> performLogin()
        ,KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);


        loginView.addSignupButtonListener(e -> {
            String username = loginView.getUsername();
            loginView.setVisible(false);
            navigator.navigateTo("Register", username);
        });
    }

    public void performLogin() {
        String username = loginView.getUsername().trim();
        String password = loginView.getPassword().trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginView, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }
        try {
            UserLogin userLogin = loginService.authenticate(username, password);
            if (userLogin.getStatusCode() == 200) {
                navigator.navigateTo("MainChat", userLogin.getUserId(), userLogin.getUsername(), TokenManager.getAccessToken());
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(loginView, "Tên đăng nhập hoặc mật khẩu không chính xác. Vui lòng nhập lại");
        }
    }

    @Override
    public void activate(Object... params) {
        loginView.setVisible(true);

    }

    @Override
    public void deactivate() {
        loginView.setVisible(false);
    }
}
