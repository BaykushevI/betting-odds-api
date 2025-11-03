# Betting Odds API

A production-ready RESTful API for managing betting odds for sports matches, built with Spring Boot and PostgreSQL.

## 📊 Project Status

```
Phase 1: Core CRUD API              ✅ COMPLETE
Phase 2: Production Features        ✅ COMPLETE (Logging System finished!)
Phase 3: Security & Testing         📋 NEXT (Unit & Integration Tests)
Phase 4: Performance & Reliability  📋 PLANNED  
Phase 5: Microservices & Gateway    🚀 FUTURE
Phase 6: Cloud Deployment           ☁️ ADVANCED
```

---

## 📖 Project Overview

This is a comprehensive **learning project** demonstrating professional backend development practices relevant to the **gambling industry**. It implements a complete CRUD API with proper architectural patterns, validation, error handling, business logic, and **enterprise-grade logging**.

### 🎯 Learning Goals

- ✅ Master Spring Boot ecosystem (Web, Data JPA, Validation, Security)
- ✅ Understand production-ready development practices
- ✅ Learn gambling industry domain concepts (odds, margins, probabilities)
- ✅ **Implement professional logging for compliance and debugging**
- ✅ Progress from monolith to microservices architecture
- ✅ Implement enterprise-level features (logging, security, monitoring)

---

## 🏗️ Architecture Evolution

### Current: Monolithic Architecture (Phase 1-2)

```
┌─────────────────────────────────────────────┐
│         CLIENT (Browser/Postman)             │
└──────────────────┬──────────────────────────┘
                   │ HTTP/JSON
                   ↓
┌─────────────────────────────────────────────┐
│          SPRING BOOT APPLICATION             │
│  ┌────────────────────────────────────────┐ │
│  │   CONTROLLER LAYER                     │ │
│  │   - REST endpoints                     │ │
│  │   - Request validation (@Valid)        │ │
│  │   - DTOs (Request/Response)            │ │
│  │   - HTTP request/response logging      │ │
│  └─────────────────┬──────────────────────┘ │
│                    ↓                         │
│  ┌────────────────────────────────────────┐ │
│  │   SERVICE LAYER                        │ │
│  │   - Business logic                     │ │
│  │   - Transaction management             │ │
│  │   - Calculations (margins, probabilities)│
│  │   - Security validation (SQL injection)│ │
│  │   - Audit logging (CREATE/UPDATE/DELETE)│
│  │   - Performance logging (execution time)│
│  └─────────────────┬──────────────────────┘ │
│                    ↓                         │
│  ┌────────────────────────────────────────┐ │
│  │   REPOSITORY LAYER                     │ │
│  │   - Spring Data JPA                    │ │
│  │   - Database queries                   │ │
│  └─────────────────┬──────────────────────┘ │
└────────────────────┼──────────────────────┘
                     ↓
         ┌──────────────────────┐
         │   PostgreSQL DB      │
         │   - betting_odds     │
         └──────────────────────┘
                     │
                     ↓
         ┌──────────────────────┐
         │   LOG FILES          │
         │   - application.log  │
         │   - errors.log       │
         │   - audit.log        │
         │   - performance.log  │
         │   - security.log     │
         └──────────────────────┘
```

### Future: Microservices Architecture (Phase 5+)

```
┌─────────────────────────────────────────────┐
│              CLIENT (Browser/Mobile)         │
└──────────────────┬──────────────────────────┘
                   │
                   ↓
         ┌──────────────────────┐
         │    API GATEWAY       │
         │  (Spring Cloud)      │
         │  - Routing           │
         │  - Auth (global)     │
         │  - Rate Limiting     │
         │  - Load Balancing    │
         └────────┬─────────────┘
                  │
      ┌───────────┴───────────────┬──────────────┐
      ↓                           ↓              ↓
┌──────────┐              ┌──────────┐    ┌──────────┐
│  Odds    │              │  User    │    │  Betting │
│ Service  │              │ Service  │    │ Service  │
│ (Port    │              │ (Port    │    │ (Port    │
│  8081)   │              │  8082)   │    │  8083)   │
└────┬─────┘              └────┬─────┘    └────┬─────┘
     │                         │               │
     ↓                         ↓               ↓
┌─────────┐              ┌─────────┐     ┌─────────┐
│PostgreSQL│             │PostgreSQL│    │PostgreSQL│
│ Odds DB  │             │ Users DB │    │ Bets DB  │
└──────────┘             └──────────┘    └──────────┘
```

