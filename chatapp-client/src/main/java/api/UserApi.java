package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.UserOther;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class UserApi {
    public List<UserOther> getAllOtherUsers(String accessToken) throws IOException {
        URL url = new URL("http://localhost:10000/api/users/listother");
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
                return Arrays.asList(mapper.readValue(response.toString(), UserOther[].class));
            }
        } else {
            throw new IOException("Server returned code: " + responseCode);
        }
    }
}
