FROM eclipse-temurin:25-jdk-alpine AS build
COPY pom.xml mvnw /app/
COPY src /app/src/
COPY .mvn /app/.mvn/
WORKDIR /app
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:25-jre-alpine
LABEL authors="meugen"
COPY --from=build /app/target/masterdetails-0.0.1-SNAPSHOT.jar /app/
WORKDIR /app
ENTRYPOINT ["java", "-jar", "masterdetails-0.0.1-SNAPSHOT.jar"]
