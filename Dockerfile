FROM openjdk:8-jdk-alpine

# https://hub.docker.com/u/samuelsantana
LABEL maintainer="samuel.santana"

VOLUME /tmp

# Refer to Maven build -> finalName
COPY target/transferencia-consumidor-kafka-0.0.1-SNAPSHOT.jar app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]