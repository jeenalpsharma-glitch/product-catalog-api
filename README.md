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


## Run with Docker

The application is containerized and published on Docker Hub.

**Docker Image:** [shar0477/product-catalog-api](https://hub.docker.com/r/shar0477/product-catalog-api)

### Prerequisites

- Docker Desktop with Docker Compose

### 3. Start the Application

```bash
docker compose up -d
```

Docker Compose automatically pulls the application image and starts the Spring Boot API alongside PostgreSQL.

The API runs at:

`http://localhost:8080`

### 4. Check Container Status and Logs

```bash
docker compose ps
docker compose logs -f app
```

### 5. Stop the Application

```bash
docker compose down
```

## Testing

```bash
./mvnw test
```