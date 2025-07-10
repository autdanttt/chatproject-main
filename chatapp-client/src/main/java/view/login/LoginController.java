package view.login;

import com.google.inject.Inject;
import controllers.ForgotPasswordDialogController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import di.BaseController;
import view.main.dialog.ForgotPasswordDialog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class LoginController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;
    private LoginView loginView;
    private ForgotPasswordDialog forgotPasswordDialog;
    private ForgotPasswordDialogController forgotPasswordDialogController;

    @Inject
    public LoginController(LoginService loginService, ForgotPasswordDialogController forgotPasswordDialogController) {
        this.loginService = loginService;
        this.forgotPasswordDialogController = forgotPasswordDialogController;
    }

    @Override
    protected void setupDependencies() {
        this.loginView = new LoginView();

        initializeListeners();
    }

    private void initializeListeners() {
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
                TokenManager.setFullName(userLogin.getFullName());
                TokenManager.setAvatarUrl(userLogin.getAvatarUrl());
                if (userLogin.getStatusCode() == 200) {
                    navigator.navigateTo("MainChat", userLogin.getUserId(), userLogin.getEmail(), userLogin.getFullName(), userLogin.getAvatarUrl(), TokenManager.getAccessToken());
                } else {
                    JOptionPane.showMessageDialog(loginView,"Email và mật khẩu không chính xác. Vui lòng nhập lại");
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

        loginView.addForgetPasswordButtonListener(e -> handleForgotPassword());
    }

    private void handleForgotPassword() {
        forgotPasswordDialog = new ForgotPasswordDialog((JFrame) SwingUtilities.getWindowAncestor(loginView));
        forgotPasswordDialogController.setForgotPasswordDialog(forgotPasswordDialog);
        forgotPasswordDialog.setVisible(true);
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
