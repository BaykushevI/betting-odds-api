# Betting Odds API

A production-ready RESTful API for managing betting odds for sports matches, built with Spring Boot and PostgreSQL.

## 📊 Project Status
```
Phase 1: Core CRUD API              ✅ COMPLETE
Phase 2.1: Production Logging       ✅ COMPLETE
Phase 2.2: Unit & Integration Tests ✅ COMPLETE (46/50 tests, 92% coverage)
Phase 3: Security & Authentication  ✅ COMPLETE (Week 3 Day 10 COMPLETE)
Phase 4: Performance & Reliability  ⚡ IN PROGRESS (Week 1 Days 1-2 COMPLETE)
Phase 5: Microservices & Gateway    🚀 FUTURE
Phase 6: Cloud Deployment           ☁️ ADVANCED
```

---

## 📖 Project Overview

This is a comprehensive **learning project** demonstrating professional backend development practices relevant to the **gambling industry**. It implements a complete CRUD API with proper architectural patterns, validation, error handling, business logic, **enterprise-grade logging**, and **JWT authentication**.

### 🎯 Learning Goals

- ✅ Master Spring Boot ecosystem (Web, Data JPA, Validation, Security)
- ✅ Understand production-ready development practices
- ✅ Learn gambling industry domain concepts (odds, margins, probabilities)
- ✅ **Implement professional logging for compliance and debugging**
- ✅ **Implement JWT-based authentication and authorization**
- ✅ Progress from monolith to microservices architecture
- ✅ Implement enterprise-level features (logging, security, monitoring)

---

## 🏗️ Architecture Evolution

### Current: Monolithic Architecture with JWT Authentication (Phase 1-3)
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
│  │   - AuthService (register, login, JWT) │ │
│  │   - Password hashing (BCrypt)          │ │
│  └─────────────────┬──────────────────────┘ │
│                    ↓                         │
│  ┌────────────────────────────────────────┐ │
│  │   REPOSITORY LAYER                     │ │
│  │   - Spring Data JPA                    │ │
│  │   - Database queries                   │ │
│  │   - UserRepository (authentication)    │ │
│  │   - BettingOddsRepository              │ │
│  └─────────────────┬──────────────────────┘ │
└────────────────────┼──────────────────────┘
                     ↓
         ┌──────────────────────┐
         │   PostgreSQL DB      │
         │   - betting_odds     │
         │   - users            │
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
- **Spring Security** - Authentication & Authorization ✅ **COMPLETE**
- **PostgreSQL 18** - Relational Database
- **Maven** - Build and dependency management

### Security Stack
- **Spring Security 6.x** - Security framework ✅ **IN USE**
- **JWT (jjwt 0.12.6)** - JSON Web Tokens ✅ **IN USE**
- **BCrypt** - Password hashing algorithm ✅ **IN USE**
- ✅ **Docker** (containerization, image/container management) - NEW Phase 4
- ✅ **Redis 7** (in-memory caching, data structures) - NEW Phase 4
- ✅ **Spring Data Redis** (caching integration) - NEW Phase 4
- ✅ **Lettuce** (Redis client, connection pooling) - NEW Phase 4

### Production Tools
- **Logback** - Advanced logging framework ✅ **COMPLETE**
- **Spring Boot Actuator** - Monitoring and health checks
- **Springdoc OpenAPI 2.8.8** - Swagger/OpenAPI documentation
- **Lombok** - Reduce boilerplate code

### Testing Tools
- **JUnit 5** - Unit testing framework ✅ **IN USE**
- **Mockito** - Mocking framework ✅ **IN USE**
- **@DataJpaTest** - Repository integration tests ✅ **IN USE**
- **H2 Database** - In-memory database for tests ✅ **IN USE**

### Phase 4 Technologies (IN USE)
- **Docker** - Containerization 🐳 ✅ **IN USE**
- **Redis 7** - In-memory caching ⚡ ✅ **IN USE**
- **Spring Data Redis** - Redis integration ✅ **IN USE**
- **Lettuce** - Redis client (connection pooling) ✅ **IN USE**

### Future Technologies
- **Prometheus** - Metrics collection 📊 *Phase 4 Week 4*
- **Grafana** - Monitoring dashboards 📈 *Phase 4 Week 4*

---

## ✨ Current Features (Phase 1-3 ✅)

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

