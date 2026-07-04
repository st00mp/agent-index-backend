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
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Endpoints

**Templates**

- `POST /templates` — create an agent template (201 with `Location` header, 400 if validation fails)
- `GET /templates` — list all agent templates (empty array if none)
- `GET /templates/{id}` — get an agent template by ID (404 if not found)
- `PUT /templates/{id}` — update an agent template (400 if validation fails, 404 if not found)
- `DELETE /templates/{id}` — delete an agent template (204 on success, 404 if not found)

**Instances**

- `POST /templates/{templateId}/instances` — create an agent instance from a template
  (201 with `Location` header, 400 if `values` is missing or required field values are
  missing/blank, 404 if the template does not exist)

## Build & tests

```bash
./mvnw verify
```