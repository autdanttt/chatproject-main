package event;

public class UserProfileUpdatedEvent {
    private String fullName;
    private String avatarUrl;

    public UserProfileUpdatedEvent(String fullName, String avatarUrl) {
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
