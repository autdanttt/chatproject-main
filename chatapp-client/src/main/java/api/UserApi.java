package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.login.LoginController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class UserApi {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    public List<User> getAllOtherUsers(String myUsername, String accessToken) throws IOException {
        URL url = new URL("http://localhost:10000/api/users/listother?username=" + URLEncoder.encode(myUsername, StandardCharsets.UTF_8));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Content-Type", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                ObjectMapper mapper = new ObjectMapper();
                return Arrays.asList(mapper.readValue(response.toString(), User[].class));
            }
        } else {
            throw new IOException("Server returned code: " + responseCode);
        }
    }

}
