ARG PGSQL_HOST=""
ARG PGSQL_USERNAME=""
ARG PGSQL_PASSWORD=""

FROM openjdk:21-jdk AS build
COPY pom.xml mvnw /app/
COPY src /app/src/
COPY .mvn /app/.mvn/
WORKDIR /app
ENV PGSQL_HOST=${PGSQL_HOST}
ENV PGSQL_USERNAME=${PGSQL_USERNAME}
ENV PGSQL_PASSWORD=${PGSQL_PASSWORD}
RUN ./mvnw package

FROM ubuntu/jre:21-24.04_stable
LABEL authors="meugen"
EXPOSE 8080
COPY --from=build /app/target/masterdetails-0.0.1-SNAPSHOT.jar /app/
WORKDIR /app
ENTRYPOINT ["java", "-jar", "masterdetails-0.0.1-SNAPSHOT.jar"]