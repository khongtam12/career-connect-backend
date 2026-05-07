# ===== BUILD =====
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn -pl services/storage-service -am clean package -DskipTests

# ===== RUN =====
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/services/storage-service/target/*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]