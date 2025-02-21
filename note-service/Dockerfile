# Étape 1 : Construction de l'application
FROM maven:3.8.5-openjdk-11-slim AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Étape 2 : Exécution de l'application
FROM openjdk:17-slim


WORKDIR /app

COPY --from=build /app/target/note-service.jar /app/note-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "note-service.jar"]
