# eCommerce Spring Boot (converted)

Automated Quarkus → Spring Boot conversion on 2025-08-24T19:43:57.

## Highlights
- Spring Boot skeleton (pom.xml, `Application`)
- JAX-RS → Spring MVC annotations (best-effort)
- Basic Quarkus config key mapping
- Actuator + springdoc for OpenAPI

## Build & Run
mvn spring-boot:run

## Manual TODOs
- Review endpoints & DTOs
- Replace Panache with Spring Data JPA if present
- Add Spring Security if JWT/OIDC used
- Adjust DB settings in `application.yml`
