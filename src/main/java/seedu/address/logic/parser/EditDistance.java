package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

/**
 * Measures how many single-character edits separate two strings.
 */
final class EditDistance {

    private EditDistance() {}

    /**
     * Returns the number of single-character insertions, deletions, or substitutions that turn {@code source}
     * into {@code target}.
     */
    static int between(String source, String target) {
        requireNonNull(source);
        requireNonNull(target);
        int[] previousRow = new int[target.length() + 1];
        for (int j = 0; j <= target.length(); j++) {
            previousRow[j] = j;
        }
        for (int i = 1; i <= source.length(); i++) {
            int[] currentRow = new int[target.length() + 1];
            currentRow[0] = i;
            for (int j = 1; j <= target.length(); j++) {
                int substitutionCost = source.charAt(i - 1) == target.charAt(j - 1) ? 0 : 1;
                currentRow[j] = Math.min(previousRow[j - 1] + substitutionCost,
                        Math.min(previousRow[j], currentRow[j - 1]) + 1);
            }
            previousRow = currentRow;
        }
        return previousRow[target.length()];
    }
}