### Production Logging System ✅ **COMPLETE**
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

### Authentication & Authorization System ✅ **COMPLETE** (Week 2)
- ✅ **JWT-based authentication** (stateless, token-based)
- ✅ **User registration and login** (POST /api/auth/register, /api/auth/login)
- ✅ **BCrypt password hashing** (60-character hash with salt)
- ✅ **JWT token generation** (24-hour expiration)
- ✅ **JWT token validation** (signature, expiration, username)
- ✅ **Custom authentication filter** (JwtAuthenticationFilter)
- ✅ **User details service** (CustomUserDetailsService)
- ✅ **Role-based user model** (USER, BOOKMAKER, ADMIN)
- ✅ **Protected endpoints** (/api/odds/** requires authentication)
- ✅ **Public endpoints** (/api/auth/** no authentication required)
- ✅ **Exception handling** (401 Unauthorized, 403 Forbidden)
- ✅ **Security context management** (request-scoped authentication)
- ✅ **Duplicate username/email prevention**
- ✅ **Account status management** (active/inactive flag)

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

#### 2.2 Unit & Integration Tests ✅ **COMPLETE**

**Goal:** Achieve 80%+ test coverage with professional testing practices

**Progress Tracker:**

📅 **Week 1: Service Layer Tests (Days 1-4)** ✅ **COMPLETE**
- [x] Day 1: Test setup + First test (createOdds - happy path) ✅ DONE
- [x] Day 2: READ tests (getById, getAll, getBySport) ✅ DONE
- [x] Day 3: UPDATE and DELETE tests (updateOdds, deactivateOdds, deleteOdds) ✅ DONE
- [x] Day 4: Business logic and security tests (margin, SQL injection, XSS) ✅ DONE

**Week 1 Summary:**
- 16 unit tests for BettingOddsService
- Approximately 80% Service layer coverage
- All CRUD operations tested
- Security validations tested
- Business logic verified

📅 **Week 2: Mapper & Repository Tests (Days 5-11)**
- [x] Day 5-6: Mapper tests (DTO to Entity, Entity to DTO conversions) ✅ DONE
- [x] Day 7-9: Repository tests (@DataJpaTest) ✅ DONE
- [x] Day 10-11: Controller integration tests (@SpringBootTest + MockMvc) ✅ DONE

**Week 2 Days 5-6 Summary:**
- 8 unit tests for OddsMapper
- 100% Mapper coverage
- All DTO ↔ Entity conversions tested
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

📅 **Week 3: Final Tests & Documentation (Days 12-14)** ✅ **COMPLETE**
- [x] Day 12: Controller integration tests completion
- [x] Day 13: End-to-end HTTP tests verification
- [x] Day 14: Test documentation

**Current Test Coverage:**

| Component | Tests Written | Coverage | Status |
|-----------|--------------|----------|--------|
| **BettingOddsService** | 16/20 | ~80% | ✅ Week 1 Complete |
| **OddsMapper** | 8/8 | ~100% | ✅ Week 2 Day 5-6 Complete |
| **BettingOddsRepository** | 10/10 | ~100% | ✅ Week 2 Day 7-9 Complete |
| **BettingOddsController** | 18/18 | ~100% | ✅ Phase 3 Week 3 Day 10 Complete (Updated with JWT) |
| **TOTAL** | **52/56** | **~95%** | 🎯 **Excellent Coverage! Target: 80%+ EXCEEDED! ✅** |

**What We Learned:**
- ✅ JUnit 5 basics (test structure, assertions)
- ✅ Mockito fundamentals (mocking dependencies)
- ✅ AAA pattern (Arrange-Act-Assert)
- ✅ Test naming conventions
- ✅ @DataJpaTest for repository testing
- ✅ H2 in-memory database for integration tests
- ✅ Testing pagination and sorting
- ✅ Testing custom @Query methods
- ✅ @SpringBootTest for full integration testing
- ✅ MockMvc for HTTP endpoint testing
- ✅ JSON assertions with JSONPath
- ✅ Testing REST API status codes
- ✅ End-to-end HTTP request/response testing
- ✅ **JWT token generation in tests** (NEW)
- ✅ **Testing with Authorization header** (NEW)
- ✅ **Role-based authorization testing** (NEW)
- ✅ **Testing 403 Forbidden and 401 Unauthorized** (NEW)

---

#### 2.3 Advanced Search & Filtering 📋 *Planned*
- [ ] Specification pattern (dynamic queries)
- [ ] Complex filter combinations
- [ ] Search by multiple criteria
- [ ] Date range filtering
- [ ] Odds range filtering

---

### Phase 3: Security & Authentication 🔐 **IN PROGRESS**
**Duration:** 3-4 weeks | **Complexity:** ⭐⭐⭐⭐ Advanced

**Current Progress: Week 2/4 - Days 6-7 COMPLETE** ✅

**Prerequisites:**
- Understanding of authentication/authorization concepts
- Basic cryptography knowledge (hashing, JWT)
- REST API security best practices

#### 📅 Week 1: Spring Security Setup (Days 1-3) ✅ **COMPLETE**

**Goal:** Set up Spring Security framework and user management foundation

**Progress:**
- [x] Day 1: Add Spring Security and JWT dependencies ✅
  - Added `spring-boot-starter-security` (3.5.6)
  - Added JWT libraries (`jjwt-api`, `jjwt-impl`, `jjwt-jackson` 0.12.6)
  - Added `spring-security-test` for testing
  - Created temporary `SecurityConfig` with `permitAll()` for all endpoints
  - Configured stateless session management (JWT preparation)
  - Disabled CSRF (not needed for token-based auth)
  - All 43 tests passing ✅

- [x] Day 2: Create User entity and Role enum ✅
  - Created `Role` enum (USER, BOOKMAKER, ADMIN)
  - Created `User` entity with:
    - Basic fields (id, username, email, password)
    - Role-based access control field
    - Soft delete support (active flag)
    - Audit timestamps (createdAt, updatedAt with @PrePersist/@PreUpdate)
  - Created `UserRepository` with custom query methods:
    - findByUsername()
    - findByEmail()
    - existsByUsername()
    - existsByEmail()
  - Configured `EnumType.STRING` for role storage
  - Added unique constraints on username and email
  - Database table `users` created successfully ✅

- [x] Day 3: Add BCrypt password encoder ✅
  - Added `BCryptPasswordEncoder` bean in SecurityConfig
  - Created `TestController` for development testing
    - POST /api/test/create-user - Create test users
    - GET /api/test/users - List all users
  - Successfully tested password hashing (60 character BCrypt hash)
  - Verified User entity and UserRepository work correctly
  - Created first test user in database ✅

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
- ✅ BCrypt password hashing (60 character hash with salt)
- ✅ Stateless session management (JWT preparation)
- ✅ CSRF disabled (token-based auth)
- ✅ User entity with role-based access control
- ✅ Soft delete support (active flag)
- ✅ Unique constraints (username, email)
- ✅ Audit timestamps (@PrePersist/@PreUpdate)

**Testing Results:**
```bash
# Password hashing works
Plain password: "secret123"
BCrypt hash: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
Hash length: 60 characters ✅

# User creation works
POST http://localhost:8080/api/test/create-user?username=john&password=secret123
Response: 200 OK, user ID: 1 ✅

# Database persistence works
SELECT * FROM users;
Result: 1 row (john, john@test.com, hashed_password, USER, true) ✅
```

#### 📅 Week 2: JWT Authentication (Days 4-7) ✅ **COMPLETE**

**Goal:** Implement complete JWT authentication flow

**Progress:**
- [x] Day 4: Create JWT utility class ✅
  - Created `JwtTokenProvider` class in security package
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
    - Token generation: ✅ (147 chars, HS256 algorithm)
    - Token validation: ✅ (signature verified)
    - Username extraction: ✅
    - Expiration check: ✅ (24 hours from creation)
    - Invalid token rejection: ✅
  
- [x] Day 5: Create authentication DTOs ✅
  - Created `LoginRequest` DTO
    - Fields: username, password
    - Validation: @NotBlank on both fields
    - Used for POST /api/auth/login
  - Created `RegisterRequest` DTO
    - Fields: username, email, password
    - Validation: @NotBlank, @Size(3-50), @Email, @Size(6-100)
    - Used for POST /api/auth/register
  - Created `AuthResponse` DTO
    - Fields: token, tokenType, username, email, role
    - Default tokenType: "Bearer"
    - Returned after successful login/register
  - Added comprehensive validation annotations
  - Documented authentication flow (register → login → authenticated request)
  
- [x] Day 6: Create AuthService and AuthController ✅
  - Created `AuthService` with register() and login() methods
    - register(): username/email uniqueness check, BCrypt hashing, JWT generation
    - login(): credential validation, password verification, JWT generation
  - Created `AuthController` with REST endpoints
    - POST /api/auth/register - User registration
    - POST /api/auth/login - User login
  - Added request validation with @Valid
  - Implemented exception handling (400, 401, 500)
  - Added logging for all operations
  - Testing results:
    - Registration: ✅ (user created, token returned)
    - Login: ✅ (credentials validated, token returned)
    - Duplicate username: ✅ (400 Bad Request)
    - Duplicate email: ✅ (400 Bad Request)
    - Wrong password: ✅ (401 Unauthorized)
    - Non-existent user: ✅ (401 Unauthorized)
    - JWT token validation: ✅ (jwt.io verified)
  
- [x] Day 7: Create JWT authentication filter ✅
  - Created `CustomUserDetailsService` implements UserDetailsService
    - loadUserByUsername() - loads User from database
    - Converts User entity to UserDetails
    - Maps Role enum to GrantedAuthority (ROLE_USER, ROLE_ADMIN, etc.)
    - Handles account status (active/inactive)
  - Created `JwtAuthenticationFilter` extends OncePerRequestFilter
    - Intercepts all HTTP requests
    - Extracts JWT token from Authorization header ("Bearer <token>")
    - Validates token (signature, expiration, username)
    - Loads UserDetails from database
    - Creates Authentication object
    - Sets authentication in SecurityContext
  - Updated `SecurityConfig` with production configuration
    - Added JwtAuthenticationFilter to security chain
    - Configured public endpoints (/api/auth/**)
    - Configured protected endpoints (all others require authentication)
    - Added exception handling (401 Unauthorized, 403 Forbidden)
    - Enabled @PreAuthorize support (@EnableMethodSecurity)
  - Deleted `TestController` (no longer needed)
  - Testing results:
    - Login and get token: ✅
    - Access protected endpoint WITHOUT token: ✅ (401 Unauthorized)
    - Access protected endpoint WITH token: ✅ (200 OK)
    - Remove token: ✅ (401 Unauthorized again)
    - JWT validation in filter: ✅
    - User loading from database: ✅
    - SecurityContext authentication: ✅

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
   POST /api/auth/register → AuthController → AuthService
   → Hash password (BCrypt) → Save User to database
   → Generate JWT token → Return AuthResponse

2. User logs in:
   POST /api/auth/login → AuthController → AuthService
   → Validate credentials (BCrypt.matches) → Generate JWT token
   → Return AuthResponse

3. Authenticated request:
   GET /api/odds (with Authorization: Bearer <token>)
   → JwtAuthenticationFilter intercepts request
   → Extract and validate token
   → Load UserDetails from database (CustomUserDetailsService)
   → Set authentication in SecurityContext
   → Request proceeds to controller (user is authenticated)
```

**Security Features Implemented:**
- ✅ JWT token generation (HMAC-SHA256, 24h expiration)
- ✅ JWT token validation (signature, expiration, username)
- ✅ BCrypt password hashing and verification
- ✅ User registration with validation
- ✅ User login with credential validation
- ✅ Request interception and authentication
- ✅ SecurityContext management (request-scoped)
- ✅ Public endpoints (/api/auth/**)
- ✅ Protected endpoints (require authentication)
- ✅ Duplicate username/email prevention
- ✅ Account status management (active/inactive)
- ✅ Exception handling (401, 403)
- ✅ User enumeration protection (same error for invalid user/password)

**Testing Results:**

| Test | Expected | Result |
|------|----------|--------|
| Register new user | 200 OK + token | ✅ Pass |
| Login with valid credentials | 200 OK + token | ✅ Pass |
| Duplicate username | 400 Bad Request | ✅ Pass |
| Duplicate email | 400 Bad Request | ✅ Pass |
| Wrong password | 401 Unauthorized | ✅ Pass |
| Non-existent user | 401 Unauthorized | ✅ Pass |
| JWT token structure | Valid JWT (3 parts) | ✅ Pass |
| Access without token | 401 Unauthorized | ✅ Pass |
| Access with valid token | 200 OK | ✅ Pass |
| Remove token | 401 Unauthorized | ✅ Pass |

#### 📅 Week 3: Role-Based Access Control (Days 8-10) ✅ **COMPLETE**

**Goal:** Secure endpoints based on user roles

**Progress:**
- [x] Day 8: Configure method security ✅
  - Added `@PreAuthorize` annotations to all BettingOddsController endpoints
  - Configured role-based permissions:
    - USER: Read-only access (GET endpoints only)
    - BOOKMAKER: Read + Create + Update (GET, POST, PUT, PATCH)
    - ADMIN: Full access including DELETE
  - Added AccessDeniedException handler (403 Forbidden responses)
  - Created test users (USER, BOOKMAKER, ADMIN)
  - Testing results:
    - USER: ✅ Can read, ❌ Cannot create/update/delete (403)
    - BOOKMAKER: ✅ Can read/create/update, ❌ Cannot delete (403)
    - ADMIN: ✅ Full access to all operations
  - Deleted temporary test endpoint (hash-password)
  
- [ ] Day 9: Advanced authorization scenarios 📋 **SKIPPED (Optional)**
  - Add more granular permissions (e.g., user can only update own bets)
  - Implement endpoint-specific authorization logic
  - Add authorization audit logging
  
- [x] Day 10: Update tests with JWT authentication ✅ **COMPLETE**
  - Updated BettingOddsControllerTest with JWT token generation
  - Added UserRepository, JwtTokenProvider, BCryptPasswordEncoder to tests
  - Created test users with different roles (USER, BOOKMAKER, ADMIN) in @BeforeEach
  - Generated unique usernames with timestamp suffix (fixes duplicate key constraint)
  - Updated all 12 existing tests to include Authorization header with JWT token
  - Added 6 NEW authorization tests:
    - POST /api/odds - USER role → 403 Forbidden
    - PUT /api/odds/{id} - USER role → 403 Forbidden
    - PATCH /api/odds/{id}/deactivate - USER role → 403 Forbidden
    - DELETE /api/odds/{id} - USER role → 403 Forbidden
    - DELETE /api/odds/{id} - BOOKMAKER role → 403 Forbidden
    - GET /api/odds - No token → 401 Unauthorized
  - Fixed pagination test (PageResponse uses "pageSize" not "size")
  - All 18 tests passing ✅

**Week 3 Summary:**
- 18 integration tests for BettingOddsController (12 updated + 6 new)
- 100% Controller coverage with JWT authentication
- Role-based access control fully tested
- Test users with unique identifiers (timestamp suffix)
- Authorization matrix verified:
  - USER: ✅ Can GET, ❌ Cannot POST/PUT/PATCH/DELETE (403)
  - BOOKMAKER: ✅ Can GET/POST/PUT/PATCH, ❌ Cannot DELETE (403)
  - ADMIN: ✅ Full access (GET/POST/PUT/PATCH/DELETE)
  - No token: ❌ 401 Unauthorized

#### 📅 Week 4: Testing & Documentation (Days 11-14) 📋 **PLANNED**

**Goal:** Comprehensive security testing and documentation

**Planned Tasks:**
- [ ] Day 11-12: Security tests
  - Test authentication (login with valid/invalid credentials)
  - Test authorization (access endpoints with different roles)
  - Test JWT validation (expired token, invalid token)
  - Update existing tests with JWT authentication
  
- [ ] Day 13: Integration tests with JWT
  - Test complete flow (register → login → access protected endpoint)
  - Test security filter chain
  - Test role-based access control
  
- [ ] Day 14: Documentation and cleanup
  - Update README with authentication guide
  - Update Swagger with security scheme
  - Final review and commit
  - Phase 3 completion celebration! 🎉

#### 3.1 Spring Security Implementation ✅ **COMPLETE**
- [x] Spring Security dependency
- [x] Security configuration class
- [x] Password encoding (BCrypt)
- [x] Authentication manager
- [x] Security filter chain
- [x] CORS configuration (disabled CSRF)

#### 3.2 JWT Token Authentication ✅ **COMPLETE**
- [x] JWT library (jjwt 0.12.6) integration
- [x] Token generation service (JwtTokenProvider)
- [x] Token validation filter (JwtAuthenticationFilter)
- [x] Token expiration handling (24 hours)
- [ ] Refresh token mechanism 📋 *Future*
- [ ] Blacklist for revoked tokens 📋 *Future*

#### 3.3 User Management ✅ **COMPLETE**
- [x] User entity (username, email, password, role, active)
- [x] User repository (with custom query methods)
- [x] User service (AuthService - register, login)
- [x] Registration endpoint (POST /api/auth/register)
- [x] Login endpoint (POST /api/auth/login)
- [ ] Logout endpoint 📋 *Future*
- [ ] Password reset functionality 📋 *Future*

#### 3.4 Role-Based Access Control (RBAC) ✅ **COMPLETE**
- [x] Role enum (USER, ADMIN, BOOKMAKER) ✅
- [x] Method-level security enabled (@EnableMethodSecurity) ✅
- [x] Endpoint-level authorization with @PreAuthorize ✅
- [x] Role-based permissions implemented:
  - USER: Read-only (GET endpoints)
  - BOOKMAKER: Read + Create + Update (GET, POST, PUT, PATCH)
  - ADMIN: Full access including DELETE
- [x] 403 Forbidden handling for insufficient permissions ✅
- [x] Testing with multiple user roles ✅

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

#### 3.5 Rate Limiting 📋 **Phase 4**
- [ ] Rate limiting interceptor
- [ ] In-memory rate limiter (Bucket4j)
- [ ] Per-user rate limits
- [ ] Per-endpoint rate limits
- [ ] Rate limit headers (X-RateLimit-*)

**Key Learning Outcomes:**
- ✅ Spring Security architecture and configuration
- ✅ Password security best practices (BCrypt hashing with salt)
- ✅ Stateless authentication (JWT tokens)
- ✅ User entity design and repository pattern
- ✅ Enum-based role management
- ✅ JWT token structure and signing (Header, Payload, Signature)
- ✅ JWT generation with HMAC-SHA256
- ✅ Token validation and claim extraction
- ✅ DTO pattern for authentication (separation of concerns)
- ✅ Bean validation (@NotBlank, @Email, @Size)
- ✅ Spring Security filter chain
- ✅ UserDetailsService implementation
- ✅ Authentication vs Authorization
- ✅ SecurityContext management
- ✅ Exception handling (401 Unauthorized, 403 Forbidden)
- ✅ **Role-based authorization** (@PreAuthorize annotations)
- ✅ **Method-level security** (hasRole, hasAnyRole)
- ✅ Role-based access control (@PreAuthorize) - **COMPLETE**
- 📋 API security patterns - *Week 4*

---

### Phase 4: Performance & Reliability ⚡ **IN PROGRESS**
**Duration:** 3-4 weeks | **Complexity:** ⭐⭐⭐⭐ Advanced

**Prerequisites:**
- Understanding of caching strategies
- Database performance tuning basics
- Docker fundamentals

#### 📅 Week 1: Redis Caching (Days 1-7) 🔄 **IN PROGRESS**

**Goal:** Implement Redis caching for performance optimization

**Progress:**

**✅ Day 1: Docker & Redis Setup (COMPLETE)**
- [x] Installed Docker Desktop for Windows
- [x] Learned Docker fundamentals (images, containers, commands)
- [x] Created Redis container with Alpine Linux (`redis:7-alpine`)
- [x] Configured Docker to use D: drive (resource optimization)
- [x] Tested Redis connection via Docker CLI
- [x] Verified basic Redis operations (SET, GET, PING)
- [x] Learned Docker container lifecycle (start, stop, restart)

**Docker Setup:**
```bash
# Redis container running on port 6379
docker run -d --name redis-betting -p 6379:6379 redis:7-alpine

# Daily workflow:
docker start redis-betting   # Start of day
docker stop redis-betting    # End of day
```

**✅ Day 2: Spring Boot + Redis Integration (COMPLETE)**
- [x] Added Spring Data Redis dependency (`spring-boot-starter-data-redis`)
- [x] Configured Redis connection in `application.properties`
  - Host: localhost, Port: 6379, Database: 0
  - Lettuce connection pooling (max-active: 8, max-idle: 8)
- [x] Created `RedisConfig` class with RedisTemplate bean
  - String serializer for keys (human-readable)
  - JSON serializer for values (GenericJackson2JsonRedisSerializer)
- [x] Created `RedisConnectionTest` with 3 test cases
  - Basic connection test (set/get/delete)
  - Complex object serialization test
  - Key deletion verification
- [x] All tests passing ✅ (Redis integration verified)

**What We Built:**
```
src/main/java/com/gambling/betting_odds_api/
├── config/
│   └── RedisConfig.java             # Redis configuration & RedisTemplate
├── src/main/resources/
│   └── application.properties       # Redis connection settings
└── src/test/java/
    └── RedisConnectionTest.java     # Integration tests
```

**Technical Achievements:**
- ✅ Docker containerization (industry-standard deployment)
- ✅ Redis in-memory caching setup
- ✅ Spring Data Redis integration
- ✅ JSON serialization for cache data
- ✅ Connection pooling with Lettuce client
- ✅ Test-driven Redis configuration

**What's Next (Day 3-4):**
- [ ] Add `@Cacheable` to BettingOddsService methods
- [ ] Implement cache eviction strategy
- [ ] Measure performance improvements
- [ ] Test cache hit/miss scenarios
- [ ] Configure TTL (Time-To-Live) for cached data

**Expected Results:**
```
Before Redis:  GET /api/odds/1 → ~50ms (database query)
After Redis:   GET /api/odds/1 → ~2ms (cached) ⚡ 25x faster!
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

### Odds Management (Protected - Requires Authentication + Role-Based Authorization)

**⚠️ Role-Based Permissions:**

| Endpoint | USER | BOOKMAKER | ADMIN | Required Role |
|----------|------|-----------|-------|---------------|
| GET /api/odds | ✅ | ✅ | ✅ | USER, BOOKMAKER, ADMIN |
| GET /api/odds/{id} | ✅ | ✅ | ✅ | USER, BOOKMAKER, ADMIN |
| GET /api/odds/sport/{sport} | ✅ | ✅ | ✅ | USER, BOOKMAKER, ADMIN |
| POST /api/odds | ❌ | ✅ | ✅ | BOOKMAKER, ADMIN |
| PUT /api/odds/{id} | ❌ | ✅ | ✅ | BOOKMAKER, ADMIN |
| PATCH /api/odds/{id}/deactivate | ❌ | ✅ | ✅ | BOOKMAKER, ADMIN |
| DELETE /api/odds/{id} | ❌ | ❌ | ✅ | ADMIN only |

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
|--------|----------|-------------|--------------|----------|------------|---------------|
| GET | `/api/odds` | Get all odds | - | `PageResponse<OddsResponse>` | ✅ | ✅ | USER |
| GET | `/api/odds/active` | Get active odds only | - | `PageResponse<OddsResponse>` | ✅ | ✅ | USER |
| GET | `/api/odds/{id}` | Get odds by ID | - | `OddsResponse` | ❌ | ✅ | USER |
| GET | `/api/odds/sport/{sport}` | Get odds by sport | - | `PageResponse<OddsResponse>` | ✅ | ✅ | USER |
| GET | `/api/odds/upcoming` | Get upcoming matches | - | `PageResponse<OddsResponse>` | ✅ | ✅ | USER |
| GET | `/api/odds/team/{teamName}` | Get matches for team | - | `PageResponse<OddsResponse>` | ✅ | ✅ | USER |
| GET | `/api/odds/{id}/margin` | Calculate bookmaker margin | - | `OddsResponse` (with calculations) | ❌ | ✅ | USER |
| POST | `/api/odds` | Create new odds | `CreateOddsRequest` | `OddsResponse` | ❌ | ✅ | BOOKMAKER |
| PUT | `/api/odds/{id}` | Update odds | `UpdateOddsRequest` | `OddsResponse` | ❌ | ✅ | BOOKMAKER |
| PATCH | `/api/odds/{id}/deactivate` | Deactivate odds (soft delete) | - | Success message | ❌ | ✅ | BOOKMAKER |
| DELETE | `/api/odds/{id}` | Delete odds permanently | - | Success message | ❌ | ✅ | ADMIN |

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
- ✅ **Security Filter Chain Pattern**
- ✅ **UserDetailsService Pattern**

### Best Practices
- ✅ Bean Validation (Declarative input validation)
- ✅ Global Exception Handling (`@RestControllerAdvice`)
- ✅ RESTful API Design (HTTP methods, status codes)
- ✅ Transaction Management (`@Transactional`)
- ✅ Clean Code (Naming, SOLID principles)
- ✅ **Production Logging** (Audit, Performance, Security)
- ✅ **Security Validation** (SQL injection, XSS prevention)
- ✅ **Unit Testing** (JUnit 5, Mockito, AAA pattern)
- ✅ **Integration Testing** (@DataJpaTest, H2 database)
- ✅ **JWT Best Practices** (secure signing, expiration, validation)
- ✅ **Password Security** (BCrypt hashing, never plain text)

### Domain Knowledge
- ✅ Gambling industry concepts (odds formats, margins)
- ✅ Business logic implementation (calculations)
- ✅ Data validation (industry constraints)
- ✅ Regulatory compliance considerations
- ✅ **Security threats** (SQL injection, XSS)
- ✅ **Audit requirements** (compliance tracking)
- ✅ **Authentication flows** (register, login, token-based)
- ✅ **Authorization concepts** (role-based access control)

### Technologies Mastered
- ✅ Spring Boot ecosystem
- ✅ PostgreSQL database operations
- ✅ RESTful API development
- ✅ Maven build management
- ✅ Git version control
- ✅ **Logback** (enterprise logging)
- ✅ **Security patterns** (input validation, attack detection)
- ✅ **JUnit 5 + Mockito** (unit testing)
- ✅ **@DataJpaTest** (integration testing)
- ✅ **H2 Database** (in-memory testing)
- ✅ **Spring Security 6.x** (authentication & authorization)
- ✅ **JWT (jjwt 0.12.6)** (token generation & validation)
- ✅ **BCrypt** (password hashing)
- 📋 **MockMvc** (REST API testing with JWT) - *Next Up*
- 📋 **@PreAuthorize** (role-based authorization) - *Week 3*
- 🚀 Microservices architecture - *Future*

---

## 🔒 Security Features

### Authentication & Authorization
- ✅ **JWT-based authentication** - Stateless, token-based auth
- ✅ **BCrypt password hashing** - Secure password storage (60 chars, salt)
- ✅ **Token validation** - Signature, expiration, username verification
- ✅ **Security filter chain** - Request interception and authentication
- ✅ **UserDetailsService** - Load users from database
- ✅ **Role-based user model** - USER, BOOKMAKER, ADMIN
- ✅ **Public endpoints** - /api/auth/** (no authentication required)
- ✅ **Protected endpoints** - All others (authentication required)
- ✅ **401 Unauthorized** - Invalid/missing token responses
- ✅ **403 Forbidden** - Insufficient permissions (ready for role-based auth)
- ✅ **User enumeration protection** - Same error for invalid user/password
- ✅ **Duplicate prevention** - Unique username and email constraints
- ✅ **Account status management** - Active/inactive flag (soft delete)

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

### Immediate Next Steps (Phase 3 Week 3 - Role-Based Authorization)
1. **@PreAuthorize annotations** - Add role restrictions to endpoints
2. **Test role-based access** - Verify USER, BOOKMAKER, ADMIN permissions
3. **Update tests with JWT** - Add authentication to existing test suite
4. **Documentation** - Complete Phase 3 authentication guide

### After Authorization (Phase 4)
1. **Redis caching** - Improve performance with caching layer
2. **Database optimization** - Query optimization and indexing
3. **Async processing** - Heavy operations in background
4. **Monitoring** - Prometheus metrics and Grafana dashboards

---

**⭐ If you find this project helpful for learning, please give it a star!**

---

## 📊 Project Statistics

- **Lines of Code**: ~7,000 (Java + XML + Properties)
- **Total Commits**: 35+
- **Features Completed**: Core CRUD + Logging + Testing + JWT Auth + RBAC + Docker + Redis Integration
- **Test Coverage**: ~95% (52/56 tests) ✅ Excellent Coverage!
- **API Endpoints**: 12 (10 protected + 2 public)
- **Database Tables**: 2 (betting_odds, users)
- **Log Files**: 5 (application, errors, audit, performance, security)
- **Test Files**: 4 (Service, Mapper, Repository, Controller with JWT)
- **Security Features**: JWT + BCrypt + Filter Chain + @PreAuthorize + Role-Based Authorization Tests