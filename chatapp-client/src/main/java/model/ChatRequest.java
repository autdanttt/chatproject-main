package model;

public class ChatRequest {
    private Long target_user_id;

    public ChatRequest() {
    }
    public ChatRequest(Long targetUserId) {
        this.target_user_id = targetUserId;
    }
    public Long getTarget_user_id() {
        return target_user_id;
    }
    public void setTarget_user_id(Long target_user_id) {
        this.target_user_id = target_user_id;
    }
}
