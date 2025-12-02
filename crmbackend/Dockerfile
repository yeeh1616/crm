# Builder
FROM maven:3.9.8-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Application
FROM eclipse-temurin:17

WORKDIR /app
COPY --from=build /app/target/*.jar application.jar

ENTRYPOINT ["java","-jar","/app/application.jar"]