package view.login;



import com.google.inject.Inject;
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
            logger.info("username : " + username + " password : " + password);

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginView, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
                return;
            }

            try {
                UserLogin userLogin = loginService.authenticate(username, password);

                if (userLogin == null) {
                    JOptionPane.showMessageDialog(loginView, "Lỗi không xác định: Không nhận được phản hồi từ server.");
                    return;
                }

                if (userLogin.getStatusCode() == 200) {
                    if (userLogin.getJwtToken() == null || userLogin.getUsername() == null) {
                        JOptionPane.showMessageDialog(loginView, "Dữ liệu phản hồi không hợp lệ.");
                        return;
                    }

                    navigator.navigateTo(
                            "MainChat",
                            userLogin.getUserId(),
                            userLogin.getUsername(),
                            userLogin.getJwtToken()
                    );
                } else if (userLogin.getStatusCode() == 401) {
                    JOptionPane.showMessageDialog(loginView, "Tên đăng nhập hoặc mật khẩu không đúng.");
                } else {
                    JOptionPane.showMessageDialog(loginView, "Đăng nhập thất bại. Mã lỗi: " + userLogin.getStatusCode());
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(loginView, "Lỗi kết nối đến máy chủ. Vui lòng kiểm tra mạng.");
                ex.printStackTrace();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(loginView, "Đã xảy ra lỗi không xác định.");
                ex.printStackTrace();
            }
        });

        loginView.addSignupButtonListener(e->{
            String username = loginView.getUsername();
            loginView.setVisible(false);
            navigator.navigateTo("Register", username);
        });
    };

    @Override
    public void activate(Object... params) {
        loginView.setVisible(true);

    }

    @Override
    public void deactivate() {
        loginView.setVisible(false);
    }
}
