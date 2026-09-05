# Current Task

- **Task:** EPIC-002 / TASK-001 — Vehicle Domain Model & Data Layer
- **State:** DONE
- **Objective:** Define the pure Kotlin vehicle model and Room-backed CRUD data layer.
- **Completed:** Added `Vehicle`, repository contract/implementation, explicit entity mappers, vehicle DAO, Hilt repository binding, JVM mapping tests, and an in-memory Room DAO instrumentation test.

## Verification

- `./gradlew --stop && ./gradlew testDebugUnitTest compileDebugKotlin` passes.
- `./gradlew connectedDebugAndroidTest` compiles and packages the instrumentation APK but cannot execute because no connected Android devices are available.
- `.gitignore` added for Android/Gradle build output, IDE files, local secrets, and local toolchains while preserving `app/schemas`.

## Next Task

- EPIC-002 / TASK-002 — Vehicle Entry & Editing Flow
