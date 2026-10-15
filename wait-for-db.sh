#!/bin/sh
set -e
: "Wait for database to become available before starting the app"
DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-3306}
TIMEOUT=${DB_WAIT_TIMEOUT:-120}
COUNT=0
# If DB_HOST is localhost or empty, skip waiting (assume local embed or developer will handle)
if [ -z "$DB_HOST" ] || [ "$DB_HOST" = "localhost" ] || [ "$DB_HOST" = "127.0.0.1" ]; then
  echo "DB_HOST is $DB_HOST — skipping remote wait"
  exec java -jar /app/app.jar
fi

echo "Waiting up to ${TIMEOUT}s for database $DB_HOST:$DB_PORT..."
while ! nc -z "$DB_HOST" "$DB_PORT" 2>/dev/null; do
  COUNT=$((COUNT+2))
  if [ "$COUNT" -ge "$TIMEOUT" ]; then
    echo "Timed out waiting for database at $DB_HOST:$DB_PORT after ${TIMEOUT}s"
    echo "Dumping last DNS lookup for $DB_HOST:"; nslookup "$DB_HOST" || true
    echo "Exiting with failure so Render shows the error logs."
    exit 1
  fi
  echo "Database not reachable yet; sleeping 2s..."
  sleep 2
done

echo "Database is reachable — starting application"
exec java -jar /app/app.jar
