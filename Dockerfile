FROM eclipse-temurin:25-jdk-alpine AS build

ARG PGSQL_HOSTNAME
ARG PGSQL_PORT
ARG PGSQL_DATABASE
ARG PGSQL_USERNAME
ARG PGSQL_PASSWORD
ARG AWS_PGSQL_SECRET
ARG REDIS_HOSTNAME
ARG REDIS_PORT
ARG REDIS_USE_SSL

COPY pom.xml mvnw /app/
COPY src /app/src/
COPY .mvn /app/.mvn/
WORKDIR /app
RUN ./mvnw package

FROM eclipse-temurin:25-jre-alpine
LABEL authors="meugen"
COPY --from=build /app/target/masterdetails-0.0.1-SNAPSHOT.jar /app/
WORKDIR /app
ENTRYPOINT ["java", "-jar", "masterdetails-0.0.1-SNAPSHOT.jar"]
