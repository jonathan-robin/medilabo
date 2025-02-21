FROM openjdk:17-alpine
RUN apk add --no-cache curl
LABEL application-name="gateway-service"
VOLUME [ "/main-app" ]
ADD target/gateway-service-3.2.1.jar gateway-service.jar
EXPOSE 8080
ENTRYPOINT [ "java","-jar","/gateway-service.jar" ]
