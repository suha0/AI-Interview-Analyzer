# AI Interview Analyzer Backend

Spring Boot API for the interview behavioral analysis platform.

## Modules

- `security`: JWT authentication and Spring Security configuration.
- `user`: account persistence and roles.
- `interview`: interview lifecycle APIs.
- `question`: AI-generated interview question orchestration.
- `analysis`: real-time metrics, final reports, and WebSocket analytics.
- `config`: CORS, WebSocket, security, and Flask AI service client configuration.

## Core Endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/interviews`
- `POST /api/interviews`
- `POST /api/interviews/{interviewId}/start`
- `POST /api/interviews/{interviewId}/complete`
- `POST /api/interviews/{interviewId}/questions/generate`
- `GET /api/interviews/{interviewId}/analysis/report`
- `POST /api/interviews/{interviewId}/analysis/metrics`
- `POST /api/ai/answers/analyze`

## WebSocket

- STOMP endpoint: `/ws/interviews`
- Client publish: `/app/interviews/{interviewId}/metrics`
- Client subscribe: `/topic/interviews/{interviewId}/analytics`

## Local Database

From the repository root:

```powershell
docker compose up postgres
```

The Flyway migration in `src/main/resources/db/migration` creates the PostgreSQL schema.
