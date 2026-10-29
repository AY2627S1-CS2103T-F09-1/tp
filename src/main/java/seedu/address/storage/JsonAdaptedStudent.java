package seedu.address.storage;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.session.SessionHistory;
import seedu.address.model.session.SessionNote;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentIdentity;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;

/**
 * Jackson-friendly version of {@link Student}.
 */
class JsonAdaptedStudent {

    static final String MISSING_FIELD_MESSAGE_FORMAT = "Student's %s field is missing!";
    static final String INVALID_ID_MESSAGE = "Student's ID is invalid!";
    static final String MISSING_SESSION_NOTE_MESSAGE = "Student's session notes contain an empty entry!";

    private final String studentId;
    private final String name;
    private final String parentPhone;
    private final String parentEmail;
    private final String subject;
    private final String currentLevel;
    private final List<JsonAdaptedSessionNote> sessionNotes = new ArrayList<>();

    /**
     * Constructs a Jackson-friendly student from persisted fields.
     * A missing {@code sessionNotes} field is treated as an empty session history.
     */
    @JsonCreator
    JsonAdaptedStudent(@JsonProperty("studentId") String studentId, @JsonProperty("name") String name,
            @JsonProperty("parentPhone") String parentPhone,
            @JsonProperty("parentEmail") String parentEmail, @JsonProperty("subject") String subject,
            @JsonProperty("currentLevel") String currentLevel,
            @JsonProperty("sessionNotes") List<JsonAdaptedSessionNote> sessionNotes) {
        this.studentId = studentId;
        this.name = name;
        this.parentPhone = parentPhone;
        this.parentEmail = parentEmail;
        this.subject = subject;
        this.currentLevel = currentLevel;
        if (sessionNotes != null) {
            this.sessionNotes.addAll(sessionNotes);
        }
    }

    /**
     * Constructs a Jackson-friendly student without session notes from persisted fields.
     */
    JsonAdaptedStudent(String studentId, String name, String parentPhone, String parentEmail, String subject,
            String currentLevel) {
        this(studentId, name, parentPhone, parentEmail, subject, currentLevel, null);
    }

    /**
     * Constructs a legacy Jackson-friendly student without an identifier.
     */
    JsonAdaptedStudent(String name, String parentPhone, String parentEmail, String subject, String currentLevel) {
        this(null, name, parentPhone, parentEmail, subject, currentLevel);
    }

    /**
     * Constructs a Jackson-friendly student from a domain student without session notes.
     */
    JsonAdaptedStudent(Student source) {
        this(source, SessionHistory.empty());
    }

    /**
     * Constructs a Jackson-friendly student from a domain student and that student's session history.
     */
    JsonAdaptedStudent(Student source, SessionHistory sessionHistory) {
        requireAllNonNull(source, sessionHistory);
        studentId = source.getId().getValue();
        name = source.getName().getValue();
        parentPhone = source.getParentGuardianContact().getPhone();
        parentEmail = source.getParentGuardianContact().getEmail().orElse(null);
        subject = source.getSubject().getValue();
        currentLevel = source.getCurrentLevel().getValue();
        sessionHistory.getNotes().stream().map(JsonAdaptedSessionNote::new).forEach(sessionNotes::add);
    }

    /**
     * Returns the validated domain student represented by this JSON object.
     *
     * @throws IllegalValueException if a persisted field is missing or invalid.
     */
    Student toModelType() throws IllegalValueException {
        StudentName modelName = new StudentName(requireValidName());
        ParentGuardianContact modelContact = new ParentGuardianContact(requireValidPhone(), optionalValidEmail());
        Subject modelSubject = new Subject(requireValidSubject());
        CurrentLevel modelCurrentLevel = new CurrentLevel(requireValidCurrentLevel());
        return new Student(modelName, modelContact, modelSubject, modelCurrentLevel,
                getStudentId(modelName, modelContact));
    }

    /**
     * Returns the validated session history represented by this JSON object's session notes.
     * Notes with equal timestamps keep their saved order.
     *
     * @throws IllegalValueException If a session note is empty or invalid.
     */
    SessionHistory toModelSessionHistory() throws IllegalValueException {
        List<SessionNote> modelNotes = new ArrayList<>();
        for (JsonAdaptedSessionNote sessionNote : sessionNotes) {
            if (sessionNote == null) {
                throw new IllegalValueException(MISSING_SESSION_NOTE_MESSAGE);
            }
            modelNotes.add(sessionNote.toModelType());
        }
        SessionHistory history = SessionHistory.of(modelNotes);
        assert history.size() == sessionNotes.size() : "Every persisted session note must be in the history";
        return history;
    }

    private StudentId getStudentId(StudentName modelName, ParentGuardianContact modelContact)
            throws IllegalValueException {
        if (studentId == null) {
            return StudentId.fromLegacyIdentity(new StudentIdentity(modelName, modelContact));
        }
        try {
            return StudentId.fromString(studentId);
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(INVALID_ID_MESSAGE);
        }
    }

    private String requireValidName() throws IllegalValueException {
        if (name == null) {
            throw missingField(StudentName.class);
        }
        if (!StudentName.isValidName(StudentName.normalize(name))) {
            throw new IllegalValueException(StudentName.MESSAGE_CONSTRAINTS);
        }
        return name;
    }

    private String requireValidPhone() throws IllegalValueException {
        return requireValidField(parentPhone, ParentGuardianContact.class,
                ParentGuardianContact::isValidPhone, ParentGuardianContact.PHONE_MESSAGE_CONSTRAINTS);
    }

    private Optional<String> optionalValidEmail() throws IllegalValueException {
        if (parentEmail == null) {
            return Optional.empty();
        }
        if (!ParentGuardianContact.isValidEmail(parentEmail)) {
            throw new IllegalValueException(ParentGuardianContact.EMAIL_MESSAGE_CONSTRAINTS);
        }
        return Optional.of(parentEmail);
    }

    private String requireValidSubject() throws IllegalValueException {
        return requireValidField(subject, Subject.class, Subject::isValidSubject, Subject.MESSAGE_CONSTRAINTS);
    }

    private String requireValidCurrentLevel() throws IllegalValueException {
        return requireValidField(currentLevel, CurrentLevel.class,
                CurrentLevel::isValidCurrentLevel, CurrentLevel.MESSAGE_CONSTRAINTS);
    }

    /**
     * Returns {@code value} when it is present and satisfies {@code isValid}.
     *
     * @param value The persisted field value.
     * @param fieldType The domain type used in the missing-field message.
     * @param isValid The validity predicate for a present value.
     * @param constraintMessage The message used when a present value is invalid.
     * @throws IllegalValueException If {@code value} is missing or invalid.
     */
    private String requireValidField(String value, Class<?> fieldType, Predicate<String> isValid,
            String constraintMessage) throws IllegalValueException {
        if (value == null) {
            throw missingField(fieldType);
        }
        if (!isValid.test(value)) {
            throw new IllegalValueException(constraintMessage);
        }
        return value;
    }

    private IllegalValueException missingField(Class<?> fieldType) {
        return new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, fieldType.getSimpleName()));
    }
}
