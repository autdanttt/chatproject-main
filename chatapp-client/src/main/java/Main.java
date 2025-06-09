import controller.LoginController;
import view.LoginView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            LoginView view = new LoginView();
            new LoginController(view);
        });
    }
}
