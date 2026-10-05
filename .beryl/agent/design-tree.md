# Design Tree

## Current Design Concept

TutorTrack will evolve the existing JavaFX desktop application into a focused local workspace for a private tutor. The core domain is a student roster: each student owns stable lesson-preparation context and a chronological history of short session notes. The existing AddressBook implementation remains the baseline until a feature plan is ratified and implemented in small vertical slices.

## Open Decisions

| Decision | Options | Current Lean | Why |
| --- | --- | --- | --- |

## Settled Decisions

| Decision | Choice | Date | ADR |
| --- | --- | --- | --- |
| Product direction | TutorTrack serves a private one-to-one tutor with a small active roster. | 2026-09-18 | n/a |
| Delivery approach | Preserve current AddressBook behaviour until ratified TutorTrack slices replace it deliberately. | 2026-09-18 | n/a |
| First-release scope | Roster, student profile, chronological session history, and adding a short session note. | 2026-09-18 | n/a |
| Student identity | Duplicate students have the same case-insensitive, whitespace-normalized name and parent or guardian phone number after spaces and hyphens are removed. | 2026-10-02 | n/a |
| Parent or guardian contact | A Student has a required phone number and optional email address for the responsible adult. | 2026-10-02 | n/a |
| Student internal identity | Each Student has an immutable UUID-backed `StudentId`; legacy records without an ID derive one deterministically from their normalized duplicate identity. | 2026-10-03 | [ADR 0002](adr/0002-stable-student-identifiers.md) |
| Student roster projection | `Model#getStudentRoster()` returns immutable `StudentRosterEntry` snapshots ordered by normalized name, normalized parent or guardian phone number, and stable student ID, with one-based display indices and note counts supplied by the owning session context. | 2026-10-03 | [ADR 0003](adr/0003-student-roster-snapshots.md) |
| Session-note timestamp | A `SessionNote` records the system date-time with its time-zone offset as an `OffsetDateTime`; `SessionHistory` orders notes newest first by instant and keeps insertion order for equal instants. | 2026-10-05 | n/a |
| Save-failure rollback | `LogicManager` copies the address book before each command and restores it when saving fails, so a failed save leaves the model unchanged for every command. | 2026-10-05 | n/a |

## Pressure Points

* The inherited AddressBook model and documentation use contact-centric names; do not silently mix those terms into new TutorTrack domain code.
* Parent or guardian contact details and session notes are personal data; avoid logging them or placing realistic private data in tests and fixtures.
* Existing data compatibility, migration, and UI replacement require explicit design decisions before implementation.

## Recording Rule (Design Tree vs ADR)

Update this file for evolving decisions or short-lived comparisons. Create an ADR for durable module boundaries, persistence shape, data migration, security model, naming used across contexts, or test strategy.
