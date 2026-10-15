---
layout: page
title: User Guide
---

TutorTrack is a **desktop application for private tutors managing a student roster, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). The light interface uses neutral surfaces and teal accents so roster, profile, command, and feedback areas remain easy to scan while you type quickly.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from the [TutorTrack releases page](https://github.com/AY2627S1-CS2103T-F09-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for TutorTrack.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. On a first launch, the student roster is empty.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all students in the roster.

   * `add n/John Doe p/98765432 sub/Mathematics l/Secondary 3` : Adds a student named `John Doe` to TutorTrack.

   * `delete 3` : Deletes the 3rd student shown in the current roster and all of that student's session notes.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `[e/PARENT_EMAIL]` means the parent email can be included or omitted when adding a student.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help` and `exit`, are ignored.<br>
  For example, `help 123` is interpreted as `help`. The `list` command rejects extra input because it only accepts the exact format shown below.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Recalling earlier commands

TutorTrack keeps the latest 100 non-blank commands from the current app session, including unsuccessful commands and duplicates. This history is kept in memory only and is cleared when the app closes.

* Press `Up` in the command box to recall older commands. Navigation stops at the oldest retained command.
* Press `Down` to move toward newer commands. Moving past the newest command restores any draft that was in the command box before history navigation started.
* Edit a recalled command and press `Enter` to run it and add the edited command as a new history entry.

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a student: `add`

Adds a student with the responsible parent or guardian's contact details, subject, and current level.

Format: `add n/NAME p/PARENT_PHONE sub/SUBJECT l/CURRENT_LEVEL [e/PARENT_EMAIL]`

The name, parent phone, subject, and current level are required. Parent email is optional. TutorTrack preserves each student's identity when the data file is saved and reloaded. A duplicate is a student whose normalized name and parent phone both match an existing student; spaces and hyphens in a parent phone do not make a distinct student.

Examples:
* `add n/Alicia Lim p/+65 9123 4567 e/mrs.lim@example.com sub/Mathematics l/Secondary 3`
* `add sub/English Literature l/JC 1 n/Dev Patel p/91234567`

### Listing the student roster: `list`

Lists all students in normalized alphabetical order and reports how many students are in the roster. When the roster is empty, TutorTrack reports that there are no students and suggests using the `add` command.

Each student card shows the roster index, student name, subject, current academic level, and number of session notes. Long names, subjects, and levels wrap within their cards, and the roster can be scrolled when it contains more students than fit in the window.

Format: `list`

### Adding a session note: `note`

Adds a short note about a lesson to a student, such as what was covered, where the student struggled, or what to do next time. TutorTrack records the current date and time with the note and adds one to the student's session-note count in the roster.

If that student's profile is open, the new note appears there immediately after the command succeeds.

Format: `note INDEX nt/NOTE`

* `INDEX` is the student's number in the roster shown by `list`. It must be a positive whole number, such as 1, 2, or 3.
* `NOTE` must be 1 to 500 characters on one line. Spaces at the start and end are removed; spaces inside the note are kept as typed.
* Only `nt/` is treated as a prefix, so text such as `sub/` or `n/` inside a note is saved as part of the note.
* You can add the same note text more than once, because each note records a separate lesson.

Examples:

* `note 1 nt/Reviewed factorisation; revise negative coefficients next lesson.`
* `note 3 nt/Completed past-year paper 2 (scored 34/40).`

If the index is missing, not a positive whole number, or larger than the number of students in the roster, or if the note is empty or too long, TutorTrack shows an error and does not add the note.

### Viewing a student profile: `view`

Displays one student's profile beside the roster. The profile shows the student's name, parent or guardian phone and email, subject, current academic level, and session notes. If no email was recorded, TutorTrack displays `Not provided`.

Session notes are shown newest first with the saved date, time, and UTC offset. Long profile fields and notes wrap within the panel, and the history can be scrolled. A student without notes displays `No session notes yet` and the command for recording the first note.

The roster card belonging to the open profile remains highlighted even if the roster is refreshed or its indices change. Hover and keyboard focus are shown separately and do not open another profile. The open profile refreshes after each successful command and closes, together with its highlight, if that student is deleted; a failed command leaves both unchanged.

Format: `view INDEX`

* `INDEX` is the student's number in the roster shown by `list`.
* `INDEX` must be a positive whole number and must refer to a student currently in the roster.

Example: `view 2`

If the index is missing, invalid, or larger than the number of students in the roster, TutorTrack shows an error and keeps the current display unchanged.

### Deleting a student: `delete`

Deletes the specified student and all session notes belonging to that student.

Format: `delete INDEX`

* `INDEX` is the student's number in the sorted roster shown by `list`.
* `INDEX` must be a positive whole number and must refer to a student currently in the roster.
* TutorTrack reports the deleted student's name and the number of session notes removed.
* The roster is redrawn with new consecutive indices. If the deleted student's profile was open, it closes and its roster highlight is cleared.

Example:

* `list` followed by `delete 2` deletes the 2nd student in the roster.

If the index is missing, invalid, contains more than one value, or is larger than the roster, TutorTrack shows an error and does not change the roster, session notes, open profile, or saved data. If TutorTrack cannot save the deletion, it restores the student and session notes.

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

TutorTrack automatically saves data after every command. You do not need to save manually.

If TutorTrack cannot save the data, for example because the data file is read-only, it shows an error and cancels the command, so the roster, session notes, and previously saved data file stay as they were before the command.

### Editing the data file

TutorTrack data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Student records include an internal `studentId` that should be preserved when manually editing the data file and must be unique for every student. Each student's session notes are stored in that student's `sessionNotes` list, and each note has a `recordedAt` date and time with a time-zone offset, such as `2026-09-18T18:35:00+08:00`, and its `text`. A `recordedAt` without the offset (for example `2026-09-18T18:35:00`) makes the data file invalid. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, TutorTrack starts with an empty student roster at the next run and shows an error. Before a command can replace the invalid file, TutorTrack keeps a uniquely named copy such as `[JAR file location]/data/addressbook.json.backup-<unique>.bak`. Correct the original file and restart TutorTrack before running a command that saves data. Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause TutorTrack to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous TutorTrack home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PARENT_PHONE sub/SUBJECT l/CURRENT_LEVEL [e/PARENT_EMAIL]`<br> e.g., `add n/James Ho p/91234567 sub/Mathematics l/Secondary 3`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**List** | `list`
**Note** | `note INDEX nt/NOTE`<br> e.g., `note 1 nt/Reviewed factorisation.`
**View** | `view INDEX`<br> e.g., `view 2`
**Help** | `help`
**Exit** | `exit`
