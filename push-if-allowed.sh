#!/bin/bash

docker run -d -p 5433:5432 --name test_postgres \
  -e POSTGRES_PASSWORD=test_password \
  -e POSTGRES_USER=masterdetails \
  -e POSTGRES_DB=masterdetails \
   postgres:17-alpine || exit 1
docker run -d -p 6378:6379 --name test_redis redis:8.2-alpine || exit 1

export PGSQL_HOSTNAME=localhost
export PGSQL_PORT=5433
export PGSQL_USERNAME=masterdetails
export PGSQL_PASSWORD=test_password
export REDIS_HOSTNAME=localhost
export REDIS_PORT=6378

./mvnw test -Dspring.jpa.hibernate.ddl-auto=create
RESULT=$?

docker stop test_postgres test_redis
docker rm test_postgres test_redis

if [ $RESULT -eq 0 ]; then
  git push
fi
