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

1. Copy the file to the folder you want to use as the _home folder_ for TutorTrack. This folder will contain the data file that stores your student roster and session notes. Rename the downloaded file to `tutortrack.jar` if it has a different name.

   Choose a folder that you can write to, such as one in your user folder. TutorTrack saves its data file, preferences and log files in this folder, so it cannot save anything if the folder is write-protected (see [Known issues](#known-issues)).

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar tutortrack.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. On a first launch, the student roster is empty.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `add n/John p/98765432 sub/Math l/Sec 3` : Adds a student named `John` to TutorTrack.

   * `edit 1 l/Sec 4` : Updates the current level of the first student while keeping the rest of that student's record and session notes.

   * `note 1 nt/Review trigo next session.` : Adds a session note to the first student shown in the current roster.

   * `edit note 1 1 nt/Corrected lesson summary.` : Replaces the first student's first session note without changing its timestamp.

   * `note 1 nt/Practice algebra next session.` : Adds a second session note, numbered `[2]`, to the first student.

   * `delete note 1 2` : Shows a confirmation prompt for deleting session note `[2]` from the first student. Enter `delete note 1 2 /confirm` next to complete the deletion.

   * `delete 1` : Shows a confirmation prompt for deleting the first student and the remaining session note. Enter `delete 1 /confirm` next to complete the deletion.

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
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.

</div>

### Recalling earlier commands

TutorTrack keeps the latest 100 non-blank commands from the current app session, including unsuccessful commands and duplicates. This history is kept in memory only and is cleared when the app closes.

* Press `Up` in the command box to recall older commands. Navigation stops at the oldest retained command.
* Press `Down` to move toward newer commands. Moving past the newest command restores any draft that was in the command box before history navigation started.
* Edit a recalled command and press `Enter` to run it and add the edited command as a new history entry.

### Fixing command errors

When a command cannot run, TutorTrack names the specific problem and does not change the roster, session notes, open profile, or saved data. Recall the command with `Up`, correct it, and press `Enter` again.

* **Missing or extra parts:** if a required part such as an index, `nt/`, or `n/NAME` is missing, an unknown or repeated parameter is entered, text appears before the first parameter, or more values are entered than the command takes, the error ends with a `Format:` line showing the correct command and an `Example:` line you can adapt. For example, `view 1 2` shows `view takes only one INDEX.` followed by `Format: view INDEX` and `Example: view 1`.
* **Invalid value:** a value that breaks a rule, such as a phone number that is too short, shows only that rule. An index such as `0`, `-1`, or `abc` shows `INDEX must be a positive whole number, e.g. 1.` A number too large to be any index, such as `99999999999`, is reported as too large. A session note that is too long reports its length, for example `Session note has 501 characters, more than the 500-character limit. Shorten it to 500 characters or fewer.` An empty note shows `Session note is empty. Enter the note text after nt/.`, and a note containing a tab or line break shows `Session note contains a tab, line break or other non-printable character. Enter the note on one line without them.`
* **Index outside the roster:** the error gives the valid range, for example `Student index [3] is outside the roster. Enter a student index from 1 to 2.` If the roster is empty, TutorTrack asks you to add a student first.
* **Note number outside the history:** `edit note` and `delete note` give the valid note-number range for that student.
* **Unknown command:** Command words must be typed in lowercase. An uppercase command such as `ADD` or a misspelled command such as `veiw 1` is rejected and named in the error together with the list of available commands.
* **Mistyped keyword:** the word `/confirm` must be typed in lowercase, so `delete 1 /CONFIRM` and `delete note 1 1 /CONFIRM` are rejected with `/confirm must be typed in lowercase.` A mistyped `note`, such as `delete notes 1 1` or `edit nte 1 1 nt/Corrected.`, is named in the error together with the command you probably meant, such as `delete note`. A mistyped parameter prefix, such as `em/` in `edit 1 em/parent@example.com`, is reported as an unknown parameter together with the list of supported parameters.

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a student: `add`

Adds a student with the responsible parent or guardian's contact details, subject, and current level.

Format: `add n/NAME p/PARENT_PHONE sub/SUBJECT l/CURRENT_LEVEL [e/PARENT_EMAIL]`

* **Required details:** Name, parent or guardian phone number, subject, and current level must be provided.
* **Student name:** Names must begin with a letter or number and may contain only letters, numbers, spaces, apostrophes, periods, hyphens, and slashes. For example, `Raj s/o Kumar` is valid.
* **Subject and current level:** Each must contain at least one letter or number. Other ordinary punctuation is allowed, so values such as `C++` and `P6+` are valid.
* **Parent or guardian phone:** Use an 8-digit Singapore number beginning with `6`, `8`, or `9`, such as `9123 4567`, or an international number beginning with `+` and containing 8 to 15 digits, such as `+65 9123 4567`. Spaces and hyphens are allowed. TutorTrack validates the format only; it cannot confirm ownership or reachability.
* **Optional detail:** Parent or guardian email may be included with `e/`. It must include `@` and a domain with a dot, such as `guardian@example.com`.
* **Accepted parameters:** Only `n/`, `p/`, `sub/`, `l/`, and optional `e/` are supported, in any order.
* **Subject and level text:** Only lowercase command parameter prefixes are recognized. Other prefix-like text, such as `P/81234567` after `sub/Math`, stays in the subject value; it does not change the phone. Obsolete AddressBook `a/` and `t/` parameters are rejected with an error.
* **Duplicate detection:** A student is considered a duplicate when the normalized name and parent phone match an existing student. Spaces and hyphens in the phone number do not create a distinct student.
* **After a successful addition:** TutorTrack preserves the student's identity when the data is saved and reloaded, and reports the student's name, subject, and current level.
* **Invalid input:** Missing values, invalid values, duplicate students, text before the first parameter, and obsolete AddressBook parameters leave the roster unchanged and explain the problem. See [Fixing command errors](#fixing-command-errors) for when the command format is shown.

Examples:

* `add n/Alicia Lim p/+65 9123 4567 e/mrs.lim@example.com sub/Mathematics l/Secondary 3`
* `add sub/English Literature l/JC 1 n/Dev Patel p/91234567`

### Editing a student: `edit`

Edits one or more details for an existing student without deleting the student or their session notes.

Format: `edit INDEX [n/NAME] [p/PARENT_PHONE] [e/PARENT_EMAIL] [sub/SUBJECT] [l/CURRENT_LEVEL]`

* `INDEX` is the student's number in the currently displayed roster. It must be a positive whole number that refers to an existing student.
* Supply at least one field. Details you omit keep their existing values.
* Use `e/` without a value to remove the parent or guardian email. Other supplied fields cannot be empty.
* The supported parameters are `n/`, `p/`, `e/`, `sub/`, and `l/`. Each parameter can appear at most once and can be supplied in any order. Only these lowercase prefixes are recognized. Other prefix-like text after a field is kept in that field's value; for example, `edit 1 sub/Math P/81234567` changes the subject to `Math P/81234567` and leaves the phone unchanged. Obsolete AddressBook `a/` and `t/` parameters are rejected.
* Changing the name or phone can move the student to a different roster index. TutorTrack still edits the student selected when the command starts, preserves that student's internal identity and session notes, and keeps an open profile selected on that student.

Examples:

* `edit 2 p/9123 4567 l/Secondary 4`
* `edit 1 e/`
* `edit 3 n/Devika Patel sub/English Literature`

A successful edit reports the student's updated name, subject, and current level, refreshes the roster and any open profile, and saves the change. TutorTrack rejects an edit that would give the student the same normalized name and parent phone as another student. Invalid indices, missing edit fields, repeated or obsolete AddressBook parameters, and invalid values leave the student, session notes, open profile, and saved data unchanged. See [Fixing command errors](#fixing-command-errors) for the messages. If saving fails, TutorTrack restores the complete original record.

### Viewing the student roster

TutorTrack displays all students in normalized alphabetical order when the application launches and after each successful command. When the roster is empty, TutorTrack shows that there are no students and suggests using the `add` command.

Each student card shows the roster index, student name, subject, current academic level, and number of session notes. Long names, subjects, and levels wrap within their cards, and the roster can be scrolled when it contains more students than fit in the window. Resizing the roster pane recalculates wrapped cards so every student remains reachable.

### Adding a session note: `note`

Adds a short note about a lesson to a student, such as what was covered, where the student struggled, or what to do next time. TutorTrack records the current date and time with the note and adds one to the student's session-note count in the roster.

If that student's profile is open, the new note appears there immediately after the command succeeds.

Format: `note INDEX nt/NOTE`

* `INDEX` is the student's number in the currently displayed roster. It must be a positive whole number, such as 1, 2, or 3.
* `NOTE` must be 1 to 500 characters on one line. Spaces at the start and end are removed; spaces inside the note are kept as typed.
* Only `nt/` is treated as a prefix, so text such as `sub/` or `n/` inside a note is saved as part of the note.
* `nt/` may appear only once. A second `nt/` after a space, as in `note 1 nt/Remember nt/ notation`, is reported as a repeated parameter and the note is not added. To mention `nt/` in a note, write it without a space before it, such as `(nt/)` or `"nt/"`.
* You can add the same note text more than once, because each note records a separate lesson.
* The date and time are recorded to the second, together with your computer's time zone, such as `18 Sep 2026, 6:35 PM (UTC+08:00)`.
* The new note gets the next note number. Note numbers count from the oldest note, `[1]`, so they follow the order in which you added the notes, even for notes added within the same second. See [Viewing a student profile](#viewing-a-student-profile-view) for where the numbers appear.

On success, TutorTrack shows `Added session note for STUDENT_NAME.`

Examples:

* `note 1 nt/Reviewed factorisation; revise negative coefficients next lesson.`
* `note 3 nt/Completed past-year paper 2 (scored 34/40).`

If the index is missing, not a positive whole number, or larger than the number of students in the roster, or if the note is empty, too long, or not on one line, TutorTrack shows an error and does not add the note. See [Fixing command errors](#fixing-command-errors) for the messages.

### Editing a session note: `edit note`

Replaces the text of one existing session note. The note's recorded date and time, chronological position, number, and the student's note count remain unchanged. If that student's profile is open, it refreshes after the command succeeds.

Format: `edit note STUDENT_INDEX NOTE_NUMBER nt/NEW_NOTE`

* `STUDENT_INDEX` is the student's number in the currently displayed roster.
* `NOTE_NUMBER` is the number shown beside the note in the student's profile. The oldest note is `[1]`.
* `NEW_NOTE` must be 1 to 500 characters on one line. Spaces at the start and end are removed; spaces inside the note are kept as typed.
* Only `nt/` is treated as a prefix, so text such as `sub/` or `n/` inside the replacement is saved as part of the note.
* As with `note`, `nt/` may appear only once. To mention `nt/` in the replacement, write it without a space before it, such as `(nt/)` or `"nt/"`.

Example: `edit note 1 2 nt/Corrected lesson summary.`

If either selector is missing, invalid, outside the current range, or too large for a supported index, or if the replacement is empty, too long, or not on one line, TutorTrack shows an error and leaves the note unchanged. An out-of-range student index includes the valid roster range. If NOTE_NUMBER is missing, the error explains that it is the number shown beside the note in the profile. If you type a likely misspelling of `note`, such as `edit notes` or `edit nte`, TutorTrack points you to the correct `edit note` format; a missing or malformed student index such as `edit n/Bob` reports the student-index rule instead. Editing records the time of the latest edit only; entering the same text still updates that time, while the original recorded time and note number remain unchanged. A failed save also restores the original note.

### Viewing a student profile: `view`

Displays one student's profile beside the roster: name, parent or guardian contact, subject, current level, and session notes. TutorTrack displays `Not provided` when no email was recorded.

Session notes are shown newest first with the saved date, time, and UTC offset. An edited note also shows the time of its latest edit; only the latest edit time is kept, and notes edited before this feature show no edit time. Each note also has a number in square brackets. The oldest note is `[1]`, and each newer note has the next number, so the newest note appears at the top with the largest number. These numbers are selectors for the current display; they are not saved with the notes and are reassigned consecutively after a deletion. Long profile fields and notes wrap within the panel, and the history can be scrolled. You can select profile values, session-note headers, and note text and copy them with `Ctrl+C` on Windows/Linux or `Cmd+C` on macOS; the displayed text remains read-only. A student without notes displays `No session notes yet` and the command for recording the first note.

Long text wraps and the note history can be scrolled. Press `PageDown` or `PageUp` in the command box to scroll the profile and notes by one page (in a short window, the profile scrolls first so the notes come into view) without losing what you have typed. Profile text is read-only, but you can select and copy it with `Ctrl+C` on Windows/Linux or `Cmd+C` on macOS.

The roster card belonging to the open profile remains highlighted even if the roster is refreshed or its indices change. Hovering over or clicking a card does not add a highlight or open another profile. The open profile refreshes after each successful command; a successful `edit note` opens the edited student's profile even when another profile was previously open. The profile closes, together with its highlight, if that student is deleted; a failed command leaves both unchanged.

Format: `view INDEX`

* `INDEX` must be the positive whole number of a student in the displayed roster.

Example: `view 2`

If the index is missing, not a positive whole number, or outside the roster, TutorTrack shows an error and keeps the current display unchanged. See [Fixing command errors](#fixing-command-errors) for the messages.

### Deleting a student: `delete`

Asks for confirmation before deleting the student and all their session notes.

Preview format: `delete INDEX`

Confirmation format: `delete INDEX /confirm`

* `INDEX` is the student's number in the currently displayed sorted roster.
* `INDEX` must be a positive whole number and must refer to a student currently in the roster.
* A valid preview opens the target student's profile, highlights that student's roster card, shows how many session notes would be removed, and displays the exact confirmation command. It does not delete or save anything.
* To delete the student, enter the exact `delete INDEX /confirm` command shown in the preview as your next correctly formatted command. `/confirm` is case-sensitive: it must be in lowercase, and it must appear once, after the index.
* If TutorTrack rejects a command because its format is wrong, correct the typo and try again; the preview stays active. A different correctly formatted command cancels the preview, even if that command then fails. Starting another deletion preview replaces it.
* A confirmation for the wrong student, or without an active preview, does not delete anything. Start a new preview to try again.
* After a matching confirmation, TutorTrack reports `Successfully deleted STUDENT_NAME.`, redraws the roster with consecutive indices, closes the deleted student's profile, and clears its roster highlight.

Example:

1. Enter `delete 2`.
1. Check that the target student's profile is open and their roster card is highlighted, then verify the name and session-note count in the prompt.
1. Enter the displayed `delete 2 /confirm` as the next correctly formatted command to delete that student, or enter another correctly formatted command to cancel.

If the index is missing, invalid, contains more than one value, or is larger than the roster, TutorTrack shows an error and does not change the roster, session notes, open profile, or saved data. See [Fixing command errors](#fixing-command-errors) for the messages. If TutorTrack cannot save a confirmed deletion, it restores the student and session notes and clears the pending deletion.

### Deleting one session note: `delete note`

Asks for confirmation before deleting one session note. The student and other notes stay in place.

Preview format: `delete note STUDENT_INDEX NOTE_NUMBER`

Confirmation format: `delete note STUDENT_INDEX NOTE_NUMBER /confirm`

* `STUDENT_INDEX` is the student's positive whole-number index in the displayed roster.
* `NOTE_NUMBER` is the positive number in square brackets beside the note in that student's profile.
* The preview opens the selected student's profile, scrolls the targeted note into view, and highlights its card. Check the note text and number before confirming. The prompt displays the exact confirmation command. No data is deleted or saved during the preview.
* To delete the note, enter the exact confirmation command shown in the preview as your next correctly formatted command.
* If TutorTrack rejects a command because its format is wrong, correct the typo and try again; the preview and note highlight stay active. A different correctly formatted command cancels the preview and clears the highlight while the profile stays open, even if that command then fails. Starting another deletion preview replaces it and highlights the new target.
* A confirmation for the wrong student or note, or without an active preview, does not delete anything. Start a new preview to try again.
* After a matching confirmation, TutorTrack removes only that note, reports `Successfully deleted session note [NOTE_NUMBER] for STUDENT_NAME.`, refreshes an open profile, and numbers the remaining notes consecutively.

Example:

1. Enter `view 1` and check the displayed note numbers.
1. Enter `delete note 1 3`.
1. Check the open profile and highlighted note, then check the student name and note number in the prompt.
1. Enter the displayed `delete note 1 3 /confirm` as the next correctly formatted command to delete that note, or enter another correctly formatted command to cancel.

If either number is missing or not a positive whole number, extra arguments are supplied, the student does not exist, the student has no notes, or the note number is outside the displayed range, TutorTrack shows a specific error and leaves stored data unchanged. An input that TutorTrack cannot parse keeps any pending preview and highlight active. An input that parses but fails validation cancels the preview and clears the highlight while the profile stays open. See [Fixing command errors](#fixing-command-errors) for the messages. If saving fails, TutorTrack restores the complete original history and clears the pending deletion.

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

TutorTrack automatically saves data-changing commands. You do not need to save manually. Student and session-note deletion previews do not save because they do not change any data.

TutorTrack saves your students and session notes in `data/tutortrack.json` inside your TutorTrack home folder, the folder containing `tutortrack.jar`. It creates this file the first time you run a command that changes data, such as `add`. To back up your roster, copy this file while TutorTrack is closed.

If TutorTrack cannot save the data, for example because the data file is read-only, it shows an error and cancels the command, so the roster, session notes, and previously saved data file stay as they were before the command. Commands that do not change data, such as `view` and `help`, still work while saving fails.

### Editing the data file

TutorTrack stores its data as JSON in `data/tutortrack.json`. Advanced users can update the data by editing this file with a text editor while TutorTrack is closed.

Each student's session notes are in that student's `sessionNotes` list, for example:

```json
"sessionNotes": [ {
  "recordedAt": "2026-09-18T18:35:00+08:00",
  "text": "Reviewed factorisation."
} ]
```

* `recordedAt` is the date and time the note was recorded, followed by the time zone as an offset from UTC, in the form `YYYY-MM-DDThh:mm:ss+hh:mm`. Use `Z` for UTC.
* `text` follows the same rules as `NOTE` in [Adding a session note](#adding-a-session-note-note): 1 to 500 characters on one line, without tabs or line breaks such as `\t` or `\n`. Spaces at the start and end are removed.
* A student without session notes may leave out `sessionNotes` or use an empty list, `[]`.
* Notes may be listed in any order. TutorTrack numbers them by `recordedAt`, starting from the oldest. For notes with the same `recordedAt`, the one listed first in the file is treated as the newer note.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If an edit makes the data file invalid, TutorTrack starts with an empty roster and shows a message saying that it could not load your data file. It also tries to keep a copy of the file in the `data` folder, with a name such as `tutortrack.json.backup-123456.bak`, and the message gives the copy's location if it succeeds. Correct the original file and restart TutorTrack **before** running any command that changes data, because such a command replaces the original file with the empty roster. Back up the data file before editing it.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Copy `tutortrack.jar` to a folder on the other computer. Then, while TutorTrack is closed, copy the `data` folder from your previous TutorTrack home folder into that folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
1. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.
1. **If the folder containing `tutortrack.jar` is write-protected**, TutorTrack still starts, but it cannot create log files or save your window preferences. Commands that change data, such as `add`, fail with an error saying TutorTrack could not save the data file, and the roster stays unchanged. The remedy is to move `tutortrack.jar` to a folder that you can write to.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PARENT_PHONE sub/SUBJECT l/CURRENT_LEVEL [e/PARENT_EMAIL]`<br> e.g., `add n/James Ho p/91234567 sub/Mathematics l/Secondary 3`
**Edit** | `edit INDEX [n/NAME] [p/PARENT_PHONE] [e/PARENT_EMAIL] [sub/SUBJECT] [l/CURRENT_LEVEL]`<br> e.g., `edit 2 p/9123 4567 l/Secondary 4`; use `edit 2 e/` to remove the email
**Delete student** | Preview: `delete INDEX`<br>Confirm: `delete INDEX /confirm`<br> e.g., `delete 3`, then `delete 3 /confirm`
**Delete note** | Preview: `delete note STUDENT_INDEX NOTE_NUMBER`<br>Confirm: `delete note STUDENT_INDEX NOTE_NUMBER /confirm`<br> e.g., `delete note 1 3`, then `delete note 1 3 /confirm`
**Note** | `note INDEX nt/NOTE`<br> e.g., `note 1 nt/Reviewed factorisation.`
**Edit note** | `edit note STUDENT_INDEX NOTE_NUMBER nt/NEW_NOTE`<br> e.g., `edit note 1 2 nt/Corrected summary.`
**View** | `view INDEX`<br> e.g., `view 2`
**Help** | `help`
**Exit** | `exit`
