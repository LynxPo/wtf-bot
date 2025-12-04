FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .
RUN javac Main.java
ENTRYPOINT ["java", "Main"]

