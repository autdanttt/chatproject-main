import com.google.inject.Guice;
import com.google.inject.Injector;
import di.AppModule;
import di.AppNavigator;
import view.login.LoginController;
import view.main.MainChatController;
import view.main.leftPanel.chatlist.ChatListController;
import view.main.rightPanel.message.MessageController;
import view.main.rightPanel.otherInfoTop.InfoOtherAndFeatureController;
import view.main.leftPanel.search.SearchController;
import view.register.RegisterController;


import javax.swing.*;

public class Main extends JFrame {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());
        AppNavigator navigator = injector.getInstance(AppNavigator.class);

        navigator.registerController("Login", LoginController.class);
        navigator.registerController("Register", RegisterController.class);
        navigator.registerController("MainChat", MainChatController.class);
        navigator.registerController("Search", SearchController.class);
        navigator.registerController("ChatList", ChatListController.class);
        navigator.registerController("Message", MessageController.class);
        navigator.registerController("InfoOtherAndFeature", InfoOtherAndFeatureController.class);

        SwingUtilities.invokeLater(()->{
            navigator.navigateTo("Login");
        });
    }
}
