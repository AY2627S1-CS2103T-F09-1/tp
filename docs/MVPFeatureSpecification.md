# TutorTrack MVP Feature Specification

**Status:** Editable specification of the current repository implementation

**Audience:** TutorTrack team members and reviewers. This is a product-behavior specification, not a user guide or API design document.

**Scope note:** This document records the behavior currently implemented in the repository. It leaves the existing [MVP specification PDF](google%20docs%20MVP%20specification.pdf) unchanged and follows the [TutorTrack API Contract](TutorTrackAPIContract.md).

## Product concept

TutorTrack is a local JavaFX desktop application for a private tutor managing a student roster. The tutor types commands in a command box and receives visual feedback in a roster/profile workspace.

The complete tutoring loop is:

1. Add a student with a parent or guardian contact, subject, and current level.
1. View the automatically displayed roster and identify a student by the displayed index.
1. View that student's preparation context and previous session notes.
1. Record a new session note after a lesson.
1. Delete a student and all of that student's notes when the record is no longer needed.

The application targets a small active roster, but the implementation does not enforce a maximum student count.

## MVP boundary and priorities

### Must-have TutorTrack features

* Add student
* Display student roster automatically
* View student profile
* Add session note
* Delete student

Student data and session notes are saved automatically to a local JSON file. These five features form one repeatable tutoring workflow.

### Application-level behavior retained from AB3

The application-level commands 'help' and 'exit' remain available. They are not TutorTrack student-roster features, but they are part of the current product.

The storage model also retains the inherited 'persons' collection for compatibility. The current parser does not provide person-oriented 'add', 'edit', 'find', or 'clear' commands; those removed command words are rejected as unknown commands.

### Not implemented in the current MVP

The current command parser does not implement student editing, searching, undo, scheduling, attendance, messaging, billing, accounts, multi-user access, cloud synchronisation, archiving, or restoring. To correct a student record, the tutor must delete it and add it again; deleting the record also deletes its session-note history.

## Shared command and UI behavior

### Command parsing

* Command words are lowercase and case-sensitive. 'add' is recognised, while 'ADD' is unknown.
* Recognised prefixes are case-sensitive: 'n/', 'p/', 'e/', 'sub/', 'l/', and 'nt/' must be lowercase.
* Leading and trailing whitespace around the complete command is ignored.
* The command box does nothing for an empty string. Every non-blank command is added to the in-memory history before execution, including commands that later fail.
* The history keeps the latest 100 non-blank commands for the current application session. Up recalls older commands, Down moves toward newer commands, and moving past the newest command restores the draft that existed before navigation started.
* A successful command clears the command box. A failed command leaves the attempted text in the box and applies an error style.
* Add prefixes may appear in any order, but each recognised single-valued prefix may appear only once.
* The note command recognises only 'nt/'. Text containing 'n/', 'p/', 'sub/', or 'l/' is ordinary note text. A second 'nt/' is a duplicate note prefix.
* Legacy 'a/' and 't/' prefixes cause the add command to reject the input. Other unknown prefix-looking text is not independently diagnosed by the current tokenizer and may become part of the preceding recognised value.
* Quotation marks have no special parsing meaning.

### Shared index rules

INDEX is a positive, non-zero unsigned decimal integer that fits in a Java int. Zero, negative numbers, signed numbers such as +1, decimals, ranges, multiple indices, text, and values above 2147483647 are invalid. Leading zeroes such as 01 are accepted.

Indices are one-based and refer to the current sorted roster. The UI refreshes the roster after every successful command, so view, note, and delete use the latest displayed order.

### Shared validation

Surrounding whitespace is removed from student value objects. Names, subjects, and current levels also collapse each run of internal whitespace to one space.

