# ===== Build stage: Maven (Java 21) =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ===== Runtime stage: JRE 21 =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/target/*.jar app.jar
USER app

# Cấu hình qua ENV (khớp application.yml: SERVER_PORT / SERVICE_NAME).
ENV SERVER_PORT=8081 \
    SERVICE_NAME=master-data \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS=""
EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
