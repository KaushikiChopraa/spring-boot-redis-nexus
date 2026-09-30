#!/usr/bin/env bash
set -uo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
REDIS_CONTAINER="${REDIS_CONTAINER:-employee-redis}"
EMPLOYEE_TTL_SECONDS="${EMPLOYEE_TTL_SECONDS:-30}"

redis() {
  docker exec "${REDIS_CONTAINER}" redis-cli "$@"
}

section() {
  printf '\n==================== %s ====================\n' "$1"
}

call() {
  printf '\n$ curl %s\n' "$*"
  curl -s -w '\n[HTTP %{http_code}, %{time_total}s]\n' "$@"
}

show_keys() {
  printf '\n$ redis-cli KEYS "*"\n'
  redis KEYS "*"
}

section "0. Start from an empty cache"
redis FLUSHALL

section "1. @Cacheable: first read is a cache MISS (check the app log), second read is a HIT"
call "${BASE_URL}/api/employees/1"
show_keys
printf '\n$ redis-cli TTL employee::1\n'
redis TTL "employee::1"
call "${BASE_URL}/api/employees/1"

section "2. @Cacheable on list and department queries"
call "${BASE_URL}/api/employees"
call "${BASE_URL}/api/employees/department/ENGINEERING"
show_keys

section "3. @CachePut: update refreshes employee::1 and evicts the list caches"
call -X PUT "${BASE_URL}/api/employees/1" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Asha","lastName":"Verma-Rao","email":"asha.verma@example.com","department":"ENGINEERING","salary":99000,"joiningDate":"2019-04-01"}'
show_keys
call "${BASE_URL}/api/employees/1"

section "4. @CacheEvict: delete removes employee::1 and the list caches"
call -X POST "${BASE_URL}/api/employees" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Temp","lastName":"Person","email":"temp.person@example.com","department":"HR","salary":45000,"joiningDate":"2023-05-01"}'
call "${BASE_URL}/api/employees"
show_keys
call -X DELETE "${BASE_URL}/api/employees/6"
show_keys
call "${BASE_URL}/api/employees/6"

section "5. TTL and expiration"
call "${BASE_URL}/api/employees/2"
printf '\n$ redis-cli TTL employee::2\n'
redis TTL "employee::2"
printf '\nWaiting %s seconds for the key to expire...\n' "$((EMPLOYEE_TTL_SECONDS + 2))"
sleep "$((EMPLOYEE_TTL_SECONDS + 2))"
printf '\n$ redis-cli TTL employee::2   (-2 means the key no longer exists)\n'
redis TTL "employee::2"
show_keys
call "${BASE_URL}/api/employees/2"
printf '\n$ redis-cli TTL employee::2   (a fresh TTL after the new cache fill)\n'
redis TTL "employee::2"
