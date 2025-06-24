package view.login;



import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import di.BaseController;

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
            String username = loginView.getUsername();
            String password = loginView.getPassword();
            try {
                UserLogin userLogin = loginService.authenticate(username, password);
                if(userLogin.getStatusCode() == 200) {
                    navigator.navigateTo("MainChat",userLogin.getUserId(), userLogin.getUsername(), TokenManager.getAccessToken());
                }

            } catch (IOException ex) {
                throw new RuntimeException(ex);
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
