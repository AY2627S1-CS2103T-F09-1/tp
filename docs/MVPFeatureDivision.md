# TutorTrack MVP Feature Division

## Quick assessment

The default app already provides general AddressBook mechanics, but only three
MVP capabilities exist as rough analogues. TutorTrack-specific behavior is
mostly new.

| MVP feature | Base-app status | Reusable parts | New work required |
| --- | --- | --- | --- |
| Add student | Partial | [`AddCommand`](../src/main/java/seedu/address/logic/commands/AddCommand.java), parser, model insertion, JSON saving | Replace `Person` fields with student name, parent contact, subject, and level; implement MVP validation and duplicate rules |
| List student roster | Partial | [`ListCommand`](../src/main/java/seedu/address/logic/commands/ListCommand.java), filtered list, numbered UI cards | Display student-specific fields, note count, sorted roster, empty state, and stable selection indices |
| View student profile | New | Command/parser framework and JavaFX window | Add the `view` command, profile model/API, profile panel, contact/context display, and chronological notes |
| Add session note | New | Command execution and JSON persistence infrastructure | Add `SessionNote`, session history, timestamps, the `note` command, validation, storage, and profile refresh |
| Delete student | Partial | [`DeleteCommand`](../src/main/java/seedu/address/logic/commands/DeleteCommand.java), index selection, removal flow | Cascade-delete session notes, show the note count, update profile/indices, and handle save failures atomically |

The main base-app layers are:

* Domain: [`AddressBook`](../src/main/java/seedu/address/model/AddressBook.java) and [`Person`](../src/main/java/seedu/address/model/person/Person.java)
* Command routing: [`AddressBookParser`](../src/main/java/seedu/address/logic/parser/AddressBookParser.java)
* Persistence: [`JsonAdaptedPerson`](../src/main/java/seedu/address/storage/JsonAdaptedPerson.java) and `JsonSerializableAddressBook`
* Roster UI: [`PersonListPanel`](../src/main/java/seedu/address/ui/PersonListPanel.java) and `PersonCard`
* Application shell: [`MainWindow`](../src/main/java/seedu/address/ui/MainWindow.java)

One important gap is transactional saving: the logic layer saves after
mutating the model, but does not currently restore the model if saving fails.
The MVP requires failed commands to leave both the model and the file
unchanged.

## Recommended five-person split

### 1. Student foundation and Add Student

Branch: `feature/student-add`

Own:

* `Student` and related value types
* Parent or guardian contact fields
* Subject and current-level validation
* Duplicate rule: same normalized name plus same parent phone
* The `add` command and parser
* Student JSON representation
* Domain and command tests

Merge this branch first because all other features depend on the student model
and storage schema.

### 2. List Student Roster

Branch: `feature/student-list`

Own:

* `list` command behavior
* Alphabetical roster ordering
* Stable student indices
* Student roster card UI
* Empty-roster state
* Note-count display
* List and UI tests

This branch can begin once the student model contract from Member 1 is stable.

### 3. Session Notes

Branch: `feature/session-note`

Own:

* `SessionNote` and `SessionHistory`
* Timestamp and newest-first ordering
* `note INDEX nt/NOTE`
* Note validation and length limits
* JSON persistence for notes
* Note command and storage tests

This is the foundational branch for both note entry and profile display.

### 4. Student Profile

Branch: `feature/student-profile`

Own:

* `view INDEX`
* `StudentProfile` read API
* Profile panel UI
* Parent contact, subject, level, and notes display
* No-notes state
* Profile command and UI tests

This branch depends on the student foundation and session-note model. It should
consume those APIs rather than modify their internals.

### 5. Delete Student and Integration

Branch: `feature/student-delete`

Own:

* `delete INDEX`
* Cascading removal of the student's session notes
* Save-failure rollback behavior
* Updated roster indices
* Closing or refreshing the profile after deletion
* End-to-end acceptance tests across add → list → view → note → delete

This branch can also coordinate final integration because deletion touches both
the student and session-history boundaries.

## Suggested merge order

```text
student-add
    ├── student-list
    └── session-note
            ├── student-profile
            └── student-delete
```

To reduce merge conflicts:

* Merge the student model and JSON schema before parallel feature work.
* Agree on public APIs for `Student`, `SessionHistory`, and roster selection
  before coding.
* Let each member add their own command class and parser tests, but have one
  person resolve the final command registration in `AddressBookParser`.
* Avoid having multiple branches directly edit `Person`, `AddressBook`, or JSON
  adapters independently.
* Integrate with vertical tests after every branch rather than merging all UI
  work at the end.

## Base-app features outside the MVP

The default app also has `edit`, `find`, `clear`, and `help`. The MVP
specification defers or excludes these. `exit` should remain as application
lifecycle behavior.

The team should explicitly decide whether deferred commands are temporarily
retained for compatibility or removed from command routing before the MVP
release. For a clean MVP acceptance test, only `add`, `list`, `view`, `note`,
and `delete` should be treated as TutorTrack domain commands.
