# Testing

The project requires a complete JDK 21 or newer for editor and Gradle tooling, including `java`, `javac`, and `jlink`. This refers to the Java toolchain; the Android compile SDK remains 35.

The foundation task is verified with `./gradlew assembleDebug`. Feature tasks should add unit tests for domain and repository logic; UI/navigation tasks should use instrumented tests where specified by `instructions.md`.
