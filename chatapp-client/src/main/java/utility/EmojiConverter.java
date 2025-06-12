package utility;

public class EmojiConverter {

    public static String emojiToUnicode(String emoji) {
        StringBuilder unicode = new StringBuilder();
        for (int i = 0; i < emoji.length(); i++) {
            int codePoint = emoji.codePointAt(i);
            unicode.append(String.format("U+%04x", codePoint));
            if(i<emoji.length()-1) unicode.append(" ");
        }
        return unicode.toString();
    }


    public static String unicodeToEmoji(String unicode) {
        String[] codes = unicode.split(" ");
        StringBuilder emoji = new StringBuilder();
        for (String code : codes) {
            if(code.startsWith("U+") || code.startsWith("u+")) {
                String hex = code.substring(2).toLowerCase();
                int codePoint = Integer.parseInt(hex, 16);
                emoji.append(new String(Character.toChars(codePoint)));
            }else {
                emoji.append(code);
            }
        }
        return emoji.toString();
    }
}
