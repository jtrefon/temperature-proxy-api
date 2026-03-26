# Temperature Proxy API

A state-of-the-art REST API that fetches current temperature data from Open-Meteo and returns a normalized response. Built with enterprise-grade architecture patterns and production-ready features.

## Architecture Highlights

### Design Patterns Implemented
- **Proxy Pattern**: Caching layer with `CachingWeatherProxy`
- **Strategy Pattern**: Pluggable cache algorithms via `CacheStrategy`
- **Factory Pattern**: Client creation with `WeatherClientFactory`
- **Circuit Breaker**: Resilience with Resilience4j
- **Observer Pattern**: Event-driven metrics collection
- **Command Pattern**: Request encapsulation
- **Builder Pattern**: Immutable object construction
- **Decorator Pattern**: Cross-cutting concerns (logging, validation)
- **Chain of Responsibility**: Validation pipeline
- **Repository Pattern**: Data access abstraction
- **Adapter Pattern**: External API integration
- **Composite Pattern**: Health check aggregation

## Technology Stack

- **Java 17** with Spring Boot 3.x
- **Spring WebFlux** for reactive programming
- **Caffeine Cache** for in-memory caching (no Redis)
- **Resilience4j** for circuit breaker and retry patterns
- **Micrometer + Prometheus** for metrics
- **JUnit 5 + AssertJ** for testing
- **WireMock** for integration testing
- **Kubernetes** for deployment

## API Endpoints

### GET /v1/temperature
Fetches current temperature for specified coordinates.

**Query Parameters:**
- `lat` (required, float): Latitude (-90 to 90)
- `lon` (required, float): Longitude (-180 to 180)

**Example Response:**
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

### GET /actuator/health
Health check endpoint with liveness and readiness probes.

### GET /actuator/metrics
Prometheus-compatible metrics.

## Building and Running

### Prerequisites
- Java 17+
- Maven 3.8+

### Build
```bash
mvn clean package
```

### Run
```bash
mvn spring-boot:run
```

### Test
```bash
mvn test
```

With coverage:
```bash
mvn test jacoco:report
```

### Docker Build
```bash
docker build -t temperature-proxy:v1.0.0 .
```

## Configuration

### Application Properties
```yaml
weather:
  open-meteo:
    base-url: https://api.open-meteo.com
  cache:
    ttl: 60s
    max-size: 1000

resilience4j:
  circuitbreaker:
    instances:
      openMeteo:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
```

## Kubernetes Deployment

```bash
kubectl apply -f k8s/deployment.yaml
```

Features:
- 3 replicas minimum with HPA (up to 10)
- Resource limits and requests
- Liveness, readiness, and startup probes
- Prometheus metrics scraping

## Testing Strategy

### Unit Tests
- Domain model validation
- Chain of Responsibility pattern
- Cache behavior

### Integration Tests
- WireMock for external API simulation
- Full request/response flow
- Error handling scenarios

### Coverage
- 90%+ line coverage enforced by JaCoCo
- 90%+ branch coverage for domain layer

## Project Structure

```
com.temperatureproxy/
├── domain/
│   ├── model/          # Immutable value objects
│   ├── repository/       # Repository interfaces
│   └── service/          # Validation chain
├── application/
│   ├── dto/             # Request/Response DTOs
│   └── service/         # Use cases
├── infrastructure/
│   ├── client/          # Open-Meteo adapter
│   ├── repository/      # Caffeine implementation
│   └── config/          # Spring configurations
└── interfaces/
    ├── rest/            # REST controllers
    └── exception/       # Global exception handler
```

## Quality Metrics

- **Performance**: P99 < 800ms (cache miss), P99 < 20ms (cache hit)
- **Availability**: 99.9% with circuit breaker
- **Coverage**: > 90% line and branch coverage
- **Memory**: < 400MB heap under load

## Contact

For questions or issues, please contact the development team.
