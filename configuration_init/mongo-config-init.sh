#!/bin/bash

set -euo pipefail

if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <path_to_json_files> <container_name>"
    exit 1
fi

MONGO_USER="admin"
MONGO_PASS="{populate-your-password}"
MONGO_AUTH_DB="admin"
MONGO_DB="test"
MONGO_NS="{populate-your-namespace}"
MONGO_URI="{populate-your-uri}"
JSON_PATH="$1"
CONTAINER="$2"
PREFIX="gw_"

if [ ! -d "$JSON_PATH" ]; then
    echo "Error: Directory '$JSON_PATH' does not exist."
    exit 1
fi

for file in "$JSON_PATH"/*.json; do
    [ -e "$file" ] || { echo "No JSON files found in '$JSON_PATH'."; exit 1; }

    COLLECTION_NAME="${PREFIX}$(basename "$file" .json)"
    echo "Processing file: $file -> Collection: $COLLECTION_NAME"

    if ! jq empty "$file" >/dev/null 2>&1; then
        echo "Error: Invalid JSON format in '$file'. Skipping..."
        continue
    fi

    kubectl -n "$MONGO_NS" exec -i "$CONTAINER" -- mongo -u "$MONGO_USER" -p "$MONGO_PASS" --authenticationDatabase "$MONGO_AUTH_DB" --eval "db.$COLLECTION_NAME.drop()" "$MONGO_DB"

    kubectl -n "$MONGO_NS" exec -i "$CONTAINER" -- mongo -u "$MONGO_USER" -p "$MONGO_PASS" --authenticationDatabase "$MONGO_AUTH_DB" --eval "db.createCollection('$COLLECTION_NAME')" "$MONGO_DB"

    if jq -e '.[0]' "$file" >/dev/null 2>&1; then
        kubectl -n "$MONGO_NS" exec -i "$CONTAINER" -- mongoimport --uri "$MONGO_URI" -c "$COLLECTION_NAME" --jsonArray < "$file"
    else
        kubectl -n "$MONGO_NS" exec -i "$CONTAINER" -- mongoimport --uri "$MONGO_URI" -c "$COLLECTION_NAME" < "$file"
    fi

done

echo "All JSON files successfully processed."
