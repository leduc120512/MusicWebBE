# Database

## Import

```bash
mysql -u root -p < db/music_db.sql
```

The script is self-contained: it creates `music_db` (utf8mb4 /
`utf8mb4_unicode_ci`), drops and recreates every table, loads the seed data, and
creates the four reporting views. Re-running it **discards existing data**.

To import into a different schema name, edit the `CREATE DATABASE` / `USE` lines
at the top of the file and set `DB_NAME` to match.

---

## `music_db.sql`

Full schema plus seed data, generated from `mysqldump`. The original dump came
from **MariaDB 10.4** and was normalised for **MySQL 8**:

| Change | Why |
|--------|-----|
| Added `CREATE DATABASE IF NOT EXISTS music_db` | the dump only had `use music_db`, so it failed on a fresh server with *Unknown database* |
| `SET NAMES utf8` → `utf8mb4` | the tables are utf8mb4; a utf8mb3 connection truncates 4-byte characters |
| Removed `DEFAULT NULL` from 9 `TEXT` / `LONGTEXT` columns | MySQL 8 rejects defaults on BLOB/TEXT; MariaDB allows them |
| `current_timestamp()` → `CURRENT_TIMESTAMP` (23 places) | portable spelling |
| Removed `DEFINER=\`root\`@\`localhost\`` from 4 views | otherwise the import fails unless you import as `root@localhost` |
| `utf8mb4_general_ci` → `utf8mb4_unicode_ci` on `user_logins` | it was the only table on a different collation, which causes *Illegal mix of collations* on joins |

### Tables

| Table | Entity |
|-------|--------|
| `users` | `User` |
| `songs` | `Song` |
| `albums` | `Album` |
| `genres` | `Genre` |
| `likes` | `Like` |
| `follows` | `Follow` |
| `comments` | `Comment` |
| `comment_reports` | `CommentReport` |
| `play_history` | `PlayHistory` |
| `playlists` | `Playlist` |
| `playlist_songs` | join table for `Playlist.songs` |
| `banners` | `Banner` |
| `popup_ads` | `PopupAd` |
| `artist_profiles` | `ArtistProfile` |
| `artist_news` | `ArtistNews` |
| `artist_registration_requests` | `ArtistRegistrationRequest` |
| `song_violation_reports` | `SongViolationReport` |
| `password_reset_token` | `PasswordResetToken` — **unused**, see below |
| `user_logins` | **no entity** — see below |

Views: `artist_stats`, `popular_songs`, `public_playlists`, `song_stats`.
No entity or repository reads them; they are reporting helpers only.

Every JPA entity column was checked against this schema and they line up, which
is why `spring.jpa.hibernate.ddl-auto` can safely be `none`.

---

## Things to clean up

**`user_logins`** — a login-audit table with 19 rows. No entity, repository, or
service touches it. Drop it, or add the entity that was meant to write to it.

**`password_reset_token`** — mapped by `PasswordResetToken`, written by
`UserService`, but no controller exposes password reset. The table, the entity,
the repository, the service, the `spring-boot-starter-mail` dependency, and the
`spring.mail.*` properties all exist for a feature that has no endpoint. Either
wire up a `POST /api/auth/forgot-password` + `POST /api/auth/reset-password`
pair, or delete the lot.

**`playlists` / `playlist_songs` carry the "favourite albums" feature.**
`FavoriteAlbumController` and `FavoriteAlbumService` operate on `Playlist`, so a
row in `playlists` is what the API calls a favourite album. The naming is
confusing; renaming the tables to `favorite_albums` / `favorite_album_songs`
would make the schema match the API.

**30 of 36 `users` rows cannot sign in.** Their `password` column holds a BCrypt
hash from an earlier version, but `SecurityConfig` uses `NoOpPasswordEncoder`
and `AuthController` compares the raw string. Only these still work:

| Username | Password | Role |
|----------|----------|------|
| `duc12` | `123456` | `ROLE_ADMIN` |
| `testu48962` | `123456` | `ROLE_AUTHOR` |
| `user1` | `123456` | `ROLE_AUTHOR` |
| `user12` | `1234` | `ROLE_AUTHOR` |
| `hunghn` | `1234` | `ROLE_USER` |
| `demo123` | `leduc` | `ROLE_USER` |

---

## Importing into Aiven (managed MySQL)

Import the same file — `db/music_db.sql`. Nothing else needs changing: the
`DEFINER` clauses were stripped precisely so a non-root user such as `avnadmin`
can create the four views.

```bash
mysql --host <service>.aivencloud.com --port <port> --user avnadmin --password --ssl-mode=REQUIRED < db/music_db.sql
```

The script creates its own `music_db` schema, so it leaves the `defaultdb` that
Aiven provisions untouched. To load into `defaultdb` instead, delete the
`CREATE DATABASE` and `USE` lines at the top of the file and pass
`--database defaultdb`.

Then point the app at it. Put this in
`src/main/resources/application-local.properties` (git-ignored), or export the
equivalent environment variables:

```properties
spring.datasource.url=jdbc:mysql://<service>.aivencloud.com:<port>/music_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Ho_Chi_Minh&sslMode=REQUIRED
spring.datasource.username=avnadmin
spring.datasource.password=<password from the Aiven service page>
```

`sslMode=REQUIRED` is not optional — Aiven refuses plaintext connections. The
default in `application.properties` is `DISABLED`, sized for a local MySQL, so
it has to be overridden (with the property above or `DB_SSL_MODE=REQUIRED`).

Keep the password out of `application.properties`: that file is committed.

---

## `patches/`

Six incremental `ALTER` / `CREATE TABLE IF NOT EXISTS` scripts from before the
full dump existed:

```
artist_comment_moderation_patch.sql
artist_feature_schema.sql
favorite_albums.sql
gender_like_patch.sql
popup_ads_and_ai_comment_moderation.sql
song_violation_report_priority.sql
```

Everything they create is already in `music_db.sql`. They are kept only as a
record of how the schema evolved — **do not run them on a database built from
`music_db.sql`**. They used to live in `src/main/resources/sql/`, where they
were packaged into the jar for no reason.
