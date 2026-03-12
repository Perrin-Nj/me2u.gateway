FROM eclipse-temurin:24-jdk AS builder

WORKDIR /app

FROM eclipse-temurin:24-jre

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]