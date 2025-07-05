package di;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import controllers.CreateChatController;
import controllers.CreateChatGroupController;
import controllers.FooterLeftController;
import service.*;
import utility.WebRTCManager;
import view.MainVideoFrame;
import view.MainVideoFrameController;
import view.login.LoginController;
import view.login.LoginService;
import view.login.LoginServiceImpl;
import view.main.MainChatController;
import view.main.MainChatView;
import utility.WebSocketClientManager;
import view.main.leftPanel.chatlist.ChatListController;
import view.main.leftPanel.chatlist.ChatListPanel;
import view.main.leftPanel.chatlist.ChatListService;
import view.main.leftPanel.chatlist.ChatListServiceImpl;
import view.main.rightPanel.RightPanel;
import view.main.rightPanel.components.FooterRightPanel;
import controllers.HeaderRightController;
import view.main.rightPanel.components.HeaderRightPanel;
import view.main.rightPanel.message.MessageController;
import view.main.rightPanel.message.MessagePanel;
import view.main.rightPanel.message.MessageService;
import view.main.rightPanel.message.MessageServiceImpl;
import view.main.rightPanel.otherInfoTop.*;
import view.main.rightPanel.sendmessage.SendMessageController;
import view.main.rightPanel.sendmessage.SendMessageService;
import view.main.rightPanel.sendmessage.SendMessageServiceImpl;
import view.main.leftPanel.search.SearchController;
import view.main.leftPanel.search.SearchPanel;
import view.register.RegisterController;
import view.register.RegisterService;
import view.register.RegisterServiceImpl;
//import view.register.RegisterController;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(EventBus.class).toInstance(new EventBus());
        bind(RegisterService.class).to(RegisterServiceImpl.class);
        bind(SearchService.class).to(SearchServiceImpl.class);
        bind(LoginService.class).to(LoginServiceImpl.class);
        bind(ChatListService.class).to(ChatListServiceImpl.class);
        bind(MessageService.class).to(MessageServiceImpl.class);
        bind(SendMessageService.class).to(SendMessageServiceImpl.class);
        bind(CallVideoService.class).to(CallVideoServiceImpl.class);
        bind(StatusUserService.class).to(StatusUserServiceImpl.class);

        //Controller Login and Register
        bind(LoginController.class);
        bind(RegisterController.class);

        // Controller Left Panel
        bind(SearchController.class);
        bind(ChatListController.class);
        bind(CreateChatController.class);
        bind(CreateChatGroupController.class);
        bind(FooterLeftController.class);


        //Controller Main
        bind(MainChatController.class);
        bind(MainChatView.class).in(Singleton.class);

        //Controller Right Panel
        bind(HeaderRightController.class);
        bind(SendMessageController.class);
        bind(MessageController.class);
        bind(InfoOtherAndFeatureController.class);
        bind(MainVideoFrameController.class);

        //View Left Panel
        bind(SearchPanel.class).in(Singleton.class);
        bind(view.main.leftPanel.components.FooterPanel.class).in(Singleton.class);
        bind(ChatListPanel.class).in(Singleton.class);
        bind(FooterRightPanel.class).in(Singleton.class);

        //View Right Pannel
        bind(InfoOtherAndFeature.class).in(Singleton.class);
        bind(RightPanel.class).in(Singleton.class);
        bind(HeaderRightPanel.class).in(Singleton.class);
        bind(MessagePanel.class).in(Singleton.class);
        bind(WebRTCManager.class).in(Singleton.class);
        bind(WebSocketClientManager.class).in(Singleton.class);

        bind(MainVideoFrame.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public AppNavigator provideAppNavigator(EventBus eventBus, Injector injector) {
        return new DIAwareAppNavigator(eventBus, injector);
    }
}

