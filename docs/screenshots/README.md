# Evidence checklist

Save each screenshot in this folder with the file name shown and reference it from the main README.

## Nexus
- `01-nexus-hosted-repository-config.png` Nexus UI > Settings > Repositories > employee-releases (type hosted, maven2, release)
- `02-mvn-deploy-success.png` terminal showing BUILD SUCCESS and uploads to employee-releases
- `03-nexus-artifact-browse.png` Nexus UI > Browse > employee-releases > com/example/employee-library/1.0.0
- `04-app-resolves-from-nexus.png` terminal output of the app build showing Downloaded from nexus-employee-releases

## Application and APIs
- `05-app-startup.png` Spring Boot started and seeded employees
- `06-postman-collection-run.png` Postman collection runner with all tests passing
- `07-postman-create-employee.png` POST 201 response
- `08-postman-validation-error.png` 400 response produced by the library validator

## Redis caching
- `09-cacheable-miss-hit-logs.png` application log with CACHE MISS once and no log on the second call
- `10-redis-keys.png` redis-cli KEYS "*" after reads
- `11-cacheput-update.png` update request and redis-cli showing employee::1 refreshed
- `12-cacheevict-delete.png` redis-cli KEYS "*" before and after delete
- `13-ttl-countdown.png` redis-cli TTL employee::2 counting down
- `14-ttl-expired.png` redis-cli TTL returning -2 after expiry, followed by a new CACHE MISS log
