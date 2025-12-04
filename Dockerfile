FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy source code
COPY . .

# Compile file java (vì mày chỉ có 1 file .java)
RUN javac SecureDiscordBot.java
