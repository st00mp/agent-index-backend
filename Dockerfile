FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom first so dependencies are cached until it changes
COPY pom.xml .
RUN mvn --batch-mode dependency:go-offline

COPY src ./src
RUN mvn --batch-mode package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Run as an unprivileged user: a compromised app must not mean root in the container
RUN useradd -r spring
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
