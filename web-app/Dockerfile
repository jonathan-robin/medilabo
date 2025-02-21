# Étape 1 : Utilisation de l'image Maven pour construire le projet
FROM maven:3.8.5-openjdk-11-slim AS build

WORKDIR /app

# Étape 2 : Installer Maven
RUN apt-get update && \
    apt-get install -y wget && \
    wget https://downloads.apache.org/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz && \
    tar -xvf apache-maven-3.8.6-bin.tar.gz && \
    mv apache-maven-3.8.6 /opt/maven && \
    ln -s /opt/maven/bin/mvn /usr/bin/mvn

# Étape 3 : Définir la variable d'environnement MAVEN_HOME
ENV MAVEN_HOME=/opt/maven
ENV PATH=${MAVEN_HOME}/bin:${PATH}

# Étape 4 : Copier le fichier pom.xml
WORKDIR /app
COPY pom.xml .

# Étape 5 : Télécharger les dépendances Maven
RUN mvn dependency:go-offline

# Étape 6 : Copier le code source du projet
COPY src /app/src

# Étape 7 : Construire l'application
RUN mvn clean package -DskipTests

# Étape 8 : Utiliser une image JRE pour l'exécution
FROM openjdk:11-jre-slim

WORKDIR /app

# Copier le fichier jar généré depuis la première étape
COPY --from=0 /app/target/web-app.jar /app/web-app.jar

# Exposer le port de l'application
EXPOSE 8082

# Commande pour démarrer l'application
CMD ["java", "-jar", "/app/web-app.jar"]
