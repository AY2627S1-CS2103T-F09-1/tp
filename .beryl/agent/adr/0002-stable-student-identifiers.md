# ADR 0002: Stable Student Identifiers

## Status

Accepted

## Context

The student roster needs an internal identifier that remains stable when entries are sorted, displayed, and reloaded. Existing persisted student records contain no identifier because the Add Student slice predates the roster contract.

## Decision

Give every new `Student` an immutable UUID-backed `StudentId` and persist it as `studentId`. When loading a legacy record without that field, derive a deterministic UUID from the existing normalized `StudentIdentity`; the next save writes the migrated identifier.

## Consequences

* Roster snapshots can identify students independently of their display index.
* Existing student files remain loadable without a destructive migration.
* Student equality remains based on the existing student fields so existing command and storage behavior is preserved.
* Future student-edit behavior must preserve `StudentId` when changing editable profile fields.
