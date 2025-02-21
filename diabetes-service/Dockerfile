FROM openjdk:17-alpine
RUN apk add --no-cache curl
LABEL application-name="diabetes-service"
VOLUME [ "/main-app" ]
ADD target/diabetes-service-0.0.1-SNAPSHOT.jar diabetes-service.jar
EXPOSE 8085
ENTRYPOINT [ "java","-jar","/diabetes-service.jar" ]

