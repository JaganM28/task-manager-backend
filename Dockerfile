# Stage 1: Build the application
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/task-manager-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

# Use exec form for proper signal handling
ENTRYPOINT ["java", "-Dspring.datasource.url=${SPRING_DATASOURCE_URL}", "-Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME}", "-Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD}", "-Dtaskmanager.app.jwtSecret=${TASKMANAGER_APP_JWTSECRET}", "-Dtaskmanager.app.jwtExpirationMs=${TASKMANAGER_APP_JWTEXPIRATIONMS}", "-jar", "app.jar"]