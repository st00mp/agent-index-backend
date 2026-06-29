# AgentIndex — Backend

Registry of agent templates: an author defines instruction templates with
placeholders, a client customizes them through a wizard, and the service
assembles the final spec by substituting the values.

REST API built with Spring Boot. First backend service of the AgentIndex project.

## Stack

- Java 21
- Spring Boot 4.1
- Spring Data JPA · H2 (dev)
- Maven

## Run locally

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

Check it's running:

```bash
curl http://localhost:8080/actuator/health
# {"status":"UP"}
```

## Build & tests

```bash
mvn verify
```