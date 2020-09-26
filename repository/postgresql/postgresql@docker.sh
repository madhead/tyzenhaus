#!/usr/bin/env bash

kill=false
seed=false
seedUnit=false
dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"

while getopts "ksu" option; do
  case $option in
  k)
    kill=true
    ;;
  s)
    seed=true
    ;;
  u)
    seedUnit=true
    ;;
  *) ;;
  esac
done

if [ $kill = true ]; then
  docker stop tyzenhaus-postgres
else
  docker run \
    --rm \
    --name tyzenhaus-postgres \
    -e POSTGRES_DB=tyzenhaus \
    -e POSTGRES_USER=tyzenhaus \
    -e POSTGRES_PASSWORD=tyzenhaus \
    -p 5432:5432 \
    -d \
    postgres:12

  if [ $seed = true ] || [ $seedUnit = true ]; then
    until docker exec tyzenhaus-postgres psql -U tyzenhaus -d tyzenhaus -c "select 1" >/dev/null 2>&1; do
      sleep 1
    done

    POSTGRES_HOST=localhost \
      POSTGRES_PORT=5432 \
      POSTGRES_DB=tyzenhaus \
      POSTGRES_USER=tyzenhaus \
      POSTGRES_PASSWORD=tyzenhaus \
      "${dir}"/../../gradlew :repository:postgresql:liquibaseUpdate

    if [ $seedUnit = true ]; then
      docker exec -i tyzenhaus-postgres psql -U tyzenhaus -d tyzenhaus <"${dir}"/src/test/sql/seed.sql
    fi
    if [ $seed = true ]; then
      docker exec -i tyzenhaus-postgres psql -U tyzenhaus -d tyzenhaus <"${dir}"/src/main/sql/seed.sql
    fi
  fi
fi
