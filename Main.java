import java.io.*;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    private static final String TOKEN = System.getenv("DISCORD_TOKEN");
    private static final String CHANNEL = System.getenv("CHANNEL_ID");
    private static final Random r = new Random();
    private static final String[] MSG = {
        "!d bump", ".bump", "!bump", "bump", "bump nha",
        "up", "up nào ae", "bump server nè", "!d bump done"
    };

    public static void main(String[] args) throws Exception {
        if (TOKEN == null || CHANNEL == null) {
            System.out.println("Thiếu DISCORD_TOKEN hoặc CHANNEL_ID");
            return;
        }

        System.out.println("=== Smart Spammer đang chạy ===");
        System.out.println("Token: " + TOKEN.substring(0, 10) + "...");
        System.out.println("Channel: " + CHANNEL + "\n");

        // Gửi thử ngay 1 phát để biết còn sống
        send("!d bump — test khởi động thành công");

        // Loop vô hạn
        while (true) {
            long delay = 7_200_000L + r.nextLong(900_000L); // 120-135 phút
            Thread.sleep(delay);
            send(MSG[r.nextInt(MSG.length)]);
            System.out.println("[" + new Date() + "] Đã bump – chờ ~" + (delay/60_000) + " phút nữa");
        }
    }

    private static void send(String content) {
        try {
            URL url = new URI("https://discord.com/api/v9/channels/" + CHANNEL + "/messages").toURL();
            HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
            c.setRequestMethod("POST");
            c.setRequestProperty("Authorization", TOKEN);
            c.setRequestProperty("Content-Type", "application/json");
            c.setRequestProperty("User-Agent", "Mozilla/5.0");
            c.setDoOutput(true);

            String json = "{\"content\":\"" + content.replace("\"", "\\\"") + "\"}";
            c.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));

            int code = c.getResponseCode();
            if (code == 200 || code == 204) {
                System.out.println("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()) + "] ĐÃ GỬI: " + content);
            } else {
                System.out.println("Lỗi " + code);
                if (code == 401 || code == 403) System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }
}
