# Gateway Service

API Gateway microservice for the me2u transit backend. Routes requests to downstream services with JWT authentication, circuit breaker resilience, and error handling.

## What It Does

- Routes HTTP requests to microservices (auth service, etc.)
- Validates JWT tokens on protected routes
- Implements circuit breaker pattern to handle service failures
- Provides consistent error responses with request tracing
- Supports multi-language error messages

## Tech Stack

- **Java 24** with Spring Boot 3.5.0
- **Spring Cloud Gateway** with reactive WebFlux
- **Resilience4j** for circuit breaker pattern
- **Nimbus JOSE + JWT** for token validation
- **Gradle** for build management

## Getting Started

### Prerequisites

- Java 24+
- Docker & Docker Compose (for local services)
- Git

### Setup Development Environment

1. **Create a feature branch** from `master`:
   ```bash
   git checkout master
   git pull origin master
   git checkout -b feature/your-feature-name
   ```

2. **Configure environment variables** (create `.env` file or set in IDE):
   ```bash
   PORT=8090
   JWT_SECRET=your-secret-key-here
   AUTH_SERVICE_URI=http://localhost:8085
   ```

3. **Build the project**:
   ```bash
   ./gradlew clean build
   ```

4. **Run the application**:
   ```bash
   ./gradlew bootRun
   ```
   Gateway runs on `http://localhost:8090`

## Project Structure

```
src/main/java/com/me2u/gateway/
├── controller/          # REST endpoints & global exception handling
├── filter/              # JWT authentication & gateway filters
├── dto/                 # Data transfer objects
└── util/                # JWT & utility helpers

src/main/resources/
├── application.yaml     # Gateway routes, CORS, circuit breaker config
└── messages*.properties # Error message localization
```

## Key Source Files

| File | Purpose |
|------|---------|
| `GatewayApplication.java` | Spring Boot entry point |
| `AuthenticationFilter.java` | JWT token validation filter |
| `JwtUtil.java` | JWT token parsing & verification |
| `FallbackController.java` | Circuit breaker fallback endpoints |
| `GatewayControllerAdvice.java` | Global exception handler |
| `application.yaml` | Routes, CORS, circuit breaker configuration |

## Configuration

**Key Properties** in `application.yaml`:

- **Server Port**: `8090` (override with `PORT` env var)
- **JWT Secret**: Configurable via `JWT_SECRET` env var
- **Auth Service URI**: `http://localhost:8085` (configurable)
- **CORS**: Allowed origins: `https://localhost:3000`, `https://admin.admindomain.com`

**Protected Routes** (require valid JWT):
- `POST /api/v1/auth/logout`

**Open Routes** (no authentication):
- `POST /api/v1/auth/magic-link`
- `POST /api/v1/auth/magic-link/verify`
- `POST /api/v1/auth/refresh`

## Making Changes

1. **Create a feature branch**:
   ```bash
   git checkout -b feature/my-change
   ```

2. **Make your changes**:
   - Add/modify Java files in `src/main/java/`
   - Update routes in `application.yaml` if needed
   - Add tests in `src/test/java/`

3. **Test locally**:
   ```bash
   ./gradlew test
   ./gradlew bootRun
   ```

4. **Commit and push**:
   ```bash
   git add .
   git commit -m "feat: describe your changes"
   git push origin feature/my-change
   ```

5. **Create a Pull Request** to `master` branch

## Build & Deployment

**Build the application**:
```bash
./gradlew build
```

**Create Docker image**:
```bash
./gradlew bootBuildImage
```

**Run tests**:
```bash
./gradlew test
```

**Clean build artifacts**:
```bash
./gradlew clean
```

## Common Commands

```bash
./gradlew bootRun            # Run application locally
./gradlew build              # Build & run tests
./gradlew test               # Run tests only
./gradlew dependencies       # Show dependency tree
./gradlew tasks              # List all gradle tasks
```

## Troubleshooting

**Port already in use**: Change `PORT` env var or stop other services
```bash
PORT=8091 ./gradlew bootRun
```

**JWT validation fails**: Ensure `JWT_SECRET` env var matches the token issuer

**Auth service unreachable**: Verify downstream service is running and `AUTH_SERVICE_URI` is correct

**Build fails**: Clear gradle cache
```bash
./gradlew clean
./gradlew build
```

## Additional Resources

- [Spring Cloud Gateway Docs](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)
- [Spring Boot 3.5 Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Resilience4j Circuit Breaker](https://resilience4j.readme.io/docs/circuitbreaker)