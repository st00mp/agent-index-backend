# AgentIndex — Backend

Registry of agent templates: an author defines instruction templates with
placeholders, a client customizes them through a wizard, and the service
assembles the final spec by substituting the values.

REST API built with Spring Boot. First backend service of the AgentIndex project.

## Stack

- Java 21
- Spring Boot 4.1
- Spring Data JPA · H2 in-memory (dev/test) · PostgreSQL (Docker)
- Maven · Docker

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

## Run with Docker

No Java or Maven required — only Docker:

```bash
docker compose up --build
```

Builds the image (multi-stage) and starts the app with a PostgreSQL database
on `http://localhost:8080`.

## Quickstart

The full chain in three calls: create a template, instantiate it with values,
then assemble the output. The `values` keys must match the template's field `key`s.

```bash
# 1. Create a template (returns 201 with a Location header, e.g. /templates/1)
curl -i -X POST http://localhost:8080/templates \
  -H 'Content-Type: application/json' \
  -d '{
        "name": "Quote Agent",
        "category": "Sales",
        "description": "Generates a personalised quote.",
        "instructions": "You are a quote assistant for {{company_name}}. Bill at {{hourly_rate}} €/h.",
        "fields": [
          {"key": "company_name", "label": "Company name", "type": "text",   "help": ""},
          {"key": "hourly_rate",  "label": "Hourly rate",  "type": "number", "help": "e.g. 65"}
        ],
        "version": "1.0.0"
      }'

# 2. Create an instance from that template (use the id from step 1)
curl -i -X POST http://localhost:8080/templates/1/instances \
  -H 'Content-Type: application/json' \
  -d '{"values": {"company_name": "Acme", "hourly_rate": "65"}}'

# 3. Assemble the output (use the instance id from step 2)
curl http://localhost:8080/instances/1/output
# You are a quote assistant for Acme. Bill at 65 €/h.
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
- `GET /instances/{id}` — get an agent instance by ID, including its template
  reference and stored field values (404 if not found)
- `GET /instances/{id}/output` — assemble and return the instance's instructions as
  plain text, substituting each `{{placeholder}}` with its stored value (200 `text/plain`,
  404 if the instance does not exist, 422 if one or more placeholders cannot be resolved)

## Build & tests

```bash
./mvnw verify
```