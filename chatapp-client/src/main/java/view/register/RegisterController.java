package view.register;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.RegisterService;
import di.BaseController;

import javax.swing.*;

public class RegisterController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);
    private final RegisterService registerService;
    private RegisterView registerView;
    private String username;

    @Inject
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    protected void setupDependencies() {
        this.registerView = new RegisterView();

        registerView.addSendButtonListener(e -> {
            String username = registerView.getUsername();
            String password = registerView.getPassword();

            if(registerService.register(username, password)) {
                registerView.setVisible(false);
                JOptionPane.showMessageDialog(null, "Registration Successful");
                navigator.navigateTo("Login");


            }else {
                registerView.showError("Invalid username or password");
            }
        });

        registerView.addCancelButtonListener(e -> {
            registerView.setVisible(false);
            navigator.navigateTo("Login");
        });

    }
    @Override
    public void activate(Object... params) {
        this.username = (String) params[0];

        if (registerView == null) {
            setupDependencies(); // Chỉ setup lần đầu
        }

        registerView.setUsername(this.username);

        registerView.setVisible(true);
    }

    @Override
    public void deactivate() {
        registerView.setVisible(false);
    }
}
