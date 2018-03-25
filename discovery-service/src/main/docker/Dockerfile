FROM openjdk:8-jdk-alpine

MAINTAINER armando.ramirez.vila@gmail.com

COPY discovery-service/target/*.jar /app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]