package event;

public class NavigationEvent {

    public enum Type {BEFORE_LEAVE, AFTER_ENTER};
    private final Type type;
    private final String controllerName;

    public NavigationEvent(Type type, String controllerName) {
        this.type = type;
        this.controllerName = controllerName;
    }

    public Type getType() {
        return type;
    }

    public String getControllerName() {
        return controllerName;
    }
}

