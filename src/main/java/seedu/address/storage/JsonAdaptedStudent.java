package seedu.address.storage;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;

/**
 * Jackson-friendly version of {@link Student}.
 */
class JsonAdaptedStudent {

    static final String MISSING_FIELD_MESSAGE_FORMAT = "Student's %s field is missing!";

    private final String name;
    private final String parentPhone;
    private final String parentEmail;
    private final String subject;
    private final String currentLevel;

    /**
     * Constructs a Jackson-friendly student from persisted fields.
     */
    @JsonCreator
    JsonAdaptedStudent(@JsonProperty("name") String name, @JsonProperty("parentPhone") String parentPhone,
            @JsonProperty("parentEmail") String parentEmail, @JsonProperty("subject") String subject,
            @JsonProperty("currentLevel") String currentLevel) {
        this.name = name;
        this.parentPhone = parentPhone;
        this.parentEmail = parentEmail;
        this.subject = subject;
        this.currentLevel = currentLevel;
    }

    /**
     * Constructs a Jackson-friendly student from a domain student.
     */
    JsonAdaptedStudent(Student source) {
        name = source.getName().getValue();
        parentPhone = source.getParentGuardianContact().getPhone();
        parentEmail = source.getParentGuardianContact().getEmail().orElse(null);
        subject = source.getSubject().getValue();
        currentLevel = source.getCurrentLevel().getValue();
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
        return new Student(modelName, modelContact, modelSubject, modelCurrentLevel);
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
        if (parentPhone == null) {
            throw missingField(ParentGuardianContact.class);
        }
        if (!ParentGuardianContact.isValidPhone(parentPhone)) {
            throw new IllegalValueException(ParentGuardianContact.PHONE_MESSAGE_CONSTRAINTS);
        }
        return parentPhone;
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
        if (subject == null) {
            throw missingField(Subject.class);
        }
        if (!Subject.isValidSubject(subject)) {
            throw new IllegalValueException(Subject.MESSAGE_CONSTRAINTS);
        }
        return subject;
    }

    private String requireValidCurrentLevel() throws IllegalValueException {
        if (currentLevel == null) {
            throw missingField(CurrentLevel.class);
        }
        if (!CurrentLevel.isValidCurrentLevel(currentLevel)) {
            throw new IllegalValueException(CurrentLevel.MESSAGE_CONSTRAINTS);
        }
        return currentLevel;
    }

    private IllegalValueException missingField(Class<?> fieldType) {
        return new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, fieldType.getSimpleName()));
    }
}
