# Product Catalog REST API

## Overview

My Product Catalog REST API is a backend application designed to manage a product catalog through a RESTful interface. It focuses on maintainable service-layer logic, data validation, persistence, and reliable testing.


## Tech Stack

| Layer | Technology |
| --- | --- |
| Language | Java |
| Framework | Spring Boot |
| Database | PostgreSQL |
| Build | Maven / Maven Wrapper |
| Testing | JUnit 5, Mockito |
| Packaging | Docker, Docker Compose |

## Key Features

- Product management with create, read, update, and delete (CRUD) operations
- Input validation and product data integrity checks
- SQL-backed product persistence
- Unique product SKU handling
- Consistent API errors through exception handling
- Unit testing with JUnit and Mockito


### Run with Docker Compose

Once `Dockerfile`, `compose.yaml`, and the database environment variables are configured:

```bash
docker compose up --build -d
```

Check container status and logs:

```bash
docker compose ps
docker compose logs -f app
```

Stop services:

```bash
docker compose down
```

## Testing

Run the test suite:

```bash
./mvnw test
```