---

## 🛠️ Technologies Used

### Core Stack
- **Java 17** - Programming language
- **Spring Boot 3.5.6** - Framework
- **Spring Data JPA** - Database access layer
- **Spring Validation** - Bean validation (Jakarta Validation)
- **PostgreSQL 18** - Relational Database
- **Maven** - Build and dependency management

### Production Tools
- **Logback** - Advanced logging framework ✅ **COMPLETE**
- **Spring Boot Actuator** - Monitoring and health checks
- **Springdoc OpenAPI 2.8.8** - Swagger/OpenAPI documentation
- **Lombok** - Reduce boilerplate code

### Future Technologies
- **JUnit 5 + Mockito** - Unit & Integration testing 📋 *Phase 2.2*
- **Spring Security** - Authentication & Authorization 🔐 *Phase 3*
- **JWT (jjwt)** - Token-based authentication 🔐 *Phase 3*
- **Redis** - Caching layer ⚡ *Phase 4*
- **Docker** - Containerization 🐳 *Phase 6*
- **Spring Cloud Gateway** - API Gateway 🚪 *Phase 5*
- **Eureka** - Service Discovery 🔍 *Phase 5*

---

## ✨ Current Features (Phase 1-2 ✅)

### Core Functionality
- ✅ Complete CRUD operations for betting odds
- ✅ DTO Layer for API/Database separation
- ✅ Automatic input validation with detailed error messages
- ✅ Pagination & Sorting - Handle large datasets efficiently
- ✅ Filter odds by sport, team, or active status
- ✅ Get upcoming matches (future dates only)
- ✅ Calculate bookmaker margin and implied probabilities
- ✅ Soft delete (deactivate) and hard delete options

### Technical Features
- ✅ RESTful API design with proper HTTP methods
- ✅ Global exception handling with custom exceptions
- ✅ Transactional operations for data consistency
- ✅ Automatic timestamps (createdAt, updatedAt)
- ✅ Comprehensive validation rules
- ✅ Clean separation of concerns
- ✅ Advanced pagination with multiple sort fields
- ✅ Swagger/OpenAPI interactive documentation
- ✅ Spring Boot Actuator for monitoring and health checks

### Production Logging System ✅ **NEW!**
- ✅ **5 specialized log files** with automatic rotation
  - Main application log (10MB rotation, 30 days retention)
  - Error-only log (separate critical errors)
  - Audit log (business operations tracking, 365 days)
  - Performance log (execution time monitoring)
  - Security log (SQL injection detection, attack attempts)
- ✅ **Audit logging** for all business operations:
  - CREATE operations (new odds records)
  - UPDATE operations (modifications)
  - DELETE operations (soft/hard deletes)
  - VALIDATION_FAILED events
- ✅ **Performance monitoring**:
  - Method execution time tracking
  - Slow query detection (>1000ms)
  - Response time analysis
- ✅ **Security features**:
  - SQL injection detection and blocking
  - XSS attack pattern detection
  - Suspicious input logging
  - Transaction rollback on security violations
- ✅ **Profile-based configuration** (dev/prod/test)
- ✅ **Async logging** for high performance
- ✅ **Colored console output** (development)
- ✅ **Structured logging** for analysis

---

## 🗺️ Complete Roadmap

### Phase 1: Core CRUD API ✅ **COMPLETE**
**Duration:** 2-3 weeks | **Complexity:** ⭐⭐ Beginner

**Features Implemented:**
- [x] Project setup (Spring Boot, PostgreSQL, Maven)
- [x] Entity model (BettingOdds) with JPA annotations
- [x] Repository layer (Spring Data JPA)
- [x] Service layer with business logic
- [x] Controller layer (REST endpoints)
- [x] DTOs (CreateOddsRequest, UpdateOddsRequest, OddsResponse)
- [x] Mapper class (DTO ↔ Entity conversion)
- [x] Bean Validation (@Valid, custom validators)
- [x] Global exception handling
- [x] Custom exceptions (ResourceNotFoundException, InvalidOddsException)
- [x] Pagination & Sorting (multiple fields)
- [x] Business logic (bookmaker margin calculation)
- [x] Swagger/OpenAPI documentation
- [x] Spring Boot Actuator (health, metrics)

