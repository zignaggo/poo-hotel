FROM maven:3.9-amazoncorretto-21-alpine AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre-alpine-3.21

ENV DB_URL="jdbc:postgresql://postgres:5432/hotel"
ENV DB_USER="postgres"
ENV DB_PASSWORD="postgres"

COPY --from=build app/target/hotel-1.0-SNAPSHOT.jar app.jar
COPY ./src/migrations/001_anya_forger.sql /src/migrations/001_anya_forger.sql
COPY logo.txt .

EXPOSE 5432

CMD ["java", "-jar", "app.jar"]