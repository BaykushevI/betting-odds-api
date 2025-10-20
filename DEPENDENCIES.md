# Project Dependencies

## Core Framework
- **Spring Boot**: 3.5.6
- **Java**: 21
- **Maven**: 3.9.x

## Spring Boot Starters
- **spring-boot-starter-web**: 3.5.6
  - Provides REST API functionality
  - Includes embedded Tomcat server
  
- **spring-boot-starter-data-jpa**: 3.5.6
  - JPA/Hibernate for database operations
  - Repository pattern support

- **spring-boot-starter-validation**: 3.5.6
  - Jakarta Bean Validation (JSR-380)
  - Automatic request validation

- **spring-boot-starter-actuator**: 3.5.6
  - Production monitoring endpoints
  - Health checks and metrics

- **spring-boot-starter-test**: 3.5.6
  - JUnit 5, Mockito, AssertJ
  - Spring Test support

## Database
- **PostgreSQL Driver**: 42.7.7 (managed by Spring Boot)
  - JDBC driver for PostgreSQL

## Documentation
- **Springdoc OpenAPI**: 2.8.8
  - Swagger UI integration
  - OpenAPI 3.0 specification generation

## Utilities
- **Lombok**: 1.18.34 (managed by Spring Boot)
  - Reduces boilerplate code
  - @Data, @Builder, @RequiredArgsConstructor

## Version Management
All Spring Boot dependencies versions are managed by `spring-boot-starter-parent:3.5.6`

## Dependency Tree
mvn dependency:tree


## Update Dependencies
mvn versions:display-dependency-updates
