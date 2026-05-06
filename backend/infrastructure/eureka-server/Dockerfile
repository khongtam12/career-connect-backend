# ===== STAGE 1: BUILD =====
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

# Build chỉ module eureka-server (bỏ qua test)
RUN mvn -pl infrastructure/eureka-server -am clean package -DskipTests

# ===== STAGE 2: RUN =====
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy file JAR đã build
COPY --from=builder /app/infrastructure/eureka-server/target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]