**Key Learning Outcomes:**
- Spring Boot fundamentals
- RESTful API design principles
- JPA/Hibernate basics
- DTO pattern
- Exception handling strategies

---

### Phase 2: Production-Ready Features ✅ **COMPLETE**
**Duration:** 3 weeks | **Complexity:** ⭐⭐⭐ Intermediate

#### 2.1 Professional Logging System ✅ **COMPLETE**
- [x] Logback configuration (`logback-spring.xml`)
  - [x] Console appender (development mode)
  - [x] File appender with rotation (10MB, 30 days)
  - [x] Error-only file appender (critical errors)
  - [x] Audit log file (365 days retention)
  - [x] Performance log file (execution time tracking)
  - [x] Security log file (attack detection)
  - [x] Async appenders (high performance)
  - [x] Spring profiles (dev/prod/test)
  - [x] Colored console output
- [x] Service layer logging
  - [x] Business operations tracking (CREATE/UPDATE/DELETE)
  - [x] Performance monitoring (method execution time)
  - [x] Security validation (SQL injection detection)
  - [x] Transaction rollback on suspicious input
- [x] Controller logging
  - [x] HTTP request/response logging
  - [x] Endpoint access tracking
- [x] Exception handler logging
  - [x] Validation failures
  - [x] Error details and stack traces
  - [x] User-friendly error responses

**Why Critical for Gambling Industry:**
- ✅ **Regulatory compliance** - Complete audit trail for authorities
- ✅ **Fraud detection** - Track suspicious patterns and attacks
- ✅ **Debugging** - Quick issue resolution in production
- ✅ **Performance monitoring** - Identify bottlenecks
- ✅ **Legal disputes** - Evidence for compliance
- ✅ **Security** - Detect and block malicious input (SQL injection, XSS)

**What We Built:**
```
logs/
├── application.log        # Main application log (10MB rotation)
├── errors.log            # Error-only log (critical issues)
├── audit.log             # Business operations (365 days)
├── performance.log       # Execution times, slow queries
└── security.log          # SQL injection, XSS attempts
```

**Example Log Outputs:**
```
[AUDIT] 2025-01-15 14:23:45 - CREATE operation: Created odds for Football match Barcelona vs Real Madrid
[PERFORMANCE] 2025-01-15 14:23:45 - Method createOdds executed in 156ms
[SECURITY] 2025-01-15 14:25:12 - SQL injection attempt detected in homeTeam: "Barcelona'; DROP TABLE--"
[SECURITY] 2025-01-15 14:25:12 - Transaction rolled back due to security violation
```

#### 2.2 Unit & Integration Tests 📋 **NEXT UP!**
- [ ] JUnit 5 setup
- [ ] Mockito for mocking
- [ ] Unit tests for Service layer
- [ ] Unit tests for Mapper
- [ ] Integration tests for Repository
- [ ] Integration tests for REST endpoints
- [ ] Test coverage > 80%
- [ ] Test documentation

#### 2.3 Advanced Search & Filtering 📋 *Planned*
- [ ] Specification pattern (dynamic queries)
- [ ] Complex filter combinations
- [ ] Search by multiple criteria
- [ ] Date range filtering
- [ ] Odds range filtering

---

### Phase 3: Security & Authentication 🔐 **PLANNED**
**Duration:** 3-4 weeks | **Complexity:** ⭐⭐⭐⭐ Advanced

**Prerequisites:**
- Understanding of authentication/authorization concepts
- Basic cryptography knowledge (hashing, JWT)
- REST API security best practices

#### 3.1 Spring Security Implementation
- [ ] Spring Security dependency
- [ ] Security configuration class
- [ ] Password encoding (BCrypt)
- [ ] Authentication manager
- [ ] Security filter chain
- [ ] CORS configuration
- [ ] CSRF protection

