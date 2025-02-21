FROM openjdk:17-alpine
RUN apk add --no-cache curl
LABEL application-name="patient-service"
VOLUME [ "/main-app" ]
ADD target/patient-service-0.0.1-SNAPSHOT.jar patient-service.jar
EXPOSE 8081
ENTRYPOINT [ "java","-jar","/patient-service.jar" ]
