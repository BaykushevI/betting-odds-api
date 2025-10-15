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
- PostgreSQL 18 - Database
- Lombok - Reduce boilerplate code
- Maven - Build tool

**Architecture**
The project follows a layered architecture:
Controller (REST API) -> Service (Business Logic) -> Repository (Data Access) -> Database

**Features**
- CRUD operations for betting odds
- Filter odds by sport, team, or status
- Get upcoming matches
- Calculate bookmaker margin
- Soft delete (deactivate) and hard delete
- Input validation
- Proper error handling

**API Endpoints**
Method    Endpoint                   Description
GET       /api/odds                  Get all odds
GET       /api/odds/active           Get active odds only 
GET       /api/odds/{id}             Get odds by ID
GET       /api/odds/sport/{sport}    Get odds by sport
GET       /api/odds/upcoming         Get upcoming matches 
GET       /api/odds/team/{teamName}  Get matches for team
GET       /api/odds/{id}/margin      Calculate bookmaker margin
POST      /api/odds                  Create new odds
PUT       /api/odds/{id}             Update odds
PATCH     /api/odds/{id}/deactivate  Deactivate odds 
DELETE    /api/odds/{id}             Delete odds

**Database Schema**
betting_odds
-  id (BIGSERIAL PRIMARY KEY)
-  sport (VARCHAR NOT NULL)
-  home_team (VARCHAR NOT NULL)
-   away_team (VARCHAR NOT NULL)
-   home_odds (DECIMAL(5,2) NOT NULL)
-   draw_odds (DECIMAL(5,2) NOT NULL)
-   away_odds (DECIMAL(5,2) NOT NULL)
-   match_date (TIMESTAMP NOT NULL)
-   active (BOOLEAN NOT NULL)
-   created_at (TIMESTAMP NOT NULL)
-  updated_at (TIMESTAMP)

**Setup Instructions**
Prerequisites
- Java 17 or higher
- PostgreSQL 18
- Maven 3.9+

Installation

Clone the repository
git clone https://github.com/BaykushevI/betting-odds-api.git
cd betting-odds-api

Create PostgreSQL database

CREATE DATABASE betting_test;

Configure database connection in src/main/resources/application.properties

properties
spring.datasource.url=jdbc:postgresql://localhost:5432/betting_test
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

Run the application
mvn spring-boot:run
The API will be available at http://localhost:8080

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
