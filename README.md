# Betting Odds API

A production-ready RESTful API for managing betting odds for sports matches, built with Spring Boot and PostgreSQL.

## 📊 Project Status
```
Phase 1: Core CRUD API              [x] COMPLETE
Phase 2.1: Production Logging       [x] COMPLETE
Phase 2.2: Unit & Integration Tests [x] COMPLETE (52/56 tests, 93% coverage)
Phase 3: Security & Authentication  [x] COMPLETE (Week 3 Day 10 COMPLETE)
Phase 4: Performance & Reliability  [~] IN PROGRESS (Week 2 Day 11 COMPLETE)
Phase 5: Microservices & Gateway    [ ] FUTURE
Phase 6: Cloud Deployment           [ ] ADVANCED
```

---

## 📖 Project Overview

This is a comprehensive **learning project** demonstrating professional backend development practices relevant to the **gambling industry**. It implements a complete CRUD API with proper architectural patterns, validation, error handling, business logic, **enterprise-grade logging**, **JWT authentication**, and **performance optimizations**.

### 🎯 Learning Goals

- [x] Master Spring Boot ecosystem (Web, Data JPA, Validation, Security)
- [x] Understand production-ready development practices
- [x] Learn gambling industry domain concepts (odds, margins, probabilities)
- [x] Implement professional logging for compliance and debugging
- [x] Implement JWT-based authentication and authorization
- [x] Implement Redis caching for performance [FAST]
- [x] Resolve N+1 query problems [NEW]
- [ ] Progress from monolith to microservices architecture
- [ ] Implement enterprise-level monitoring (Prometheus, Grafana)

---

## Architecture Evolution

### Current: Monolithic Architecture with JWT Authentication & Redis Caching (Phase 1-4)
```
┌─────────────────────────────────────────────┐
│         CLIENT (Browser/Postman)             │
└──────────────────┬──────────────────────────┘
                   │ HTTP/JSON
                   │ Authorization: Bearer <JWT>
                   ↓
┌─────────────────────────────────────────────┐
│          SPRING BOOT APPLICATION             │
│  ┌────────────────────────────────────────┐ │
│  │   SECURITY FILTER CHAIN                │ │
│  │   - JwtAuthenticationFilter            │ │
│  │   - Extract & validate JWT token       │ │
│  │   - Load user from database            │ │
│  │   - Set SecurityContext                │ │
│  └─────────────────┬──────────────────────┘ │
│                    ↓                         │
│  ┌────────────────────────────────────────┐ │
│  │   CONTROLLER LAYER                     │ │
│  │   - REST endpoints                     │ │
│  │   - Request validation (@Valid)        │ │
│  │   - DTOs (Request/Response)            │ │
│  │   - HTTP request/response logging      │ │
│  │   - Authentication endpoints           │ │
│  │     POST /api/auth/register            │ │
│  │     POST /api/auth/login               │ │
│  │   - Cache admin endpoints [NEW]        │ │
│  │     GET /api/admin/cache/stats         │ │
│  │     POST /api/admin/cache/clear        │ │
│  └─────────────────┬──────────────────────┘ │
│                    ↓                         │
│  ┌────────────────────────────────────────┐ │
│  │   SERVICE LAYER                        │ │
│  │   - Business logic                     │ │
│  │   - Transaction management             │ │
│  │   - Redis caching (@Cacheable) [FAST]  │ │
│  │   - Calculations (margins, probabilities)│
│  │   - Security validation (SQL injection)│ │
│  │   - Audit logging (CREATE/UPDATE/DELETE)│
│  │   - Performance logging (execution time)│
│  │   - AuthService (register, login, JWT) │ │
│  │   - Password hashing (BCrypt)          │ │
│  └─────────────────┬──────────────────────┘ │
│                    ↓                         │
│  ┌────────────────────────────────────────┐ │
│  │   REPOSITORY LAYER                     │ │
│  │   - Spring Data JPA                    │ │
│  │   - Database queries                   │ │
│  │   - JOIN FETCH (N+1 prevention) [NEW]  │ │
│  │   - UserRepository (authentication)    │ │
│  │   - BettingOddsRepository              │ │
│  └─────────────────┬──────────────────────┘ │
└────────────────────┼──────────────────────┘
                     ↓
         ┌──────────────────────┐
         │   PostgreSQL DB      │
         │   - betting_odds     │
         │   - users            │
         │   - Migrations (3)   │
         └──────────┬───────────┘
                    │
         ┌──────────┴───────────┐
         │                      │
         ↓                      ↓
┌─────────────────┐   ┌─────────────────┐
│   Redis Cache   │   │   LOG FILES     │
│   - 6 namespaces│   │   - application │
│   - 30min TTL   │   │   - errors      │
│   [FAST]        │   │   - audit       │
└─────────────────┘   │   - performance │
                      │   - security    │
                      └─────────────────┘
```

### Future: Microservices Architecture (Phase 5+)
[!] NOT IMPLEMENTED YET - This section shows future architecture
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

## 🛠️ Technologies Used

### Core Stack
- **Java 17** - Programming language
- **Spring Boot 3.5.6** - Framework
- **Spring Data JPA** - Database access layer
- **Spring Validation** - Bean validation (Jakarta Validation)
- **Spring Security 6.x** - Authentication & Authorization [COMPLETE]
- **PostgreSQL 18** - Relational Database
- **Maven** - Build and dependency management

### Security Stack
- **Spring Security 6.x** - Security framework [IN USE]
- **JWT (jjwt 0.12.6)** - JSON Web Tokens [IN USE]
- **BCrypt** - Password hashing algorithm [IN USE]
- ✅ **Docker** (containerization, image/container management) - NEW Phase 4
- ✅ **Redis 7** (in-memory caching, data structures) - NEW Phase 4
- ✅ **Spring Data Redis** (caching integration) - NEW Phase 4
- ✅ **Lettuce** (Redis client, connection pooling) - NEW Phase 4

### Production Tools
- **Logback** - Advanced logging framework [COMPLETE]
- **Spring Boot Actuator** - Monitoring and health checks
- **Springdoc OpenAPI 2.8.8** - Swagger/OpenAPI documentation
- **Lombok** - Reduce boilerplate code

### Testing Tools
- **JUnit 5** - Unit testing framework [IN USE]
- **Mockito** - Mocking framework [IN USE]
- **AssertJ** - Fluent assertions [IN USE]
- **@DataJpaTest** - Repository integration tests [IN USE]
- **H2 Database** - In-memory database for tests [IN USE]
- **Testcontainers** - Docker containers for tests [IN USE]

### Phase 4 Technologies (IN USE)
- **Docker** - Containerization 🐳 ✅ **IN USE**
- **Redis 7** - In-memory caching ⚡ ✅ **IN USE**
- **Spring Data Redis** - Redis integration ✅ **IN USE**
- **Lettuce** - Redis client (connection pooling) ✅ **IN USE**
- **Spring AOP** - Aspect-oriented programming ✅ **IN USE**
- **Micrometer** - Application metrics (already in Actuator) ✅ **IN USE**

### Performance Stack [NEW Phase 4]
- **Docker** - Containerization [IN USE]
- **Redis 7 Alpine** - In-memory caching [IN USE]
- **Spring Data Redis** - Redis integration [IN USE]
- **Lettuce** - Redis client (connection pooling) [IN USE]
- **Spring Cache** - Caching abstraction (@Cacheable) [IN USE]

### Future Technologies
- **Prometheus** - Metrics collection [PLANNED Phase 4 Week 4]
- **Grafana** - Monitoring dashboards [PLANNED Phase 4 Week 4]

### Database Tools
- **Flyway** - Database migrations [IN USE]
- **HikariCP** - Connection pooling (default in Spring Boot)

## Current Features (Phase 1-4)

### Core Functionality
- [x] Complete CRUD operations for betting odds
- [x] DTO Layer for API/Database separation
- [x] Automatic input validation with detailed error messages
- [x] Pagination & Sorting - Handle large datasets efficiently
- [x] Filter odds by sport, team, or active status
- [x] Get upcoming matches (future dates only)
- [x] Calculate bookmaker margin and implied probabilities
- [x] Soft delete (deactivate) and hard delete options

### Technical Features
- [x] RESTful API design with proper HTTP methods
- [x] Global exception handling with custom exceptions
- [x] Transactional operations for data consistency
- [x] Automatic timestamps (createdAt, updatedAt)
- [x] Comprehensive validation rules
- [x] Clean separation of concerns
- [x] Advanced pagination with multiple sort fields
- [x] Swagger/OpenAPI interactive documentation
- [x] Spring Boot Actuator for monitoring and health checks

### Production Logging System [COMPLETE]
- [x] **5 specialized log files** with automatic rotation
  - Main application log (10MB rotation, 30 days retention)
  - Error-only log (separate critical errors)
  - Audit log (business operations tracking, 365 days)
  - Performance log (execution time monitoring)
  - Security log (SQL injection detection, attack attempts)
- [x] **Audit logging** for all business operations:
  - CREATE operations (new odds records)
  - UPDATE operations (modifications)
  - DELETE operations (soft/hard deletes)
  - VALIDATION_FAILED events
- [x] **Performance monitoring**:
  - Method execution time tracking
  - Slow query detection (>1000ms)
  - Response time analysis
- [x] **Security features**:
  - SQL injection detection and blocking
  - XSS attack pattern detection
  - Suspicious input logging
  - Transaction rollback on security violations
- [x] **Profile-based configuration** (dev/prod/test)
- [x] **Async logging** for high performance
- [x] **Colored console output** (development)
- [x] **Structured logging** for analysis