#### 3.2 JWT Token Authentication
- [ ] JWT library (jjwt) integration
- [ ] Token generation service
- [ ] Token validation filter
- [ ] Refresh token mechanism
- [ ] Token expiration handling
- [ ] Blacklist for revoked tokens

#### 3.3 User Management
- [ ] User entity (username, email, password, roles)
- [ ] User repository
- [ ] User service (CRUD operations)
- [ ] Registration endpoint
- [ ] Login endpoint (returns JWT)
- [ ] Logout endpoint
- [ ] Password reset functionality

#### 3.4 Role-Based Access Control (RBAC)
- [ ] Role enum (USER, ADMIN, BOOKMAKER)
- [ ] Method-level security (`@PreAuthorize`)
- [ ] Endpoint-level authorization
- [ ] Custom authorization logic

**Example Roles:**
```java
// USER - Can only view odds
GET /api/odds - Allowed

// BOOKMAKER - Can create/update odds
POST /api/odds - Allowed
PUT /api/odds/{id} - Allowed

// ADMIN - Full access including delete
DELETE /api/odds/{id} - Allowed
```

#### 3.5 Rate Limiting
- [ ] Rate limiting interceptor
- [ ] In-memory rate limiter (Bucket4j)
- [ ] Per-user rate limits
- [ ] Per-endpoint rate limits
- [ ] Rate limit headers (X-RateLimit-*)

**Key Learning Outcomes:**
- Authentication vs Authorization
- JWT token structure and validation
- Spring Security architecture
- Password security best practices
- API security patterns

---

### Phase 4: Performance & Reliability ⚡ **PLANNED**
**Duration:** 3-4 weeks | **Complexity:** ⭐⭐⭐⭐ Advanced

**Prerequisites:**
- Understanding of caching strategies
- Database performance tuning
- Async programming concepts

#### 4.1 Caching with Redis
- [ ] Redis installation and setup
- [ ] Spring Data Redis integration
- [ ] Cache configuration
- [ ] Cacheable methods (`@Cacheable`)
- [ ] Cache eviction strategies
- [ ] Cache-aside pattern
- [ ] Redis monitoring

**Caching Strategy:**
```java
// Cache frequently accessed odds
@Cacheable(value = "odds", key = "#id")
public OddsResponse getOddsById(Long id)

// Evict cache on update
@CacheEvict(value = "odds", key = "#id")
public OddsResponse updateOdds(Long id, UpdateOddsRequest request)
```

#### 4.2 Database Optimization
- [ ] Query optimization (EXPLAIN ANALYZE)
- [ ] N+1 problem resolution (JOIN FETCH)
- [ ] Database connection pooling (HikariCP tuning)
- [ ] Index optimization
- [ ] Read replicas for scaling

#### 4.3 Async Processing
- [ ] Spring async configuration
- [ ] `@Async` methods for heavy operations
- [ ] CompletableFuture usage
- [ ] Thread pool configuration

#### 4.4 Monitoring & Observability
- [ ] Micrometer metrics
- [ ] Custom metrics (odds created, calculations performed)
- [ ] Prometheus endpoint
- [ ] Grafana dashboards
- [ ] Alerting rules

**Key Learning Outcomes:**
- Caching strategies and patterns
- Database performance optimization
- Asynchronous programming
- Production monitoring

---

### Phase 5: Microservices Architecture 🚀 **FUTURE**
**Duration:** 6-8 weeks | **Complexity:** ⭐⭐⭐⭐⭐ Expert

**Prerequisites:**
- Strong understanding of distributed systems
- Microservices design patterns
- Docker and containerization
- Service communication patterns

**⚠️ IMPORTANT:** Only transition to microservices when:
- Monolith becomes too complex (> 50k lines)
- Different parts of system scale differently
- Multiple teams working on codebase
- Need independent deployment cycles

#### 5.1 Service Decomposition
**Split monolith into:**

**1. Odds Service** (Port 8081)
- Manages betting odds
- Calculates margins and probabilities
- Existing functionality from monolith

**2. User Service** (Port 8082)
- User registration/login
- Profile management
- Wallet/balance management

**3. Betting Service** (Port 8083)
- Place bets
- Bet history
- Settlement logic

**4. Payment Service** (Port 8084)
- Deposits/withdrawals
- Payment gateway integration
- Transaction history

