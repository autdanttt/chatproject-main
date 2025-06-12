package utility;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class EmojiUtil {

    public static final Map<String, String> EMOJIS_MAP = new HashMap<>();

    static {
        InputStream inputStream = EmojiUtil.class.getClassLoader().getResourceAsStream("emoji-test.txt");
        if (inputStream == null) {
            System.err.println("Error: emoji-test.txt not found in classpath!");
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(";", 2);
                if (parts.length == 2) {
                    String[] codePoints = parts[0].trim().split("\\s+"); // Tách các code point
                    String[] emojiData = parts[1].trim().split(" # ", 2);
                    if (emojiData.length == 2) {
                        String status = emojiData[0].trim();
                        String emojiName = emojiData[1].trim().split(" ")[0]; // Lấy ký tự emoji
                        if (status.equals("fully-qualified")) {
                            StringBuilder emojiBuilder = new StringBuilder();
                            for (String codePoint : codePoints) {
                                int codePointValue = Integer.parseInt(codePoint, 16); // Chuyển đổi từng code point
                                emojiBuilder.append(Character.toString(codePointValue));
                            }
                            String emoji = emojiBuilder.toString();
                            String unicode = "U+" + String.join(" U+", codePoints); // Tạo mã Unicode kết hợp
                            EMOJIS_MAP.put(emoji, unicode);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error reading emoji-test.txt: " + e.getMessage());
        }
    }

    public static String[] getEmojiArray(){
        return EMOJIS_MAP.keySet().toArray(new String[0]);
    }

    public static String getUnicode(String emoji){
        return EMOJIS_MAP.getOrDefault(emoji,emoji);
    }
}
