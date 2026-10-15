FROM node:22-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app/backend
COPY backend/pom.xml ./
RUN mvn -B dependency:go-offline
COPY backend/src ./src
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend-build /app/backend/target/examconnect-backend-1.0.0.jar ./app.jar
# Install netcat for simple TCP checks and DNS tooling used by the start script
RUN apt-get update \
  && apt-get install -y --no-install-recommends netcat-openbsd dnsutils \
  && rm -rf /var/lib/apt/lists/*

# Copy and use the wait-for-db entrypoint so the container waits for DB accessibility
COPY wait-for-db.sh /wait-for-db.sh
RUN chmod +x /wait-for-db.sh
EXPOSE 8080
ENTRYPOINT ["/bin/sh","/wait-for-db.sh"]