| Field | Current accepted value | Error message | Rationale |
| --- | --- | --- | --- |
| Student name | Unicode letters or numbers, with spaces, apostrophes, periods, and hyphens allowed after the first character. There is no implemented length limit. | Student names must contain letters or numbers and may include spaces, apostrophes, periods, and hyphens. | Allows common punctuation and Unicode names. |
| Parent or guardian phone | Optional leading plus, then digits/spaces/hyphens; at least three digits remain after spaces and hyphens are removed. Typed formatting is stored. | Parent or guardian phone numbers must contain at least three digits and may use spaces or hyphens. | Supports common copied phone formats and normalised duplicate checks. |
| Parent or guardian email | Optional; trimmed value has one at sign, non-blank local part/domain, and no whitespace. No dotted-domain or length limit is implemented. | Parent or guardian email addresses must contain one '@' and a non-blank local part and domain. | Stores an optional contact channel without mailbox verification. |
| Subject | Non-blank after normalisation and no carriage-return or line-feed characters. Other characters are accepted. | Subjects must not be blank or contain line breaks. | Supports tutor-defined subjects and punctuation. |
| Current level | Non-blank after normalisation and no carriage-return or line-feed characters. Other characters are accepted. | Current levels must not be blank or contain line breaks. | Supports different school systems and tutor-defined levels. |
| Session note | 1–500 Unicode code points after stripping surrounding whitespace; no control, line-separator, or paragraph-separator characters. Internal whitespace is preserved. | Session notes must be 1 to 500 printable characters on one line. | Keeps notes short and scannable. |

### Shared roster, storage, and failure behavior

* Students sort case-insensitively by normalised name, then normalised parent phone, then stable UUID string. Indices are assigned after sorting.
* Duplicate identity is the pair of lower-case normalised name and parent phone with spaces and hyphens removed.
* New students receive a random UUID-backed studentId. A legacy student without an ID receives a deterministic ID derived from that duplicate identity.
* The file is data/addressbook.json relative to the application's working directory. The footer displays the save path.
* The JSON root contains persons and students. A student contains studentId, name, parentPhone, optional parentEmail, subject, currentLevel, and optional sessionNotes. A note contains recordedAt with a date, time, and UTC offset, and text.
* A missing file starts as an empty address book. A later successful command creates it.
* An unreadable or invalid file starts as an empty address book, shows a recovery alert, and is copied to a uniquely named sibling backup when that backup can be created, before a later saving command can replace it.
* Every successful command is saved automatically. Before execution, the logic layer copies the current address-book state. If saving fails, it restores that copy and reports the save error.
* Existing files are replaced through a temporary file and atomic move. Failed parses and failed commands leave the current model, roster, profile, and existing file unchanged.

## Feature 1 — Add student

**Priority:** Must-have.

**Purpose:** Creates a student record with name, responsible-adult contact, subject, and current level so the tutor can view and annotate the automatically displayed roster.

### Command format

~~~text
add n/NAME p/PARENT_PHONE sub/SUBJECT l/CURRENT_LEVEL [e/PARENT_EMAIL]
~~~

### Example commands

~~~text
add n/Ari Tan p/9123 4567 sub/Mathematics l/Secondary 3
add n/Ari Tan p/+65 9123 4567 e/guardian@example.com sub/Mathematics l/Secondary 3
add sub/English Literature l/JC 1 n/Bea Lim p/9876 5432
~~~

The third example demonstrates free prefix order.

The add usage shown in an invalid-format error is:

~~~text
add: Adds a student to TutorTrack. Parameters: n/NAME p/PARENT_PHONE sub/SUBJECT l/CURRENT_LEVEL [e/PARENT_EMAIL]
Example: add n/Alicia Lim p/+65 9123 4567 e/mrs.lim@example.com sub/Mathematics l/Secondary 3
~~~

### Parameters

| Parameter | Acceptable values | Error when invalid or missing | Rationale |
| --- | --- | --- | --- |
| n/NAME | A value satisfying the shared student-name rule. | Invalid structure produces Invalid command format! followed by add usage; invalid value produces the shared name message. | Identifies the student in the roster and profile. |
| p/PARENT_PHONE | Required value satisfying the shared phone rule. | Invalid structure produces Invalid command format! followed by add usage; invalid value produces the shared phone message. | Every record needs a responsible-adult phone. |
| sub/SUBJECT | Non-blank, single-line value satisfying the shared subject rule. | Invalid structure produces Invalid command format! followed by add usage; invalid value produces the shared subject message. | Gives the tutor learning context. |
| l/CURRENT_LEVEL | Non-blank, single-line value satisfying the shared level rule. | Invalid structure produces Invalid command format! followed by add usage; invalid value produces the shared level message. | Distinguishes preparations such as JC 1 and Secondary 3. |
| e/PARENT_EMAIL | Optional value satisfying the shared email rule. | An invalid supplied email produces the shared email message. | Adds another contact channel when available. |

