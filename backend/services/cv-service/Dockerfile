# ===== STAGE 1: BUILD =====
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn -pl services/cv-service -am clean package -DskipTests

# ===== STAGE 2: RUN =====
FROM mcr.microsoft.com/playwright/java:v1.43.0-jammy

WORKDIR /app

COPY --from=builder /app/services/cv-service/target/*.jar app.jar

# Playwright requires some environment variables to find the browser
ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1

EXPOSE 8087

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
