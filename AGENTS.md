# print-models-archive

Spring Boot 3.4 WebFlux app (Kotlin 1.9, Java 21, Gradle 8.9 wrapper). Scans local folders of 3D print models (
`scan.address1/2/3`), indexes them in MongoDB, stores images in Minio, and generates AI tags/metadata via
Gemini/OpenRouter. Single-module project.

## Commands (Windows: `gradlew.bat`)

- Build / verify: `./gradlew build`
- Run locally: `./gradlew bootRun --args='--spring.profiles.active=local'` → http://localhost:8081/archive
    - The `local` profile points Mongo/Minio at localhost and scans `F:\[Patreon]\[test]`. The default profile targets
      docker hostnames (`mongo`, `minio`) — only useful inside compose.
- Swagger UI (enabled in `local` profile only): http://localhost:8081/archive/swagger-ui.html

## Gotchas

- **No test suite exists** — there is no `src/test`; `./gradlew test` does nothing. Verify with `build` + manual API
  checks.
- All endpoints sit under the `/archive` base path (`spring.webflux.base-path`), e.g. models API at
  `/archive/api/models`.
- Thymeleaf templates load from the filesystem (`file:src/main/resources/templates/`), not the classpath — run with
  project root as working dir (bootRun is fine). The Dockerfile's `COPY src src` exists for this reason.
- AI API keys are **not** in yaml — they live in the Mongo `app_settings` document (`geminiApiKey`, `openRouterApiKey`).
  Cron jobs run every minute but only act when the flags in that same settings doc are enabled (e.g. `imageAiMetaJob`).
- The experimental `ru.starskvim:infrastructure-webflux-3-kotlin-autoconfiguration` dependency resolves from
  mavenLocal (`~/.m2`) — builds fail on a machine without it.
- MapStruct mappers are generated via kapt with the Spring component model; don't hand-write mapper implementations.
- Default-profile Minio endpoint is malformed: `http:/minio:9099` (single slash) in `application.yaml`.

## Manual verification endpoints

POST `/archive/api/test/...` — trigger tag generation, meta retry, and context processing on demand instead of waiting
for cron (see `TestApiController.kt`).

## Docker / deployment

- Documented flow (README): `./gradlew build` → `docker build -t starskvim/starskvim-archive-app .` → compose. The
  Dockerfile expects the jar at `/build/libs/print-models-archive-0.0.2-SNAPSHOT.jar`.
- JIB plugin is also configured (`jib.to.image = "archive-app"`) as an alternative image path.
- `docker-compose-local.yaml` (Windows paths) / `docker-compose-stand.yaml` (Linux paths): archive app + mongo + minio +
  front-end. App listens on **8081** (Dockerfile's `EXPOSE 8082` is stale). Production compose bind-mounts model folders
  into `/Pigure`, `/Pack`, `/Other`.
- Minio runs server on 9099, console on 9000; credentials `minioadmin/minioadmin`.