### Outputs, duplicates, and possible errors

On success, the exact result is New student added: <Name>. The roster refreshes and re-indexes, an open profile remains selected if its student still exists, the data is saved, and the command box is cleared.

A duplicate produces A student with this name and parent phone already exists. and no record is added. A repeated recognised prefix produces Multiple values specified for the following single-valued field(s): <prefix>. A legacy a/ or t/ prefix, missing required prefix, or non-empty preamble produces Invalid command format! followed by the add usage. A save failure reports the save error and restores the pre-command model.

A student with a different phone may share a name, and a student with a different name may share a phone. A valid but semantically incorrect subject cannot be detected. A typo that passes validation can only be corrected by deleting and re-adding the record, which also deletes its session notes.

### Relevant UI mock-up

~~~text
+--------------------------- TutorTrack ----------------------------+
| File   Help                                                       |
| [add n/Ari Tan p/9123 4567 sub/Mathematics l/Secondary 3]        |
| New student added: Ari Tan                                       |
+-----------------------------+----------------------------------+
| 1. Ari Tan                  | No student selected             |
| Subject: Mathematics        | Use view INDEX to open a       |
| Level: Secondary 3          | student profile.               |
| Session notes: 0            |                                  |
+-----------------------------+----------------------------------+
| ./data/addressbook.json                                          |
+------------------------------------------------------------------+
~~~

## Feature 2 — Automatic student roster display

**Priority:** Must-have.

**Purpose:** Shows every stored student on launch and after successful commands, establishing the indices used by view, note, and delete.

### Display behavior and possible errors

The roster is displayed automatically when the application starts and after every successful command. Every student card shows its index, name, subject, current level, and session-note count. The roster refreshes while preserving any open profile by stable student ID.

With no students, the roster pane shows `No students yet` and `Use the add command to create your first student profile.` Invalid stored data invokes the shared startup recovery behavior. The roster remains scrollable and has no enforced student-count cap.

### Relevant UI mock-up

The current populated screen is also shown in [docs/images/Ui.png](images/Ui.png).

~~~text
+--------------------------- TutorTrack ----------------------------+
| command box                                                       |
| result message                                                    |
+-----------------------------+----------------------------------+
| 1. Ari Tan                  | selected student profile        |
| Subject: Mathematics        | phone, email, subject, level    |
| Level: Secondary 3          | session-note history            |
| Session notes: 2            |                                  |
|-----------------------------|----------------------------------|
| 2. Bea Lim                  |                                  |
| Subject: English            |                                  |
| Level: JC 1                  |                                  |
| Session notes: 0            |                                  |
+-----------------------------+----------------------------------+
| save location                                                     |
+------------------------------------------------------------------+
~~~

## Feature 3 — View student profile

**Priority:** Must-have.

**Purpose:** Opens one student's preparation context beside the roster so the tutor can check contact details, subject, level, and previous outcomes before a lesson.

### Command format and examples

~~~text
view INDEX
~~~

~~~text
view 1
~~~

### Parameters

| Parameter | Acceptable values | Error when invalid | Rationale |
| --- | --- | --- | --- |
| INDEX | One positive index in the current sorted roster. | Missing/malformed/extra input produces Invalid command format! followed by view usage. An out-of-range parsed index produces The student index provided is invalid. | Fast, unambiguous selector for a small roster. |

View usage is:

~~~text
view: Displays the profile of the student identified by the index number in the student roster.
Parameters: INDEX (must be a positive integer)
Example: view 1
~~~

### Outputs, duplicates, and possible errors

