FROM openjdk:21-jdk AS build
COPY pom.xml mvnw /app/
COPY src /app/src/
COPY .mvn /app/.mvn/
WORKDIR /app
RUN ./mvnw package -DskipTests

FROM ubuntu/jre:21-24.04_stable
LABEL authors="meugen"
EXPOSE 8080
COPY --from=build /app/target/masterdetails-0.0.1-SNAPSHOT.jar /app/
WORKDIR /app
ENTRYPOINT ["java", "-jar", "masterdetails-0.0.1-SNAPSHOT.jar"]