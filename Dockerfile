FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/task-manager-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

# FIX: We will now read the environment variables from Render and pass them directly to the Spring Boot application as system properties.
ENTRYPOINT ["java", \
            "-Dspring.datasource.url=${SPRING_DATASOURCE_URL}", \
            "-Dtaskmanager.app.jwtSecret=${TASKMANAGER_APP_JWTSECRET}", \
            "-Dtaskmanager.app.jwtExpirationMs=${TASKMANAGER_APP_JWTEXPIRATIONMS}", \
            "-jar", \
            "app.jar"]