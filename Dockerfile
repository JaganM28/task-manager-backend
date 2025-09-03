FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app

ARG DB_URL
ARG JWT_SECRET
ARG JWT_EXPIRATION

ENV SPRING_DATASOURCE_URL=${DB_URL}
ENV TASKMANAGER_APP_JWTSECRET=${JWT_SECRET}
ENV TASKMANAGER_APP_JWTEXPIRATIONMS=${JWT_EXPIRATION}

COPY --from=build /app/target/task-manager-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]