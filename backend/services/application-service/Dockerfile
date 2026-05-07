# ===== STAGE 1: BUILD =====
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn -pl services/application-service -am clean package -DskipTests

# ===== STAGE 2: RUN =====
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/services/application-service/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
