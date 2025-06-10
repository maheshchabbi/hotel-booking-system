# Stage 1: Build the application with JDK
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon

# Stage 2: Use JRE for minimal runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/booking-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "/app/app.jar"]
