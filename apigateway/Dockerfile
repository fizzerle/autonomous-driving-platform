FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/apigateway-0.0.2-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]