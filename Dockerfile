# Dùng image runtime, không phải JDK (nhẹ + không chạy JShell)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy toàn bộ file
COPY . .

# Compile
RUN javac Main.java

# Chạy chương trình bình thường (quan trọng: không dùng shell form)
ENTRYPOINT ["java", "Main"]