On success, the exact result is Displaying profile for <Name>. The profile shows name, parent/guardian phone, parent/guardian email, subject, current level, and session notes. Missing email displays Not provided. Notes are newest first by recorded instant, with insertion order retained for equal timestamps. Each timestamp is displayed as d MMM uuuu, h:mm a (UTC+HH:MM), with UTC displayed as UTC+00:00. The selected roster card is highlighted.

With no notes, the profile shows No session notes yet and Use note INDEX nt/NOTE to record the first lesson update. On failure, the result display shows the error and the current roster, selection, and profile remain unchanged.

View creates or merges nothing. If a selected student is deleted, the profile pane and roster highlight are cleared. If storage cannot load, correct the original file and restart.

### Relevant UI mock-up

~~~text
+-----------------------------+--------------------------------------+
| 1. Ari Tan                  | Ari Tan                              |
| Subject: Mathematics        | Parent/guardian phone  9123 4567    |
| Level: Secondary 3          | Parent/guardian email guardian@...  |
| Session notes: 2            | Subject                Mathematics  |
|                             | Current level          Secondary 3   |
|                             | Session notes                        |
|                             | 14 Oct 2026, 6:35 PM (UTC+08:00)    |
|                             | Practised completing the square.    |
|                             | 7 Oct 2026, 6:30 PM (UTC+08:00)     |
|                             | Reviewed factorisation.              |
+-----------------------------+--------------------------------------+
~~~

## Feature 4 — Add session note

**Priority:** Must-have.

**Purpose:** Records a short lesson outcome against one student so the tutor can resume context next time.

### Command format and examples

~~~text
note INDEX nt/NOTE
~~~

~~~text
note 1 nt/Reviewed factorisation; revise negative coefficients next lesson.
note 2 nt/Completed past-year paper 2 (scored 34/40).
note 1 nt/Next: 3 sub/topics, n/a for l/2
~~~

The third example stores the full note because only nt/ is recognised.

### Parameters

| Parameter | Acceptable values | Error when invalid | Rationale |
| --- | --- | --- | --- |
| INDEX | One positive index in the current sorted roster. | Missing/malformed input produces Invalid command format! followed by note usage. An out-of-range parsed index produces The student index provided is invalid. | Reuses the view and delete selector. |
| nt/NOTE | Required one-line printable text of 1–500 Unicode code points after surrounding whitespace is stripped. | Empty or overlong text produces Session notes must be 1 to 500 printable characters on one line. | Keeps the lesson log short and readable. |

Note usage is:

~~~text
note: Adds a session note to the student identified by the index number in the student roster.
Parameters: INDEX (must be a positive integer) nt/NOTE
Example: note 1 nt/Reviewed factorisation; revise negative coefficients next lesson.
~~~

### Outputs, duplicates, and possible errors

On success, the note receives the current system date-time in the default time zone, truncated to whole seconds, and is inserted newest first. The exact result is Added session note for <Name>. The roster count increases, an open profile refreshes, roster order does not change, the data is saved, and the command box is cleared.

Identical note text is allowed repeatedly; each note is a separate lesson event. A second nt/ produces Multiple values specified for the following single-valued field(s): nt/. Control characters, line separators, empty text, overlong text, and invalid indices are rejected without a partial update. If saving fails, the new note is removed from memory and the save error is shown. There is no per-note edit or delete command; add a follow-up note to correct a previous note.

### Relevant UI mock-up

~~~text
| 1. Ari Tan                  | Session notes                        |
| Session notes: 3            | 14 Oct 2026, 6:35 PM (UTC+08:00)    |
|                             | Practised completing the square.    |
|                             | 7 Oct 2026, 6:30 PM (UTC+08:00)     |
|                             | Reviewed factorisation.              |
|                             | 30 Sep 2026, 6:20 PM (UTC+08:00)    |
|                             | Added note text appears here.       |
~~~

## Feature 5 — Delete student

**Priority:** Must-have.

**Purpose:** Removes one student and that student's complete session-note history. It is also the current MVP's way to correct a bad record.

