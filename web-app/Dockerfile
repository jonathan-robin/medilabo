# Étape 1 : Utilisation de l'image Maven pour construire le projet
FROM openjdk:17-slim AS build

WORKDIR /app

# Copier le fichier pom.xml et télécharger les dépendances Maven
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier tout le code source du projet
COPY src ./src

# Construire l'application et générer le fichier .jar
RUN mvn clean package -DskipTests

# Étape 2 : Utilisation d'une image JRE pour exécuter l'application
FROM openjdk:17-slim

WORKDIR /app

# Copier le fichier .jar généré depuis l'étape précédente
COPY --from=build /app/target/web-app-*.jar web-app.jar

# Exposer le port 8082
EXPOSE 8082

# Démarrer l'application Spring Boot
CMD ["java", "-jar", "web-app.jar"]
