FROM anapsix/alpine-java:8

MAINTAINER armando.ramirez.vila@gmail.com

COPY transactions-service/target/*.jar /app.jar

ENV LC_ALL=C

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]