**5. Notification Service** (Port 8085)
- Email notifications
- SMS notifications
- Push notifications

#### 5.2 API Gateway (Spring Cloud Gateway) 🚪
**Why Gateway?**
- Single entry point for all clients
- Centralized authentication
- Request routing to appropriate services
- Global rate limiting
- Load balancing
- API composition

**Gateway Configuration:**
```yaml
# application.yml in Gateway service
spring:
  cloud:
    gateway:
      routes:
        - id: odds-service
          uri: lb://ODDS-SERVICE
          predicates:
            - Path=/api/odds/**
          filters:
            - name: CircuitBreaker
            - name: RateLimiter
            
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/api/users/**
```

**Gateway Features:**
- [ ] Spring Cloud Gateway setup
- [ ] Route configuration
- [ ] Global authentication filter
- [ ] Circuit breaker (Resilience4j)
- [ ] Rate limiting (Redis)
- [ ] Request/response logging
- [ ] API versioning
- [ ] Load balancing

#### 5.3 Service Discovery (Eureka)
- [ ] Eureka Server setup
- [ ] Service registration
- [ ] Service discovery
- [ ] Health checks
- [ ] Load balancing with Ribbon

**Architecture:**
```
       ┌─────────────────┐
       │  Eureka Server  │
       │  (Port 8761)    │
       └────────┬────────┘
                │
       ┌────────┴───────────┐
       │    Register &      │
       │    Discover        │
       │                    │
┌──────▼─────┐    ┌────────▼───────┐
│   Gateway   │    │   Services     │
│  (Port 8080)│    │ (8081-8085)   │
└─────────────┘    └────────────────┘
```

#### 5.4 Inter-Service Communication
- [ ] Synchronous (REST with RestTemplate/Feign)
- [ ] Asynchronous (Message queue with RabbitMQ/Kafka)
- [ ] Service-to-service authentication
- [ ] Circuit breakers for fault tolerance
- [ ] Retry mechanisms

#### 5.5 Distributed Data Management
- [ ] Database per service pattern
- [ ] Saga pattern for distributed transactions
- [ ] Event sourcing (optional)
- [ ] CQRS pattern (optional)

#### 5.6 Configuration Management
- [ ] Spring Cloud Config Server
- [ ] Centralized configuration
- [ ] Environment-specific configs
- [ ] Dynamic configuration updates

**Key Learning Outcomes:**
- Microservices design patterns
- API Gateway pattern
- Service discovery mechanisms
- Distributed system challenges
- Inter-service communication

---

### Phase 6: Cloud Deployment & DevOps ☁️ **ADVANCED**
**Duration:** 4-6 weeks | **Complexity:** ⭐⭐⭐⭐⭐ Expert

**Prerequisites:**
- Docker fundamentals
- CI/CD concepts
- Cloud platform basics (AWS/Azure/GCP)
- Infrastructure as Code

#### 6.1 Containerization (Docker)
- [ ] Dockerfile for each service
- [ ] Docker Compose for local development
- [ ] Multi-stage builds (optimization)
- [ ] Docker networking
- [ ] Volume management
- [ ] Docker security best practices

#### 6.2 CI/CD Pipeline
- [ ] GitHub Actions workflow
- [ ] Automated testing (unit + integration)
- [ ] Code quality checks (SonarQube)
- [ ] Docker image building
- [ ] Automated deployment
- [ ] Rollback strategies

#### 6.3 Cloud Deployment (AWS/Azure)
- [ ] Cloud platform selection
- [ ] Infrastructure setup (VPC, subnets, security groups)
- [ ] Database deployment (RDS/Azure SQL)
- [ ] Container orchestration (ECS/AKS)
- [ ] Load balancer configuration
- [ ] Auto-scaling policies
- [ ] SSL/TLS certificates

#### 6.4 Database Migration
- [ ] Flyway/Liquibase integration
- [ ] Migration scripts (V1__initial.sql, V2__add_users.sql)
- [ ] Version control for database
- [ ] Rollback strategies

#### 6.5 Monitoring & Logging (Production)
- [ ] Centralized logging (ELK Stack / CloudWatch)
- [ ] Application Performance Monitoring (New Relic / Datadog)
- [ ] Error tracking (Sentry)
- [ ] Real-time alerts
- [ ] SLA monitoring

