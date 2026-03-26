# Temperature Proxy API

Spring Boot 3 service that fetches current weather data from Open-Meteo, normalizes the response, and serves it through a small HTTP API.

## What Is Actually Implemented

- Hexagonal-style package split: `interfaces`, `application`, `domain`, `infrastructure`
- Immutable domain value objects for `Location`, `WeatherData`, and `WeatherMetrics`
- `OpenMeteoAdapter` as the external API adapter
- In-memory caching through a `WeatherRepository` backed by Caffeine
- Resilience4j retry, circuit breaker, and time limiter around the upstream call
- Actuator health and Prometheus metrics endpoints
- Unit and integration tests with JUnit 5, AssertJ, and WireMock

This repository deliberately avoids claiming patterns that are not present in code. The design favors small, explicit abstractions over pattern inflation.

## API

### `GET /v1/temperature`

Query parameters:

- `lat`: latitude in the inclusive range `[-90, 90]`
- `lon`: longitude in the inclusive range `[-180, 180]`

Example response:

```json
{
  "location": {
    "lat": 52.52,
    "lon": 13.41
  },
  "current": {
    "temperatureC": 15.5,
    "windSpeedKmh": 12.3
  },
  "source": "open-meteo",
  "retrievedAt": "2026-01-11T10:12:54Z"
}
```

Operational endpoints:

- `GET /actuator/health`
- `GET /actuator/prometheus`
- `GET /actuator/metrics`

## Build And Quality Gates

Prerequisites:

- Java 17+
- Maven 3.8.6+

Common commands:

```bash
mvn clean verify
```

```bash
mvn checkstyle:check spotbugs:check
```

```bash
mvn org.owasp:dependency-check-maven:check
```

The repository-owned quality toolchain is intentionally based on open source Maven plugins:

- JaCoCo for coverage
- Checkstyle for style and consistency checks
- SpotBugs for bytecode-level bug detection
- OWASP Dependency-Check for dependency vulnerability scanning
- Maven Enforcer for Java and Maven version guardrails

This replaces cloud-only gates such as SonarCloud and Codecov so the repo can be built and checked independently.

## CI/CD

Two GitHub Actions workflows are provided:

- `ci.yml`: runs verification, tests, coverage, Checkstyle, SpotBugs, and OWASP Dependency-Check on pushes and pull requests
- `release.yml`: runs on version tags matching `v*`, builds the production jar, creates a release bundle, and publishes a GitHub Release with downloadable assets

To publish a release:

```bash
git tag v1.0.1
git push origin v1.0.1
```

The release workflow uploads:

- the executable Spring Boot jar
- a `.tar.gz` release bundle containing the jar, `README.md`, `Dockerfile`, and Kubernetes manifests

## Project Layout

```text
src/main/java/com/temperatureproxy
├── application
├── domain
├── infrastructure
└── interfaces
```

## Notes For Publication

Before making the repository public, confirm the non-code publication items that cannot be inferred safely here:

- choose and add a license
- confirm repository description/topics
- confirm package/release naming policy
- decide whether Docker image publishing is required in addition to GitHub Releases
