package view.main.chatlist.chatlist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.ChatResponse;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChatListServiceImpl implements ChatListService {
    @Override
    public ChatResponse[] getChatList(String jwtToken) {
        try {

            String url = "http://localhost:10000/chats"; // Không cần ?username nữa
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken); // Giữ nguyên


            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

                ChatResponse[] responses = mapper.readValue(conn.getInputStream(), ChatResponse[].class);

                return responses;
            }

        }catch (Exception e) {

        }
        return new ChatResponse[0];
    }
}
