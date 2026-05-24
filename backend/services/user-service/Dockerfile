# syntax=docker/dockerfile:1.6

# ===== STAGE 1: BUILD =====
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY .mvn/settings.xml /root/.m2/settings.xml

COPY . .

RUN --mount=type=cache,target=/root/.m2/repository,sharing=locked mvn -pl services/user-service -am clean package -DskipTests

# ===== STAGE 2: RUN =====
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/services/user-service/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
