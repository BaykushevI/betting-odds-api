**Betting Odds API**
A production-ready RESTful API for managing betting odds for sports matches, built with Spring Boot and PostgreSQL.

**Project Overview**
This is a comprehensive learning project demonstrating professional backend development practices relevant to the gambling industry. It implements a complete CRUD API with:
- DTO Layer for API/Database separation
- Bean Validation for input validation
- Global exception handling
- Business logic for odds calculations
- Proper architectural layering

**Technologies Used**
- Java 17 - Programming language
- Spring Boot 3.5.6 - Framework
- Spring Data JPA - Database access layer
- Spring Valudation - Bean validation (Jakarta Validation)
- PostgreSQL 18 - Relational Database
- Lombok - Reduce boilerplate code
- Maven - Build tool

**Architecture**
The project follows a clean, layered architecture with separation of concerns:
Client (Postman, Browser) -> HTTP Requests (JSON)
- Controller Layer (REST API Endpoints) -> DTOs (CreateOddsRequest, OddsResponse)
  - Handles HTTP requests/responses
  - Validates input with @Valid
  - Returns DTOs 
- Service (Business Logic) -> Entity (BettingOdds)
  - Business rules and calculations  
  - Transaction management 
  - Orchestrates operations
- Repository layer (Data Access) -> SQL
  - Database queries (JPA)
  - CRUD operations 
- PostgreSQL Database

**Key Components**
- DTOs (Data Transfer Objects) - Separate API contracts from database entities
- Mapper - Converts between DTOs and Entities
- Exception Handling - Global exception handler with custom exceptions
- Validation - Declarative validation with Jakarta Bean Validation

**Features**
Core Functionality
- Complete CRUD operations for betting odds
- DTO Layer for API/Database separation
- Automatic input validation with detailed error messages
- Pagination & Sorting - Handle large datasets efficiently
- Filter odds by sport, team, or active status
- Get upcoming matches (future dates only)
- Calculate bookmaker margin and implied probabilities
- Soft delete (deactivate) and hard delete options

Technical Feautures
- RESTful API design with proper HTTP methods
- Global exception handling with custom exceptions
- Transactional operations for data consistency
- Automatic timestamps (createdAt, updatedAt)
- Comprehensive validation rules
- Clean separation of concerns
- Advanced pagination with multiple sort fields

**API Endpoints**
Method    Endpoint                   Description                  Request body        Response
GET       /api/odds                  Get all odds                 -                   List<OddsResponse>
GET       /api/odds/active           Get active odds only         -                   List<OddsResponse>
GET       /api/odds/{id}             Get odds by ID               -                   OddsResponse
GET       /api/odds/sport/{sport}    Get odds by sport            -                   List<OddsResponse>
GET       /api/odds/upcoming         Get upcoming matches         -                   List<OddsResponse>
GET       /api/odds/team/{teamName}  Get matches for team         -                   List<OddsResponse>
GET       /api/odds/{id}/margin      Calculate bookmaker margin   -                   OddsResponse (with computed fields)
POST      /api/odds                  Create new odds              CreateOddsRequest   OddsResponse 
PUT       /api/odds/{id}             Update odds                  UpdateOddsRequest   OddsResponse 
PATCH     /api/odds/{id}/deactivate  Deactivate odds              -                   Success message
DELETE    /api/odds/{id}             Delete odds                  -                   Success message

**Database Schema**
betting_odds
-  id (BIGSERIAL PRIMARY KEY)
-  sport (VARCHAR NOT NULL)
-  home_team (VARCHAR NOT NULL)
-  away_team (VARCHAR NOT NULL)
-  home_odds (DECIMAL(5,2) NOT NULL)
-  draw_odds (DECIMAL(5,2) NOT NULL)
-  away_odds (DECIMAL(5,2) NOT NULL)
-  match_date (TIMESTAMP NOT NULL)
-  active (BOOLEAN NOT NULL)
-  created_at (TIMESTAMP NOT NULL)
-  updated_at (TIMESTAMP)

Indexes:
- idx_sport_active ON betting_odds(sport, active)
- idx_match_date ON betting_odds(match_date)
- idx_home_team ON betting_odds(home_team)
- idx_away_team ON betting_odds(away_team)

**Setup Instructions**
Prerequisites
- Java 17 or higher
- PostgreSQL 18
- Maven 3.9+
- Postman (for API testing)

Installation
1.Clone the repository
git clone https://github.com/BaykushevI/betting-odds-api.git
cd betting-odds-api

2.Create PostgreSQL database
CREATE DATABASE betting_test;

