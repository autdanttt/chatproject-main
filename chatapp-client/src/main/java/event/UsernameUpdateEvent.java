package event;

public class UsernameUpdateEvent {
    private final String username;

    public UsernameUpdateEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
         return username;
    }
}
