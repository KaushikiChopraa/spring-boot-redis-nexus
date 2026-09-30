# Spring Boot + Redis + Nexus Artifact Repository

An end-to-end assignment that builds a reusable Maven library, publishes it to a Nexus hosted repository, and consumes it from a Spring Boot REST application that persists to a database and caches with Redis.


## Prerequisites

JDK 17 or later, Maven 3.8+, Docker with Docker Compose, Postman, Git. `curl` is needed for the scripts.

## Step 1. Start Redis and Nexus

```bash
docker compose up -d
```

Nexus takes one to two minutes to start. Open http://localhost:8081 and wait for the UI.

Get the generated admin password and sign in (the wizard asks you to set a new password):

```bash
docker exec employee-nexus cat /nexus-data/admin.password
```

During the setup wizard you can leave anonymous access disabled. The Maven configuration below works either way because it always sends credentials.

## Step 2. Configure the Nexus hosted Maven repository

The repository is named `employee-releases` and has these settings: format `maven2`, type `hosted`, version policy `Release`, layout policy `Strict`, blob store `default`, deployment policy `Allow redeploy`.

Create it with the REST API:

```bash
export NEXUS_PASSWORD='your-admin-password'
./nexus/create-hosted-repo.sh
```

A response of `HTTP status: 201` means it was created.

Or create it in the UI: Settings (gear icon) > Repositories > Create repository > `maven2 (hosted)`, name `employee-releases`, version policy `Release`, deployment policy `Allow redeploy`.

The repository URL is `http://localhost:8081/repository/employee-releases/`.

## Step 3. Configure Maven credentials

Copy the template into your Maven settings and set the password. If you already have a `~/.m2/settings.xml`, add only the `<server>` block.

```bash
cp nexus/settings.xml.example ~/.m2/settings.xml
```

The `<id>` in the `<server>` block (`nexus-employee-releases`) must match the repository ids used in both POMs.

## Step 4. Build and publish the library to Nexus

```bash
cd employee-library
mvn clean deploy
```

This runs the library tests, builds `employee-library-1.0.0.jar`, attaches a sources jar, and uploads everything to the hosted repository, as configured in `distributionManagement` in `employee-library/pom.xml`.

Verify the artifact:

- In Nexus: Browse > `employee-releases` > `com/example/employee-library/1.0.0`
- Or from the command line:

```bash
curl -u admin:your-admin-password -I \
  http://localhost:8081/repository/employee-releases/com/example/employee-library/1.0.0/employee-library-1.0.0.jar
```

Expect `HTTP/1.1 200 OK`.

## Step 5. Build and run the application (artifact consumption)

The application POM declares the Nexus repository and the dependency:

```xml
<repositories>
  <repository>
    <id>nexus-employee-releases</id>
    <url>http://localhost:8081/repository/employee-releases/</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.example</groupId>
  <artifactId>employee-library</artifactId>
  <version>${employee-library.version}</version>
</dependency>
```

Make sure Redis is running, then:

```bash
cd ../employee-redis-app
mvn clean package
mvn spring-boot:run
```

The API is available at http://localhost:8080. The H2 console is at http://localhost:8080/h2-console using JDBC URL `jdbc:h2:mem:employeedb`, user `sa`, empty password.

Five employees (ids 1 to 5) are seeded at startup. Set `app.seed.enabled=false` to turn this off.


Sample request body:

```json
{
  "firstName": "Karan",
  "lastName": "Malhotra",
  "email": "karan.malhotra@example.com",
  "department": "MARKETING",
  "salary": 58000,
  "joiningDate": "2022-09-12"
}
```


### TTL configuration

TTLs are set per cache in `employee-redis-app/src/main/resources/application.yml` and are deliberately short so expiration can be shown live:

```yaml
app:
  cache:
    default-ttl: 60s
    employee-ttl: 30s
    employee-list-ttl: 20s
    department-ttl: 20s
```

For production-like use, raise these (for example `10m`). Override at runtime with environment variables such as `APP_CACHE_EMPLOYEETTL=5m`.

## Caching and TTL demonstration

Automated walkthrough (application and Redis must be running, fresh start so the new employee gets id 6):

```bash
./scripts/demo.sh
```

It flushes Redis, then shows in order: cache miss then hit, keys in Redis, `@CachePut` on update, `@CacheEvict` on delete, and TTL countdown, expiry and refill.

Manual version:

```bash
docker exec -it employee-redis redis-cli
```

1. Read twice and watch the application log. The first call logs `CACHE MISS - loading employee 1 from database`. The second call logs nothing because Redis answered.
   ```bash
   curl http://localhost:8080/api/employees/1
   curl http://localhost:8080/api/employees/1
   ```
2. Inspect Redis: `KEYS *` then `TTL employee::1`.
3. `@CachePut`: send a PUT for employee 1, then `GET /api/employees/1`. The new data is returned and no cache miss is logged.
4. `@CacheEvict`: `DELETE /api/employees/6`, then `KEYS *`. `employee::6` and `employees::all` are gone. A GET returns 404.
5. Expiration: read employee 2, run `TTL employee::2` (about 30), wait past the TTL, run `TTL employee::2` again (`-2` means the key is gone), then call the API again and see a new `CACHE MISS`.

## Testing

Library unit tests (validator and utilities):

```bash
cd employee-library && mvn test
```

Application tests in `EmployeeCachingIntegrationTest` prove the caching annotations without needing Redis. The test profile uses Spring's in-memory cache (`spring.cache.type=simple`) and changes database rows behind the cache's back to show that reads come from the cache until it is evicted or updated:

```bash
cd employee-redis-app && mvn test
```

API testing: import `postman/employee-redis-app.postman_collection.json` into Postman and run the collection in order. It contains 11 requests with assertions covering create, read, department query, summary, update, delete, 404, 400 and 409.

## Switching the database

The application uses in-memory H2 by default. To use MySQL or PostgreSQL, add the driver dependency to `employee-redis-app/pom.xml` and override the datasource settings, for example:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/employees \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver \
mvn spring-boot:run
```
