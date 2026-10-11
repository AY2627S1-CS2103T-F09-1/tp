# TutorTrack API Contract

**Status:** Proposed shared contract for MVP implementation

This document defines the interfaces and behavior that feature branches must
share. It is intentionally independent of JavaFX controls, Jackson classes,
file paths, and the inherited AddressBook implementation.

The team should ratify this document before implementing feature branches. Any
change to a contract below requires agreement from the owners of the affected
branches and an update to this document before dependent work continues.

## MVP boundary

TutorTrack supports one local tutor managing a roster of students. The MVP
includes:

* Adding a student
* Listing the student roster
* Viewing a student profile
* Adding a session note
* Deleting a student and the student's session notes
* Saving and loading data from a local, human-editable JSON file

The MVP does not include editing, searching, undo, scheduling, attendance,
messaging, billing, accounts, multi-user access, or cloud synchronisation.
The `exit` command remains application lifecycle behavior rather than a
TutorTrack domain feature.

## Contract decisions

| Topic | Contract decision |
| --- | --- |
| Student identity | Each student has an internal `StudentId`. The ID is not entered by the tutor or displayed as the roster selector. |
| Duplicate student | Two students are duplicates when their normalized names and normalized parent phone numbers both match. |
| Parent contact | Parent or guardian phone is required; parent or guardian email is optional. |
| Roster selection | User-facing selection uses a positive, one-based index in the current sorted roster. Domain APIs use `StudentId`. |
| Roster order | Students are sorted by normalized name, case-insensitively. Equal names retain creation order. |
| Session timestamp | A session note receives the current local date and time when it is added. |
| Note order | Profiles display notes newest first. Equal timestamps retain insertion order. |
| Persistence | The data file is local, human-editable JSON. No database or network service is required. |
| Failed mutation | Validation or storage failure leaves the in-memory model, UI, and file unchanged. |

The internal `StudentId` is recommended even though the MVP selects students by
roster index. It prevents profile, note, and delete operations from depending
on a row number after the roster is redrawn.

## Domain vocabulary

The following terms are shared across branches:

* `Student`: A learner managed by the tutor.
* `StudentRoster`: The collection of active students and its ordering and
  duplicate invariants.
* `ParentGuardianContact`: The required phone and optional email belonging to a
  student's parent or guardian.
* `Subject`: The topic taught to the student.
* `CurrentLevel`: The student's current stage in the subject.
* `SessionNote`: A short, timestamped record of what mattered in a lesson.
* `SessionHistory`: The ordered notes belonging to one student.
* `StudentProfile`: A read-only composition of student context and session
  history.
* `Storage`: The adapter that reads and writes the local JSON representation.

## Public domain APIs

These signatures describe the contract. The implementation may use different
private classes, but feature branches must preserve the same responsibilities
and observable behavior.

### Student roster

```java
public interface StudentRoster {
    /** Returns all students in the current display order. */
    List<RosterEntry> listStudents();

    /** Returns the student selected by a one-based roster index. */
    StudentId studentAt(RosterIndex index);

    /** Returns a read-only student snapshot. */
    StudentSnapshot getStudent(StudentId studentId);

    /** Adds a validated student and returns its internal identifier. */
    StudentId addStudent(NewStudent student);

    /** Removes one student record from the roster. */
    StudentSnapshot removeStudent(StudentId studentId);
}
```

`RosterEntry` contains the user-visible index, `StudentId`, name, subject,
current level, and session-note count. `StudentSnapshot` contains the student
fields but not mutable domain collections. The deletion coordinator combines
the removed student snapshot and the removed note count into a `DeletedStudent`
result for the success message.

### Profile and session API

```java
public interface StudentProfileService {
    /** Composes the read-only profile for one student when that student exists. */
    Optional<StudentProfile> getProfile(StudentId studentId);

    /** Validates, timestamps, and appends one session note. */
    SessionNote addSessionNote(StudentId studentId, String noteText);

    /** Removes and returns the number of notes owned by one student. */
    int removeSessionHistory(StudentId studentId);
}
```

`StudentProfile` contains the student's stable ID, name, parent or guardian contact, subject, current level, and an immutable list of `SessionNote` objects ordered newest first. `SessionNote` contains its saved timestamp and text. An unknown or deleted student ID returns an empty optional, which lets the UI discard stale profile state without exposing model internals.

Neither API exposes JavaFX controls, Jackson DTOs, mutable collections, or file
paths. The profile/session context does not perform cross-student searches or
directly access storage.

### Storage API

```java
public interface TutorTrackStorage {
    /** Loads a snapshot, or an empty snapshot when no data file exists. */
    TutorTrackSnapshot load() throws StorageException;

    /** Writes a complete valid snapshot. */
    void save(TutorTrackSnapshot snapshot) throws StorageException;
}
```

The storage adapter owns JSON serialization, file paths, loading, saving, and
storage-specific failures. Domain validation remains in the domain layer.

## Command contract

The parser accepts these commands:

```text
add n/NAME p/PHONE sub/SUBJECT l/LEVEL [e/EMAIL]
list
view INDEX
note INDEX nt/NOTE
delete INDEX
```

Command words and prefixes are case-insensitive. Add prefixes may appear in
any order, but each recognized prefix may appear at most once. The `note`
command recognizes only `nt/` as its note-text prefix.

### Add student

