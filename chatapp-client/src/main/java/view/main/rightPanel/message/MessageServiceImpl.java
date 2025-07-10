package view.main.rightPanel.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.MessageResponse;
import utility.Config;

import java.net.HttpURLConnection;
import java.net.URL;

public class MessageServiceImpl implements MessageService {
    @Override
    public MessageResponse[] getMessageByChatId(Long chatId, String jwtToken) {
        try{
            String url = Config.BASE_HTTP_URL + "chats/" + chatId + "/messages";
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

                MessageResponse[] messages = objectMapper.readValue(conn.getInputStream(), MessageResponse[].class);

                return messages;
            }
        }catch (Exception e){}

        return new MessageResponse[0];
    }

    @Override
    public MessageResponse[] getGroupMessageByGroupId(Long groupId, String jwtToken) {

        try{
            String url = Config.BASE_HTTP_URL + "api/groups/" + groupId + "/messages";
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                ObjectMapper objectMapper = new ObjectMapper();
//                objectMapper.registerModule(new JavaTimeModule());
//                objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

                MessageResponse[] messages = objectMapper.readValue(conn.getInputStream(), MessageResponse[].class);

                return messages;
            }
        }catch (Exception e){}

        return new MessageResponse[0];
    }
}
