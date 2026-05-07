# ===== STAGE 1: BUILD =====
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn -pl services/notification-service -am clean package -DskipTests

# ===== STAGE 2: RUN =====
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/services/notification-service/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