### Authentication & Authorization System [COMPLETE]
- [x] **JWT-based authentication** (stateless, token-based)
- [x] **User registration and login** (POST /api/auth/register, /api/auth/login)
- [x] **BCrypt password hashing** (60-character hash with salt)
- [x] **JWT token generation** (24-hour expiration)
- [x] **JWT token validation** (signature, expiration, username)
- [x] **Custom authentication filter** (JwtAuthenticationFilter)
- [x] **User details service** (CustomUserDetailsService)
- [x] **Role-based user model** (USER, BOOKMAKER, ADMIN)
- [x] **Protected endpoints** (/api/odds/** requires authentication)
- [x] **Public endpoints** (/api/auth/** no authentication required)
- [x] **Exception handling** (401 Unauthorized, 403 Forbidden)
- [x] **Security context management** (request-scoped authentication)
- [x] **Duplicate username/email prevention**
- [x] **Account status management** (active/inactive flag)
- [x] **Method-level security** (@PreAuthorize annotations)
- [x] **Role-based access control** (USER: read-only, BOOKMAKER: read+write, ADMIN: full access)

### Performance & Reliability Features [NEW Phase 4]
#### Redis Caching System [COMPLETE Week 1]
- [x] **In-memory caching** with Redis 7 Alpine
- [x] **Docker containerization** for Redis
- [x] **Spring Cache abstraction** (@Cacheable, @CachePut, @CacheEvict)
- [x] **Automatic cache management**:
  - Single record caching (getOddsById) [FAST 15-37x]
  - Pagination caching (first 3 pages) [FAST]
  - Sport-specific caching (separate per sport)
  - Team-specific caching (separate per team)
  - TTL: 30 minutes (configurable)
- [x] **Cache invalidation strategies**:
  - @CachePut on UPDATE (refresh cache)
  - @CacheEvict on DELETE (remove from cache)
  - Multi-cache eviction on CREATE (clear all pagination caches)
- [x] **Performance improvement**: 15-37x faster for cached queries
- [x] **Cache monitoring** [NEW Week 1 Days 5-6]:
  - Cache statistics endpoint (GET /api/admin/cache/stats)
  - Cache health checks (GET /api/admin/cache/health)
  - Manual cache clearing (POST /api/admin/cache/clear)
  - Cache error handling (graceful degradation)
  - Performance logging for cache operations
  - AOP-based monitoring (@CacheMonitoringAspect)
- [x] **16 comprehensive cache tests** with Testcontainers
#### Database Optimization [COMPLETE Week 2 Days 6-11]
- [x] **Database migrations** with Flyway:
  - V1: Initial schema (betting_odds table)
  - V2: Add users table
  - V3: Add created_by relationship (N+1 prevention) [NEW]
- [x] **Strategic indexes**:
  - idx_sport_active (composite index on sport + active)
  - idx_match_date (single index on match_date)
  - idx_home_team (single index on home_team)
  - idx_away_team (single index on away_team)
  - idx_active_match_date (composite index for upcoming matches)
- [x] **Query optimization**:
  - UNION approach for team searches (18% faster, 100-500x at scale) [Day 8]
  - Index Scan instead of Seq Scan (0.059ms execution time) [Day 7]
  - EXPLAIN ANALYZE for performance analysis [Day 7]
- [x] **N+1 Problem Resolution** [NEW Days 9-11]:
  - @ManyToOne relationship (BettingOdds -> User)
  - JOIN FETCH in JPQL (1 query instead of 1+N)
  - @EntityGraph alternative (same performance)
  - Performance: 10-100x faster for large datasets
  - Paginated JOIN FETCH support
  - 5 comprehensive N+1 tests (NPlusOneProblemTest)
## Complete Roadmap

### Phase 1: Core CRUD API [COMPLETE]
**Duration:** 2-3 weeks | **Complexity:** [BEGINNER]

**Features Implemented:**
- [x] Project setup (Spring Boot, PostgreSQL, Maven)
- [x] Entity model (BettingOdds) with JPA annotations
- [x] Repository layer (Spring Data JPA)
- [x] Service layer with business logic
- [x] Controller layer (REST endpoints)
- [x] DTOs (CreateOddsRequest, UpdateOddsRequest, OddsResponse)
- [x] Mapper class (DTO <-> Entity conversion)
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

### Phase 2: Production-Ready Features [COMPLETE]
**Duration:** 3 weeks | **Complexity:** [INTERMEDIATE]

#### 2.1 Professional Logging System [COMPLETE]
- [x] Logback configuration (logback-spring.xml)
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
- [x] **Regulatory compliance** - Complete audit trail for authorities
- [x] **Fraud detection** - Track suspicious patterns and attacks
- [x] **Debugging** - Quick issue resolution in production
- [x] **Performance monitoring** - Identify bottlenecks
- [x] **Legal disputes** - Evidence for compliance
- [x] **Security** - Detect and block malicious input (SQL injection, XSS)

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

#### 2.2 Unit & Integration Tests [COMPLETE]

**Goal:** Achieve 80%+ test coverage with professional testing practices

**Progress Tracker:**

**Week 1: Service Layer Tests (Days 1-4)** [COMPLETE]
- [x] Day 1: Test setup + First test (createOdds - happy path)
- [x] Day 2: READ tests (getById, getAll, getBySport)
- [x] Day 3: UPDATE and DELETE tests (updateOdds, deactivateOdds, deleteOdds)
- [x] Day 4: Business logic and security tests (margin, SQL injection, XSS)

**Week 1 Summary:**
- 16 unit tests for BettingOddsService
- Approximately 80% Service layer coverage
- All CRUD operations tested
- Security validations tested
- Business logic verified

**Week 2: Mapper & Repository Tests (Days 5-11)** [COMPLETE]
- [x] Day 5-6: Mapper tests (DTO to Entity, Entity to DTO conversions)
- [x] Day 7-9: Repository tests (@DataJpaTest)
- [x] Day 10-11: Controller integration tests (@SpringBootTest + MockMvc)

**Week 2 Days 5-6 Summary:**
- 8 unit tests for OddsMapper
- 100% Mapper coverage
- All DTO <-> Entity conversions tested
- Margin calculations verified

**Week 2 Days 7-9 Summary:**
- 10 integration tests for BettingOddsRepository
- 100% Repository coverage
- Tested against real H2 in-memory database
- All custom queries verified (@Query annotations)
- Pagination, sorting, and date filtering tested

**Week 2 Days 10-11 Summary:**
- 12 integration tests for BettingOddsController
- 100% Controller coverage
- All REST endpoints tested with MockMvc
- HTTP status codes verified (200, 201, 400, 404)
- JSON request/response validation
- Security tests (SQL injection blocking)
- End-to-end integration testing

**Week 3: Final Tests & Documentation (Days 12-14)** [COMPLETE]
- [x] Day 12: Controller integration tests completion
- [x] Day 13: End-to-end HTTP tests verification
- [x] Day 14: Test documentation

**Current Test Coverage:**

| Component | Tests Written | Coverage | Status |
|-----------|--------------|----------|--------|
| **BettingOddsService** | 16/20 | ~80% | [x] Week 1 Complete |
| **BettingOddsServiceCache** | 16/16 | ~100% | [x] Phase 4 Week 1 Complete |
| **NPlusOneProblemTest** | 5/5 | ~100% | [x] Phase 4 Week 2 Complete |
| **OddsMapper** | 8/8 | ~100% | [x] Week 2 Complete |
| **BettingOddsRepository** | 10/10 | ~100% | [x] Week 2 Complete |
| **BettingOddsController** | 18/18 | ~100% | [x] Phase 3 Week 3 Complete |
| **TOTAL** | **73/77** | **~95%** | [x] **Excellent Coverage!** |

**What We Learned:**
- [x] JUnit 5 basics (test structure, assertions)
- [x] Mockito fundamentals (mocking dependencies)
- [x] AAA pattern (Arrange-Act-Assert)
- [x] Test naming conventions
- [x] @DataJpaTest for repository testing
- [x] H2 in-memory database for integration tests
- [x] Testing pagination and sorting
- [x] Testing custom @Query methods
- [x] @SpringBootTest for full integration testing
- [x] MockMvc for HTTP endpoint testing
- [x] JSON assertions with JSONPath
- [x] Testing REST API status codes
- [x] End-to-end HTTP request/response testing
- [x] JWT token generation in tests
- [x] Testing with Authorization header
- [x] Role-based authorization testing
- [x] Testing 403 Forbidden and 401 Unauthorized
- [x] Docker containerization basics
- [x] Redis in-memory caching
- [x] Testcontainers for integration tests [NEW]
- [x] Spring Cache abstraction (@Cacheable, @CachePut, @CacheEvict)
- [x] TTL (Time-To-Live) configuration
- [x] Jackson JSON serialization with Java 8 Time API
- [x] Cache hit/miss scenarios
- [x] Cache eviction strategies
- [x] N+1 problem detection and resolution [NEW]
- [x] JOIN FETCH vs @EntityGraph comparison [NEW]

---
#### 2.3 Advanced Search & Filtering [PLANNED]
- [ ] Specification pattern (dynamic queries)
- [ ] Complex filter combinations
- [ ] Search by multiple criteria
- [ ] Date range filtering
- [ ] Odds range filtering

---

### Phase 3: Security & Authentication [COMPLETE]
**Duration:** 3-4 weeks | **Complexity:** [ADVANCED]

**Prerequisites:**
- Understanding of authentication/authorization concepts
- Basic cryptography knowledge (hashing, JWT)
- REST API security best practices

#### Week 1: Spring Security Setup (Days 1-3) [COMPLETE]

**Goal:** Set up Spring Security framework and user management foundation

**Progress:**
- [x] Day 1: Add Spring Security and JWT dependencies
  - Added spring-boot-starter-security (3.5.6)
  - Added JWT libraries (jjwt-api, jjwt-impl, jjwt-jackson 0.12.6)
  - Added spring-security-test for testing
  - Created temporary SecurityConfig with permitAll() for all endpoints
  - Configured stateless session management (JWT preparation)
  - Disabled CSRF (not needed for token-based auth)
  - All 43 tests passing

- [x] Day 2: Create User entity and Role enum
  - Created Role enum (USER, BOOKMAKER, ADMIN)
  - Created User entity with:
    - Basic fields (id, username, email, password)
    - Role-based access control field
    - Soft delete support (active flag)
    - Audit timestamps (createdAt, updatedAt with @PrePersist/@PreUpdate)
  - Created UserRepository with custom query methods:
    - findByUsername()
    - findByEmail()
    - existsByUsername()
    - existsByEmail()
  - Configured EnumType.STRING for role storage
  - Added unique constraints on username and email
  - Database table users created successfully

- [x] Day 3: Add BCrypt password encoder
  - Added BCryptPasswordEncoder bean in SecurityConfig
  - Created TestController for development testing
    - POST /api/test/create-user - Create test users
    - GET /api/test/users - List all users
  - Successfully tested password hashing (60 character BCrypt hash)
  - Verified User entity and UserRepository work correctly
  - Created first test user in database

**What We Built:**
```
src/main/java/com/gambling/betting_odds_api/
├── config/
│   └── SecurityConfig.java          # Spring Security configuration
├── model/
│   ├── Role.java                    # USER, BOOKMAKER, ADMIN enum
│   └── User.java                    # User entity with BCrypt password
├── repository/
│   └── UserRepository.java          # User data access layer
└── controller/
    └── TestController.java          # Temporary testing endpoints (DELETED in Week 2)

Database:
└── users table (id, username, email, password, role, active, timestamps)

Configuration:
└── application.properties (basic Spring Security settings)
```

**Security Features Implemented:**
- [x] BCrypt password hashing (60 character hash with salt)
- [x] Stateless session management (JWT preparation)
- [x] CSRF disabled (token-based auth)
- [x] User entity with role-based access control
- [x] Soft delete support (active flag)
- [x] Unique constraints (username, email)
- [x] Audit timestamps (@PrePersist/@PreUpdate)

**Testing Results:**
```bash
# Password hashing works
Plain password: "secret123"
BCrypt hash: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
Hash length: 60 characters [x]

# User creation works
POST http://localhost:8080/api/test/create-user?username=john&password=secret123
Response: 200 OK, user ID: 1 [x]

# Database persistence works
SELECT * FROM users;
Result: 1 row (john, john@test.com, hashed_password, USER, true) [x]
```

#### Week 2: JWT Authentication (Days 4-7) [COMPLETE]

**Goal:** Implement complete JWT authentication flow

**Progress:**
- [x] Day 4: Create JWT utility class
  - Created JwtTokenProvider class in security package
  - Added JWT configuration in application.properties
    - jwt.secret (signing key)
    - jwt.expiration (24 hours = 86400000ms)
    - jwt.prefix (Bearer)
  - Implemented token generation (generateToken)
  - Implemented token validation (validateToken with username check)
  - Implemented claim extraction (extractUsername, extractExpiration)
  - Used JJWT 0.12.6 API (parser(), verifyWith(), parseSignedClaims())
  - Added test endpoints in TestController
    - POST /api/test/generate-token
    - POST /api/test/validate-token
  - Testing results:
    - Token generation: [x] (147 chars, HS256 algorithm)
    - Token validation: [x] (signature verified)
    - Username extraction: [x]
    - Expiration check: [x] (24 hours from creation)
    - Invalid token rejection: [x]
  
- [x] Day 5: Create authentication DTOs
  - Created LoginRequest DTO
    - Fields: username, password
    - Validation: @NotBlank on both fields
    - Used for POST /api/auth/login
  - Created RegisterRequest DTO
    - Fields: username, email, password
    - Validation: @NotBlank, @Size(3-50), @Email, @Size(6-100)
    - Used for POST /api/auth/register
  - Created AuthResponse DTO
    - Fields: token, tokenType, username, email, role
    - Default tokenType: "Bearer"
    - Returned after successful login/register
  - Added comprehensive validation annotations
  - Documented authentication flow (register -> login -> authenticated request)
  
- [x] Day 6: Create AuthService and AuthController
  - Created AuthService with register() and login() methods
    - register(): username/email uniqueness check, BCrypt hashing, JWT generation
    - login(): credential validation, password verification, JWT generation
  - Created AuthController with REST endpoints
    - POST /api/auth/register - User registration
    - POST /api/auth/login - User login
  - Added request validation with @Valid
  - Implemented exception handling (400, 401, 500)
  - Added logging for all operations
  - Testing results:
    - Registration: [x] (user created, token returned)
    - Login: [x] (credentials validated, token returned)
    - Duplicate username: [x] (400 Bad Request)
    - Duplicate email: [x] (400 Bad Request)
    - Wrong password: [x] (401 Unauthorized)
    - Non-existent user: [x] (401 Unauthorized)
    - JWT token validation: [x] (jwt.io verified)
  
- [x] Day 7: Create JWT authentication filter
  - Created CustomUserDetailsService implements UserDetailsService
    - loadUserByUsername() - loads User from database
    - Converts User entity to UserDetails
    - Maps Role enum to GrantedAuthority (ROLE_USER, ROLE_ADMIN, etc.)
    - Handles account status (active/inactive)
  - Created JwtAuthenticationFilter extends OncePerRequestFilter
    - Intercepts all HTTP requests
    - Extracts JWT token from Authorization header ("Bearer <token>")
    - Validates token (signature, expiration, username)
    - Loads UserDetails from database
    - Creates Authentication object
    - Sets authentication in SecurityContext
  - Updated SecurityConfig with production configuration
    - Added JwtAuthenticationFilter to security chain
    - Configured public endpoints (/api/auth/**)
    - Configured protected endpoints (all others require authentication)
    - Added exception handling (401 Unauthorized, 403 Forbidden)
    - Enabled @PreAuthorize support (@EnableMethodSecurity)
  - Deleted TestController (no longer needed)
  - Testing results:
    - Login and get token: [x]
    - Access protected endpoint WITHOUT token: [x] (401 Unauthorized)
    - Access protected endpoint WITH token: [x] (200 OK)
    - Remove token: [x] (401 Unauthorized again)
    - JWT validation in filter: [x]
    - User loading from database: [x]
    - SecurityContext authentication: [x]

**What We Built:**
```
src/main/java/com/gambling/betting_odds_api/
├── config/
│   └── SecurityConfig.java          # Production security configuration
├── model/
│   ├── Role.java                    # USER, BOOKMAKER, ADMIN enum
│   └── User.java                    # User entity
├── repository/
│   └── UserRepository.java          # User data access
├── security/
│   ├── JwtTokenProvider.java        # JWT generation and validation
│   ├── CustomUserDetailsService.java # Load users for Spring Security
│   └── JwtAuthenticationFilter.java  # Request interception and authentication
├── dto/
│   ├── LoginRequest.java            # Login DTO
│   ├── RegisterRequest.java         # Register DTO
│   └── AuthResponse.java            # Auth response DTO
├── service/
│   └── AuthService.java             # Authentication business logic
└── controller/
    └── AuthController.java          # Authentication REST endpoints

Database:
└── users table (id, username, email, password, role, active, timestamps)

Configuration:
└── application.properties (jwt.secret, jwt.expiration, jwt.prefix)
```


**Authentication Flow:**
```
1. User registers:
   POST /api/auth/register -> AuthController -> AuthService
   -> Hash password (BCrypt) -> Save User to database
   -> Generate JWT token -> Return AuthResponse

2. User logs in:
   POST /api/auth/login -> AuthController -> AuthService
   -> Validate credentials (BCrypt.matches) -> Generate JWT token
   -> Return AuthResponse

3. Authenticated request:
   GET /api/odds (with Authorization: Bearer <token>)
   -> JwtAuthenticationFilter intercepts request
   -> Extract and validate token
   -> Load UserDetails from database (CustomUserDetailsService)
   -> Set authentication in SecurityContext
   -> Request proceeds to controller (user is authenticated)
```

**Security Features Implemented:**
- [x] JWT token generation (HMAC-SHA256, 24h expiration)
- [x] JWT token validation (signature, expiration, username)
- [x] BCrypt password hashing and verification
- [x] User registration with validation
- [x] User login with credential validation
- [x] Request interception and authentication
- [x] SecurityContext management (request-scoped)
- [x] Public endpoints (/api/auth/**)
- [x] Protected endpoints (require authentication)
- [x] Duplicate username/email prevention
- [x] Account status management (active/inactive)
- [x] Exception handling (401, 403)
- [x] User enumeration protection (same error for invalid user/password)

**Testing Results:**

| Test | Expected | Result |
|------|----------|--------|
| Register new user | 200 OK + token | [x] Pass |
| Login with valid credentials | 200 OK + token | [x] Pass |
| Duplicate username | 400 Bad Request | [x] Pass |
| Duplicate email | 400 Bad Request | [x] Pass |
| Wrong password | 401 Unauthorized | [x] Pass |
| Non-existent user | 401 Unauthorized | [x] Pass |
| JWT token structure | Valid JWT (3 parts) | [x] Pass |
| Access without token | 401 Unauthorized | [x] Pass |
| Access with valid token | 200 OK | [x] Pass |
| Remove token | 401 Unauthorized | [x] Pass |

#### Week 3: Role-Based Access Control (Days 8-10) [COMPLETE]

**Goal:** Secure endpoints based on user roles

**Progress:**
- [x] Day 8: Configure method security
  - Added @PreAuthorize annotations to all BettingOddsController endpoints
  - Configured role-based permissions:
    - USER: Read-only access (GET endpoints only)
    - BOOKMAKER: Read + Create + Update (GET, POST, PUT, PATCH)
    - ADMIN: Full access including DELETE
  - Added AccessDeniedException handler (403 Forbidden responses)
  - Created test users (USER, BOOKMAKER, ADMIN)
  - Testing results:
    - USER: [x] Can read, [x] Cannot create/update/delete (403)
    - BOOKMAKER: [x] Can read/create/update, [x] Cannot delete (403)
    - ADMIN: [x] Full access to all operations
  - Deleted temporary test endpoint (hash-password)
  
- [ ] Day 9: Advanced authorization scenarios [SKIPPED - Optional]
  - Add more granular permissions (e.g., user can only update own bets)
  - Implement endpoint-specific authorization logic
  - Add authorization audit logging
  
- [x] Day 10: Update tests with JWT authentication
  - Updated BettingOddsControllerTest with JWT token generation
  - Added UserRepository, JwtTokenProvider, BCryptPasswordEncoder to tests
  - Created test users with different roles (USER, BOOKMAKER, ADMIN) in @BeforeEach
  - Generated unique usernames with timestamp suffix (fixes duplicate key constraint)
  - Updated all 12 existing tests to include Authorization header with JWT token
  - Added 6 NEW authorization tests:
    - POST /api/odds - USER role -> 403 Forbidden
    - PUT /api/odds/{id} - USER role -> 403 Forbidden
    - PATCH /api/odds/{id}/deactivate - USER role -> 403 Forbidden
    - DELETE /api/odds/{id} - USER role -> 403 Forbidden
    - DELETE /api/odds/{id} - BOOKMAKER role -> 403 Forbidden
    - GET /api/odds - No token -> 401 Unauthorized
  - Fixed pagination test (PageResponse uses "pageSize" not "size")
  - All 18 tests passing

**Week 3 Summary:**
- 18 integration tests for BettingOddsController (12 updated + 6 new)
- 100% Controller coverage with JWT authentication
- Role-based access control fully tested
- Test users with unique identifiers (timestamp suffix)
- Authorization matrix verified:
  - USER: [x] Can GET, [x] Cannot POST/PUT/PATCH/DELETE (403)
  - BOOKMAKER: [x] Can GET/POST/PUT/PATCH, [x] Cannot DELETE (403)
  - ADMIN: [x] Full access (GET/POST/PUT/PATCH/DELETE)
  - No token: [x] 401 Unauthorized

#### Week 4: Testing & Documentation (Days 11-14) [PLANNED]

**Goal:** Comprehensive security testing and documentation

**Planned Tasks:**
- [ ] Day 11-12: Security tests
  - Test authentication (login with valid/invalid credentials)
  - Test authorization (access endpoints with different roles)
  - Test JWT validation (expired token, invalid token)
  - Update existing tests with JWT authentication
  
- [ ] Day 13: Integration tests with JWT
  - Test complete flow (register -> login -> access protected endpoint)
  - Test security filter chain
  - Test role-based access control
  
- [ ] Day 14: Documentation and cleanup
  - Update README with authentication guide
  - Update Swagger with security scheme
  - Final review and commit
  - Phase 3 completion celebration

#### Phase 3 Feature Summary

**3.1 Spring Security Implementation [COMPLETE]**
- [x] Spring Security dependency
- [x] Security configuration class
- [x] Password encoding (BCrypt)
- [x] Authentication manager
- [x] Security filter chain
- [x] CORS configuration (disabled CSRF)

**3.2 JWT Token Authentication [COMPLETE]**
- [x] JWT library (jjwt 0.12.6) integration
- [x] Token generation service (JwtTokenProvider)
- [x] Token validation filter (JwtAuthenticationFilter)
- [x] Token expiration handling (24 hours)
- [ ] Refresh token mechanism [FUTURE]
- [ ] Blacklist for revoked tokens [FUTURE]

**3.3 User Management [COMPLETE]**
- [x] User entity (username, email, password, role, active)
- [x] User repository (with custom query methods)
- [x] User service (AuthService - register, login)
- [x] Registration endpoint (POST /api/auth/register)
- [x] Login endpoint (POST /api/auth/login)
- [ ] Logout endpoint [FUTURE]
- [ ] Password reset functionality [FUTURE]

**3.4 Role-Based Access Control (RBAC) [COMPLETE]**
- [x] Role enum (USER, ADMIN, BOOKMAKER)
- [x] Method-level security enabled (@EnableMethodSecurity)
- [x] Endpoint-level authorization with @PreAuthorize
- [x] Role-based permissions implemented:
  - USER: Read-only (GET endpoints)
  - BOOKMAKER: Read + Create + Update (GET, POST, PUT, PATCH)
  - ADMIN: Full access including DELETE
- [x] 403 Forbidden handling for insufficient permissions
- [x] Testing with multiple user roles

**Role Permissions (Implemented):**
```java
// USER - Read-only access
@PreAuthorize("hasAnyRole('USER', 'BOOKMAKER', 'ADMIN')")
GET /api/odds - Allowed for all authenticated users

// BOOKMAKER - Create and update odds
@PreAuthorize("hasAnyRole('BOOKMAKER', 'ADMIN')")
POST /api/odds - Allowed for BOOKMAKER and ADMIN
PUT /api/odds/{id} - Allowed for BOOKMAKER and ADMIN

// ADMIN - Full access including delete
@PreAuthorize("hasRole('ADMIN')")
DELETE /api/odds/{id} - Allowed for ADMIN only
```

**3.5 Rate Limiting [PLANNED Phase 4]**
- [ ] Rate limiting interceptor
- [ ] In-memory rate limiter (Bucket4j)
- [ ] Per-user rate limits
- [ ] Per-endpoint rate limits
- [ ] Rate limit headers (X-RateLimit-*)

**Key Learning Outcomes:**
- [x] Spring Security architecture and configuration
- [x] Password security best practices (BCrypt hashing with salt)
- [x] Stateless authentication (JWT tokens)
- [x] User entity design and repository pattern
- [x] Enum-based role management
- [x] JWT token structure and signing (Header, Payload, Signature)
- [x] JWT generation with HMAC-SHA256
- [x] Token validation and claim extraction
- [x] DTO pattern for authentication (separation of concerns)
- [x] Bean validation (@NotBlank, @Email, @Size)
- [x] Spring Security filter chain
- [x] UserDetailsService implementation
- [x] Authentication vs Authorization
- [x] SecurityContext management
- [x] Exception handling (401 Unauthorized, 403 Forbidden)
- [x] Role-based authorization (@PreAuthorize annotations)
- [x] Method-level security (hasRole, hasAnyRole)
- [ ] API security patterns [PLANNED Week 4]

---

### Phase 4: Performance & Reliability [IN PROGRESS]
**Duration:** 3-4 weeks | **Complexity:** [ADVANCED]

**Prerequisites:**
- Understanding of caching strategies
- Database performance tuning basics
- Docker fundamentals
- JPA/Hibernate relationship mapping

---
#### Week 1: Redis Caching (Days 1-6) [COMPLETE]

**Goal:** Implement Redis caching for dramatic performance improvement

**Progress:**

**Day 1: Docker & Redis setup [COMPLETE]**
- [x] Installed Docker Desktop
- [x] Configured Redis 7 Alpine container (redis-betting on port 6379)
- [x] Learned Docker basics (images, containers, commands)
- [x] Established daily workflow (docker start/stop)

**Day 2: Spring Boot + Redis integration [COMPLETE]**
- [x] Added Spring Data Redis dependency
- [x] Configured Redis connection (localhost:6379, Lettuce pooling)
- [x] Created RedisConfig.java with RedisTemplate bean
- [x] Created RedisConnectionTest.java (3 tests passing)
- [x] Verified basic Redis operations (set/get/delete)
  
**Day 3: Caching annotations implementation [COMPLETE]**
- [x] Configured CacheManager with 30-minute TTL
- [x] Added @Cacheable to getOddsById() (automatic caching)
- [x] Added @CachePut to updateOdds() (cache updates)
- [x] Added @CacheEvict to deleteOdds() and deactivateOdds() (cache invalidation)
- [x] Fixed Jackson LocalDateTime serialization (JSR310 module)
- [x] Fixed Jackson type information (PolymorphicTypeValidator)
- [x] Tested all caching scenarios with Postman
- [x] Wrote 8 unit tests with Testcontainers
- [x] All cache tests passing (8/8)
- [x] Performance improvement: 15-37x faster (750ms -> 20-50ms) [FAST]

**Day 4: Pagination Caching Implementation [COMPLETE]**
- [x] Added @Cacheable to pagination methods
  - getAllOdds() - caches first 3 pages (page 0-2)
  - getActiveOdds() - caches first 3 pages
  - getOddsBySport() - caches first 3 pages per sport
  - getUpcomingMatches() - caches first 3 pages
  - getMatchesForTeam() - caches first 3 pages per team
- [x] Updated @CacheEvict annotations
  - createOdds() - evicts ALL caches (single + pagination)
  - updateOdds() - @CachePut (single) + @CacheEvict (pagination)
  - deactivateOdds() - evicts ALL caches
  - deleteOdds() - evicts ALL caches
- [x] Added 8 new unit tests (total 16 tests)
  - Cache hit/miss for pagination
  - Different pages create separate cache entries
  - Page 3 not cached (condition: pageNumber < 3)
  - Cache eviction on CREATE/UPDATE/DELETE
  - Sport/Team-specific caching
- [x] All tests passing (16/16)
- [x] Comprehensive JavaDoc comments with production recommendations

**Day 5: Cache Monitoring & Admin Endpoints [COMPLETE]**
- [x] Created CacheStatisticsService
  - getAllCacheStatistics() - get stats for all caches
  - getCacheStatistics(cacheName) - get stats for specific cache
  - clearAllCaches() - clear all caches (destructive)
  - clearCache(cacheName) - clear specific cache
  - isRedisHealthy() - check Redis connectivity
  - getCacheHealth() - comprehensive health status
- [x] Created CacheAdminController (ADMIN only)
  - GET /api/admin/cache/stats - cache statistics
  - GET /api/admin/cache/{name}/stats - specific cache stats
  - GET /api/admin/cache/health - cache health check
  - POST /api/admin/cache/clear - clear all caches
  - POST /api/admin/cache/{name}/clear - clear specific cache
- [x] All endpoints secured with @PreAuthorize("hasRole('ADMIN')")
- [x] Comprehensive JavaDoc documentation

**Day 6: Cache Event Logging & Metrics [COMPLETE]**
- [x] Created CacheEventLogger (error handling)
  - Custom CacheErrorHandler implementation
  - Graceful degradation on cache failures
  - Detailed error logging for debugging
  - Prevents cache errors from breaking app
- [x] Extended PerformanceLogger with cache metrics
  - logCacheOperation() - track cache hits/misses
  - Performance thresholds for cache operations
  - Automatic slow operation detection
- [x] Created CacheMonitoringAspect (optional)
  - Automatic monitoring of @Cacheable methods
  - Automatic monitoring of @CachePut methods
  - Automatic monitoring of @CacheEvict methods
  - AOP-based performance tracking
- [x] Added Spring AOP dependency
- [x] Production-ready cache monitoring system

**Docker Setup:**
```bash
# Redis container running on port 6379
docker run -d --name redis-betting -p 6379:6379 redis:7-alpine

# Daily workflow:
docker start redis-betting   # Start of day
docker stop redis-betting    # End of day
```

**What We Built:**
```
src/main/java/com/gambling/betting_odds_api/
├── config/
│   └── RedisConfig.java             # Redis configuration & CacheManager
├── service/
│   └── CacheStatisticsService.java  # Cache monitoring service [NEW Day 5]
├── controller/
│   └── CacheAdminController.java    # Admin cache endpoints [NEW Day 5]
├── logging/
│   ├── CacheEventLogger.java        # Cache error handler [NEW Day 6]
│   └── PerformanceLogger.java       # Extended with cache metrics [UPDATED Day 6]
├── aspect/
│   └── CacheMonitoringAspect.java   # AOP cache monitoring [NEW Day 6]
└── src/test/java/
    └── BettingOddsServiceCacheTest.java  # 16 cache tests with Testcontainers
```

**What We Learned (Days 1-6):**
- [x] Docker containerization basics
- [x] Redis in-memory caching
- [x] Spring Cache abstraction (@Cacheable, @CachePut, @CacheEvict)
- [x] TTL (Time-To-Live) configuration
- [x] Jackson JSON serialization with Java 8 Time API
- [x] Jackson type information for polymorphic types
- [x] Cache hit/miss scenarios
- [x] Cache eviction strategies
- [x] Pagination caching with SpEL expressions
- [x] Conditional caching (condition attribute)
- [x] Multi-cache eviction (value array)
- [x] Integration testing with Testcontainers
- [x] Cache monitoring and health checks [NEW Day 5]
- [x] Admin endpoints for cache management [NEW Day 5]
- [x] Error handling for cache operations [NEW Day 6]
- [x] AOP-based performance monitoring [NEW Day 6]

**Caching Strategy Implemented:**

**Single Record Cache:**
```bash
# Cache hits (fast!)
GET /api/odds/1 -> First time: 750ms (DB query + cache)
GET /api/odds/1 -> Second time: 20-50ms (from Redis) [FAST]

# Cache updates
PUT /api/odds/1 -> Updates DB + Updates Redis cache

# Cache eviction
DELETE /api/odds/1 -> Deletes from DB + Removes from Redis
PATCH /api/odds/1/deactivate -> Updates DB + Removes from Redis
```

**Pagination Caching [NEW Day 4]:**
```bash
# Page 0-2 cached (fast!)
GET /api/odds?page=0 -> First time: 750ms (DB query + cache)
GET /api/odds?page=0 -> Second time: 20-50ms (from Redis) [FAST]
GET /api/odds?page=1 -> First time: 750ms (separate cache entry)
GET /api/odds?page=1 -> Second time: 20-50ms (from Redis) [FAST]

# Page 3+ NOT cached (always query DB)
GET /api/odds?page=3 -> Always: 750ms (condition: pageNumber < 3)

# Sport-specific caching
GET /api/odds/sport/Football?page=0 -> Cached separately from Basketball
GET /api/odds/sport/Basketball?page=0 -> Independent cache

# Team-specific caching
GET /api/odds/team/Barcelona?page=0 -> Cached separately per team
GET /api/odds/team/Real Madrid?page=0 -> Independent cache

# Cache eviction on CREATE/UPDATE/DELETE
POST /api/odds -> Evicts ALL caches (single + pagination)
PUT /api/odds/1 -> Updates single cache + Evicts pagination caches
DELETE /api/odds/1 -> Evicts ALL caches
```

**Redis Configuration:**

**Single Record Cache:**
- Cache namespace: `odds`
- Cache keys: `odds::{id}`
- Example: `odds::123`

**Pagination Caches [NEW Day 4]:**
- Cache namespace: `odds-all` (getAllOdds)
  - Cache keys: `odds-all::{pageNumber}-{pageSize}-{sort}`
  - Example: `odds-all::0-10-matchDate: DESC`
  
- Cache namespace: `odds-active` (getActiveOdds)
  - Cache keys: `odds-active::{pageNumber}-{pageSize}-{sort}`
  
- Cache namespace: `odds-sport` (getOddsBySport)
  - Cache keys: `odds-sport::{sport}-{pageNumber}-{pageSize}-{sort}`
  - Example: `odds-sport::Football-0-10-matchDate: DESC`
  
- Cache namespace: `odds-upcoming` (getUpcomingMatches)
  - Cache keys: `odds-upcoming::{pageNumber}-{pageSize}-{sort}`
  
- Cache namespace: `odds-team` (getMatchesForTeam)
  - Cache keys: `odds-team::{teamName}-{pageNumber}-{pageSize}-{sort}`
  - Example: `odds-team::Barcelona-0-10-matchDate: DESC`

**Global Settings:**
- TTL: 30 minutes (configurable in RedisConfig.java)
- Serialization: JSON with type hints
- Storage format: ["ClassName", {...data}]
- Page limit: First 3 pages only (page 0-2)

**Production Recommendation:**
- Consider shorter TTL (5-10 minutes) for live betting odds
- Cache only page 0-1 for frequently changing data

---

#### Week 2: Database Optimization & N+1 Problem Resolution (Days 6-11) [COMPLETE]

**Goal:** Optimize database queries and eliminate N+1 query problem

**Progress:**

**Day 6: Problem Identification [COMPLETE]**
- [x] Identify N+1 problem in codebase
- [x] Analyze query patterns with logging
- [x] Measure baseline performance (1 + N queries)
- [x] Document problem scope

**What is N+1 Problem?**
```java
// BAD: Lazy Loading causes N+1 queries
List odds = repository.findAll();  // 1 query
for (BettingOdds odd : odds) {
    String creator = odd.getCreatedBy().getUsername();  // N queries!
}
// Total: 1 + N queries (if 100 records = 101 queries!)
```

**Day 7: Database Schema Migration [COMPLETE]**
- [x] Create migration script (V3__add_created_by_column.sql)
- [x] Add created_by_user_id column to betting_odds table
- [x] Add foreign key constraint to users table
- [x] Add database index on created_by_user_id
- [x] Test migration on H2 and PostgreSQL

**Migration Script:**
```sql
-- V3__add_created_by_column.sql
ALTER TABLE betting_odds 
ADD COLUMN created_by_user_id BIGINT;

ALTER TABLE betting_odds 
ADD CONSTRAINT fk_betting_odds_created_by 
FOREIGN KEY (created_by_user_id) REFERENCES users(id);

CREATE INDEX idx_created_by_user_id 
ON betting_odds(created_by_user_id);
```

**Day 8: Entity Relationship Implementation [COMPLETE]**
- [x] Add @ManyToOne relationship in BettingOdds entity
- [x] Configure FetchType.LAZY (default, avoid eager loading)
- [x] Add @JoinColumn annotation (created_by_user_id)
- [x] Update existing tests to handle new relationship

**Entity Changes:**
```java
@Entity
@Table(name = "betting_odds")
public class BettingOdds {
    // ... existing fields
    
    // NEW: Relationship to User (who created this odds record)
    @ManyToOne(fetch = FetchType.LAZY)  // Lazy = N+1 problem by default!
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;
}
```
**Day 9: Repository Optimization [COMPLETE]**
- [x] Create findAllOdds() - BAD method (demonstrates N+1)
- [x] Create findAllOddsWithCreator() - GOOD method (JOIN FETCH)
- [x] Create findAllOddsWithCreatorUsingEntityGraph() - Alternative (@EntityGraph)
- [x] Add paginated version with JOIN FETCH
- [x] Fix all cache tests (16/16 passing)
- [x] Fix Sort field naming issues (matchDate vs match_date)

**Repository Methods:**
```java
// BAD: Causes N+1 problem
@Query("SELECT o FROM BettingOdds o WHERE o.active = true")
List findAllOdds();

// GOOD: JOIN FETCH prevents N+1 (1 query only!)
@Query("SELECT o FROM BettingOdds o LEFT JOIN FETCH o.createdBy WHERE o.active = true")
List findAllOddsWithCreator();

// ALTERNATIVE: @EntityGraph (same result as JOIN FETCH)
@EntityGraph(attributePaths = {"createdBy"})
@Query("SELECT o FROM BettingOdds o WHERE o.active = true")
List findAllOddsWithCreatorUsingEntityGraph();

// Paginated version
@Query(
    value = "SELECT o FROM BettingOdds o LEFT JOIN FETCH o.createdBy WHERE o.active = true",
    countQuery = "SELECT COUNT(o) FROM BettingOdds o WHERE o.active = true"
)
Page findAllOddsWithCreator(Pageable pageable);
```

**Day 10: N+1 Problem Testing [COMPLETE]**
- [x] Create NPlusOneProblemTest.java (5 comprehensive tests)
- [x] Test N+1 problem demonstration (BAD method)
- [x] Test JOIN FETCH solution (GOOD method)
- [x] Test @EntityGraph alternative
- [x] Verify data integrity across methods
- [x] Test paginated JOIN FETCH

**Test Results:**
```
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS [x]
```

**Day 11: Documentation & README Update [COMPLETE]**
- [x] Update README.md with N+1 section
- [x] Add performance metrics
- [x] Document best practices
- [x] Update project statistics

**What We Built (Week 2):**
```
src/main/resources/db/migration/
└── V3__add_created_by_column.sql          # Database migration

src/main/java/.../model/
└── BettingOdds.java                      # Added @ManyToOne relationship

src/main/java/.../repository/
└── BettingOddsRepository.java            # N+1 solution methods:
    ├── findAllOdds()                      # BAD: Causes N+1
    ├── findAllOddsWithCreator()          # GOOD: JOIN FETCH
    └── findAllOddsWithCreatorUsingEntityGraph()  # GOOD: @EntityGraph

src/test/java/.../repository/
└── NPlusOneProblemTest.java              # 5 tests proving solution works
```

**Performance Impact:**

| Scenario | Method | Queries | Time | Improvement |
|----------|--------|---------|------|-------------|
| 10 records | findAllOdds() (BAD) | 11 (1+10) | ~100-500ms | Baseline |
| 10 records | JOIN FETCH (GOOD) | 1 | ~10-50ms | **10-50x faster** [FAST] |
| 100 records | findAllOdds() (BAD) | 101 (1+100) | ~1-5sec | Baseline |
| 100 records | JOIN FETCH (GOOD) | 1 | ~50-100ms | **20-50x faster** [FAST] |
| 1000 records | findAllOdds() (BAD) | 1001 (1+1000) | ~10-50sec | Unacceptable |
| 1000 records | JOIN FETCH (GOOD) | 1 | ~200-500ms | **50-100x faster** [FAST] |

**SQL Generated:**

**BAD: N+1 Problem (11 queries for 10 records)**
```sql
SELECT * FROM betting_odds WHERE active = true;        -- 1 query
SELECT * FROM users WHERE id = ?;                      -- Query 1
SELECT * FROM users WHERE id = ?;                      -- Query 2
SELECT * FROM users WHERE id = ?;                      -- Query 3
-- ... (7 more queries)
```

**GOOD: JOIN FETCH Solution (1 query total)**
```sql
SELECT o.*, u.* 
FROM betting_odds o 
LEFT JOIN users u ON o.created_by_user_id = u.id 
WHERE o.active = true;                                  -- 1 query only!
```
**Key Learning Outcomes (Week 2):**
- [x] Understanding N+1 problem symptoms and detection
- [x] Database migration with foreign keys
- [x] JPA relationship mapping (@ManyToOne, @JoinColumn)
- [x] Lazy vs Eager fetching strategies
- [x] JOIN FETCH in JPQL queries
- [x] @EntityGraph as alternative to JOIN FETCH
- [x] Query performance optimization techniques
- [x] Writing tests that prove performance improvements
- [x] Hibernate Sort & Query naming conventions
- [x] JPQL vs Native SQL differences
- [x] Cache test debugging and fixing

**Testing Results:**
- NPlusOneProblemTest: 5/5 tests passing [x]
- BettingOddsServiceCacheTest: 16/16 tests passing [x]
- Total Week 2 tests: 21/21 passing [x]

---
#### 📅 Week 3: Async Processing & Advanced Optimization (Days 12-14)

**Goal:** Implement async processing for heavy operations

**Day 12: Spring Async Configuration [COMPLETE]**
- [x] Created AsyncConfig class with thread pool
  - Core pool size: 5 threads (always alive)
  - Max pool size: 10 threads (grows on demand)
  - Queue capacity: 25 tasks (buffered before rejection)
  - Thread name prefix: "Async-" (for debugging)
  - Graceful shutdown: waits 30s for tasks to complete
- [x] Custom exception handler (AsyncUncaughtExceptionHandler)
  - Logs exceptions instead of silent failures
  - Includes method name and parameters in error logs
- [x] @EnableAsync annotation activated
- [x] Thread pool configured and verified in startup logs

**What We Built:**
```
src/main/java/com/gambling/betting_odds_api/
├── config/
│   └── AsyncConfig.java             # Async configuration [NEW Day 12]
│       ├── ThreadPoolTaskExecutor    # Thread pool setup
│       ├── CustomAsyncExceptionHandler  # Error handling
│       └── @EnableAsync              # Async activation
```

**Thread Pool Configuration:**
```
Core Threads: 5 (always alive, low idle overhead)
Max Threads: 10 (handles traffic spikes)
Queue: 25 tasks (buffer before scaling up)
Shutdown: 30s graceful timeout

Request Flow:
- First 5 tasks → Core threads (immediate execution)
- Tasks 6-30 → Queue (buffered)
- Tasks 31-35 → On-demand threads (scale up)
- Beyond capacity → RejectedExecutionException
```

**Key Learning Outcomes (Day 12):**
- [x] Spring async configuration basics
- [x] ThreadPoolTaskExecutor setup
- [x] Graceful shutdown patterns
- [x] Exception handling in async methods
- [x] Thread naming for debugging

**Day 13: @Async Methods Implementation [PLANNED]**
- [ ] Identify heavy operations for async processing
- [ ] Create async service methods
- [ ] Return CompletableFuture<T>
- [ ] Handle async exceptions

**Day 14: Testing Async Operations [PLANNED]**
- [ ] Write unit tests for async methods
- [ ] Test CompletableFuture completion
- [ ] Test async exception handling
- [ ] Performance comparison (sync vs async)

**Week 4: Monitoring & Observability**
- [ ] Day 15-17: Micrometer metrics
  - [ ] Custom metrics (odds created, calculations performed)
  - [ ] Prometheus endpoint
  - [ ] Grafana dashboards
  - [ ] Alerting rules
- [ ] Day 18-21: Production monitoring
  - [ ] Health checks
  - [ ] Performance dashboards
  - [ ] Error rate monitoring
  - [ ] SLA tracking

---

#### Phase 4 Feature Summary

**4.1 Redis Caching [COMPLETE]**
- [x] Docker containerization
- [x] Redis 7 Alpine setup
- [x] Spring Data Redis integration
- [x] @Cacheable annotation (getOddsById)
- [x] @CachePut annotation (updateOdds)
- [x] @CacheEvict annotation (deleteOdds, deactivateOdds)
- [x] Pagination caching (first 3 pages)
- [x] Sport/Team-specific caching
- [x] Cache monitoring endpoints (ADMIN only)
- [x] Cache error handling
- [x] AOP-based performance monitoring
- [x] 16 comprehensive cache tests with Testcontainers

**4.2 Database Optimization [COMPLETE Week 2]**
- [x] Database migrations (Flyway)
  - V1: Initial schema (betting_odds table)
  - V2: Add users table
  - V3: Add created_by relationship [NEW]
- [x] Strategic indexes:
  - idx_sport_active (composite index on sport + active)
  - idx_match_date (single index on match_date)
  - idx_home_team (single index on home_team)
  - idx_away_team (single index on away_team)
  - idx_active_match_date (composite index for upcoming matches)
  - idx_created_by_user_id (foreign key index) [NEW]
- [x] Query optimization:
  - UNION approach for team searches (18% faster, 100-500x at scale)
  - Index Scan instead of Seq Scan (0.059ms execution time)
  - EXPLAIN ANALYZE for performance analysis
- [x] N+1 Problem Resolution [NEW]:
  - @ManyToOne relationship (BettingOdds -> User)
  - JOIN FETCH in JPQL (1 query instead of 1+N)
  - @EntityGraph alternative (same performance)
  - Performance: 10-100x faster for large datasets [FAST]
  - Paginated JOIN FETCH support
  - 5 comprehensive N+1 tests (NPlusOneProblemTest)

**4.3 Async Processing [PLANNED]**
- [ ] Spring async configuration
- [ ] @Async methods for heavy operations
- [ ] CompletableFuture usage
- [ ] Thread pool configuration

**4.4 Monitoring & Observability [PLANNED]**
- [ ] Micrometer metrics
- [ ] Custom metrics (odds created, calculations performed)
- [ ] Prometheus endpoint
- [ ] Grafana dashboards
- [ ] Alerting rules

**Key Learning Outcomes (Phase 4):**
- [x] Redis caching strategies and patterns
- [x] Spring Cache abstraction
- [x] Jackson JSON serialization challenges
- [x] TTL and cache eviction
- [x] Docker containerization
- [x] Testcontainers for integration testing
- [x] Cache monitoring and health checks
- [x] AOP-based monitoring
- [x] Database migrations with Flyway
- [x] JPA relationship mapping (@ManyToOne)
- [x] N+1 problem detection and resolution
- [x] JOIN FETCH vs @EntityGraph comparison
- [x] Query performance optimization
- [x] EXPLAIN ANALYZE usage
- [ ] Asynchronous programming [PLANNED]
- [ ] Production monitoring with Prometheus/Grafana [PLANNED]

---
### Phase 5: Microservices Architecture - FUTURE
**Duration:** 6-8 weeks | **Complexity:** [EXPERT]

**Prerequisites:**
- Strong understanding of distributed systems
- Microservices design patterns
- Docker and containerization
- Service communication patterns

**IMPORTANT:** Only transition to microservices when:
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

#### 5.2 API Gateway (Spring Cloud Gateway)
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

### Phase 6: Cloud Deployment & DevOps - ADVANCED
**Duration:** 4-6 weeks | **Complexity:** [EXPERT]

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

### Authentication Endpoints (Public)

| Method | Endpoint | Description | Request Body | Response | Auth Required |
|--------|----------|-------------|--------------|----------|---------------|
| POST | `/api/auth/register` | Register new user | `RegisterRequest` | `AuthResponse` (with JWT) | ❌ |
| POST | `/api/auth/login` | Login user | `LoginRequest` | `AuthResponse` (with JWT) | ❌ |

**Authentication Flow:**
```bash
# 1. Register new user
POST /api/auth/register
Body: {
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}
Response: {
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "username": "john",
  "email": "john@example.com",
  "role": "USER"
}

# 2. Login existing user
POST /api/auth/login
Body: {
  "username": "john",
  "password": "password123"
}
Response: {
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "username": "john",
  "email": "john@example.com",
  "role": "USER"
}

# 3. Use token for authenticated requests
GET /api/odds
Headers: {
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9..."
}
```
### Cache Admin Endpoints (ADMIN Only)

**Security:** All cache admin endpoints require ADMIN role!

| Method | Endpoint | Description | Auth Required | Min Role |
|--------|----------|-------------|---------------|----------|
| GET | `/api/admin/cache/stats` | Get statistics for all caches | YES | ADMIN |
| GET | `/api/admin/cache/{name}/stats` | Get statistics for specific cache | YES | ADMIN |
| GET | `/api/admin/cache/health` | Check cache health status | YES | ADMIN |
| POST | `/api/admin/cache/clear` | Clear all caches (destructive!) | YES | ADMIN |
| POST | `/api/admin/cache/{name}/clear` | Clear specific cache | YES | ADMIN |

**Example Usage:**
```bash
# Login as ADMIN
POST /api/auth/login
Content-Type: application/json
{
  "username": "admin",
  "password": "admin123"
}

# Get cache health
GET /api/admin/cache/health
Authorization: Bearer 

Response:
{
  "status": "UP",
  "cacheManager": "RedisCacheManager",
  "cacheCount": 6,
  "cacheNames": ["odds", "odds-all", "odds-active", "odds-sport", "odds-upcoming", "odds-team"]
}

# Get cache statistics
GET /api/admin/cache/stats
Authorization: Bearer 

Response:
{
  "odds": {
    "name": "odds",
    "type": "RedisCache",
    "nativeType": "RedisCache"
  },
  "odds-all": { ... }
}

# Clear all caches (use with caution!)
POST /api/admin/cache/clear
Authorization: Bearer 

Response:
{
  "message": "All caches cleared successfully",
  "warning": "All subsequent requests will query database until cache rebuilds"
}

# Clear specific cache
POST /api/admin/cache/odds/clear
Authorization: Bearer 

Response:
{
  "message": "Cache 'odds' cleared successfully"
}
```

**Production Warning:**
- Clearing caches causes temporary performance degradation
- All requests will query database until cache rebuilds
- Use only for maintenance or debugging
- Consider scheduling during low-traffic periods

### Odds Management (Protected - Requires Authentication + Role-Based Authorization)

**Role-Based Permissions:**

| Endpoint | USER | BOOKMAKER | ADMIN | Required Role |
|----------|------|-----------|-------|---------------|
| GET /api/odds | YES | YES | YES | USER, BOOKMAKER, ADMIN |
| GET /api/odds/{id} | YES | YES | YES | USER, BOOKMAKER, ADMIN |
| GET /api/odds/sport/{sport} | YES | YES | YES | USER, BOOKMAKER, ADMIN |
| POST /api/odds | NO | YES | YES | BOOKMAKER, ADMIN |
| PUT /api/odds/{id} | NO | YES | YES | BOOKMAKER, ADMIN |
| PATCH /api/odds/{id}/deactivate | NO | YES | YES | BOOKMAKER, ADMIN |
| DELETE /api/odds/{id} | NO | NO | YES | ADMIN only |

All GET endpoints support pagination and sorting.

**Query Parameters:**
- `page` - Page number (0-indexed)
- `size` - Items per page (default: 20, max: 100)
- `sort` - Sort field and direction (format: `property,direction`)

**Examples:**
```bash
# First page with 10 items (requires JWT token)
GET /api/odds?page=0&size=10
Headers: Authorization: Bearer <your-jwt-token>

# Sort by date descending
GET /api/odds?sort=matchDate,desc
Headers: Authorization: Bearer <your-jwt-token>

# Multiple sort fields
GET /api/odds?page=1&size=20&sort=sport,asc&sort=homeOdds,desc
Headers: Authorization: Bearer <your-jwt-token>
```

### Endpoints Table

| Method | Endpoint | Description | Request Body | Response | Pagination | Auth Required | Min Role |
|--------|----------|-------------|--------------|----------|------------|---------------|----------|
| GET | `/api/odds` | Get all odds | - | `PageResponse<OddsResponse>` | YES | YES | USER |
| GET | `/api/odds/active` | Get active odds only | - | `PageResponse<OddsResponse>` | YES | YES | USER |
| GET | `/api/odds/{id}` | Get odds by ID | - | `OddsResponse` | NO | YES | USER |
| GET | `/api/odds/sport/{sport}` | Get odds by sport | - | `PageResponse<OddsResponse>` | YES | YES | USER |
| GET | `/api/odds/upcoming` | Get upcoming matches | - | `PageResponse<OddsResponse>` | YES | YES | USER |
| GET | `/api/odds/team/{teamName}` | Get matches for team | - | `PageResponse<OddsResponse>` | YES | YES | USER |
| GET | `/api/odds/{id}/margin` | Calculate bookmaker margin | - | `OddsResponse` (with calculations) | NO | YES | USER |
| POST | `/api/odds` | Create new odds | `CreateOddsRequest` | `OddsResponse` | NO | YES | BOOKMAKER |
| PUT | `/api/odds/{id}` | Update odds | `UpdateOddsRequest` | `OddsResponse` | NO | YES | BOOKMAKER |
| PATCH | `/api/odds/{id}/deactivate` | Deactivate odds (soft delete) | - | Success message | NO | YES | BOOKMAKER |
| DELETE | `/api/odds/{id}` | Delete odds permanently | - | Success message | NO | YES | ADMIN |

---

## Database Schema

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

### `users` Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGSERIAL | PRIMARY KEY | Unique identifier |
| `username` | VARCHAR(50) | NOT NULL, UNIQUE | User's login name |
| `email` | VARCHAR(100) | NOT NULL, UNIQUE | User's email address |
| `password` | VARCHAR(255) | NOT NULL | BCrypt hashed password (60 chars) |
| `role` | VARCHAR(20) | NOT NULL | User role (USER, BOOKMAKER, ADMIN) |
| `active` | BOOLEAN | NOT NULL, DEFAULT true | Account status (soft delete) |
| `created_at` | TIMESTAMP | NOT NULL | Account creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL | Last update timestamp |

**Indexes:**
- `users_pkey` PRIMARY KEY on `id`
- `uk_username` UNIQUE CONSTRAINT on `username`
- `uk_email` UNIQUE CONSTRAINT on `email`

**Security Features:**
- Passwords stored as BCrypt hash (never plain text)
- Unique username and email (prevent duplicates)
- Soft delete with `active` flag (preserve audit trail)
- Automatic timestamps with @PrePersist/@PreUpdate

### Indexes
- `idx_sport_active` ON `betting_odds(sport, active)` - Fast filtering by sport
- `idx_match_date` ON `betting_odds(match_date)` - Fast date queries
- `idx_home_team` ON `betting_odds(home_team)` - Fast team searches
- `idx_away_team` ON `betting_odds(away_team)` - Fast team searches

---

## Getting Started

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

# JWT Configuration
jwt.secret=your-secret-key-here-min-256-bits
jwt.expiration=86400000
jwt.prefix=Bearer
```

4. **Install dependencies**
```bash
mvn clean install -DskipTests
```

5. **Run the application**
```bash
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080`

### Quick Test

**Step 1: Register a user**
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

**Step 2: Login (get JWT token)**
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "username": "testuser",
  "email": "test@example.com",
  "role": "USER"
}
```

**Step 3: Access protected endpoint**
```bash
POST http://localhost:8080/api/odds
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
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

## API Documentation & Monitoring

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

## Logging System

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
- [x] Layered Architecture (Controller -> Service -> Repository)
- [x] DTO Pattern (API/Database separation)
- [x] Mapper Pattern (DTO <-> Entity conversion)
- [x] Repository Pattern (Data access abstraction)
- [x] Dependency Injection (IoC)
- [x] Security Filter Chain Pattern
- [x] UserDetailsService Pattern

### Best Practices
- [x] Bean Validation (Declarative input validation)
- [x] Global Exception Handling (`@RestControllerAdvice`)
- [x] RESTful API Design (HTTP methods, status codes)
- [x] Transaction Management (`@Transactional`)
- [x] Clean Code (Naming, SOLID principles)
- [x] Production Logging (Audit, Performance, Security)
- [x] Security Validation (SQL injection, XSS prevention)
- [x] Unit Testing (JUnit 5, Mockito, AAA pattern)
- [x] Integration Testing (@DataJpaTest, H2 database, Testcontainers)
- [x] JWT Best Practices (secure signing, expiration, validation)
- [x] Password Security (BCrypt hashing, never plain text)
- [x] N+1 Query Resolution (JOIN FETCH, @EntityGraph)

### Domain Knowledge
- [x] Gambling industry concepts (odds formats, margins)
- [x] Business logic implementation (calculations)
- [x] Data validation (industry constraints)
- [x] Regulatory compliance considerations
- [x] Security threats (SQL injection, XSS)
- [x] Audit requirements (compliance tracking)
- [x] Authentication flows (register, login, token-based)
- [x] Authorization concepts (role-based access control)

### Technologies Mastered
- [x] Spring Boot ecosystem
- [x] PostgreSQL database operations
- [x] RESTful API development
- [x] Maven build management
- [x] Git version control
- [x] Logback (enterprise logging)
- [x] Security patterns (input validation, attack detection)
- [x] JUnit 5 + Mockito (unit testing)
- [x] @DataJpaTest (integration testing)
- [x] H2 Database (in-memory testing)
- [x] Spring Security 6.x (authentication & authorization)
- [x] JWT (jjwt 0.12.6) (token generation & validation)
- [x] BCrypt (password hashing)
- [x] Docker (containerization)
- [x] Redis 7 (in-memory caching)
- [x] Spring Data Redis (caching integration)
- [x] Lettuce (Redis client)
- [x] Spring Cache (@Cacheable, @CachePut, @CacheEvict)
- [x] Testcontainers (integration testing with real containers)
- [x] Flyway (database migrations)
- [x] N+1 Query Resolution (JOIN FETCH, @EntityGraph)
- [x] Spring AOP (aspect-oriented programming)

---
## Redis Caching System

### Cache Configuration
All cache operations use Redis for in-memory storage with automatic expiration:
```
Cache namespace: "odds"
Cache keys: "odds::{id}"
TTL: 30 minutes (configurable in RedisConfig.java)
Storage: JSON with type information
Connection: localhost:6379 (Lettuce client with pooling)
```

### Caching Annotations
- [x] `@Cacheable` on `getOddsById()` - Automatically cache results
- [x] `@CachePut` on `updateOdds()` - Update cache with new values
- [x] `@CacheEvict` on `deleteOdds()` - Remove from cache
- [x] `@CacheEvict` on `deactivateOdds()` - Remove from cache
- [x] Pagination caching - First 3 pages per endpoint

### Performance Impact
```bash
# Without cache (baseline):
GET /api/odds/1 → 750ms (PostgreSQL query)
GET /api/odds/1 → 750ms (PostgreSQL query again)
GET /api/odds/1 → 750ms (PostgreSQL query again)

# With Redis cache:
GET /api/odds/1 → 750ms (cache miss, query DB, store in Redis)
GET /api/odds/1 → 20-50ms (cache hit, from Redis) ⚡ 15-37x faster!
GET /api/odds/1 → 20-50ms (cache hit, from Redis) ⚡
```

### Cache Invalidation
```bash
# Update operation (@CachePut)
PUT /api/odds/1 → Updates DB + Updates Redis cache
GET /api/odds/1 → Returns from cache (fast!) ⚡

# Delete operation (@CacheEvict)
DELETE /api/odds/1 → Deletes from DB + Removes from Redis
GET /api/odds/1 → 404 Not Found (correct!)

# Deactivate operation (@CacheEvict)
PATCH /api/odds/1/deactivate → Updates DB + Removes from Redis
GET /api/odds/1 → Cache miss, queries DB with new data
```

### Redis Docker Commands
```bash
# Start Redis container
docker start redis-betting

# Stop Redis container
docker stop redis-betting

# Check Redis status
docker ps

# Access Redis CLI
docker exec -it redis-betting redis-cli

# View all cached keys
KEYS *

# View specific cached value
GET odds::1

# Clear all cache
FLUSHALL

# Exit Redis CLI
exit
```

### Cache Configuration (production tuning)
Current configuration in `RedisConfig.java`:
```java
.entryTtl(Duration.ofMinutes(30))  // Development: 30 minutes
```

**Production recommendation:**
```java
.entryTtl(Duration.ofMinutes(5))   // Production: 5 minutes for live odds
```

Betting odds change frequently in real gambling systems, so shorter TTL is recommended for production!

## Redis Caching Tests

### Test Strategy
All caching behavior is verified with **Testcontainers** - using a real Redis Docker container for integration testing.

**Why Testcontainers?**
- Uses actual Redis 7 Alpine container (same as production)
- Automatic container lifecycle management
- Industry-standard approach for integration testing
- Catches serialization/deserialization issues
- Tests real TTL and eviction behavior

### Test Coverage (8/8 tests passing ✅)

| Test | Description | Status |
|------|-------------|--------|
| `testCacheable_FirstCall` | First call caches the result | [x] PASS |
| `testCacheable_SecondCall` | Second call returns from cache (no DB query) | [x] PASS |
| `testCacheable_CacheMiss` | Cache miss queries database | [x] PASS |
| `testCachePut_Update` | Update refreshes cache with new values | [x] PASS |
| `testCachePut_MultipleUpdates` | Multiple updates keep cache fresh | [x] PASS |
| `testCacheEvict_Delete` | Delete removes entry from cache | [x] PASS |
| `testCacheEvict_Deactivate` | Deactivate removes entry from cache | [x] PASS |
| `testCacheEvict_Deactivate_ForceQuery` | Deactivate forces next GET to query DB | [x] PASS |
| `testPaginationCache_FirstPage` | First page caching | [x] PASS |
| `testPaginationCache_DifferentPages` | Different pages cached separately | [x] PASS |
| `testPaginationCache_NotCachedBeyondLimit` | Page 3+ not cached | [x] PASS |
| `testSportCache_Separation` | Sport caches are separate | [x] PASS |
| `testTeamCache_Separation` | Team caches are separate | [x] PASS |
| `testCreateEvictsAllCaches` | CREATE evicts all pagination caches | [x] PASS |
| `testUpdateEvictsPaginationCaches` | UPDATE evicts pagination caches | [x] PASS |
| `testDeactivateEvictsAllCaches` | DEACTIVATE evicts all caches | [x] PASS |

### Running Cache Tests
```bash
# Run all cache tests
mvn test -Dtest=BettingOddsServiceCacheTest

# Prerequisites: Docker Desktop must be running
# Testcontainers will automatically pull redis:7-alpine image
```

### Test Isolation
Each test:
1. Starts with empty cache (cleared in `@BeforeEach`)
2. Creates fresh test data in database
3. Executes test scenario
4. Cleans up test data in `@AfterEach`
5. Redis container is shared across all tests for performance

---

## N+1 Query Problem Resolution

### What is N+1 Problem?

The N+1 problem occurs when your application executes 1 query to fetch N records, then executes N additional queries to fetch related data for each record. This results in **1 + N total queries** instead of just 1 optimized query.

### Example Scenario
```java
// You have 100 betting odds records in database
// Each odds record has a relationship to User (who created it)

// BAD: Lazy Loading causes N+1 queries
List<BettingOdds> odds = repository.findAll();  // 1 query
for (BettingOdds odd : odds) {
    String creator = odd.getCreatedBy().getUsername();  // 100 queries!
}
// Total: 1 + 100 = 101 queries!
```

### Performance Impact

| Records | Method | Queries | Time | Status |
|---------|--------|---------|------|--------|
| 10 | findAllOdds() (BAD) | 11 (1+10) | ~100-500ms | Baseline |
| 10 | JOIN FETCH (GOOD) | 1 | ~10-50ms | [FAST] 10-50x faster |
| 100 | findAllOdds() (BAD) | 101 (1+100) | ~1-5sec | Slow |
| 100 | JOIN FETCH (GOOD) | 1 | ~50-100ms | [FAST] 20-50x faster |
| 1000 | findAllOdds() (BAD) | 1001 (1+1000) | ~10-50sec | Unacceptable |
| 1000 | JOIN FETCH (GOOD) | 1 | ~200-500ms | [FAST] 50-100x faster |

### Solution: JOIN FETCH (Recommended)
```java
// GOOD: JOIN FETCH prevents N+1 problem
@Query("SELECT o FROM BettingOdds o LEFT JOIN FETCH o.createdBy WHERE o.active = true")
List<BettingOdds> findAllOddsWithCreator();

// This generates a single SQL query:
// SELECT o.*, u.* 
// FROM betting_odds o 
// LEFT JOIN users u ON o.created_by_user_id = u.id 
// WHERE o.active = true;
```

### Alternative: @EntityGraph
```java
// GOOD: @EntityGraph achieves same result as JOIN FETCH
@EntityGraph(attributePaths = {"createdBy"})
@Query("SELECT o FROM BettingOdds o WHERE o.active = true")
List<BettingOdds> findAllOddsWithCreatorUsingEntityGraph();
```

### Paginated JOIN FETCH
```java
// For pagination, you need separate value and countQuery
@Query(
    value = "SELECT o FROM BettingOdds o LEFT JOIN FETCH o.createdBy WHERE o.active = true",
    countQuery = "SELECT COUNT(o) FROM BettingOdds o WHERE o.active = true"
)
Page<BettingOdds> findAllOddsWithCreator(Pageable pageable);
```

### How We Detected N+1 Problem

**1. Enable Hibernate SQL logging** in application.properties:
```properties
# Show SQL queries
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Show query statistics
spring.jpa.properties.hibernate.generate_statistics=true
logging.level.org.hibernate.stat=DEBUG
```

**2. Look for repeated SELECT queries** in logs:
Hibernate: SELECT * FROM betting_odds WHERE active = true
Hibernate: SELECT * FROM users WHERE id = ?
Hibernate: SELECT * FROM users WHERE id = ?
Hibernate: SELECT * FROM users WHERE id = ?
... (repeated N times)

**3. Write tests to prove the problem** (NPlusOneProblemTest.java):
```java
@Test
@DisplayName("BAD: Lazy loading causes N+1 queries")
void testNPlusOneProblem_Demonstration() {
    // This will execute 1 + N queries
    List<BettingOdds> odds = repository.findAllOdds();
    
    // Force lazy loading by accessing relationship
    for (BettingOdds odd : odds) {
        odd.getCreatedBy().getUsername();  // Each access = 1 query!
    }
}

@Test
@DisplayName("GOOD: JOIN FETCH prevents N+1 queries")
void testJoinFetch_Solution() {
    // This executes only 1 query with JOIN
    List<BettingOdds> odds = repository.findAllOddsWithCreator();
    
    // No additional queries needed!
    for (BettingOdds odd : odds) {
        odd.getCreatedBy().getUsername();  // Already loaded!
    }
}
```

### Database Migration (V3)
```sql
-- Add foreign key relationship
ALTER TABLE betting_odds 
ADD COLUMN created_by_user_id BIGINT;

ALTER TABLE betting_odds 
ADD CONSTRAINT fk_betting_odds_created_by 
FOREIGN KEY (created_by_user_id) REFERENCES users(id);

-- Add index for performance
CREATE INDEX idx_created_by_user_id 
ON betting_odds(created_by_user_id);
```

### Key Takeaways

1. [x] Always use JOIN FETCH for @ManyToOne relationships when you know you'll access related data
2. [x] Monitor Hibernate statistics to detect N+1 problems early
3. [x] Write tests that prove your solution works (NPlusOneProblemTest)
4. [x] Use @EntityGraph as alternative to JOIN FETCH for flexibility
5. [x] Add database indexes on foreign keys for better performance
6. [x] Profile your queries with EXPLAIN ANALYZE in PostgreSQL

### Testing Results
- NPlusOneProblemTest: 5/5 tests passing [COMPLETE]
- Performance improvement: 10-100x faster [FAST]
- Query reduction: 1 query instead of 1+N

---

## Security Features

### Authentication & Authorization
- [x] JWT-based authentication - Stateless, token-based auth
- [x] BCrypt password hashing - Secure password storage (60 chars, salt)
- [x] Token validation - Signature, expiration, username verification
- [x] Security filter chain - Request interception and authentication
- [x] UserDetailsService - Load users from database
- [x] Role-based user model - USER, BOOKMAKER, ADMIN
- [x] Public endpoints - /api/auth/** (no authentication required)
- [x] Protected endpoints - All others (authentication required)
- [x] 401 Unauthorized - Invalid/missing token responses
- [x] 403 Forbidden - Insufficient permissions
- [x] User enumeration protection - Same error for invalid user/password
- [x] Duplicate prevention - Unique username and email constraints
- [x] Account status management - Active/inactive flag (soft delete)

### Input Validation & Sanitization
- [x] SQL injection detection (DROP, DELETE, INSERT, etc.)
- [x] XSS attack prevention (script tags, event handlers)
- [x] Suspicious pattern detection (multiple SQL keywords)
- [x] Transaction rollback on security violations
- [x] Comprehensive security logging

### Defense in Depth
1. **Layer 1**: Bean Validation (@Valid, @NotBlank, etc.)
2. **Layer 2**: Service layer security checks (SQL injection, XSS)
3. **Layer 3**: JPA prepared statements (parameterized queries)
4. **Layer 4**: Transaction management (rollback on violations)
5. **Layer 5**: Logging and monitoring (attack detection)
6. **Layer 6**: Spring Security filter chain (authentication & authorization)
7. **Layer 7**: JWT token validation (signature, expiration)

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

### Example: Authentication Flow
```bash
# Without token - 401 Unauthorized
GET /api/odds
Response: {
  "error": "Unauthorized",
  "message": "Authentication required. Please provide a valid JWT token."
}

# With valid token - 200 OK
GET /api/odds
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Response: [
  {
    "id": 1,
    "sport": "Football",
    ...
  }
]
```

---

## Author

**Iliyan Baykushev**

- GitHub: [@BaykushevI](https://github.com/BaykushevI)
- LinkedIn: [Iliyan Baykushev](https://www.linkedin.com/in/iliyan-baykushev/)

---

## License

This project is for **educational purposes** only.

---

## Contributing

This is a learning project, but suggestions and feedback are welcome!

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---
## What's Next?

### Immediate Next Steps (Phase 4 Week 3)
1. **Async Processing** - @Async methods for heavy operations
2. **CompletableFuture** - Advanced async patterns
3. **Thread Pool Configuration** - Optimize async execution
4. **Performance Testing** - Load testing with async operations

### After Async Processing (Phase 4 Week 4)
1. **Prometheus Integration** - Metrics collection
2. **Grafana Dashboards** - Real-time monitoring
3. **Custom Metrics** - Business KPIs tracking
4. **Alerting Rules** - Proactive issue detection

---

## Project Statistics

- **Lines of Code**: ~9,000+ (Java + XML + Properties + SQL)
- **Total Commits**: 42+
- **Features Completed**: Core CRUD + Logging + Testing + JWT Authentication + Redis Caching + N+1 Resolution [FAST]
- **Test Coverage**: ~95% (73/77 tests) [COMPLETE]
- **API Endpoints**: 17 (10 protected + 2 public + 5 admin)
- **Database Tables**: 2 (betting_odds, users)
- **Database Migrations**: 3 (V1: Initial schema, V2: Users table, V3: N+1 prevention)
- **Database Indexes**: 6 strategic indexes (including N+1 foreign key index)
- **Log Files**: 5 (application, errors, audit, performance, security)
- **Test Files**: 5 (Service, Mapper, Repository, Controller, ServiceCache, NPlusOne)
- **Security Features**: JWT + BCrypt + Filter Chain + UserDetailsService + Role-based Authorization + SQL Injection Prevention
- **Performance Features**: 
  - Redis Caching (15-37x faster) [FAST]
  - N+1 Query Resolution (10-100x faster) [FAST]
  - Strategic Database Indexes
  - Cache Monitoring & Admin Endpoints
- **Docker Containers**: 1 (Redis 7 Alpine)
- **Cache Namespaces**: 6 (odds, odds-all, odds-active, odds-sport, odds-upcoming, odds-team)

---
## Key Achievements

### Performance Optimization [FAST]
- **Redis Caching**: 15-37x performance improvement (750ms -> 20-50ms)
- **N+1 Query Resolution**: 10-100x improvement for related data fetching
- **Pagination Caching**: First 3 pages cached per endpoint
- **Query Optimization**: Strategic indexes reducing query time from seconds to milliseconds
- **JOIN FETCH Implementation**: Single query instead of 1+N queries

### Security & Authentication [COMPLETE]
- **JWT Authentication**: Stateless, token-based security
- **BCrypt Password Hashing**: Industry-standard 60-character hashes with salt
- **Role-Based Access Control**: USER, BOOKMAKER, ADMIN with @PreAuthorize
- **SQL Injection Prevention**: Detection and blocking with transaction rollback
- **XSS Attack Prevention**: Pattern detection and input sanitization
- **Security Logging**: Dedicated security.log with 365-day retention

### Testing Excellence [COMPLETE]
- **73/77 Tests Passing**: 95% test coverage
- **Unit Tests**: 24 tests (Service layer, Mapper)
- **Integration Tests**: 28 tests (Repository, Controller with JWT)
- **Cache Tests**: 16 tests with Testcontainers (real Redis container)
- **N+1 Tests**: 5 comprehensive tests proving solution
- **Testing Tools**: JUnit 5, Mockito, AssertJ, @DataJpaTest, Testcontainers

### Professional Development Practices [COMPLETE]
- **5 Specialized Log Files**: Application, Errors, Audit, Performance, Security
- **Audit Trail**: Complete compliance logging (CREATE/UPDATE/DELETE operations)
- **Performance Monitoring**: Execution time tracking, slow query detection
- **Database Migrations**: Flyway-based versioned schema evolution
- **Cache Monitoring**: Admin endpoints for statistics and health checks
- **Error Handling**: Graceful degradation with comprehensive exception handling

### Architecture & Design [COMPLETE]
- **Layered Architecture**: Controller -> Service -> Repository separation
- **DTO Pattern**: Complete API/Database decoupling
- **Security Filter Chain**: Request interception and JWT validation
- **Repository Pattern**: Spring Data JPA with custom queries
- **Dependency Injection**: Spring IoC container
- **RESTful API Design**: Proper HTTP methods and status codes

### Test Breakdown by Category

| Category | Tests | Coverage | Status |
|----------|-------|----------|--------|
| Service Layer | 16 | ~80% | [COMPLETE] |
| Service Cache | 16 | ~100% | [COMPLETE] |
| N+1 Problem | 5 | ~100% | [COMPLETE] |
| Mapper | 8 | ~100% | [COMPLETE] |
| Repository | 10 | ~100% | [COMPLETE] |
| Controller | 18 | ~100% | [COMPLETE] |
| **TOTAL** | **73** | **~95%** | **[COMPLETE]** |

### Performance Metrics Summary

| Feature | Before | After | Improvement | Status |
|---------|--------|-------|-------------|--------|
| Single Record Query | 750ms | 20-50ms | 15-37x | [FAST] |
| Related Data (10 records) | 100-500ms (11 queries) | 10-50ms (1 query) | 10-50x | [FAST] |
| Related Data (100 records) | 1-5sec (101 queries) | 50-100ms (1 query) | 20-50x | [FAST] |
| Related Data (1000 records) | 10-50sec (1001 queries) | 200-500ms (1 query) | 50-100x | [FAST] |
| Pagination Cache Hit | 750ms | 20-50ms | 15-37x | [FAST] |

### What Makes This Project Special

1. **Production-Ready**: Not just CRUD - includes logging, security, caching, monitoring
2. **Performance-Focused**: Real optimizations with measurable improvements (10-100x faster)
3. **Testing Excellence**: 95% coverage with real Docker containers (Testcontainers)
4. **Security-First**: Multi-layer defense with JWT, BCrypt, SQL injection prevention
5. **Professional Practices**: Audit logging, migrations, cache monitoring, error handling
6. **Learning Journey**: Documented progression from basics to advanced topics
7. **Real-World Scenarios**: N+1 problem resolution, caching strategies, role-based auth