**Key Learning Outcomes:**
- Docker and containerization
- CI/CD pipeline design
- Cloud infrastructure management
- Production monitoring and alerting

---

## 📊 API Endpoints

### Odds Management
All GET endpoints support pagination and sorting.

**Query Parameters:**
- `page` - Page number (0-indexed)
- `size` - Items per page (default: 20, max: 100)
- `sort` - Sort field and direction (format: `property,direction`)

**Examples:**
```bash
# First page with 10 items
GET /api/odds?page=0&size=10

# Sort by date descending
GET /api/odds?sort=matchDate,desc

# Multiple sort fields
GET /api/odds?page=1&size=20&sort=sport,asc&sort=homeOdds,desc
```

### Endpoints Table

| Method | Endpoint | Description | Request Body | Response | Pagination |
|--------|----------|-------------|--------------|----------|------------|
| GET | `/api/odds` | Get all odds | - | `PageResponse<OddsResponse>` | ✅ |
| GET | `/api/odds/active` | Get active odds only | - | `PageResponse<OddsResponse>` | ✅ |
| GET | `/api/odds/{id}` | Get odds by ID | - | `OddsResponse` | ❌ |
| GET | `/api/odds/sport/{sport}` | Get odds by sport | - | `PageResponse<OddsResponse>` | ✅ |
| GET | `/api/odds/upcoming` | Get upcoming matches | - | `PageResponse<OddsResponse>` | ✅ |
| GET | `/api/odds/team/{teamName}` | Get matches for team | - | `PageResponse<OddsResponse>` | ✅ |
| GET | `/api/odds/{id}/margin` | Calculate bookmaker margin | - | `OddsResponse` (with calculations) | ❌ |
| POST | `/api/odds` | Create new odds | `CreateOddsRequest` | `OddsResponse` | ❌ |
| PUT | `/api/odds/{id}` | Update odds | `UpdateOddsRequest` | `OddsResponse` | ❌ |
| PATCH | `/api/odds/{id}/deactivate` | Deactivate odds (soft delete) | - | Success message | ❌ |
| DELETE | `/api/odds/{id}` | Delete odds permanently | - | Success message | ❌ |

---

## 🗄️ Database Schema

### `betting_odds` Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGSERIAL | PRIMARY KEY | Unique identifier |
| `sport` | VARCHAR | NOT NULL | Sport type (Football, Basketball, etc.) |
| `home_team` | VARCHAR | NOT NULL | Home team name |
| `away_team` | VARCHAR | NOT NULL | Away team name |
| `home_odds` | DECIMAL(5,2) | NOT NULL | Decimal odds for home win |
| `draw_odds` | DECIMAL(5,2) | NOT NULL | Decimal odds for draw |
| `away_odds` | DECIMAL(5,2) | NOT NULL | Decimal odds for away win |
| `match_date` | TIMESTAMP | NOT NULL | Match date and time |
| `active` | BOOLEAN | NOT NULL | Whether odds are active |
| `created_at` | TIMESTAMP | NOT NULL | Record creation timestamp |
| `updated_at` | TIMESTAMP | - | Record update timestamp |

### Indexes
- `idx_sport_active` ON `betting_odds(sport, active)` - Fast filtering by sport
- `idx_match_date` ON `betting_odds(match_date)` - Fast date queries
- `idx_home_team` ON `betting_odds(home_team)` - Fast team searches
- `idx_away_team` ON `betting_odds(away_team)` - Fast team searches

---

## 🚀 Getting Started

### Prerequisites
- **Java 21** or higher
- **PostgreSQL 18**
- **Maven 3.9+**
- **Postman** (for API testing)
- **Git** (for version control)

### Installation Steps

1. **Clone the repository**
```bash
git clone https://github.com/BaykushevI/betting-odds-api.git
cd betting-odds-api
```

2. **Create PostgreSQL database**
```sql
CREATE DATABASE betting_test;
```

