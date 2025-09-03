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

# CRITICAL FIX: Use a shell to construct the full JDBC URL from the separate
# environment variables. This bypasses any formatting issues from Render.
# It then passes all properties correctly to the java -jar command.
ENTRYPOINT ["sh", "-c", "export DB_URL=\"jdbc:postgresql://${DB_HOST}/${DB_NAME}\" && echo '--- Constructed Database URL:' $DB_URL && java -Dspring.datasource.url=$DB_URL -Dspring.datasource.username=$DB_USER -Dspring.datasource.password=$DB_PASSWORD -Dtaskmanager.app.jwtSecret=$TASKMANAGER_APP_JWTSECRET -Dtaskmanager.app.jwtExpirationMs=$TASKMANAGER_APP_JWTEXPIRATIONMS -jar app.jar"]