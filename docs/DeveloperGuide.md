---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in `docs/diagrams`. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of contacts
* prefers desktop apps over other types of applications
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Manage contacts faster than with a typical mouse-driven GUI application.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                             | I want to …​                                                                     | So that I can…​                                                                            |
| -------- | --------------------------------------------------- | -------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------ |
| `* * *`  | tutor new to TutorTrack                             | add a student to my roster                                                       | begin keeping their tutoring context in one place                                          |
| `* * *`  | tutor                                               | record a student's parent contact                                                | find the correct contact when I need it                                                    |
| `* * *`  | tutor                                               | record the subject I teach a student                                             | identify the context of their tuition                                                      |
| `* * *`  | tutor                                               | record a student's current level                                                 | prepare appropriately for them                                                             |
| `* * *`  | tutor who types fast                                | add a student with all their details in a single command                         | record a new student quickly between lessons                                               |
| `* * *`  | tutor                                               | be stopped from adding the same student twice                                    | avoid splitting one student's session notes across two entries in my roster                |
| `* * *`  | tutor teaching siblings                             | add each sibling as a separate student with the same parent phone                | keep their learning histories separate while retaining their shared parent contact         |
| `* * *`  | tutor                                               | see all the students in my roster                                                | keep track of all the students I am managing                                               |
| `* * *`  | tutor                                               | see how many session notes each student in my roster has                         | tell apart students who share the same name                                                |
| `* * *`  | tutor preparing for a lesson                        | view a student's subject, level, and parent contact together                     | orient myself before the lesson                                                            |
| `* * *`  | tutor preparing for a lesson                        | review a student's recent session notes                                          | continue from where the student previously left off                                        |
| `* * *`  | tutor                                               | see the date and time each session note was written                              | know when each lesson took place                                                           |
| `* * *`  | tutor who has just completed a lesson               | add a short session note about a student's progress, difficulties, or next steps | preserve what happened while it is still fresh                                             |
| `* * *`  | tutor                                               | delete a student from my roster                                                  | remove information I no longer need, or re-enter a student whose details I entered wrongly |
| `* * *`  | tutor                                               | be told how many session notes were removed when I delete a student              | notice immediately if I deleted the wrong student                                          |
| `* * *`  | tutor                                               | be told exactly what is wrong when a command fails                               | fix the mistake without guessing or retyping the whole command                             |
| `* * *`  | tutor                                               | have my roster and session notes saved automatically after every change          | continue where I left off the next time I open TutorTrack                                  |
| `* * *`  | tutor new to TutorTrack                             | see usage instructions                                                           | refer to instructions when I forget how to use TutorTrack                                  |
| `* *`    | tutor trying TutorTrack for the first time          | see sample students and session notes                                            | understand how TutorTrack supports my tutoring work                                        |
| `* *`    | tutor ready to use TutorTrack for real              | remove all sample data at once                                                   | start my roster with only my own students                                                  |
| `* *`    | tutor                                               | update a student's parent contact without losing the student's session notes     | keep the parent contact reliable when a parent changes their phone number or email         |
| `* *`    | tutor                                               | remove a parent email that is no longer valid                                    | avoid contacting an address that no longer works                                           |
| `* *`    | tutor                                               | correct a misspelled student name without losing the student's session notes     | keep my roster accurate                                                                    |
| `* *`    | tutor                                               | update a student's level without losing the student's session notes              | keep the student's details in line with their current stage of learning                    |
| `* *`    | tutor                                               | update the subject I teach a student without losing the student's session notes  | keep the student's details relevant when their tuition changes                             |
| `* *`    | tutor preparing for a lesson                        | find a student by name                                                           | retrieve their context without scanning my whole roster                                    |
| `* *`    | tutor with students in different subjects or levels | narrow my roster to students of a particular subject or level                    | focus on the students I am working with                                                    |
| `* *`    | tutor                                               | return to my full roster after a search                                          | select any student by their roster number again                                            |
| `* *`    | tutor                                               | undo my most recent change                                                       | recover from a mistaken change, such as deleting the wrong student                         |
| `* *`    | tutor new to TutorTrack                             | see the exact format of a specific command                                       | enter it correctly without trial and error                                                 |
| `* *`    | tutor                                               | enter a parent phone with spaces or hyphens                                      | copy numbers from messages without reformatting them                                       |
| `* *`    | tutor                                               | see my roster sorted by student name                                             | locate a student quickly                                                                   |
| `* *`    | tutor                                               | back up my roster and session notes by copying a single data file                | avoid losing my students' history if my computer fails                                     |
| `*`      | tutor who has used TutorTrack for a long time       | archive a student who is no longer receiving tuition                             | keep my current roster uncluttered                                                         |
| `*`      | tutor                                               | view an archived student's details and session notes                             | refer to their past context if I need it                                                   |
| `*`      | tutor whose former student resumes lessons          | restore an archived student to my roster                                         | continue teaching with their previous context available                                    |
| `*`      | tutor teaching siblings                             | link siblings to one shared parent contact                                       | update the parent contact once for all of them                                             |
| `*`      | tutor                                               | correct a mistake in a saved session note                                        | keep each student's history accurate                                                       |
| `*`      | tutor                                               | delete several students in one command                                           | remove students who have stopped lessons quickly                                           |
| `*`      | tutor                                               | confirm before a student is deleted                                              | avoid deleting the wrong student by accident                                               |
| `*`      | tutor who works on more than one computer           | access my roster from any of my computers                                        | prepare for lessons wherever I am                                                          |

