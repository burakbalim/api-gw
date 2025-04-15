#!/bin/bash

set -euo pipefail

if [ "$#" -ne 6 ]; then
    echo "Usage: $0 <path_to_json_files> <container_name> <postgres_user> <postgres_password> <postgres_uri> <namespace>"
    exit 1
fi

JSON_PATH="$1"
CONTAINER="$2"
PG_USER="$3"
PG_PASS="$4"
PG_URI="$5"
PG_NS="$6"
PG_DB="api_gw"
PREFIX="gw_"

if [ ! -d "$JSON_PATH" ]; then
    echo "Error: Directory '$JSON_PATH' does not exist."
    exit 1
fi

for file in "$JSON_PATH"/*.json; do
    [ -e "$file" ] || { echo "No JSON files found in '$JSON_PATH'."; exit 1; }

    TABLE_NAME="${PREFIX}$(basename "$file" .json)"
    echo "Processing file: $file -> Table: $TABLE_NAME"

    if ! jq empty "$file" >/dev/null 2>&1; then
        echo "Error: Invalid JSON format in '$file'. Skipping..."
        continue
    fi

    kubectl -n "$PG_NS" exec -i "$CONTAINER" -- psql -U "$PG_USER" -d "$PG_DB" -c "DROP TABLE IF EXISTS $TABLE_NAME;"

    kubectl -n "$PG_NS" exec -i "$CONTAINER" -- psql -U "$PG_USER" -d "$PG_DB" -c "CREATE TABLE $TABLE_NAME (id SERIAL PRIMARY KEY, data JSONB);"

    cat "$file" | jq -c  | while read -r row; do
        kubectl -n "$PG_NS" exec -i "$CONTAINER" -- psql -U "$PG_USER" -d "$PG_DB" -c "INSERT INTO $TABLE_NAME (data) VALUES ('$row');"
    done

done

echo "All JSON files successfully processed."
