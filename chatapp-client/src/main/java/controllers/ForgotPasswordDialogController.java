package controllers;

import api.SendOtpApi;
import com.google.inject.Inject;
import di.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.login.LoginController;
import view.main.dialog.ForgotPasswordDialog;

import javax.swing.*;


@Slf4j
public class ForgotPasswordDialogController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private ForgotPasswordDialog forgotPasswordDialog;
    private SendOtpApi sendOtpApi;
    private String email;

    @Inject
    public ForgotPasswordDialogController(SendOtpApi sendOtpApi) {
        this.sendOtpApi = sendOtpApi;
    }

    public void setForgotPasswordDialog(ForgotPasswordDialog forgotPasswordDialog) {
        this.forgotPasswordDialog = forgotPasswordDialog;
        initializeListeners();
    }

    private void initializeListeners() {
        forgotPasswordDialog.addSendOtpButtonListener(e -> handleSendOTP());
        forgotPasswordDialog.addSubmitButtonListener(e -> handleSubmit());
    }

    private void handleSubmit() {
        String otp = forgotPasswordDialog.getOtp().trim();
        String newpass = forgotPasswordDialog.getNewPassword().trim();
        String confirmpass = forgotPasswordDialog.getConfirmPassword().trim();

        if (otp.isEmpty()) {
            forgotPasswordDialog.showMessage("Vui lòng nhập mã OTP.");
            return;
        }

        if (newpass.isEmpty()) {
            forgotPasswordDialog.showMessage("Vui lòng nhập mật khẩu mới.");
            return;
        }

        if (confirmpass.isEmpty()) {
            forgotPasswordDialog.showMessage("Vui lòng nhập lại mật khẩu để xác nhận.");
            return;
        }

        if (!newpass.equals(confirmpass)) {
            forgotPasswordDialog.showMessage("Mật khẩu xác nhận không khớp với mật khẩu mới.");
            return;
        }

        if (newpass.length() < 6) {
            forgotPasswordDialog.showMessage("Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }

        new Thread(() -> {
            String message = sendOtpApi.resetPasswordByOtp(email, otp, newpass);
            if (message != null) {
                SwingUtilities.invokeLater(() ->
                        forgotPasswordDialog.showMessage(message)
                );
            } else {
                SwingUtilities.invokeLater(() ->
                        forgotPasswordDialog.showMessage("Không thể đặt lại mật khẩu. Vui lòng thử lại.")
                );
            }
        }).start();
        forgotPasswordDialog.dispose();
        navigator.navigateTo("Login");
    }

    private void handleSendOTP() {
        email = forgotPasswordDialog.getEmail().trim();

        if (email.isEmpty()) {
            forgotPasswordDialog.showMessage("Vui lòng nhập email.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            forgotPasswordDialog.showMessage("Địa chỉ email không hợp lệ. Vui lòng kiểm tra lại.");
            return;
        }

        new Thread(() -> {
            String message = sendOtpApi.sendOtp(email);

            if (message != null) {
                SwingUtilities.invokeLater(() ->
                        forgotPasswordDialog.showMessage(message)
                );
            } else {
                SwingUtilities.invokeLater(() ->
                        forgotPasswordDialog.showMessage("Không thể gửi OTP. Vui lòng kiểm tra kết nối server.")
                );
            }
        }).start();
    }

    @Override
    protected void setupDependencies() {

    }

    @Override
    public void activate(Object... params) {

    }

    @Override
    public void deactivate() {

    }
}
