package di;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

public abstract class BaseController {

    @Inject protected EventBus eventBus;
    @Inject protected AppNavigator navigator;

    protected boolean initialized = false;

    protected Object[] initParams;

    public void initialize(Object... params) {
        if (!initialized) {
            this.initParams = params;
            setupDependencies();
            initialized = true;
        }
    }

    protected abstract void setupDependencies();
    public abstract void activate(Object... params);
    public abstract void deactivate();
}
