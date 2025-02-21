FROM openjdk:17-alpine
RUN apk add --no-cache curl
LABEL application-name="gateway-service"
VOLUME [ "/main-app" ]
ADD target/gateway-service-0.0.1-SNAPSHOT.jar gateway-service.jar
EXPOSE 8080
ENTRYPOINT [ "java","-jar","/gateway-service.jar" ]
