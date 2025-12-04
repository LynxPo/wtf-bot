# Sử dụng Java image nhẹ
FROM openjdk:17-jdk-alpine

# Tạo thư mục làm việc
WORKDIR /app

# Copy file code vào
COPY SecureDiscordBot.java .

# Biên dịch file java
RUN javac SecureDiscordBot.java

# Lệnh chạy bot khi khởi động
CMD ["java", "SecureDiscordBot"]