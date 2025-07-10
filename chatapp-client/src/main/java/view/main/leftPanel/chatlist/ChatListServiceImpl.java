package view.main.leftPanel.chatlist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.ChatGroupResponse;
import model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.Config;

import java.net.HttpURLConnection;
import java.net.URL;

public class ChatListServiceImpl implements ChatListService {
    private static final Logger log = LoggerFactory.getLogger(ChatListServiceImpl.class);

    @Override
    public ChatResponse[] getChatList(String jwtToken) {
        try {
            String url = Config.BASE_HTTP_URL +  "chats";
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

                ChatResponse[] responses = mapper.readValue(conn.getInputStream(), ChatResponse[].class);
                return responses;
            }

        }catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ChatResponse[0];
    }

    @Override
    public ChatGroupResponse[] getChatGroupList(String jwtToken) {
        try {
            String url = Config.BASE_HTTP_URL + "api/groups/my";
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

                ChatGroupResponse[] responses = mapper.readValue(conn.getInputStream(), ChatGroupResponse[].class);

                for (ChatGroupResponse group : responses) {
                    log.info("Group: " + group);
                }
                return responses;
            }

        }catch (Exception e) {

        }
        return new ChatGroupResponse[0];
    }
}
