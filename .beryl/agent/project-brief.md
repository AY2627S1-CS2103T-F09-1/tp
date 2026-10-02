# Project Brief

## Product Goal

Build **TutorTrack** for private one-to-one tutors managing roughly 10–40 active students so they can keep student contacts and lesson context organised, prepare quickly for each lesson, and record what matters afterwards.

## Value Proposition

TutorTrack helps private tutors keep student contacts organised alongside the context they need for each lesson: parent contact, subject, current level, and short notes on recent sessions so they can prepare quickly.

## Primary Workflows

1. **Maintain a student roster**: a tutor can add a selectable student record containing the student's name, parent or guardian contact details, subject, and current level, then browse all current students in one roster view.
2. **Retrieve lesson context**: a tutor can open a student profile and see the parent or guardian contact details, subject, current level, and chronological session-note history, with the most recent note easy to identify.
3. **Record a session note**: from a student's profile, a tutor can add a short lesson note that is saved with that student and appears in the session history.

## Brownfield Baseline

The repository currently contains the AddressBook Level 3 JavaFX application. Its behaviour, build, tests, documentation, and GitHub Actions are preserved until a ratified TutorTrack implementation plan deliberately changes them. TutorTrack is the target product context for future work; it does not claim that the current AddressBook implementation already provides these workflows.

## Non-Goals

* Multi-tutor accounts, authentication, sharing, or cloud synchronisation.
* Lesson scheduling, billing, attendance, grading, messaging, or parent portal.
* Replacing the current application behaviour as part of this Beryl setup.

## External Systems

| System | Why it exists | Interface owner | Failure fallback |
| --- | --- | --- | --- |
| Local application data file | Persist the tutor's roster and session context. | Storage boundary | Preserve valid existing data; report a recoverable storage error. |

## Definition Of Done

A TutorTrack feature is complete only when it has all of the following:

1. A design artifact update (`design-tree.md` and/or ADR) when durable design changes.
2. Clear domain, UI, and persistence boundaries where the change crosses them.
3. Behaviour tests plus at least one relevant edge-case test.
4. The relevant Gradle checks and `./.beryl/scripts/check.sh` run.
5. No unrelated AddressBook behaviour, workflow, or GitHub Action is changed.
