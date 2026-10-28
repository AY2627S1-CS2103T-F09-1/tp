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
| Stable student editing | `edit` resolves the roster index to a stable ID, reads the immutable student-domain record through `Model#getStudent`, replaces only supplied fields, and preserves the student's `StudentId` and session history. The roster owns one conflict lookup used by both the command-facing `Model#findConflictingStudent` query and authoritative replacement. The descriptor records email presence separately from its optional value, so an explicitly empty `e/` removes the email. | 2026-10-22 | n/a |
| Session-note timestamp | A `SessionNote` records the system date-time with its time-zone offset as an `OffsetDateTime`; `SessionHistory` orders notes newest first by instant, treats a note added later as newer among equal instants, and restores saved histories in their saved order. | 2026-10-21 | n/a |
| Save-failure rollback | `LogicManager` copies the address book before commands on the persistence path and restores it when saving fails, so a failed save leaves the model unchanged; non-mutating deletion previews bypass persistence. | 2026-10-19 | n/a |
| Student profile projection | `Model#getStudentProfile(StudentId)` returns an optional immutable `StudentProfile` snapshot; an absent ID returns an empty optional so UI state can recover safely after deletion. | 2026-10-06 | n/a |
| Displayed profile identity | `MainWindow` retains the selected student's stable ID, synchronizes a dedicated matching roster profile-selection state separately from temporary list focus, refreshes that profile after successful commands, leaves both unchanged after failed commands, and clears both if the student no longer exists. | 2026-10-15 | n/a |
| Atomic student deletion | `Model#deleteStudent(StudentId)` validates the stable ID before removing the student and session history together, and returns the number of removed notes. | 2026-10-07 | n/a |
| Chronological note selectors | Profiles show histories newest first but number notes chronologically, with one for the oldest and the history size for the newest; `SessionHistory` maps the transient number to its newest-first position without persisting an identifier. | 2026-10-20 | n/a |
| Confirmed destructive deletion | `LogicManager` keeps one transient pending deletion for either a student or one session note. Student requests bind the displayed index to a stable `StudentId`; note requests also bind the note number to the immutable history snapshot. Only the immediately following matching `/confirm` command may delete, while any other submitted input consumes and cancels the pending deletion. | 2026-10-20 | n/a |
| Session-note editing | `edit note STUDENT_INDEX NOTE_NUMBER nt/NEW_NOTE` replaces only the selected note's text, preserving its timestamp, chronological position, and transient number; the existing save rollback path protects the mutation. | 2026-10-22 | n/a |
| Command error messages | A rejected command names one specific problem. Only structure errors (missing, unknown, repeated, or out-of-place parts) append a one-line `Format:` from the command's `COMMAND_FORMAT` and a one-line `Example:` from its `COMMAND_EXAMPLE`; value errors show only the broken rule, and model-state errors such as an empty roster or out-of-range index give the next step. Indices are described as positive whole numbers and parsed through shared `ParserUtil` helpers, and roster lookups go through `Command#getRosterEntry`. | 2026-10-27 | n/a |
| Session-note text prefix | `note` and `edit note` reject a second `nt/` after a space as a repeated prefix, like every other command, so all commands share one prefix rule. Users who need to mention `nt/` write it without a preceding space, such as `(nt/)`, which the tokenizer does not treat as a prefix. | 2026-10-28 | n/a |
| Atomic data-file replacement | `FileUtil#writeToFile` writes content to a temporary file beside the target and atomically replaces the target, so a failed save preserves the previous data file. | 2026-10-11 | n/a |
| Unreadable data recovery | Storage preserves each unreadable data file in a distinct sibling backup before a command can replace it, so no earlier recovery copy can hide later invalid content. | 2026-10-12 | n/a |

## Pressure Points

* The inherited AddressBook model and documentation use contact-centric names; do not silently mix those terms into new TutorTrack domain code.
* Parent or guardian contact details and session notes are personal data; avoid logging them or placing realistic private data in tests and fixtures.
* Existing data compatibility, migration, and UI replacement require explicit design decisions before implementation.

## Recording Rule (Design Tree vs ADR)

Update this file for evolving decisions or short-lived comparisons. Create an ADR for durable module boundaries, persistence shape, data migration, security model, naming used across contexts, or test strategy.
