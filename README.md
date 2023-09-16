# Kopring RESTAPI boilerplate

![github action status](https://github.com/yangga/kopring-restapi-boilerplate/actions/workflows/test.yml/badge.svg)

## Description <!-- omit in toc -->

Kopring(Kotlin + Spring Boot) REST API boilerplate with coroutines

## Table of Contents <!-- omit in toc -->

- [Requirements](#requirements)
- [Features](#features)
- [Quick run](#quick-run)
- [Links](#links)
- [Tests](#tests)

## Requirements

### Required

#### JAVA

- version: 17

### Optional

#### [SDKMAN](https://sdkman.io)

If your java version it not 17. You can install local java by sdkman.
```bash
// install java specific version in .sdk.manrc has
sdk env install

// change java versrion
sdk env
```

## Features

- [x] Database.
  - [r2dbc](https://r2dbc.io)
  - [r2dbc-pool](https://github.com/r2dbc/r2dbc-pool)
  - [jooq](https://www.jooq.org)
- [x] Seeding.
- [x] Sign in and sign up via email.
  - [spring-security](https://spring.io/projects/spring-security)
  - jwt bearer token
- [x] User roles.
- [x] Hexagonal Architecture
- [x] OpenAPI (Swagger).
- [x] [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator)
- [x] E2E and units tests.
  - [kotest](https://kotest.io)
  - [mockk](https://mockk.io)
  - [Testcontainers](https://testcontainers.com)
- [x] Reporting
  - [sentry](https://sentry.io)
- [x] Validation
  - ResponseValidationAspect
- [x] Logging
  - [log4j2](https://logging.apache.org/log4j/2.x)
  - Request & Response logging
- [x] Git hooks
- [x] CI (Github Actions).

## Quick run

Clone this repository

```bash
git clone --depth 1 https://github.com/yangga/kopring-restapi-boilerplate.git my-app
cd my-app/
```

Start infra docker containers

```bash
docker-compose up -d
```

Start an application

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

## Links

- Swagger: <http://localhost:8080>

## Tests

```bash
./gradlew test
````
