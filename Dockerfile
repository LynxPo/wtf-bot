# Sử dụng image Eclipse Temurin (thay thế cho openjdk cũ)
FROM eclipse-temurin:17-jdk-alpine

# Tạo thư mục làm việc
WORKDIR /app

# Copy file code vào
COPY SecureDiscordBot.java .

# Biên dịch file java
RUN javac SecureDiscordBot.java

# Lệnh chạy bot
CMD ["java", "SecureDiscordBot"]
