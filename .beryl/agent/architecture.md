# Architecture

## Brownfield Baseline

The current codebase is AddressBook Level 3: a Java 25 Gradle/JavaFX desktop application rooted in `seedu.address`. Its existing model, logic, storage, UI, tests, documentation, and GitHub Actions remain authoritative for current behaviour. This document records the intended TutorTrack boundaries; it does not authorise speculative renaming or replacement before a ratified plan.

## Intended TutorTrack Bounded Contexts

| Context | Owns | Does Not Own | Public Entry Point |
| --- | --- | --- | --- |
| Student roster | Student records and roster operations. | UI controls, file formats, session-note rendering. | `Model#getStudentRoster` and `Model#deleteStudent`. |
| Student profile and sessions | Profile composition and a student's chronological session notes. | Cross-student queries and storage implementation. | `Model#getStudentProfile`, `Model#addSessionNote`, `Model#editSessionNote`, `Model#getSessionHistory`, `Model#deleteSessionNote`, and `Model#removeSessionHistory`. |
| Persistence | Serialisation, loading, saving, and migration of local data. | Domain validation or JavaFX state. | Storage interface/manager. |
| JavaFX UI | Roster and profile presentation plus user input. | Domain rules and serialisation. | Existing UI/controller boundary. |

## Boundary Rules

1. UI code delegates to logic/model APIs and does not manipulate persisted records or storage formats directly.
1. Domain logic owns validation and invariants; persistence adapters translate data without embedding UI concerns.
1. A context may use another context only through its public API, never its internal representation.
1. New TutorTrack persistence must be designed explicitly for existing-data compatibility and failure handling before replacing AddressBook storage.
1. Do not alter existing AddressBook boundaries merely to make future work look cleaner; change them only in a planned, tested TutorTrack slice.

## Public Interface Rule

Keep public interfaces small, explicit, and expressed in TutorTrack vocabulary. Avoid exposing JavaFX controls, Jackson DTOs, mutable collections, or file-path details outside their owning boundary.
