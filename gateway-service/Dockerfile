# Étape 1 : Construction de l'application
FROM maven:3.8.5-openjdk-11-slim AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Étape 2 : Exécution de l'application
FROM openjdk:17-slim

WORKDIR /app

COPY --from=build /app/target/gateway-service.jar /app/gateway-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "gateway-service.jar"]