1. Validate all supplied fields before changing the model.
1. Reject a duplicate normalized name and parent phone combination.
1. Save the new student before publishing the mutation to the active state.
1. Show the redrawn roster in alphabetical order.
1. Report `Added student: <Name> (<Subject>, <Level>).` on success.

Required fields are name, parent phone, subject, and current level. Email is
optional.

### List roster

1. Display every valid student in sorted order.
1. Show each student's index, name, subject, current level, and note count.
1. Establish the displayed indices as the current selection context.
1. Report an empty-roster message when there are no students.

`list` accepts no arguments and does not mutate stored data.

### View profile

1. Resolve `INDEX` using the current roster selection context.
1. Display the student's name, parent or guardian phone, optional email,
   subject, and current level.
1. Display all session notes newest first, including timestamp and text.
1. Display `No session notes recorded.` when the history is empty.

An invalid, missing, or out-of-range index leaves any existing profile
unchanged.

### Add session note

1. Resolve `INDEX` using the current roster selection context.
1. Validate `NOTE` as a one-line printable value between 1 and 500 characters.
1. Timestamp and append the note to that student's history.
1. Save the mutation and refresh an open profile if one exists.
1. Report `Added session note for <Name>.` on success.

Identical note text is allowed more than once because each note represents a
separate lesson event.

### Delete student

1. Resolve `INDEX` using the current roster selection context.
1. Remove the selected student and all of that student's session notes.
1. Save the mutation before publishing the new active state.
1. Report the student's name and the number of notes removed.
1. Redraw the roster and establish its new indices.

If the deleted student's profile is open, the profile closes. Deletion accepts
one index only; ranges and multiple indices are invalid.

## Validation rules

Validation runs after trimming leading and trailing whitespace.

| Field | Rule |
| --- | --- |
| Name | 1–80 characters after normalization; begins with a Unicode letter; remaining characters are Unicode letters, spaces, apostrophes, hyphens, or full stops. Internal spaces collapse to one. |
| Parent phone | Required. After removing spaces and hyphens: an optional leading `+` followed by 8–15 ASCII digits. |
| Parent email | Optional. 3–254 ASCII characters, no whitespace, exactly one `@`, and a dotted domain. |
| Subject | 1–50 characters using Unicode letters, digits, spaces, `&`, hyphens, slashes, commas, or full stops. Internal spaces collapse to one. |
| Current level | 1–50 characters using Unicode letters, digits, spaces, hyphens, parentheses, or full stops. Internal spaces collapse to one. |
| Session note | 1–500 printable Unicode characters on one line. Internal spaces are preserved. |
| Roster index | Positive base-10 whole number matching a row in the current roster. |

Invalid input, missing values, duplicate prefixes, unknown prefixes, extra
arguments, and out-of-range indices must produce an error without a partial
mutation.

## Persistence contract

The logical JSON shape is:

```json
{
  "students": [
    {
      "id": "generated-internal-id",
      "name": "Student Name",
      "parentPhone": "+65 9123 4567",
      "parentEmail": "parent@example.com",
      "subject": "Mathematics",
      "currentLevel": "Secondary 3",
      "sessionNotes": [
        {
          "recordedAt": "2026-09-18T18:35:00+08:00",
          "text": "Reviewed factorisation."
        }
      ]
    }
  ]
}
```

The exact generated ID format is an implementation detail, but it must be
unique and stable across saves. JSON DTOs must remain inside the storage
boundary.

Malformed stored records must not silently become valid students. The storage
layer must report the problem using the agreed load-error behavior. Migration
from the inherited AddressBook `persons` schema is a separate decision and
must not silently reinterpret old `Person` records as TutorTrack students.

## Mutation and failure behavior

All mutating commands follow this sequence:

1. Parse the request.
1. Validate the complete proposed change.
1. Create a candidate snapshot.
1. Save the candidate snapshot.
1. Publish the candidate snapshot to the active model and UI.

If validation or saving fails, the previous snapshot remains active and the
storage file remains unchanged. The implementation may use a private rollback
mechanism instead, but callers must observe the same behavior.

Shared error categories are:

* Invalid command format
* Missing required value
* Invalid field value
* Duplicate student
* Invalid or missing index
* Index outside the roster
* Storage unavailable or unwritable

Feature branches may choose user-friendly message wording, but they must keep
the category and no-partial-update behavior consistent.

## Branch ownership and integration rules

| Contract area | Primary owner |
| --- | --- |
| Student value types and `StudentRoster` | Add Student member |
| `SessionNote`, `SessionHistory`, and profile/session service | Session Note member |
| Roster display and index context | List member |
| Profile presentation | Student Profile member |
| Command registration and top-level UI wiring | Delete/Integration member |
| End-to-end acceptance tests | Delete/Integration member |

Each branch may add its own command class, parser, UI component, and tests, but
must not redefine shared domain types or JSON fields. Shared files such as the
top-level parser, model facade, storage root, and main FXML should have one
integration owner to reduce merge conflicts.

## Ratification checklist

Before feature branches begin, the team should confirm:

* The API names and method responsibilities above
* The duplicate rule and internal ID decision
* The JSON field names and timestamp format
* The validation limits and normalization rules
* The no-partial-update storage guarantee
* The policy for inherited `edit`, `find`, `clear`, and `help` commands
* The migration behavior for existing AddressBook data

After ratification, this document becomes the reference for branch
implementation, tests, code review, and integration decisions.
