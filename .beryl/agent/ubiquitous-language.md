# Ubiquitous Language

| Business Term | Technical Symbol | Definition | Constraints | Avoid |
| --- | --- | --- | --- | --- |
| TutorTrack | `TutorTrack` | The product being built from this brownfield codebase. | Future user-facing language uses this name. | AddressBook as the product name in new TutorTrack work |
| Tutor | `Tutor` | A private one-to-one tutor who manages the roster. | The first release serves one local tutor; it is not multi-user. | Administrator, account owner |
| Student | `Student` | A learner currently managed by the tutor. | Has a name, parent or guardian contact details, subject, and current level. | Person, contact, client |
| Student Roster | `StudentRoster` | The tutor's complete collection of active student records. | Presents clear selectable student entries. | Address book, contact list |
| Student Profile | `StudentProfile` | The dedicated view of one student's essential context. | Shows contact details, subject, level, and session history together. | Student page, details blob |
| Parent or Guardian Contact | `ParentGuardianContact` | Contact details for the adult responsible for a student. | Is associated with the relevant student record. | Student contact when it means the parent or guardian |
| Subject | `Subject` | The topic taught to a student. | Stored with the student record. | Course when no course model exists |
| Current Level | `CurrentLevel` | The student's current stage in the subject. | Stored with the student record and shown in the profile. | Grade unless it specifically means a school grade |
| Session Note | `SessionNote` | A short record of what mattered in a lesson. | Belongs to one student and is ordered chronologically in that student's history. | Generic note, task, comment |
| Recent-session History | `SessionHistory` | The chronological collection of a student's session notes. | The most recent note must be easy to identify. | Activity feed if it includes unrelated events |
| Persistence | `Storage` | The local mechanism that saves and reloads TutorTrack data. | Must preserve valid data and isolate file details from domain logic. | UI state |
| ADR | `ADR` | Architecture Decision Record for a durable design decision. | Link it from `design-tree.md`. | Random note |
