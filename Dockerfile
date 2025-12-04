FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .
RUN javac SmartLevelSpammer.java
ENTRYPOINT ["java", "SmartLevelSpammer"]