3. **Configure database connection**

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/betting_test
spring.datasource.username=postgres
spring.datasource.password=admin123
```

4. **Install dependencies**
```bash
mvn clean install
```

5. **Run the application**
```bash
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080`

### Quick Test

**Option 1: Via Swagger UI**
1. Open http://localhost:8080/swagger-ui.html
2. Find `POST /api/odds` endpoint
3. Click "Try it out"
4. Use example JSON
5. Click "Execute"

**Option 2: Via Postman**
```bash
POST http://localhost:8080/api/odds
Content-Type: application/json

{
  "sport": "Football",
  "homeTeam": "Barcelona",
  "awayTeam": "Real Madrid",
  "homeOdds": 2.10,
  "drawOdds": 3.40,
  "awayOdds": 3.60,
  "matchDate": "2025-10-20T20:00:00"
}
```

---

## 📚 API Documentation & Monitoring

After starting the application:

| URL | Description |
|-----|-------------|
| http://localhost:8080/swagger-ui.html | Interactive API documentation |
| http://localhost:8080/api-docs | OpenAPI 3.0 specification (JSON) |
| http://localhost:8080/actuator/health | Health check endpoint |
| http://localhost:8080/actuator/info | Application metadata |
| http://localhost:8080/actuator/metrics | Application metrics |
| http://localhost:8080/actuator/loggers | View/change log levels at runtime |

---

## 📝 Logging System

### Log Files Location
All log files are stored in the `logs/` directory:
```
logs/
├── application.log        # Main application log (10MB rotation, 30 days)
├── errors.log            # Error-only log (critical issues)
├── audit.log             # Business operations (365 days retention)
├── performance.log       # Execution times, slow queries
└── security.log          # SQL injection, XSS attempts
```

### Log Levels by Profile
- **Development** (`dev`): DEBUG level, colored console + file output
- **Production** (`prod`): INFO level, file output only, async logging
- **Test** (`test`): WARN level, minimal logging

### Change Log Level at Runtime
```bash
# View current log level
GET http://localhost:8080/actuator/loggers/com.gambling.betting_odds_api

# Change log level to DEBUG
POST http://localhost:8080/actuator/loggers/com.gambling.betting_odds_api
Content-Type: application/json

{
  "configuredLevel": "DEBUG"
}
```

### Audit Log Examples
```
[AUDIT] 2025-01-15 14:23:45 - CREATE: Created odds for Football match Barcelona vs Real Madrid
[AUDIT] 2025-01-15 14:25:12 - UPDATE: Updated odds #123 - Football: Barcelona vs Real Madrid
[AUDIT] 2025-01-15 14:30:08 - DELETE: Soft deleted odds #123
[AUDIT] 2025-01-15 14:35:22 - VALIDATION_FAILED: Invalid home team name format
```

### Performance Log Examples
```
[PERFORMANCE] 2025-01-15 14:23:45 - Method createOdds executed in 156ms
[PERFORMANCE] 2025-01-15 14:25:12 - Method updateOdds executed in 89ms
[PERFORMANCE] 2025-01-15 14:30:08 - SLOW QUERY DETECTED: getOddsBySport took 1234ms
```

### Security Log Examples
```
[SECURITY] 2025-01-15 14:25:12 - SQL injection attempt detected in homeTeam: "Barcelona'; DROP TABLE--"
[SECURITY] 2025-01-15 14:25:12 - Transaction rolled back due to security violation
[SECURITY] 2025-01-15 14:28:45 - XSS attempt detected in sport field: "<script>alert('xss')</script>"
[SECURITY] 2025-01-15 14:30:22 - Suspicious input blocked: Multiple SQL keywords detected
```

---

## 💡 Business Logic Example

### Bookmaker Margin Calculation

```
Given odds:
- Home win: 2.10
- Draw: 3.40
- Away win: 3.60

Implied Probabilities:
- Home: 1 / 2.10 = 47.6%
- Draw: 1 / 3.40 = 29.4%
- Away: 1 / 3.60 = 27.8%

Total: 47.6% + 29.4% + 27.8% = 104.8%

