package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Represents an immutable, display-ready snapshot of the student's roster.
 */
public final class StudentRoster {

    private static final int DISPLAY_INDEX_OFFSET = 1;
    private static final Comparator<Student> BY_DISPLAY_NAME = new StudentNameComparator();

    private final List<StudentRosterEntry> entries;

    /**
     * Constructs a roster with zero session-note counts for all students.
     *
     * @param students The students to include in the roster.
     */
    public StudentRoster(Collection<Student> students) {
        this(students, Map.of());
    }

    /**
     * Constructs a roster snapshot with the supplied session-note counts.
     *
     * @param students The students to include in the roster.
     * @param noteCounts Session-note counts keyed by stable student ID.
     */
    public StudentRoster(Collection<Student> students, Map<StudentId, Integer> noteCounts) {
        requireNonNull(students);
        requireNonNull(noteCounts);
        Set<StudentId> studentIds = collectStudentIds(students);
        validateNoteCounts(noteCounts, studentIds);
        entries = createEntries(students, noteCounts);
    }

    /**
     * Returns immutable entries sorted by normalized name with one-based display indices.
     */
    public List<StudentRosterEntry> getEntries() {
        return entries;
    }

    /**
     * Returns the number of students in this roster snapshot.
     */
    public int size() {
        return entries.size();
    }

    private static Set<StudentId> collectStudentIds(Collection<Student> students) {
        Set<StudentId> studentIds = new HashSet<>();
        students.forEach(student -> {
            requireNonNull(student);
            checkArgument(studentIds.add(student.getId()), "Student IDs must be unique.");
        });
        return studentIds;
    }

    private static void validateNoteCounts(Map<StudentId, Integer> noteCounts, Set<StudentId> studentIds) {
        noteCounts.forEach((studentId, noteCount) -> {
            requireAllNonNull(studentId, noteCount);
            checkArgument(studentIds.contains(studentId), "Session-note counts must refer to roster students.");
            checkArgument(noteCount >= 0, "Session-note counts must not be negative.");
        });
    }

    private static List<StudentRosterEntry> createEntries(Collection<Student> students,
            Map<StudentId, Integer> noteCounts) {
        List<Student> sortedStudents = students.stream()
                .map(student -> requireNonNull(student))
                .sorted(BY_DISPLAY_NAME)
                .toList();
        return IntStream.range(0, sortedStudents.size())
                .mapToObj(index -> createEntry(sortedStudents.get(index), index + DISPLAY_INDEX_OFFSET, noteCounts))
                .toList();
    }

    private static StudentRosterEntry createEntry(Student student, int rosterIndex,
            Map<StudentId, Integer> noteCounts) {
        int noteCount = noteCounts.getOrDefault(student.getId(), 0);
        return new StudentRosterEntry(student.getId(), rosterIndex, student.getName(), student.getSubject(),
                student.getCurrentLevel(), noteCount);
    }
}
