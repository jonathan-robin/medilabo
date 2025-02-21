# Étape 1 : Construction de l'application
FROM maven:3.8.5-openjdk-11-slim AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Étape 2 : Exécution de l'application
FROM openjdk:11-jre-slim

WORKDIR /app

COPY --from=build /app/target/auth.jar /app/auth.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "auth.jar"]
