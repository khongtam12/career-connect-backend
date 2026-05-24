#!/usr/bin/env bash

set -euo pipefail

POSTGRES_CONTAINER="${POSTGRES_CONTAINER:-postgres-server}"
POSTGRES_USER="${POSTGRES_USER:-postgres}"
DB_LIST=(
  "user-service"
  "company-service"
  "job-service"
  "application-service"
  "payment-service"
  "cv-service"
)

echo "Using postgres container: ${POSTGRES_CONTAINER}"

for db in "${DB_LIST[@]}"; do
  dump_path="${HOME}/${db}.sql"

  if [[ ! -f "${dump_path}" ]]; then
    echo "Missing dump file: ${dump_path}" >&2
    exit 1
  fi

  echo "Resetting schema for ${db}..."
  docker exec -i "${POSTGRES_CONTAINER}" psql -U "${POSTGRES_USER}" -d "${db}" \
    -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"

  echo "Importing ${dump_path} into ${db}..."
  docker exec -i "${POSTGRES_CONTAINER}" psql -U "${POSTGRES_USER}" -d "${db}" < "${dump_path}"
done

echo "All databases imported successfully."
