# Stage 1: Build the JAR using Maven inside Docker
FROM maven:3.9.9-eclipse-temurin-24 AS build
WORKDIR /app

# Copy project files into container
COPY . .

# Build the JAR (skip tests to save time)
RUN mvn clean package -DskipTests

# Stage 2: Run the JAR
FROM openjdk:24-jdk-slim
WORKDIR /auth-service

# Copy the built JAR from stage 1
COPY --from=build /app/target/auth-service-0.0.1-SNAPSHOT.jar  .

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "auth-service-0.0.1-SNAPSHOT.jar"]




