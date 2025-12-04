import java.io.*;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

    private static final String USER_TOKEN = System.getenv("DISCORD_TOKEN");
    private static final String CHANNEL_ID = System.getenv("CHANNEL_ID");

    private static final List<String> MESSAGES = Arrays.asList(
        "!d bump", ".bump", "!bump", "bump", "bump nha",
        "!d bump done", ".bump done", "up", "up nào ae", "bump server nè"
    );

    private static final Random random = new Random();
    private static Timer timer;
    private static volatile boolean isRunning = true;

    public static void main(String[] args) {
        System.out.println("=== Smart Level Spammer (2h00 - 2h15 random) ===\n");

        if (USER_TOKEN == null || USER_TOKEN.isEmpty() || USER_TOKEN.length() < 30) {
            System.err.println("LỖI: DISCORD_TOKEN sai hoặc chưa đặt!");
            return;
        }
        if (CHANNEL_ID == null || CHANNEL_ID.isEmpty()) {
            System.err.println("LỖI: Thiếu CHANNEL_ID!");
            return;
        }

        System.out.println("Token: " + mask(USER_TOKEN));
        System.out.println("Channel: " + CHANNEL_ID);
        System.out.println("Bump mỗi 120-135 phút (random) + tin nhắn random\n");

        // Gửi thử 1 tin ngay khi khởi động để biết nó sống
        sendRandomMessage();
        System.out.println("[TEST] Đã gửi tin nhắn thử – nếu thấy trong Discord = đang chạy ngon!\n");

        startSpammer();
    }

    private static void startSpammer() {
        System.out.println("Spammer chính thức chạy 24/7! Chờ 2h-2h15 sẽ tự bump...\n");
        scheduleNextBump();
    }

    private static long randomDelay() {
        long extra = (long) (random.nextDouble() * 900_000L); // 0-15 phút
        return 7_200_000L + extra; // 120 phút + extra
    }

    private static void scheduleNextBump() {
        if (!isRunning) return;
        long delay = randomDelay();
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendRandomMessage();
                scheduleNextBump();
            }
        }, delay);

        System.out.println("[" + timeNow() + "] Lần bump tiếp theo sau ~" + (delay / 60_000) + " phút");
    }

    private static void sendRandomMessage() {
        String message = MESSAGES.get(random.nextInt(MESSAGES.size()));
        try {
            URL url = new URI("https://discord.com/api/v9/channels/" + CHANNEL_ID + "/messages").toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", USER_TOKEN);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setDoOutput(true);

            String payload = "{\"content\":\"" + message.replace("\"", "\\\"") + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            if (code == 200 || code == 204) {
                System.out.println("[" + timeNow() + "] ĐÃ BUMP: " + message);
            } else {
                String err = readStream(code >= 400 ? conn.getErrorStream() : conn.getInputStream());
                System.out.println("[" + timeNow() + "] LỖI " + code + ": " + err);
                if (code == 401 || code == 403) {
                    System.err.println("TOKEN DIE RỒI! Dừng luôn đây...");
                    System.exit(0);
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            System.out.println("[" + timeNow() + "] Lỗi: " + e.getMessage());
        }
    }

    private static String timeNow() {
        return new java.text.SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    private static String readStream(InputStream stream) throws IOException {
        if (stream == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private static String mask(String s) {
        if (s.length() < 10) return "****";
        return s.substring(0, 8) + "..." + s.substring(s.length() - 6);
    }
}
