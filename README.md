# Music API

REST backend for a music streaming site: songs, albums, genres, likes, comments,
play history, artist studio, and admin moderation. Spring Boot 3.2 / Java 17 /
MySQL 8, stateless JWT authentication.

---

## Requirements

| Tool  | Version |
|-------|---------|
| JDK   | 17 (the build targets `java.version=17`) |
| MySQL | 8.0 or newer |
| Maven | use the bundled `./mvnw` wrapper |

---

## Getting started

**1. Create the database**

```bash
mysql -u root -p < db/music_db.sql
```

Creates `music_db` with the schema, the four reporting views, and seed data.
See [db/README.md](db/README.md) — it also covers importing into Aiven or any
other managed MySQL.

**2. Point the app at your MySQL**

Nothing sensitive is committed; the defaults assume `root` with an empty password
on `localhost:3306`. Copy the sample profile and edit it — the real file is
git-ignored:

```bash
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Or export the variables instead:

```bash
export DB_USERNAME=root DB_PASSWORD=your-password UPLOAD_DIR="D:/web nhac/duan1/upload"
./mvnw spring-boot:run
```

**3. Check it is up**

```bash
curl http://localhost:8082/api/health
```

---

## API documentation

Swagger UI is served by the running app:

* **<http://localhost:8082/swagger-ui.html>** — browsable, with an **Authorize**
  button. Sign in through `POST /api/auth/signin` and paste the `accessToken`.
* **<http://localhost:8082/v3/api-docs>** — the raw OpenAPI 3 document
  (79 paths, 106 operations).

---

## Testing

**Unit tests** — no database required:

```bash
./mvnw test
```

29 tests covering the response envelope, the error contract, file storage, and
the authentication rules.

**API tests** — the Postman collection in `postman/`:

1. Import `postman/music-api.postman_collection.json`
2. Import `postman/music-api.postman_environment.json` and select it
3. Run the collection **top to bottom** — folder `01 - Auth` signs in as each
   role and stores the tokens the later folders reuse

Or headless:

```bash
npx newman run postman/music-api.postman_collection.json -e postman/music-api.postman_environment.json
```

74 requests / 336 assertions. Every request inherits three collection-level
checks: the body must use the envelope, `success` must agree with the HTTP
status, and `message` must not contain internal error text. Folder
`99 - Error contract` holds the negative cases.

Seed accounts the collection uses:

| Username | Password | Role |
|----------|----------|------|
| `duc12`  | `123456` | `ROLE_ADMIN` |
| `user1`  | `123456` | `ROLE_AUTHOR` |
| `hunghn` | `1234`   | `ROLE_USER` |

---

## Response contract

Every endpoint — success, failure, 401, 403, 404, 405, 409, 500 — answers with
the same envelope:

```json
{
  "success": true,
  "message": "Login successful",
  "data": { }
}
```

* `success` always agrees with the HTTP status class (`true` only for 2xx).
* `message` is written for the caller. Internal detail (stack traces, JDBC or
  Jackson messages) is logged server-side and never returned.
* `data` is `null` when there is nothing to return.

`GlobalExceptionHandler` owns every error body. `JwtAuthenticationEntryPoint`
and `JwtAccessDeniedHandler` keep 401 and 403 in the same shape instead of
falling back to Spring's whitelabel page. Services signal failures with
`ResponseStatusException`, so the status is decided where the rule lives.

| Status | When |
|--------|------|
| 200 / 201 | success |
| 400 | validation failure, unparsable body, bad parameter type |
| 401 | missing, invalid or expired token; wrong credentials |
| 403 | authenticated but the role does not cover the endpoint |
| 404 | unknown endpoint or missing entity |
| 405 | wrong HTTP verb on a known path |
| 409 | duplicate username/email, or a row still referenced by other data |
| 413 | upload above `spring.servlet.multipart.max-file-size` |
| 500 | unhandled — logged, generic message returned |

An anonymous request to an unknown path answers **401, not 404**: Spring
Security evaluates `anyRequest().authenticated()` before routing, so callers
cannot probe for which endpoints exist.

---

## Layout

```
src/main/java/com/musicapi/
  config/      SecurityConfig, StaticResourceConfig, OpenApiConfig
  controller/  REST endpoints - binding and delegation only
  dto/         request/response payloads, the ApiResponse envelope
  error/       GlobalExceptionHandler, StorageException
  model/       JPA entities and enums
  repository/  Spring Data repositories
  security/    JWT filter, provider, entry point, access-denied handler
  service/     business rules; the only layer that touches repositories
src/test/java/ unit tests
db/
  music_db.sql schema + seed data
  patches/     historical migrations, superseded by music_db.sql
docs/          use-case diagram
postman/       collection + environment
```

Conventions worth keeping:

* Controllers never inject a repository. They call a service, and the service
  raises `ResponseStatusException` when a rule is broken.
* Dependencies are constructor-injected — there is no `@Autowired` field in the
  codebase.
* All uploads go through `FileStorageService`; no controller touches `Files`.
* API messages are English.

---

## Configuration

| Property | Env var | Default |
|----------|---------|---------|
| `spring.datasource.url` | `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_SSL_MODE` | `localhost:3306/music_db`, SSL `DISABLED` |
| `spring.datasource.username` | `DB_USERNAME` | `root` |
| `spring.datasource.password` | `DB_PASSWORD` | *(empty)* |
| `server.port` | `SERVER_PORT` | `8082` |
| `app.upload.dir` | `UPLOAD_DIR` | `D:/web nhac/duan1/upload` |
| `jwt.secret` | `JWT_SECRET` | dev value — **set this anywhere shared** |
| `jwt.expiration` | `JWT_EXPIRATION` | `86400000` (24h) |
| `ollama.base-url` | `OLLAMA_BASE_URL` | `http://localhost:11434` |

`spring.jpa.hibernate.ddl-auto` is `none`: the schema belongs to
`db/music_db.sql`, not to Hibernate. Switch it to `validate` once you have
confirmed a fresh import starts cleanly.

Uploads are written under `app.upload.dir` and served from `/upload/**`. The
paths in the seed data (`/upload/userImg/...`, `/upload/uploadalbums/...`)
resolve against that directory, so point `UPLOAD_DIR` at the folder holding the
existing media.

---

## Known issues

**`spring.jpa.open-in-view` is `true`.** Several endpoints still serialise JPA
entities directly, so the Hibernate session has to stay open through rendering.
Turning it off is worthwhile, but only alongside a pass that returns a DTO from
every endpoint — otherwise those endpoints fail with `LazyInitializationException`.

**Some endpoints still return entities rather than DTOs.** `/api/auth/**` and
the artist-request endpoints were converted; comments, albums and songs mostly
were not.

**Dead code.** `UserService` and `PasswordResetToken` implement password reset,
but no controller exposes it — `spring-boot-starter-mail` and every
`spring.mail.*` property exist only for that. Either add
`POST /api/auth/forgot-password` and `POST /api/auth/reset-password`, or delete
the lot. The `user_logins` table has no entity either.

**No integration tests.** The 29 unit tests need no database; the Postman
collection is the only end-to-end coverage and it has to be run by hand.

**`favorite albums` are stored in the `playlists` table.** The API and the
schema disagree on the name; see [db/README.md](db/README.md).
