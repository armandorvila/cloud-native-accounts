FROM anapsix/alpine-java

MAINTAINER armando.ramirez.vila@gmail.com

COPY discovery-service/target/*.jar /app.jar

ENV LC_ALL=C

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]