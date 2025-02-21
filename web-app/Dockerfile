FROM openjdk:17-alpine
LABEL application-name="web-app"
VOLUME [ "/main-app" ]
ADD target/web-app-1.0.0.jar web-app.jar
EXPOSE 8082
ENTRYPOINT [ "java","-jar","/web-app.jar" ]