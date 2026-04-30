# Job Recruitment Platform - Server

Backend service for a job recruitment platform, built with Spring Boot 3 and Java 21.

## Overview

A RESTful API backend for a job recruitment platform providing user management, job postings, candidate tracking, and AI/ML integration for intelligent job recommendations.

## Features

- **User Management**: Registration, login, role-based access (Candidate / Recruiter / Admin)
- **Authentication & Authorization**: JWT, OAuth2 (Google), Spring Security
- **Job Management**: CRUD operations, search, filtering by criteria
- **Resume Management**: Upload/parse CVs, automatic information extraction
- **AI/ML Integration**:
  - Personalized job recommendations (Recommendation Service)
  - Semantic search (Search Service)
  - CV information extraction (Resume Extractor Service)
- **Email Notifications**: Account verification, interview scheduling
- **Media Storage**: Cloudinary integration for avatars and CVs
- **Caching**: Redis for performance optimization

## Tech Stack

| Component | Technology |
|-----------|------------|
| **Framework** | Spring Boot 3.5.6 |
| **Language** | Java 21 |
| **Database** | PostgreSQL 18 |
| **Cache** | Redis 7.4 |
| **ORM** | Spring Data JPA, Hibernate |
| **Migration** | Flyway |
| **Auth** | Spring Security, OAuth2, JWT |
| **Email** | Spring Mail |
| **Cloud Storage** | Cloudinary |
| **Build Tool** | Maven |
| **Container** | Docker |

## Project Structure

```
src/main/java/org/toanehihi/jobrecruitmentplatformserver/
├── application/          # Application services, DTOs, mappers
├── domain/               # Entities, repositories, domain logic
├── infrastructure/       # External integrations, configurations
└── interfaces/           # Controllers, REST endpoints
```

Clean Architecture with distinct layers:
- **Domain Layer**: Business logic and entities
- **Application Layer**: Use cases, DTOs
- **Infrastructure Layer**: Configuration, external integrations
- **Interfaces Layer**: REST Controllers, API endpoints

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 18+
- Redis 7.4+

### Using Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Job-Recruitment-Platform-Server
   ```

2. **Configure environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with the required values
   ```

3. **Start services**
   ```bash
   docker-compose up -d
   ```

   Server runs at: `http://localhost:8080`

### Local Development

1. **Ensure PostgreSQL and Redis are running**

2. **Configure environment variables in `.env`**

3. **Build and run**
   ```bash
   ./mvnw spring-boot:run
   ```

## Environment Variables

| Variable | Description |
|----------|-------------|
| `POSTGRES_HOST` | PostgreSQL host |
| `POSTGRES_PORT` | PostgreSQL port |
| `POSTGRES_DB` | Database name |
| `POSTGRES_USERNAME` | Database username |
| `POSTGRES_PASSWORD` | Database password |
| `REDIS_HOST` | Redis host |
| `REDIS_PORT` | Redis port |
| `JWT_SECRET` | JWT secret key |
| `CLIENT_ID` | Google OAuth2 Client ID |
| `CLIENT_SECRET` | Google OAuth2 Client Secret |
| `MAIL_USERNAME` | SMTP email username |
| `MAIL_PASSWORD` | SMTP email password (app password) |
| `CLOUDINARY_NAME` | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret |
| `FRONTEND_URL` | Frontend URL |
| `NER_SERVICE_URL` | Resume Extractor Service URL |
| `SEARCH_SERVICE_URL` | Search Service URL |
| `RECOMMENDATION_SERVICE_URL` | Recommendation Service URL |

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register account
- `POST /api/auth/login` - Login
- `POST /api/auth/google` - Google login

### Jobs
- `GET /api/jobs` - List jobs
- `GET /api/jobs/{id}` - Job details
- `POST /api/jobs` - Create job
- `PUT /api/jobs/{id}` - Update job
- `DELETE /api/jobs/{id}` - Delete job

### Candidates
- `GET /api/candidates/profile` - Get profile
- `PUT /api/candidates/profile` - Update profile
- `POST /api/candidates/resume` - Upload CV

### Recommendations
- `GET /api/recommendations` - Get job recommendations

### Health Check
- `GET /actuator/health` - Service health status

## Docker

### Build image
```bash
docker build -t jrp-server:1.0 .
```

### Run container
```bash
docker run -p 8080:8080 --env-file .env jrp-server:1.0
```

## System Architecture

```
+------------------+     +------------------+
|   Frontend       |---->|   Backend API    |
|   (Next.js)      |     |  (Spring Boot)   |
+------------------+     +--------+---------+
                                  |
                     +------------+------------+
                     |            |            |
               +-----+------+ +--+------+ +---+-------+
               |   Resume   | |  Search | | Recommend |
               | Extractor  | | Service | |  Service  |
               +-----+------+ +--+------+ +---+-------+
                     |            |            |
               +-----+------+ +--+------------+------+
               |   NER      | |      Milvus          |
               |   Model    | |   (Vector DB)        |
               +------------+ +-----------------------+
```

## Development Notes

- Flyway manages database migrations
- Spring Actuator is integrated for monitoring
- Hot-reload supported via Spring DevTools in development

## License

Developed for educational and research purposes.
