package di;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import service.*;
import utility.WebRTCManager;
import view.login.LoginController;
import view.login.LoginService;
import view.login.LoginServiceImpl;
import view.main.MainChatController;
import view.main.MainChatView;
import utility.WebSocketClientManager;
import view.main.chatlist.chatlist.ChatListController;
import view.main.chatlist.chatlist.ChatListPanel;
import view.main.chatlist.chatlist.ChatListService;
import view.main.chatlist.chatlist.ChatListServiceImpl;
import view.main.rightpanel.RightPanel;
import view.main.rightpanel.message.MessageController;
import view.main.rightpanel.message.MessagePanel;
import view.main.rightpanel.message.MessageService;
import view.main.rightpanel.message.MessageServiceImpl;
import view.main.rightpanel.sendmessage.SendMessageController;
import view.main.rightpanel.sendmessage.SendMessagePanel;
import view.main.rightpanel.sendmessage.SendMessageService;
import view.main.rightpanel.sendmessage.SendMessageServiceImpl;
import view.main.rightpanel.usernameinfo.UsernameInfoPanel;
import view.main.rightpanel.usernameinfo.callvideo.CallVideoController;
import view.main.rightpanel.usernameinfo.callvideo.CallVideoPanel;
import view.main.rightpanel.usernameinfo.usernamestatus.UsernameStatusController;
import view.main.rightpanel.usernameinfo.usernamestatus.UsernameStatusPanel;
import view.main.search.search.SearchController;
import view.main.search.search.SearchPanel;
import view.register.RegisterController;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(EventBus.class).toInstance(new EventBus());
        bind(AuthService.class).to(AuthServiceImpl.class);
        bind(RegisterService.class).to(RegisterServiceImpl.class);
        bind(SearchService.class).to(SearchServiceImpl.class);
        bind(LoginService.class).to(LoginServiceImpl.class);
        bind(ChatListService.class).to(ChatListServiceImpl.class);
        bind(MessageService.class).to(MessageServiceImpl.class);
        bind(SendMessageService.class).to(SendMessageServiceImpl.class);

        bind(LoginController.class);
        bind(RegisterController.class);
        bind(SearchController.class);
        bind(MainChatController.class);
        bind(CallVideoController.class);
        bind(UsernameStatusController.class);
        bind(SendMessageController.class);
        bind(ChatListController.class);
        bind(MessageController.class);

        bind(CallVideoPanel.class).in(Singleton.class);
        bind(UsernameStatusPanel.class).in(Singleton.class);
        bind(UsernameInfoPanel.class).in(Singleton.class);
        bind(MessagePanel.class).in(Singleton.class);
        bind(SendMessagePanel.class).in(Singleton.class);
        bind(MainChatView.class).in(Singleton.class);
        bind(ChatListPanel.class).in(Singleton.class);
        bind(SearchPanel.class).in(Singleton.class);
        bind(RightPanel.class).in(Singleton.class);
        bind(ChatListPanel.class).in(Singleton.class);
        bind(MessagePanel.class).in(Singleton.class);
        bind(WebSocketClientManager.class).in(Singleton.class);
        bind(WebRTCManager.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public AppNavigator provideAppNavigator(EventBus eventBus, Injector injector) {
        return new DIAwareAppNavigator(eventBus, injector);
    }
}

