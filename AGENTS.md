# print-models-archive

Spring Boot 3.4 WebFlux app (Kotlin 1.9, Java 21, Gradle 8.9 wrapper). Scans local folders of 3D print models (
`scan.address1/2/3`), indexes them in MongoDB, stores images in Minio, and generates AI tags/metadata via
Gemini/OpenRouter. Single-module project.

## Commands (Windows: `gradlew.bat`)

- Build / verify: `./gradlew build` (~2–3 min cold). Needs JDK 21; `JAVA_HOME` may be unset in the shell — the
  IntelliJ-managed JDK lives at `~/.jdks/liberica-full-21.0.10` (e.g. `JAVA_HOME=~/.jdks/liberica-full-21.0.10 ./gradlew build`).
- Run locally: `./gradlew bootRun --args='--spring.profiles.active=local'` → http://localhost:8081/archive
    - `application-local.yaml` overlays `application.yaml` (keys it doesn't set, e.g. `job.*`, `openrouter.*`, are
      inherited). It points Mongo/Minio at localhost and scans `F:\[Patreon]\[test]`. The default profile targets
      docker hostnames (`mongo`, `minio`) — only useful inside compose.
- Swagger UI (enabled in `local` profile only): http://localhost:8081/archive/swagger-ui.html

## Gotchas

- **No tests** — `src/test/kotlin/...` contains only empty package directories and there are no test dependencies;
  `./gradlew test` reports `NO-SOURCE`. Verify with `build` + manual API checks.
- All endpoints sit under the `/archive` base path (`spring.webflux.base-path`): REST under `/archive/api/...`
  (`models`, `models/favorites`, `catalog`, `admin`, `test`), Thymeleaf pages under `/archive/models`, `/archive/admin`.
- Thymeleaf templates load from the filesystem (`file:src/main/resources/templates/`), not the classpath — run with
  project root as working dir (bootRun is fine). The Dockerfile's `COPY src src` exists for this reason.
- AI API keys are **not** in yaml — they live in Mongo collection `app_settings`, document `_id: "app_settings_id"`
  (`geminiApiKey`, `openRouterApiKey`, plus a SOCKS5 `proxy` block). `AppSettingsService` creates/back-fills this doc
  with defaults on startup (jobs off, `commonBatchSize: 10`).
    - The Gemini/OpenRouter `WebClient` beans read the proxy (and the OpenRouter key) **once at startup** — restart the
      app after changing them. The Gemini key is read per request.
- Scheduled jobs (cron every 60s via `job.*.cron`) check settings flags on each tick:
    - `ImageAiMetaJob` → `imageAiMetaJob`; `ImageAiMetaRetryJob` → `imageAiMetaClearJob`.
    - `LocalContextJob` has **no** `@Scheduled` and is only created when `local-context.job=true` (false by default);
      context processing is triggered manually via `/api/test/context`.
- The experimental `ru.starskvim:infrastructure-webflux-3-kotlin-autoconfiguration` dependency resolves from
  mavenLocal (`~/.m2`) — builds fail on a machine without it.
- MapStruct mappers are generated via kapt with the Spring component model; don't hand-write mapper implementations.
- Default-profile Minio endpoint is malformed: `http:/minio:9099` (single slash) in `application.yaml`.

## Manual verification endpoints

POST `/archive/api/test/...` (`TestApiController.kt`) — run work on demand instead of waiting for cron:
`/tags` and `/tags/openrouter` (body `{path, name}`), `/tags/{id}`, `/context`, `/context/{id}`, `/context/load`,
`/meta-retry`.

## Docker / deployment

- Image flow: `./gradlew build` → `docker build -t starskvim/starskvim-archive-app .` → `docker push` (README lists
  only the docker steps). The Dockerfile copies `build/libs/print-models-archive-0.0.2-SNAPSHOT.jar` (the boot jar; a
  `-plain.jar` is also produced) — update it if `version` in `build.gradle.kts` changes.
- JIB plugin is also configured (`jib.to.image = "archive-app"`) as an alternative image path.
- `docker-compose-local.yaml` (Windows paths) / `docker-compose-stand.yaml` (Linux paths): archive app + mongo + minio +
  front-end (port 3000). App listens on **8081** (Dockerfile's `EXPOSE 8082` is stale). Only the stand compose
  bind-mounts model folders into `/Pigure`, `/Pack`, `/Other`.
    - Local compose also runs Elasticsearch 8.19.19 (9200, security off) + Kibana (5601); data under
      `C:/DbStorage/{mongo,minio,elasticsearch}`. Mount container data dirs exactly: Mongo `/data/db`, ES
      `/usr/share/elasticsearch/data`.
- Minio runs server on 9099, console on 9000; credentials `minioadmin/minioadmin`.
