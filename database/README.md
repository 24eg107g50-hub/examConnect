# ExamConnect Database Architecture & Setup

## Overview
ExamConnect uses **MySQL 8.0** with strict normalization, foreign key integrity, and automated schema migrations powered by **Flyway**.

### Migration Scripts:
- `backend/src/main/resources/db/migration/V1__init_schema.sql`: Contains the primary DDL definitions for users, exams, subjects, topics, study materials, questions, mock tests, attempts, doubts, answers, reports, and AI vector chunks.
- `backend/src/main/resources/db/migration/V2__seed_data.sql`: Seed data for development and testing, including admin and student accounts, 6 major exam categories, 30+ questions with verified explanations, mock tests, and sample doubts.

### Connecting to MySQL locally:
```powershell
mysql -u root -p
```

Create the database:
```sql
CREATE DATABASE IF NOT EXISTS examconnect CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Spring Boot will run Flyway migrations automatically on startup!

### Backend environment

Set `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (at least 32 random bytes), and
`INITIAL_ADMIN_PASSWORD` in the backend process environment before starting the
application. The values in `backend/.env.example` are intentionally blank for
secrets; Spring Boot does not load `.env` files automatically. Keep real values
in your local environment or deployment provider's secret settings, not in Git.

### Deploying the full-stack app to Render

The repository-root `render.yaml` and `Dockerfile` build the React frontend
and Spring Boot backend into one web service, so browser API requests remain
same-origin. In Render, create a Blueprint from this repository and provide the
MySQL host, database name, username, password, and a strong initial admin
password when prompted. Set `DB_HOST`, `DB_NAME`, `DB_USER`, and `DB_PASSWORD`
to the values from your hosted MySQL provider; Render generates `JWT_SECRET`.
Allow the Render service to connect to your MySQL server before deploying.
Flyway applies the database migrations when the service starts.

The free web-service filesystem is ephemeral. Uploaded materials stored in
`STORAGE_UPLOAD_DIR` will not survive a redeploy or instance replacement; use
durable object storage or a persistent disk before relying on production
uploads.
