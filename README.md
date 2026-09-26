# TSIS Spring Boot: Practice 1-3

## Run

    mvn spring-boot:run -Dspring-boot.run.profiles=dev
    mvn spring-boot:run -Dspring-boot.run.profiles=test
    mvn spring-boot:run

In IntelliJ IDEA: open the project folder (pom.xml), run `TsisApplication`, and set
`Active profiles` in the run configuration to `dev` or `test`.

## Endpoints

    GET  /api/moods?mood=happy
    POST /api/moods            (body: plain text)

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
