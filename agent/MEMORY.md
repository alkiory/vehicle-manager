# Repository Memory

## Architecture

- Presentation uses Jetpack Compose and ViewModels exposing `StateFlow<UiState>`.
- Domain code must remain pure Kotlin with no Android or Room imports.
- Data code owns Room entities, DAOs, repositories, and entity-to-domain mappers.
- Room is the offline-first canonical source of truth.
- Monetary values use `Long` minor units; fuel quantities use integer liters multiplied by 100; odometers use kilometers.

## Current implementation

- Android namespace and application ID: `com.example.vehiclemanager`.
- The first implementation task is EPIC-001 / TASK-001.
