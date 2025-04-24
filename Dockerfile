FROM gradle:7.2.0-jdk as builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew clean build -x test --no-daemon

FROM openjdk:17
WORKDIR /app
COPY --from=builder /app/build/libs/meet-u-backend-0.0.1-SNAPSHOT.jar /app/meet-u-backend.jar
ENTRYPOINT ["java", "-jar", "/app/meet-u-backend.jar"]