import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SecureDiscordBot {

    // ============================================
    // PHẦN CẤU HÌNH
    // ============================================

    // Channel ID muốn gửi tin
    private static final String CHANNEL_ID = "1011985329529569400";

    // Cấu hình thời gian (Đơn vị: PHÚT)
    private static final int MIN_MINUTES = 120; // Tối thiểu 2 tiếng
    private static final int MAX_MINUTES = 135; // Tối đa 2 tiếng 15 phút

    // Danh sách tin nhắn để random
    private static final String[] MESSAGES = {
            "Xin chào mọi người!",
            "Chúc một ngày tốt lành.",
            "Tôi đang treo máy, đừng tag nhé.",
            "Hello world!",
            "Cảm ơn vì đã chia sẻ.",
            "Tuyệt vời quá."
    };

    // Tên biến môi trường chứa Token (Bảo mật)
    private static final String ENV_VAR_NAME = "MY_DISCORD_TOKEN";

    // ============================================
    // LOGIC XỬ LÝ
    // ============================================

    private static volatile boolean isRunning = true;
    private static final Random random = new Random();
    private static String USER_TOKEN = "";

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("Discord Advanced Self-Bot");
        System.out.println("=================================\n");

        // 1. Lấy Token từ biến môi trường (BẢO MẬT)
        USER_TOKEN = System.getenv(ENV_VAR_NAME);

        if (USER_TOKEN == null || USER_TOKEN.isEmpty()) {
            System.err.println("❌ LỖI: Không tìm thấy Token!");
            System.err.println("Vui lòng tạo biến môi trường tên: " + ENV_VAR_NAME);
            System.err.println("Cách làm: Xem hướng dẫn bên dưới code.");
            return;
        }

        if (CHANNEL_ID.equals("123456789012345678")) {
            System.err.println("❌ LỖI: Bạn chưa thay đổi CHANNEL_ID trong code!");
            return;
        }

        System.out.println("--- CẤU HÌNH ---");
        System.out.println("Token: " + maskToken(USER_TOKEN));
        System.out.println("Channel ID: " + CHANNEL_ID);
        System.out.println("Thời gian: Random từ " + MIN_MINUTES + " đến " + MAX_MINUTES + " phút.");
        System.out.println("Số lượng tin nhắn mẫu: " + MESSAGES.length);
        System.out.println("----------------\n");

        // Thread lắng nghe phím bấm để dừng
        Thread inputThread = new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (isRunning) {
                    if (reader.ready()) {
                        String input = reader.readLine();
                        if (input != null && input.trim().equals("9")) {
                            System.out.println("\n⏳ Đang dừng bot...");
                            isRunning = false;
                            break;
                        }
                    }
                    Thread.sleep(100);
                }
            } catch (Exception e) {}
        });
        inputThread.start();

        System.out.println("Bot đang chạy. 👉 Nhấn số 9 và Enter để dừng.\n");

        // Vòng lặp chính
        runBotLoop();
    }

    private static void runBotLoop() {
        while (isRunning) {
            // 2. Chọn tin nhắn ngẫu nhiên
            String messageToSend = MESSAGES[random.nextInt(MESSAGES.length)];

            // Gửi tin
            sendMessage(messageToSend);

            // 3. Tính toán thời gian ngủ ngẫu nhiên
            // Công thức: (Min + random(0 đến Max-Min)) * 60 * 1000 (đổi ra mili giây)
            int randomMinutes = MIN_MINUTES + random.nextInt(MAX_MINUTES - MIN_MINUTES + 1);
            long sleepMillis = randomMinutes * 60 * 1000L;

            // Thêm một chút giây lẻ để tự nhiên hơn (cộng thêm 0-59 giây)
            sleepMillis += random.nextInt(60) * 1000L;

            System.out.println("💤 Bot sẽ nghỉ trong: " + randomMinutes + " phút (" + (sleepMillis/1000) + " giây)...");

            try {
                // Ngủ... (có thể bị đánh thức nếu user bấm dừng)
                for (long i = 0; i < sleepMillis; i += 1000) {
                    if (!isRunning) return;
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("🛑 BOT ĐÃ DỪNG.");
    }

    private static void sendMessage(String message) {
        if (!isRunning) return;
        try {
            String apiUrl = "https://discord.com/api/v9/channels/" + CHANNEL_ID + "/messages";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", USER_TOKEN);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            conn.setDoOutput(true);

            String jsonPayload = String.format("{\"content\":\"%s\"}", escapeJson(message));

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());

            if (responseCode == 200 || responseCode == 201) {
                System.out.println("[" + timestamp + "] ✓ Đã gửi: \"" + message + "\"");
            } else {
                System.out.println("[" + timestamp + "] ✗ Lỗi (" + responseCode + ")");
            }
            conn.disconnect();

        } catch (Exception e) {
            System.out.println("✗ Lỗi kết nối: " + e.getMessage());
        }
    }

    private static String maskToken(String token) {
        if (token == null || token.length() <= 10) return "***";
        return token.substring(0, 5) + "..." + token.substring(token.length() - 5);
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}