3.Configure database connection in src/main/resources/application.properties
properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/betting_test
spring.datasource.username=postgres
spring.datasource.password=

4.Install dependencies
mvn clean install

5.Run the application
mvn spring-boot:run

The API will be available at http://localhost:8080

Quick test:
Use Postman to create your odds:
------------------
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
------------------
Response (201 Created);
{
  "id": 1,
  "sport": "Football",
  "homeTeam": "Barcelona",
  "awayTeam": "Real Madrid",
  "homeOdds": 2.10,
  "drawOdds": 3.40,
  "awayOdds": 3.60,
  "matchDate": "2025-10-20T20:00:00",
  "active": true,
  "createdAt": "2025-10-13T01:30:00",
  "updatedAt": "2025-10-13T01:30:00",
  "impliedProbabilityHome": null,
  "impliedProbabilityDraw": null,
  "impliedProbabilityAway": null,
  "bookmakerMargin": null
}
------------------
Create Odds (Validation Error)
POST http://localhost:8080/api/odds

{
  "sport": "F",
  "homeTeam": "Barcelona",
  "awayTeam": "Real Madrid",
  "homeOdds": 0.50,
  "drawOdds": 3.40,
  "awayOdds": 3.60,
  "matchDate": "2020-01-01T20:00:00"
}
------------------
Response (400 Bad Request):
{
  "timestamp": "2025-10-13T01:35:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "fieldErrors": {
    "sport": "Sport must be between 2 and 50 characters",
    "homeOdds": "Home odds must be at least 1.01",
    "matchDate": "Match date must be in the future"
  },
  "path": "/api/odds"
}
------------------
Get Odds with Margin Calculation
GET http://localhost:8080/api/odds/1/margin
{
  "id": 1,
  "sport": "Football",
  "homeTeam": "Barcelona",
  "awayTeam": "Real Madrid",
  "homeOdds": 2.10,
  "drawOdds": 3.40,
  "awayOdds": 3.60,
  "matchDate": "2025-10-20T20:00:00",
  "active": true,
  "createdAt": "2025-10-13T01:30:00",
  "updatedAt": "2025-10-13T01:30:00",
  "impliedProbabilityHome": 0.47619,
  "impliedProbabilityDraw": 0.29411,
  "impliedProbabilityAway": 0.27777,
  "bookmakerMargin": 4.808
}
------------------
Get Active Odds
GET http://localhost:8080/api/odds/active
Response (200 OK)
[
  {
    "id": 1,
    "sport": "Football",
    "homeTeam": "Barcelona",
    "awayTeam": "Real Madrid",
    ...
  },
  {
    "id": 2,
    "sport": "Basketball",
    ...
  }
]
------------------
Update Odds
PUT http://localhost:8080/api/odds/1

{
  "sport": "Football",
  "homeTeam": "Barcelona",
  "awayTeam": "Real Madrid",
  "homeOdds": 2.20,
  "drawOdds": 3.30,
  "awayOdds": 3.50,
  "matchDate": "2025-10-20T20:00:00",
  "active": true
}
Response (200 OK)
{
  "id": 1,
  "homeOdds": 2.20,
  "drawOdds": 3.30,
  "awayOdds": 3.50,
  "updatedAt": "2025-10-13T02:00:00",
  ...
}
------------------
Error Handling - Not Found
------------------
Business Logic Explanation:
- impliedProbabilityHome = 1 / 2.10 = 47.6%
- impliedProbabilityDraw = 1 / 3.40 = 29.4%
- impliedProbabilityAway = 1 / 3.60 = 27.8%
- bookmakerMargin = (47.6% + 29.4% + 27.8% - 100%) = 4.8%
The bookmaker's profit margin is 4.8%!

**Example Usage**
Create Odds
bashPOST http://localhost:8080/api/odds
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
Get Active Odds
GET http://localhost:8080/api/odds/active

**Learning Outcomes**
This project demonstrates:
- RESTful API design principles
- Layered architecture (Controller-Service-Repository)
- Spring Boot dependency injection
- JPA/Hibernate ORM
- Database relationship mapping
- Input validation and error handling
- Business logic implementation (margin calculation)
- Git version control

**Future Enhancements**
- Add user authentication (Spring Security)
- Implement real-time odds updates (WebSockets)
- Add integration with payment systems
- Implement betting slip management
- Add comprehensive unit and integration tests
- Deploy to cloud (AWS/Azure)

**Author**
Iliyan Baykushev

GitHub: BaykushevI
LinkedIn: https://www.linkedin.com/in/iliyan-baykushev/

This project is for educational purposes.
