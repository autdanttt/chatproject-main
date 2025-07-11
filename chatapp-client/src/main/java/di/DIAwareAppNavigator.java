package di;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import event.NavigationEvent;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class DIAwareAppNavigator implements AppNavigator {
    private final EventBus eventBus;
    private final Injector injector;
    private final Map<String, Class<? extends BaseController>> controllerRegistry;
    private final Stack<BaseController> navigationStack;

    @Inject
    public DIAwareAppNavigator(EventBus eventBus, Injector injector) {
        this.eventBus = eventBus;
        this.injector = injector;
        this.controllerRegistry = new ConcurrentHashMap<>();
        this.navigationStack = new Stack<>();
    }


    @Override
    public void registerController(String key, Class<? extends BaseController> controllerClass) {
        controllerRegistry.put(key, controllerClass);
    }

    @Override
    public void navigateTo(String controllerKey, Object... params) {
        Class<? extends BaseController> controllerClass = controllerRegistry.get(controllerKey);
        if (controllerClass != null) {
            BaseController controller = injector.getInstance(controllerClass);

            if (!navigationStack.isEmpty()) {
                eventBus.post(new NavigationEvent(NavigationEvent.Type.BEFORE_LEAVE, navigationStack.peek().getClass().getSimpleName()));
                navigationStack.peek().deactivate();
            }

            controller.initialize(params);
            controller.activate(params);
            navigationStack.push(controller);

            eventBus.post(new NavigationEvent(NavigationEvent.Type.AFTER_ENTER, controller.getClass().getSimpleName()));
        }
    }
}
