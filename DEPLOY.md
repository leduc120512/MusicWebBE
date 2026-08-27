# Deploying to Render

The app ships as a Docker image. Render builds it from [`Dockerfile`](Dockerfile)
straight out of this repository — no Docker Hub account and no local Docker
needed on your side.

---

## 1. Prepare the database (Aiven)

Import the schema once:

```bash
mysql --host <service>.aivencloud.com --port <port> --user avnadmin --password --ssl-mode=REQUIRED < db/music_db.sql
```

The script creates its own `music_db` schema and leaves Aiven's `defaultdb`
alone. Details and the `defaultdb` variant are in [db/README.md](db/README.md).

Verify before moving on:

```bash
mysql --host <service>.aivencloud.com --port <port> --user avnadmin --password --ssl-mode=REQUIRED -e "SELECT COUNT(*) FROM music_db.songs;"
```

44 rows means the import worked.

---

## 2. Create the Render service

In the Render dashboard: **New → Blueprint**, pick this repository. Render reads
[`render.yaml`](render.yaml) and asks for the four values it will not read from
the repo:

| Prompt | Value |
|--------|-------|
| `DB_HOST` | `<service>.aivencloud.com` |
| `DB_PORT` | the port on the Aiven service page |
| `DB_PASSWORD` | the Aiven password |
| `OLLAMA_BASE_URL` | leave blank unless you have a public Ollama |

Everything else is already set in the blueprint: `DB_NAME=music_db`,
`DB_USERNAME=avnadmin`, `DB_SSL_MODE=REQUIRED`, a generated `JWT_SECRET`,
`UPLOAD_DIR=/tmp/upload`, region `singapore` (closest to Aiven's Bangalore
region), and `healthCheckPath=/api/health`.

Prefer clicking through instead of a blueprint? **New → Web Service**, choose
**Docker**, then add those environment variables by hand.

---

## 3. Check the deploy

```bash
curl https://<your-service>.onrender.com/api/health
```

```json
{"success":true,"message":"Service is healthy","data":{"status":"UP"}}
```

Swagger UI is at `https://<your-service>.onrender.com/swagger-ui.html`.

Run the whole API suite against the deployed instance:

```bash
npx newman run postman/music-api.postman_collection.json -e postman/music-api.postman_environment.json --env-var baseUrl=https://<your-service>.onrender.com
```

---

## What the free plan costs you

**Uploads do not survive a redeploy.** Free instances have no persistent disk,
so `UPLOAD_DIR=/tmp/upload` is wiped on every deploy and every restart. Anything
a user uploads is gone the next time the service restarts. Three ways out:

1. Attach a Render **Disk** (needs a paid instance) and set `UPLOAD_DIR` to its
   mount path.
2. Move uploads to object storage — Cloudinary has a free tier that suits this
   app. That means changing `FileStorageService` to upload instead of writing to
   disk; it is the only class that touches the filesystem, so the change is
   contained.
3. Accept it for a demo.

**The seed data already points at files nobody has.** 112 rows reference
`/upload/...` paths (44 audio files, 44 song covers, 10 album covers, 9 banners,
5 avatars). Those files lived under `D:/web nhac/duan1/upload`, which no longer
exists on the development machine either — so audio and images already 404
locally, and will on Render too. The API itself works; only the media is
missing. Re-upload through the API, or restore that folder and point
`UPLOAD_DIR` at it.

**The service sleeps.** Free instances spin down after about 15 minutes of
inactivity; the next request takes roughly 30–60 seconds while it wakes up. The
health check does not keep it awake.

**512 MB of RAM.** The Dockerfile caps the heap at 70% and uses the serial
collector, which is enough for this app but leaves little headroom.

---

## Running the image locally

Nothing here is Render-specific — the same image runs anywhere:

```bash
docker build -t music-api .
```

```bash
docker run --rm -p 8082:8082 -e PORT=8082 -e DB_HOST=host.docker.internal -e DB_PORT=3306 -e DB_NAME=music_db -e DB_USERNAME=root -e DB_PASSWORD=yourpassword -e DB_SSL_MODE=DISABLED music-api
```

`host.docker.internal` reaches the MySQL running on your machine from inside the
container.

---

## Continuous integration

[`.github/workflows/ci.yml`](.github/workflows/ci.yml) runs on every push and
pull request: `./mvnw verify` for the 29 unit tests, then a `docker build` of
this same Dockerfile — so a broken image fails on GitHub before Render ever
tries.