### Use cases

The following use cases describe the TutorTrack MVP at the same level of
detail. Each use case focuses on the externally visible interaction between
the tutor and the system.

**System:** `TutorTrack`

**Actor:** `Tutor`

**Use case: Add a student**

**Preconditions:** The system is running and ready to accept requests.

**Guarantees:** A valid student record is saved locally and appears in the
student roster.

**MSS**

1. Tutor requests to add a student.
2. Tutor provides the student's name, parent or guardian phone number, subject,
   and current level, and optionally an email address.
3. Tutor submits the student details.
4. TutorTrack validates the details and creates the student record.
5. TutorTrack saves the record and displays the updated roster in
   case-insensitive alphabetical order.

   Use case ends.

**Extensions**

* 2a. One or more required details are missing or invalid.

  * 2a1. TutorTrack reports the relevant validation error and makes no changes.
  * 2a2. Tutor corrects the details.

    Use case resumes at step 2.

* 4a. A student with the same name and parent or guardian phone number already
  exists.

  * 4a1. TutorTrack reports the duplicate and makes no changes.

    Use case ends.

* 5a. TutorTrack cannot save the updated data.

  * 5a1. TutorTrack reports the storage error and does not create the record.

    Use case ends.

**Use case: List the student roster**

**Preconditions:** The system is running and the student roster is available.

**Guarantees:** The complete roster is displayed with stable indices that can
be used to select a student in a subsequent use case. No data is changed.

**MSS**

1. Tutor requests to list the student roster.
2. TutorTrack displays every student sorted by normalised name, together with
   an index, subject, current level, and session-note count.
3. TutorTrack reports the number of students shown and establishes the
   displayed indices as the current selection context.

   Use case ends.

**Extensions**

* 2a. The roster is empty.

  * 2a1. TutorTrack reports that there are no students and shows how to add
    one.

    Use case ends.

* 1a. The request contains an index, parameter, or other extra input.

  * 1a1. TutorTrack reports the correct usage and leaves the displayed roster
    and selection context unchanged.

    Use case ends.

**Use case: View a student profile**

**Preconditions:** The system is running and the current roster index context
is available.

**Guarantees:** The selected student's profile is displayed without changing
the roster or stored data.

**MSS**

1. Tutor selects a student by the student's roster index.
2. TutorTrack displays the student's name, parent or guardian contact details,
   subject, and current level.
3. TutorTrack displays all session notes for the student in newest-first order,
   including each note's saved date and time.
4. TutorTrack confirms that the student's profile is being shown.

   Use case ends.

**Extensions**

* 1a. The index is missing, invalid, or outside the roster.

  * 1a1. TutorTrack reports the index error and leaves any currently displayed
    profile unchanged.

    Use case ends.

* 3a. The student has no session notes.

  * 3a1. TutorTrack reports that no session notes have been recorded.

    Use case ends.

**Use case: Add a session note**

**Preconditions:** The system is running and the current roster index context
is available.

**Guarantees:** A valid session note is appended to the student's session
history and saved locally. No other student record is changed.

**MSS**

1. Tutor selects a student by the student's roster index and provides a short
   session note.
2. TutorTrack validates the note.
3. TutorTrack records the note with the current local date and time and saves
   it in the student's session history.
4. TutorTrack confirms that the note was added and updates the student's
   session-note count.

   Use case ends.

**Extensions**

* 1a. The index is missing, invalid, or outside the roster.

  * 1a1. TutorTrack reports the index error and makes no changes.

    Use case ends.

* 2a. The note is missing, empty, too long, or contains a line break or control
  character.

  * 2a1. TutorTrack reports the note validation error and makes no changes.

    Use case ends.

* 3a. TutorTrack cannot save the updated data.

  * 3a1. TutorTrack reports the storage error and does not retain the note.

    Use case ends.

**Use case: Delete a student**

**Preconditions:** The system is running and the current roster index context
is available.

**Guarantees:** The selected student and all of the student's session notes are
removed from the roster and local storage.

**MSS**

1. Tutor selects a student by the student's roster index.
2. TutorTrack identifies the selected student and the number of session notes
   associated with the student.
3. TutorTrack removes the student and all associated session notes and saves the
   updated roster.
4. TutorTrack confirms the deletion, including the student's name and the
   number of session notes removed.
5. TutorTrack displays the updated roster and establishes its displayed indices
   as the current selection context.

   Use case ends.

**Extensions**

* 1a. The index is missing, invalid, or outside the roster.

  * 1a1. TutorTrack reports the index error and makes no changes.

    Use case ends.

* 3a. TutorTrack cannot save the updated data.

  * 3a1. TutorTrack reports the storage error and does not delete the student or
    the session notes.

    Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2.  Should be able to hold up to 1000 persons without noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.

*{More to be added}*

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Private contact detail**: A contact detail that is not meant to be shared with others

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases …​ }_
