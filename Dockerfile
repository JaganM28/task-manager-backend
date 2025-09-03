# Stage 1: Build the application using the official Maven image
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final, lightweight image for running the app
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/task-manager-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

# CRITICAL FIX: Use the 'exec' form of ENTRYPOINT to run a shell ('sh -c').
ENTRYPOINT ["sh", "-c", "echo '--- Starting application with the following environment variables ---' && echo 'DATABASE URL:' $SPRING_DATASOURCE_URL && echo '---' && java -Dspring.datasource.url=$SPRING_DATASOURCE_URL -Dtaskmanager.app.jwtSecret=$TASKMANAGER_APP_JWTSECRET -Dtaskmanager.app.jwtExpirationMs=$TASKMANAGER_APP_JWTEXPIRATIONMS -jar app.jar"]