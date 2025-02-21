# Utilisation de l'image officielle de Java
FROM maven:3.8.5-openjdk-11-slim AS build
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]