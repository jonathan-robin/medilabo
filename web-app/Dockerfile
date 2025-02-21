# Étape 1 : Construction de l'application avec Maven
FROM maven:3.8.5-openjdk-11-slim AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier pom.xml pour télécharger les dépendances
COPY pom.xml .

# Télécharger les dépendances Maven et les mettre en cache
RUN mvn dependency:go-offline

# Copier tout le code source
COPY src ./src

# Compiler et empaqueter l'application
RUN mvn clean package -DskipTests

# Étape 2 : Utilisation de l'image JRE pour exécuter l'application
FROM openjdk:11-jre-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR généré dans l'image
COPY --from=build /app/target/web-app.jar /app/web-app.jar

# Exposer le port de l'application
EXPOSE 8082

# Lancer l'application
ENTRYPOINT ["java", "-jar", "/app/web-app.jar"]
