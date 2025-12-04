FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy file JAR từ thư mục target
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
