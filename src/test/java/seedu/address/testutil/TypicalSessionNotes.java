package seedu.address.testutil;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import seedu.address.model.session.SessionNote;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;

/**
 * Provides synthetic students and session notes for use in tests.
 */
public class TypicalSessionNotes {

    public static final Student ALEX = new Student(new StudentName("Alex Tan"),
            new ParentGuardianContact("9123 4567", Optional.empty()), new Subject("Mathematics"),
            new CurrentLevel("Secondary 3"), studentId("123e4567-e89b-12d3-a456-426614174000"));
    public static final Student BEA = new Student(new StudentName("Bea Lim"),
            new ParentGuardianContact("9876 5432", Optional.empty()), new Subject("English"),
            new CurrentLevel("JC 1"), studentId("123e4567-e89b-12d3-a456-426614174001"));

    public static final SessionNote INDICES_NOTE = new SessionNote("Reviewed indices.",
            OffsetDateTime.parse("2026-09-11T18:35:00+08:00"));
    public static final SessionNote FACTORISATION_NOTE = new SessionNote("Reviewed factorisation.",
            OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));

    private TypicalSessionNotes() {} // prevents instantiation

    private static StudentId studentId(String uuid) {
        return new StudentId(UUID.fromString(uuid));
    }
}
