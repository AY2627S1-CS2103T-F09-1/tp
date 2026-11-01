package seedu.address.model.student;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Compares students using the roster's natural, user-facing name order.
 */
final class StudentNameComparator implements Comparator<Student> {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Map<Character, Character> NON_DECOMPOSING_LETTERS = Map.of(
            'Ø', 'O',
            'ø', 'o',
            'Ł', 'L',
            'ł', 'l');

    @Override
    public int compare(Student firstStudent, Student secondStudent) {
        String firstName = firstStudent.getName().getValue();
        String secondName = secondStudent.getName().getValue();
        int naturalComparison = compareNaturalName(sortableName(firstName), sortableName(secondName));
        if (naturalComparison != 0) {
            return naturalComparison;
        }
        int caseInsensitiveComparison = String.CASE_INSENSITIVE_ORDER.compare(firstName, secondName);
        if (caseInsensitiveComparison != 0) {
            return caseInsensitiveComparison;
        }
        int phoneComparison = firstStudent.getParentGuardianContact().getNormalizedPhone()
                .compareTo(secondStudent.getParentGuardianContact().getNormalizedPhone());
        if (phoneComparison != 0) {
            return phoneComparison;
        }
        return firstStudent.getId().getValue().compareTo(secondStudent.getId().getValue());
    }

    private static String sortableName(String name) {
        String decomposedName = Normalizer.normalize(name, Normalizer.Form.NFD);
        String nameWithoutDiacritics = DIACRITICS.matcher(decomposedName).replaceAll("");
        StringBuilder sortableName = new StringBuilder(nameWithoutDiacritics.length());
        for (int index = 0; index < nameWithoutDiacritics.length(); index++) {
            char character = nameWithoutDiacritics.charAt(index);
            sortableName.append(NON_DECOMPOSING_LETTERS.getOrDefault(character, character));
        }
        return sortableName.toString();
    }

    private static int compareNaturalName(String firstName, String secondName) {
        int firstIndex = 0;
        int secondIndex = 0;
        while (firstIndex < firstName.length() && secondIndex < secondName.length()) {
            char firstCharacter = firstName.charAt(firstIndex);
            char secondCharacter = secondName.charAt(secondIndex);
            if (Character.isDigit(firstCharacter) && Character.isDigit(secondCharacter)) {
                int numericComparison = compareNumericRuns(firstName, firstIndex, secondName, secondIndex);
                if (numericComparison != 0) {
                    return numericComparison;
                }
                firstIndex = skipDigits(firstName, firstIndex);
                secondIndex = skipDigits(secondName, secondIndex);
                continue;
            }
            int characterComparison = Character.compare(Character.toLowerCase(firstCharacter),
                    Character.toLowerCase(secondCharacter));
            if (characterComparison != 0) {
                return characterComparison;
            }
            firstIndex++;
            secondIndex++;
        }
        return Integer.compare(firstName.length() - firstIndex, secondName.length() - secondIndex);
    }

    private static int compareNumericRuns(String firstName, int firstStart, String secondName, int secondStart) {
        int firstEnd = skipDigits(firstName, firstStart);
        int secondEnd = skipDigits(secondName, secondStart);
        int firstSignificantStart = skipLeadingZeros(firstName, firstStart, firstEnd);
        int secondSignificantStart = skipLeadingZeros(secondName, secondStart, secondEnd);
        int firstSignificantLength = firstEnd - firstSignificantStart;
        int secondSignificantLength = secondEnd - secondSignificantStart;

        if (firstSignificantLength != secondSignificantLength) {
            return Integer.compare(firstSignificantLength, secondSignificantLength);
        }
        for (int offset = 0; offset < firstSignificantLength; offset++) {
            int firstDigit = Character.digit(firstName.charAt(firstSignificantStart + offset), 10);
            int secondDigit = Character.digit(secondName.charAt(secondSignificantStart + offset), 10);
            if (firstDigit != secondDigit) {
                return Integer.compare(firstDigit, secondDigit);
            }
        }
        return 0;
    }

    private static int skipDigits(String name, int start) {
        int end = start;
        while (end < name.length() && Character.isDigit(name.charAt(end))) {
            end++;
        }
        return end;
    }

    private static int skipLeadingZeros(String name, int start, int end) {
        int firstSignificant = start;
        while (firstSignificant < end && Character.digit(name.charAt(firstSignificant), 10) == 0) {
            firstSignificant++;
        }
        return firstSignificant == end ? end - 1 : firstSignificant;
    }
}
