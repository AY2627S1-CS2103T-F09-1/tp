package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class StudentRosterTest {

    private static final StudentId ALEX_ID = id("123e4567-e89b-12d3-a456-426614174000");
    private static final StudentId BEA_ID = id("123e4567-e89b-12d3-a456-426614174001");
    private static final StudentId ZOE_ID = id("123e4567-e89b-12d3-a456-426614174002");

    @Test
    public void constructor_studentsAreSortedAndIndexed() {
        Student alex = student("  alex   tan ", ALEX_ID, "Mathematics", "Secondary 3");
        Student bea = student("Bea Tan", BEA_ID, "English", "JC 1");
        Student zoe = student("zoe tan", ZOE_ID, "Physics", "Secondary 4");

        StudentRoster roster = new StudentRoster(List.of(zoe, bea, alex), Map.of(ALEX_ID, 2, ZOE_ID, 1));

        assertEquals(List.of(ALEX_ID, BEA_ID, ZOE_ID), roster.getEntries().stream()
                .map(StudentRosterEntry::getStudentId).toList());
        StudentRosterEntry firstEntry = roster.getEntries().get(0);
        assertEquals(ALEX_ID, firstEntry.getStudentId());
        assertEquals(1, firstEntry.getRosterIndex());
        assertEquals("alex tan", firstEntry.getName().getValue());
        assertEquals("Mathematics", firstEntry.getSubject().getValue());
        assertEquals("Secondary 3", firstEntry.getCurrentLevel().getValue());
        assertEquals(firstEntry, new StudentRosterEntry(ALEX_ID, 1, firstEntry.getName(), firstEntry.getSubject(),
                firstEntry.getCurrentLevel(), 2));
        assertEquals(firstEntry.hashCode(), new StudentRosterEntry(ALEX_ID, 1, firstEntry.getName(),
                firstEntry.getSubject(), firstEntry.getCurrentLevel(), 2).hashCode());
        assertEquals(firstEntry, firstEntry);
        assertFalse(firstEntry.equals(null));
        assertFalse(firstEntry.equals("not an entry"));
        assertEquals(firstEntry, firstEntry);
        assertEquals(2, roster.getEntries().get(1).getRosterIndex());
        assertEquals(3, roster.getEntries().get(2).getRosterIndex());
        assertEquals(2, roster.getEntries().get(0).getNoteCount());
        assertEquals(0, roster.getEntries().get(1).getNoteCount());
    }

    @Test
    public void constructor_equalNamesAreOrderedByNormalizedParentPhone() {
        Student laterPhone = student("Alex Tan", "9876 5432", ZOE_ID, "Mathematics", "Secondary 3");
        Student earlierPhone = student(" alex   tan ", "9123-4567", ALEX_ID, "English", "JC 1");

        StudentRoster roster = new StudentRoster(List.of(laterPhone, earlierPhone));

        assertEquals(List.of(ALEX_ID, ZOE_ID), roster.getEntries().stream()
                .map(StudentRosterEntry::getStudentId).toList());
    }

    @Test
    public void constructor_emptyStudents_returnsEmptySnapshot() {
        StudentRoster roster = new StudentRoster(List.of());

        assertEquals(List.of(), roster.getEntries());
        assertEquals(0, roster.size());
    }

    @Test
    public void getEntries_modifySnapshot_throwsUnsupportedOperationException() {
        StudentRoster roster = new StudentRoster(List.of(student("Alex Tan", ALEX_ID, "Mathematics", "Secondary 3")));

        assertThrows(UnsupportedOperationException.class, () -> roster.getEntries().clear());
    }

    @Test
    public void constructor_negativeNoteCount_throwsIllegalArgumentException() {
        Student student = student("Alex Tan", ALEX_ID, "Mathematics", "Secondary 3");

        assertThrows(IllegalArgumentException.class, () -> new StudentRoster(List.of(student), Map.of(ALEX_ID, -1)));
    }

    @Test
    public void constructor_unknownNoteCountStudentId_throwsIllegalArgumentException() {
        Student student = student("Alex Tan", ALEX_ID, "Mathematics", "Secondary 3");

        assertThrows(IllegalArgumentException.class, () -> new StudentRoster(List.of(student), Map.of(ZOE_ID, 1)));
    }

    @Test
    public void constructor_duplicateStudentIds_throwsIllegalArgumentException() {
        Student firstStudent = student("Alex Tan", ALEX_ID, "Mathematics", "Secondary 3");
        Student secondStudent = student("Bea Tan", ALEX_ID, "English", "JC 1");

        assertThrows(IllegalArgumentException.class, () -> new StudentRoster(List.of(firstStudent, secondStudent)));
    }

    @Test
    public void entry_negativeNoteCount_throwsIllegalArgumentException() {
        Student student = student("Alex Tan", ALEX_ID, "Mathematics", "Secondary 3");

        assertThrows(IllegalArgumentException.class, () -> new StudentRosterEntry(ALEX_ID, 1, student.getName(),
                student.getSubject(), student.getCurrentLevel(), -1));
    }

    @Test
    public void entry_differentFields_areNotEqual() {
        StudentRosterEntry entry = entry(ALEX_ID, 1, "Alex Tan", "Mathematics", "Secondary 3", 0);

        assertFalse(entry.equals(entry(BEA_ID, 1, "Alex Tan", "Mathematics", "Secondary 3", 0)));
        assertFalse(entry.equals(entry(ALEX_ID, 2, "Alex Tan", "Mathematics", "Secondary 3", 0)));
        assertFalse(entry.equals(entry(ALEX_ID, 1, "Bea Tan", "Mathematics", "Secondary 3", 0)));
        assertFalse(entry.equals(entry(ALEX_ID, 1, "Alex Tan", "English", "Secondary 3", 0)));
        assertFalse(entry.equals(entry(ALEX_ID, 1, "Alex Tan", "Mathematics", "JC 1", 0)));
        assertFalse(entry.equals(entry(ALEX_ID, 1, "Alex Tan", "Mathematics", "Secondary 3", 1)));
    }
    
    @Test
    public void constructor_unknownNoteCountStudentId_throwsIllegalArgumentException() {
        Student student = student("Alex Tan", ALEX_ID, "Mathematics", "Secondary 3");

        assertThrows(IllegalArgumentException.class, () -> new StudentRoster(List.of(student), Map.of(ZOE_ID, 1)));
    }

    @Test
    public void entry_nonPositiveIndex_throwsIllegalArgumentException() {
        Student student = student("Alex Tan", ALEX_ID, "Mathematics", "Secondary 3");

        assertThrows(IllegalArgumentException.class, () -> new StudentRosterEntry(ALEX_ID, 0, student.getName(),
                student.getSubject(), student.getCurrentLevel(), 0));
    }

    @Test
    public void entry_negativeNoteCount_throwsIllegalArgumentException() {
        Student student = student("Alex Tan", ALEX_ID, "Mathematics", "Secondary 3");

        assertThrows(IllegalArgumentException.class, () -> new StudentRosterEntry(ALEX_ID, 1, student.getName(),
                student.getSubject(), student.getCurrentLevel(), -1));
    }

    @Test
    public void entry_differentFields_areNotEqual() {
        StudentRosterEntry entry = entry(ALEX_ID, 1, "Alex Tan", "Mathematics", "Secondary 3", 0);

        assertFalse(entry.equals(entry(BEA_ID, 1, "Alex Tan", "Mathematics", "Secondary 3", 0)));
        assertFalse(entry.equals(entry(ALEX_ID, 2, "Alex Tan", "Mathematics", "Secondary 3", 0)));
        assertFalse(entry.equals(entry(ALEX_ID, 1, "Bea Tan", "Mathematics", "Secondary 3", 0)));
        assertFalse(entry.equals(entry(ALEX_ID, 1, "Alex Tan", "English", "Secondary 3", 0)));
        assertFalse(entry.equals(entry(ALEX_ID, 1, "Alex Tan", "Mathematics", "JC 1", 0)));
        assertFalse(entry.equals(entry(ALEX_ID, 1, "Alex Tan", "Mathematics", "Secondary 3", 1)));
    }

    private static StudentId id(String value) {
        return StudentId.fromString(value);
    }

    private Student student(String name, StudentId id, String subject, String level) {
        return student(name, "9123 4567", id, subject, level);
    }

    private Student student(String name, String phone, StudentId id, String subject, String level) {
        return new Student(new StudentName(name), new ParentGuardianContact(phone, Optional.empty()),
                new Subject(subject), new CurrentLevel(level), id);
    }

    private StudentRosterEntry entry(StudentId id, int rosterIndex, String name, String subject, String level,
            int noteCount) {
        Student student = student(name, id, subject, level);
        return new StudentRosterEntry(id, rosterIndex, student.getName(), student.getSubject(),
                student.getCurrentLevel(), noteCount);
    }
}
