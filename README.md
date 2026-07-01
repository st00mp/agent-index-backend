# AgentIndex — Backend

Registry of agent templates: an author defines instruction templates with
placeholders, a client customizes them through a wizard, and the service
assembles the final spec by substituting the values.

REST API built with Spring Boot. First backend service of the AgentIndex project.

## Stack

- Java 21
- Spring Boot 4.1
- Spring Data JPA · H2 in-memory (dev/test)
- Maven

## Run locally

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

Check it's running:

```bash
curl http://localhost:8080/actuator/health
# {"status":"UP"}
```

## API

The API is documented and browsable via Swagger UI once the app is running:

- Swagger UI: `http://localhost:8080/swagger-ui.html`

### Endpoints

- `POST /templates` — create an agent template

## Build & tests

```bash
./mvnw verify
```