Bookmaker Margin: 104.8% - 100% = 4.8%
```

**The 4.8% is the bookmaker's profit margin!**

---

## 🎓 Learning Outcomes

### Architecture & Design Patterns
- ✅ Layered Architecture (Controller → Service → Repository)
- ✅ DTO Pattern (API/Database separation)
- ✅ Mapper Pattern (DTO ↔ Entity conversion)
- ✅ Repository Pattern (Data access abstraction)
- ✅ Dependency Injection (IoC)

### Best Practices
- ✅ Bean Validation (Declarative input validation)
- ✅ Global Exception Handling (`@RestControllerAdvice`)
- ✅ RESTful API Design (HTTP methods, status codes)
- ✅ Transaction Management (`@Transactional`)
- ✅ Clean Code (Naming, SOLID principles)
- ✅ **Production Logging** (Audit, Performance, Security)
- ✅ **Security Validation** (SQL injection, XSS prevention)

### Domain Knowledge
- ✅ Gambling industry concepts (odds formats, margins)
- ✅ Business logic implementation (calculations)
- ✅ Data validation (industry constraints)
- ✅ Regulatory compliance considerations
- ✅ **Security threats** (SQL injection, XSS)
- ✅ **Audit requirements** (compliance tracking)

### Technologies Mastered
- ✅ Spring Boot ecosystem
- ✅ PostgreSQL database operations
- ✅ RESTful API development
- ✅ Maven build management
- ✅ Git version control
- ✅ **Logback** (enterprise logging)
- ✅ **Security patterns** (input validation, attack detection)
- 📋 JUnit 5 + Mockito - *Next Up*
- 📋 Spring Security - *Planned*
- 🚀 Microservices architecture - *Future*

---

## 🔒 Security Features

### Input Validation & Sanitization
- ✅ SQL injection detection (DROP, DELETE, INSERT, etc.)
- ✅ XSS attack prevention (script tags, event handlers)
- ✅ Suspicious pattern detection (multiple SQL keywords)
- ✅ Transaction rollback on security violations
- ✅ Comprehensive security logging

### Defense in Depth
1. **Layer 1**: Bean Validation (@Valid, @NotBlank, etc.)
2. **Layer 2**: Service layer security checks (SQL injection, XSS)
3. **Layer 3**: JPA prepared statements (parameterized queries)
4. **Layer 4**: Transaction management (rollback on violations)
5. **Layer 5**: Logging and monitoring (attack detection)

### Example: Blocked Malicious Input
```json
// Request with SQL injection attempt
{
  "sport": "Football",
  "homeTeam": "Barcelona'; DROP TABLE betting_odds--",
  "awayTeam": "Real Madrid",
  ...
}

// Response
{
  "timestamp": "2025-01-15T14:25:12",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid input: SQL injection pattern detected in home team name"
}

// Logged to security.log
[SECURITY] 2025-01-15 14:25:12 - SQL injection attempt detected in homeTeam
[SECURITY] 2025-01-15 14:25:12 - Transaction rolled back due to security violation
```

---

## 👨‍💻 Author

**Iliyan Baykushev**

- GitHub: [@BaykushevI](https://github.com/BaykushevI)
- LinkedIn: [Iliyan Baykushev](https://www.linkedin.com/in/iliyan-baykushev/)

---

## 📄 License

This project is for **educational purposes** only.

---

## 🤝 Contributing

This is a learning project, but suggestions and feedback are welcome!

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📮 Questions?

If you have questions about the project or want to discuss implementation details, feel free to open an issue on GitHub!

---

## 🎯 What's Next?

### Immediate Next Steps (Phase 2.2)
1. **Unit Tests** - Test Service layer business logic
2. **Integration Tests** - Test REST endpoints end-to-end
3. **Test Coverage** - Achieve >80% code coverage
4. **Test Documentation** - Document test strategies

### After Testing (Phase 3)
1. **Spring Security** - Authentication & Authorization
2. **JWT Tokens** - Stateless authentication
3. **User Management** - Registration, login, roles
4. **Rate Limiting** - Prevent API abuse

---

**⭐ If you find this project helpful for learning, please give it a star!**

---

## 📊 Project Statistics

- **Lines of Code**: ~2,000 (Java + XML)
- **Total Commits**: 20+
- **Features Completed**: Core CRUD + Logging System
- **Test Coverage**: 0% (Phase 2.2 upcoming)
- **API Endpoints**: 10
- **Database Tables**: 1 (odds)
- **Log Files**: 5 (application, errors, audit, performance, security)