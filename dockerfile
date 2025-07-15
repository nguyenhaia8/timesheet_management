# Stage 1: Build ứng dụng bằng Maven
FROM maven:3.9.5-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Chạy file jar
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/TimeSheetManagement-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
