FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src


RUN mvn spotless:apply
RUN mvn clean install

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/interconnecting-flights-ryanair-0.0.1.jar ./app.jar

# Expose port 8080
EXPOSE 8080

# Command to run the Java application
CMD ["java", "-jar", "app.jar"]