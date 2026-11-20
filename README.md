# TSIS Spring Boot: Practice 1-3

## Run

    mvn spring-boot:run -Dspring-boot.run.profiles=dev
    mvn spring-boot:run -Dspring-boot.run.profiles=test
    mvn spring-boot:run

In IntelliJ IDEA: open the project folder, run `TsisApplication`, and set
`Active profiles` in the run configuration to `dev` or `test`.

## Endpoints

    GET  /api/moods?mood=happy
    POST /api/moods            

## Conditional bean: MoodAuditor

`MoodAuditor` is created only when `app.mood-audit.enabled=true` (`@ConditionalOnProperty`).

How to toggle it:

- By profile: `--spring.profiles.active=dev` (ON) or `test` (OFF)
- By property: `--app.mood-audit.enabled=true` or `false`
- By environment variable: `APP_MOODAUDIT_ENABLED=true`

When ON, every request to `/api/moods` logs: `[env] mood received: ...`.
When OFF, the bean does not exist and `MoodService` works without it, because it
receives the auditor through `ObjectProvider`.

## Practice 3 notes

- All service-layer classes use constructor injection, fields are `private final`
- No `@Autowired` on fields


## Practice 6: documented API + resilient call

- Swagger UI: http://localhost:8080/swagger-ui.html (OpenAPI JSON: `/v3/api-docs`), via `springdoc-openapi-starter-webmvc-ui`
- Every endpoint has `@Operation` and `@ApiResponse`; error responses use the `ProblemDetail` schema (`application/problem+json`)
- Outbound client: HTTP interface `HolidayApi` on top of `RestClient` (Nager.Date public holidays), `GET /api/v1/holidays?year=2026`
- Timeouts come from `app.holiday-client.connect-timeout` and `app.holiday-client.read-timeout` in `application.yml`
- Fallback: on connection error, timeout or 4xx/5xx `HolidayService` returns 200 with `source=FALLBACK` and an empty list
- Failure path tests: `HolidayServiceFailureTest` (remote stopped, read timeout, HTTP 500)
