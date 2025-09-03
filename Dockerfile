# Stage 1: Build the application using Maven
# This uses an official Maven image that has Java 17 installed.
FROM maven:3.8-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and the source code into the container
COPY pom.xml .
COPY src ./src

# Run the Maven build command to create the .jar file
RUN mvn clean package -DskipTests

# Stage 2: Create the final, lightweight image for running the app
# This uses a slim Java 17 image, making our final container smaller.
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Expose the port that the Spring Boot application runs on
EXPOSE 8080

# The command that will be run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]