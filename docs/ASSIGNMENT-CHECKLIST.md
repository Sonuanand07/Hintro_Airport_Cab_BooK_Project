# Backend Engineer Assignment Checklist

## Problem Statement
- Smart airport ride pooling backend implemented in Java + Spring Boot.

## Functional Requirements
- Group passengers into shared cabs: implemented in `src/main/java/com/hintro/airportpooling/service/MatchingService.java`.
- Respect luggage and seat constraints: implemented in `src/main/java/com/hintro/airportpooling/service/MatchingService.java`.
- Minimize total travel deviation: implemented in `src/main/java/com/hintro/airportpooling/service/RoutePlanner.java`.
- Ensure no passenger exceeds detour tolerance: implemented in `src/main/java/com/hintro/airportpooling/service/RoutePlanner.java`.
- Handle real-time cancellations: implemented in `src/main/java/com/hintro/airportpooling/service/CancellationService.java`.
- Support 10,000 concurrent users: architecture supports horizontal scaling (stateless API + lock-safe DB workflow).
- Handle 100 requests per second: configurable matching, indexes, and `docs/loadtest/k6-pricing-rps100.js`.
- Maintain latency under 300ms: measured target via k6 threshold (`p(95)<300`) in load script.

## Expected Deliverables
- DSA approach + complexity: documented in `README.md`.
- Low Level Design + patterns: documented in `README.md`.
- High Level Architecture diagram: documented in `README.md`.
- Concurrency strategy: implemented with JPA pessimistic locks + optimistic versions, documented in `README.md`.
- DB schema + indexing strategy: implemented in `src/main/resources/db/migration/V1__init.sql`.
- Dynamic pricing formula: implemented in `src/main/java/com/hintro/airportpooling/service/PricingService.java`, documented in `README.md`.

## Mandatory Implementation Requirements
- Working backend code: implemented.
- Runnable locally: implemented using Docker Compose (`docker-compose.yml`).
- All required APIs implemented: implemented under `src/main/java/com/hintro/airportpooling/controller`.
- Concurrency demonstrated in code: implemented in repositories/entities/services.
- DB schema implemented with migrations: implemented in Flyway SQL migrations.

## Submission Requirements
- Detailed README: `README.md`.
- API docs: Swagger/OpenAPI + Postman collection.
Postman file: `docs/postman/Smart-Airport-Ride-Pooling.postman_collection.json`.
- Tech stack + assumptions: included in README.
- Sample test data: `src/main/resources/db/migration/V2__sample_data.sql`.
- Algorithm complexity: included in README.

## Additional Artifacts
- Load test: `docs/loadtest/k6-pricing-rps100.js`.
- Docker helper scripts:
`scripts/run-docker.ps1`
`scripts/stop-docker.ps1`
