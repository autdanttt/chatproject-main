package di;


public interface AppNavigator {
    void registerController(String key, Class<? extends BaseController> controllerClass);
    void navigateTo(String controllerKey, Object... params);
}
