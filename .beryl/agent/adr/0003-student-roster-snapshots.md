# ADR 0003: Student Roster Snapshots

## Status

Accepted

## Context

The list feature needs stable display data without exposing mutable student collections to commands or JavaFX. Roster ordering and display indices are presentation concerns, while session-note storage belongs to the profile and sessions context.

## Decision

Expose `Model#getStudentRoster()` as an immutable `StudentRoster` snapshot. Each `StudentRosterEntry` contains the stable student ID, one-based display index, student fields, and a non-negative session-note count. `StudentRoster` sorts by normalized student name, then normalized parent or guardian phone number, and finally stable student ID; it accepts note counts as projection input rather than owning session-note data.

## Consequences

* Commands and UI can consume a deterministic, read-only contract.
* Sorting changes display indices without changing student identity.
* Session-note code remains responsible for calculating note counts.
* The current student-only implementation reports zero note counts until session history is integrated.
