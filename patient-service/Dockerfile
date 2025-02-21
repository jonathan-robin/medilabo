# Étape 1 : Construction de l'application
FROM maven:3.8.5-openjdk-11-slim AS build


# Répertoire de travail pour la construction
WORKDIR /app

# Copie des fichiers pom.xml et du code source
COPY pom.xml .
COPY src ./src

# Téléchargement des dépendances et compilation du projet
RUN mvn clean package -DskipTests

# Étape 2 : Exécution de l'application
FROM openjdk:11-jre-slim


# Répertoire de travail pour l'exécution
WORKDIR /app

# Copier le fichier JAR généré
COPY --from=build /app/target/patient-service.jar /app/patient-service.jar

# Exposer le port d'écoute de l'application
EXPOSE 8081

# Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "patient-service.jar"]