### Command format and examples

~~~text
delete INDEX
~~~

~~~text
view 2
delete 2
~~~

The recommended workflow is to view a student first, then delete the confirmed index. Deletion accepts one index only; ranges and multiple indices are invalid.

### Parameters

| Parameter | Acceptable values | Error when invalid | Rationale |
| --- | --- | --- | --- |
| INDEX | Exactly one positive index in the current sorted roster. | Missing, malformed, multiple, range-like, or out-of-range input produces the delete parse or index error. | Prevents accidental batch deletion and matches view and note. |

Delete usage is:

~~~text
delete: Deletes the student identified by the index number used in the student roster.
Parameters: INDEX (must be a positive integer)
Example: delete 1
~~~

### Outputs, duplicates, and possible errors

On success, the student and all of its session notes are removed. The exact result is Deleted student: <Name>. Session notes removed: <count>. The roster refreshes with new consecutive indices, the deleted profile closes and its highlight is cleared, and another open profile remains selected by stable ID. The data is saved and the command box is cleared.

Delete creates or merges nothing. If two non-duplicate students have the same name, deleting one leaves the other and its notes intact. Out-of-range indices produce The student index provided is invalid. Inputs such as delete 1 2, delete 1-2, or delete 1 n/Ari produce invalid command format followed by delete usage. There is no confirmation prompt or undo command. A save failure restores the student and notes.

### Relevant UI mock-up

~~~text
+-----------------------------+--------------------------------------+
| 1. Ari Tan                  | No student selected                  |
| Subject: Mathematics        | Use view INDEX to open a student    |
| Level: Secondary 3          | profile.                             |
| Session notes: 2            |                                      |
+-----------------------------+--------------------------------------+
| Result: Deleted student: Bea Lim. Session notes removed: 0.       |
+-------------------------------------------------------------------+
~~~

## Application-level feature — Help

The help command is retained from AB3. It accepts extra text, opens or focuses the help window, and returns the exact result Opened help window. The Help menu item and F1 accelerator do the same.

~~~text
help
help anything
~~~

## Application-level feature — Exit

The exit command accepts extra text, saves current GUI settings, hides the main and help windows, and returns the exact result Exiting Address Book as requested ...

~~~text
exit
exit now
~~~

## Cross-feature acceptance flow

~~~text
add n/Ari Tan p/9123 4567 sub/Mathematics l/Secondary 3
add n/Bea Lim p/9876 5432 e/guardian@example.com sub/English l/JC 1
view 1
note 1 nt/Reviewed factorisation; revisit negative coefficients next lesson.
view 1
delete 2
~~~

Expected outcomes:

* The roster contains two sorted students with correct indices and note counts.
* view 1 opens Ari Tan's profile.
* note 1 adds a timestamped note and changes Ari Tan's count from 0 to 1.
* view 1 shows the new note.
* delete 2 removes Bea Lim and reports the number of removed notes.
* The final automatically refreshed roster shows only Ari Tan with index 1.
* Reloading preserves the remaining student, studentId, and session-note history.

## Implementation references

The behavior described here is implemented and tested in [AddressBookParser](../src/main/java/seedu/address/logic/parser/AddressBookParser.java), [Student](../src/main/java/seedu/address/model/student/Student.java), [StudentRoster](../src/main/java/seedu/address/model/student/StudentRoster.java), [SessionNote](../src/main/java/seedu/address/model/session/SessionNote.java), [SessionHistory](../src/main/java/seedu/address/model/session/SessionHistory.java), [JsonAdaptedStudent](../src/main/java/seedu/address/storage/JsonAdaptedStudent.java), [JsonAddressBookStorage](../src/main/java/seedu/address/storage/JsonAddressBookStorage.java), [MainApp](../src/main/java/seedu/address/MainApp.java), [MainWindow](../src/main/java/seedu/address/ui/MainWindow.java), [StudentRosterPanel](../src/main/java/seedu/address/ui/StudentRosterPanel.java), and [StudentProfilePanel](../src/main/java/seedu/address/ui/StudentProfilePanel.java).

