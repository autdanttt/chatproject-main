import com.google.inject.Guice;
import com.google.inject.Injector;
import di.AppModule;
import di.AppNavigator;
import view.login.LoginController;
import view.main.MainChatController;
import view.main.chatlist.chatlist.ChatListController;
import view.main.rightpanel.message.MessageController;
import view.main.rightpanel.usernameinfo.callvideo.CallVideoController;
import view.main.rightpanel.usernameinfo.usernamestatus.UsernameStatusController;
import view.main.search.search.SearchController;
import view.register.RegisterController;


import javax.swing.*;

public class Main {
    public static void main(String[] args) {
//        SwingUtilities.invokeLater(()->{
//            LoginView view = new LoginView();
//            new LoginControllerMain(view);
 //       });

        Injector injector = Guice.createInjector(new AppModule());
        AppNavigator navigator = injector.getInstance(AppNavigator.class);

        navigator.registerController("Login", LoginController.class);
        navigator.registerController("Register", RegisterController.class);
        navigator.registerController("MainChat", MainChatController.class);
        navigator.registerController("Search", SearchController.class);
        navigator.registerController("ChatList", ChatListController.class);
        navigator.registerController("Message", MessageController.class);
        navigator.registerController("CallVideo", CallVideoController.class);
        navigator.registerController("UsernameStatus", UsernameStatusController.class);

        SwingUtilities.invokeLater(()->{
            navigator.navigateTo("Login");
        });
    }
}
