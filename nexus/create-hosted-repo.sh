#!/usr/bin/env bash
set -euo pipefail

NEXUS_URL="${NEXUS_URL:-http://localhost:8081}"
NEXUS_USER="${NEXUS_USER:-admin}"
: "${NEXUS_PASSWORD:?Set NEXUS_PASSWORD to the Nexus admin password}"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

curl -sS -u "${NEXUS_USER}:${NEXUS_PASSWORD}" \
  -X POST \
  -H "Content-Type: application/json" \
  --data @"${SCRIPT_DIR}/hosted-repo.json" \
  -w "\nHTTP status: %{http_code} (201 means created)\n" \
  "${NEXUS_URL}/service/rest/v1/repositories/maven/hosted"
