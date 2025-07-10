package view.main.rightPanel.sendmessage;

import model.MessageResponse;
import model.MessageType;
import org.springframework.http.ResponseEntity;
import view.ApiResult;

import java.io.File;
import java.util.Map;

public interface SendMessageService {
    public ApiResult<MessageResponse> sendTextMessage(String jwtToken, Long fromUseId, Long toUserId, String messageContent, MessageType messageType);
    public ResponseEntity<Map> sendFile(String jwtToken, Long userId, File file);

    public ApiResult<MessageResponse> sendTextGroupMessage(String jwtToken, Long userId, Long groupId, String messageContent,MessageType messageType);
}
