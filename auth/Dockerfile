FROM openjdk:17-alpine
RUN apk add --no-cache curl
LABEL application-name="auth"
VOLUME [ "/main-app" ]
ADD target/auth-0.0.1-SNAPSHOT.jar auth.jar
EXPOSE 8084
ENTRYPOINT [ "java","-jar","/auth.jar" ]