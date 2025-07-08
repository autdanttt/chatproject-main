package view.main.dialog.Rename;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.ChatGroupDTO;
import model.ChatGroupResponse;
import model.UpdateGroupRequest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class RenameGroupImpl implements RenameGroupService {
    private Logger logger = LoggerFactory.getLogger(RenameGroupImpl.class);
    @Override
        public ChatGroupDTO renameGroup(Long groupId, String newGroupName, String jwtToken) {
            try{
                String url = "http://localhost:10000/api/groups/" + groupId;
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");

                conn.setDoOutput(true);

                UpdateGroupRequest renameRequest = new UpdateGroupRequest();
                renameRequest.setGroupName(newGroupName);

                ObjectMapper mapper = new ObjectMapper();
                String jsonBody = mapper.writeValueAsString(renameRequest);

                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input);
                    os.flush();
                }
                if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            logger.error("Server error response: " + line);
                        }
                    }
                }
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    String responseJson = new BufferedReader(new InputStreamReader(conn.getInputStream()))
                            .lines().reduce("", (acc, line) -> acc + line + "\n");
                    return mapper.readValue(responseJson, ChatGroupDTO.class);

                } else if(conn.getResponseCode() == 403) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null,"Lỗi: Bạn không phải admin","Lỗi quyền hạn",JOptionPane.ERROR_MESSAGE);
                    });
                }

            }catch (Exception e) {}

            return null;
        }

public String updateGroupImage(Long groupId, File imageFile, String jwtToken) {
    try {

        HttpPut put = new HttpPut("http://localhost:10000/api/groups/" + groupId + "/image");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(StandardCharsets.UTF_8);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


        builder.addBinaryBody(
                "image",
                imageFile,
                ContentType.create("image/jpeg"),
                imageFile.getName()
        );

        HttpEntity multipart = builder.build();
        put.setEntity(multipart);

        put.setHeader("Authorization", "Bearer " + jwtToken);
        put.setHeader("Accept", "application/json");

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(put);

        int statusCode = response.getStatusLine().getStatusCode();

        String responseJson = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
                .lines().reduce("", (acc, line) -> acc + line + "\n");


        if (statusCode == 200) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(responseJson, new TypeReference<Map<String, String>>() {});
            return map.get("image");
        } else if(statusCode == 403){
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Lỗi: Bạn không phải là admin của nhóm", "Lỗi quyền hạn", JOptionPane.ERROR_MESSAGE);
            });
        }

    } catch (Exception e) {
        logger.error("❌ Exception while uploading image: ", e);
    }

    return null;
}




}
