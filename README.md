# IMS — Inventory Management System

A full-stack inventory management system built with Spring Boot and Angular.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Environment Configuration](#environment-configuration)
- [Running the Application](#running-the-application)
  - [Production Stack](#production-stack)
  - [Test Stack](#test-stack)
  - [Local Development](#local-development)
- [Profiles](#profiles)
- [API Documentation](#api-documentation)
- [Authentication](#authentication)
- [Project Structure](#project-structure)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend language | Java 21 |
| Backend framework | Spring Boot 3.3.5 |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| Messaging | Apache Kafka (Outbox pattern) |
| Security | Spring Security + JWT (JJWT 0.12.6) |
| Build tool | Maven |
| Frontend framework | Angular 18 |
| UI components | Angular Material 18 |
| Containerisation | Docker + Docker Compose |

---

## Architecture

The backend follows a **hexagonal (ports & adapters) architecture** split into Maven modules:

```
ims-domain          →  Domain models, value objects, repository ports
ims-application     →  Use cases, commands, queries, inbound ports
ims-infrastructure  →  JPA entities, Flyway migrations, Kafka, adapters
ims-api             →  REST controllers, DTOs, security (JWT filter)
ims-bootstrap       →  Spring Boot entry point, application.yml
```

---

## Features

- **Items** — register, update, delete inventory items with SKU, price, and storage location
- **Categories** — create and manage item categories dynamically
- **Storage** — view warehouse stock grouped by category
- **Markets** — create markets, allocate stock, track sales
- **Transactions** — full transaction history with stock deltas
- **Reports** — market and cross-market sales summaries
- **Authentication** — JWT-based login; all API routes are protected

---

## Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (or Docker Engine + Compose plugin)
- Java 21 and Maven — only needed for local development or manual builds

---

## Getting Started

```bash
# 1. Clone the repo
git clone <repo-url>
cd ims

# 2. Create your environment file
cp .env.example .env

# 3. Build and start
./build.sh
```

The `build.sh` script compiles the backend, builds the Docker image, and starts all containers.

---

## Environment Configuration

All configuration lives in `.env` (git-ignored). Use `.env.example` as the template:

```bash
cp .env.example .env
```

**Key variables:**

| Variable | Default | Description |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` | Active Spring profile (`prod` or `test`) |
| `POSTGRES_DB` | `imsdb` | Postgres database name |
| `POSTGRES_USER` | `ims` | Postgres user |
| `POSTGRES_PASSWORD` | — | Postgres password |
| `DB_HOST` | `postgres` | Database host (service name in Docker) |
| `DB_PORT` | `5432` | Database port |
| `DB_USERNAME` | `ims` | App database user |
| `DB_PASSWORD` | — | App database password |
| `KAFKA_BOOTSTRAP` | `localhost:9092` | Kafka broker address |
| `JWT_SECRET` | — | Base64-encoded signing key — **change in production** |
| `JWT_EXPIRATION_MS` | `86400000` | Token lifetime in ms (default: 24 h) |
| `APP_IMAGE` | `application:1.0.0` | Backend Docker image tag |
| `APP_PORT` | `8080` | Host port for the backend |
| `FRONTEND_PORT` | `80` | Host port for the frontend |

Test stack variables use the same `.env` file with a `TEST_` prefix:

| Variable | Default | Description |
|---|---|---|
| `TEST_SPRING_PROFILES_ACTIVE` | `test` | Spring profile for test stack |
| `TEST_POSTGRES_DB` | `imsdb_test` | Test database name |
| `TEST_DB_HOST_PORT` | `5433` | Host port for test Postgres |
| `TEST_APP_PORT` | `8081` | Host port for test backend |
| `TEST_FRONTEND_PORT` | `8082` | Host port for test frontend |

Generate a secure JWT secret:
```bash
openssl rand -base64 64
```

---

## Running the Application

### Production Stack

```bash
# Build the backend image (first time or after code changes)
cd backend && docker build -t application:1.0.0 . && cd ..

# Start all services
docker compose up -d

# Or use the build script (builds + starts in one step)
./build.sh
```

**Build script options:**

```bash
./build.sh                # default: skip tests, use Docker cache
./build.sh --with-tests   # run Maven tests during build
./build.sh --no-cache     # rebuild Docker images from scratch
./build.sh --clean        # wipe database volumes before starting
```

**Access:**

| Service | URL |
|---|---|
| Frontend | http://localhost |
| Backend API | http://localhost:8080/api/v1 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

**Stop:**
```bash
docker compose down        # stop containers, keep data
docker compose down -v     # stop containers and wipe volumes
```

---

### Test Stack

The test stack runs on separate ports and uses an isolated database (`imsdb_test`) so it never touches production data.

```bash
# Build the backend image if not already built
cd backend && docker build -t application:1.0.0 . && cd ..

# Start the test stack
docker compose -f docker-compose.test.yml up -d
```

**Access:**

| Service | URL |
|---|---|
| Frontend | http://localhost:8082 |
| Backend API | http://localhost:8081/api/v1 |
| Swagger UI | http://localhost:8081/swagger-ui.html |
| Postgres | `localhost:5433` |

**Stop:**
```bash
docker compose -f docker-compose.test.yml down
docker compose -f docker-compose.test.yml down -v   # also wipe test DB
```

---

### Local Development

**Backend:**
```bash
cd backend

# Start only the database
docker compose -f ../docker-compose.yml up -d postgres

# Run with test profile (points to imsdb_test)
mvn spring-boot:run -pl ims-bootstrap -Dspring-boot.run.profiles=test

# Or with prod profile (all env vars must be set)
mvn spring-boot:run -pl ims-bootstrap -Dspring-boot.run.profiles=prod
```

**Frontend:**
```bash
cd frontend/ims-frontend
npm install
ng serve   # http://localhost:4200
```

The Angular dev server proxies all `/api` requests to `http://localhost:8080` via `proxy.conf.json`.

---

## Profiles

| Profile | Database | Swagger | SQL logging | Flyway clean | Log level |
|---|---|---|---|---|---|
| *(none / base)* | `imsdb` | enabled | off | disabled | INFO / DEBUG |
| `test` | `imsdb_test` | enabled | on | allowed | INFO / DEBUG |
| `prod` | env vars (required) | disabled | off | disabled | WARN / INFO |

**Activate a profile:**

```bash
# JAR
java -Dspring.profiles.active=test -jar ims-bootstrap/target/ims-bootstrap-*.jar

# Maven
mvn spring-boot:run -pl ims-bootstrap -Dspring-boot.run.profiles=test

# Docker — Spring Boot reads SPRING_PROFILES_ACTIVE automatically
SPRING_PROFILES_ACTIVE=prod docker compose up
```

> In the `prod` profile all database and JWT environment variables are required with no fallback defaults.

---

## API Documentation

Swagger UI is available when running with the `test` profile or the base config:

```
http://localhost:8080/swagger-ui.html
```

Disabled in the `prod` profile.

**Endpoints:**

| Tag | Base path | Description |
|---|---|---|
| Auth | `/api/v1/auth` | Login and register |
| Items | `/api/v1/items` | Inventory item management |
| Categories | `/api/v1/categories` | Category management |
| Markets | `/api/v1/markets` | Market management |
| Market Stock | `/api/v1/markets/{id}/stock` | Allocate and track market stock |
| Storage | `/api/v1/storage` | Warehouse overview |
| Transactions | `/api/v1/transactions` | Transaction history |

---

## Authentication

All API routes (except `/api/v1/auth/**`) require a Bearer token.

**Login:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ims.com","password":"Admin123!"}'
```

**Use the token:**
```bash
curl http://localhost:8080/api/v1/items \
  -H "Authorization: Bearer <token>"
```

**Test user** (created automatically on first startup):

| Field | Value |
|---|---|
| Email | `admin@ims.com` |
| Password | `Admin123!` |

---

## Project Structure

```
ims/
├── backend/
│   ├── ims-domain/             # Domain models and port interfaces
│   ├── ims-application/        # Use cases, commands, queries
│   ├── ims-infrastructure/     # JPA, Flyway, Kafka, security adapters
│   ├── ims-api/                # REST controllers, DTOs, JWT filter
│   ├── ims-bootstrap/          # Entry point, application.yml, profiles
│   └── Dockerfile
├── frontend/
│   └── ims-frontend/           # Angular 18 application
│       └── src/app/
│           ├── core/           # Guards, interceptors, auth service
│           ├── features/       # auth, categories, dashboard, items,
│           │                   # markets, market-stock, reports,
│           │                   # storage, transactions
│           ├── layout/         # Shell with sidenav and toolbar
│           └── shared/         # Reusable components, pipes, models
├── docker-compose.yml          # Production stack
├── docker-compose.test.yml     # Test stack
├── .env.example                # Environment variable template
├── build.sh                    # One-command build and deploy script
└── .gitignore
```
