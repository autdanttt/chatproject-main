package view.main.rightPanel.otherInfoTop;

import org.springframework.http.ResponseEntity;

public interface CallVideoService{
    public ResponseEntity<?> sendCallRequest(Long fromUserId, Long toUserId,String fullName, String jwtToken);
}
