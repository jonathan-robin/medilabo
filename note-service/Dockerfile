FROM openjdk:17-alpine
RUN apk add --no-cache curl
LABEL application-name="note-service"
VOLUME [ "/main-app" ]
ADD target/note-service-0.0.1-SNAPSHOT.jar note-service.jar
EXPOSE 8081
ENTRYPOINT [ "java","-jar","/note-service.jar" ]
