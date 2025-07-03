package view.register;

import com.google.inject.Inject;
import custom.CreateLoadingDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import di.BaseController;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class RegisterController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private RegisterView registerView;
    private RegisterServiceImpl registerService;

    @Inject
    public RegisterController(RegisterServiceImpl registerService) {
        this.registerService = registerService;
    }

    @Override
    protected void setupDependencies() {
        this.registerView = new RegisterView();
        initializeListeners();
    }

    public void initializeListeners() {
        registerView.addCancelButtonListener(e -> {
            navigator.navigateTo("Login");
            registerView.setVisible(false);
        });

        registerView.addRegisterButtonListener(e -> {
            String email = registerView.getEmail();
            String fullName = registerView.getFullName();
            String password = registerView.getPassword();
            String confirmPassword = registerView.getConfirmPassword();

            if (email.isEmpty() || fullName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(registerView, "Vui lòng nhập đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(registerView, "Mật khẩu không khớp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog loadingDialog = CreateLoadingDialog.create(registerView);

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    try {
                        registerService.registerApi(email, fullName, password, confirmPassword);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(registerView, "Đăng ký thất bại: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                    return null;
                }

                @Override
                protected void done() {
                    loadingDialog.dispose();
                    navigator.navigateTo("Login");
                    registerView.setVisible(false);
                }
            };
            worker.execute();
            new Thread(() -> loadingDialog.setVisible(true)).start();
        });
    }

    @Override
    public void activate(Object... params) {

    }

    @Override
    public void deactivate() {

    }
}
