FROM openjdk:25-ea-21-slim-bookworm

WORKDIR /opt/app

COPY target/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]