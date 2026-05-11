# ===== STAGE 1: BUILD =====
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN --mount=type=cache,target=/root/.m2 \
    bash -lc 'for i in 1 2 3; do \
      mvn -B -pl services/cv-service -am clean package -DskipTests -Dmaven.wagon.http.retryHandler.count=5 && exit 0; \
      echo "Maven build failed on attempt $i. Retrying in 15 seconds..."; \
      sleep 15; \
    done; \
    exit 1'

# ===== STAGE 2: RUN =====
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends \
    chromium-browser \
    fonts-liberation \
    fonts-noto-color-emoji \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/services/cv-service/target/*.jar app.jar

ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1
ENV PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH=/usr/bin/chromium-browser

EXPOSE 8087

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
