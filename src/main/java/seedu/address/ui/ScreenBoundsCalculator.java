package seedu.address.ui;

import java.util.List;

import javafx.geometry.Rectangle2D;

/**
 * Calculates window placement and size limits from screen geometry, independent of JavaFX windows.
 */
final class ScreenBoundsCalculator {

    private static final double SCREEN_SIZE_TOLERANCE = 1;
    private static final double NO_SCALE_CORRECTION = 1;

    private ScreenBoundsCalculator() {}

    /**
     * Returns true if the output scale can only be corrected when exactly one screen is reported by both
     * JavaFX and the graphics environment, because only then can the screen be matched to its display mode.
     */
    static boolean canCorrectOutputScale(int screenCount, int graphicsDeviceCount) {
        return screenCount == 1 && graphicsDeviceCount == 1;
    }

    /**
     * Returns the index of the screen bounds that overlap {@code windowBounds} the most.
     * Ties keep the earliest screen.
     */
    static int findLargestOverlapIndex(Rectangle2D windowBounds, List<Rectangle2D> screenBounds) {
        assert !screenBounds.isEmpty() : "At least one screen must be available";
        int largestOverlapIndex = 0;
        double largestOverlapArea = calculateIntersectionArea(windowBounds, screenBounds.get(0));
        for (int i = 1; i < screenBounds.size(); i++) {
            double overlapArea = calculateIntersectionArea(windowBounds, screenBounds.get(i));
            if (overlapArea > largestOverlapArea) {
                largestOverlapIndex = i;
                largestOverlapArea = overlapArea;
            }
        }
        return largestOverlapIndex;
    }

    /**
     * Returns {@code savedCoordinate} limited to the range from {@code minimumCoordinate} to
     * {@code maximumCoordinate}. When the range is empty, such as a window larger than its screen, the
     * minimum is returned.
     */
    static double clampCoordinate(double savedCoordinate, double minimumCoordinate, double maximumCoordinate) {
        double usableMaximum = Math.max(minimumCoordinate, maximumCoordinate);
        return Math.max(minimumCoordinate, Math.min(savedCoordinate, usableMaximum));
    }

    /**
     * Returns {@code visualBounds} with the output scale removed from each axis whose screen size exceeds the
     * physical display, which indicates that the axis was reported in physical rather than logical pixels.
     */
    static Rectangle2D adjustVisualBoundsForOutputScale(Rectangle2D screenBounds, Rectangle2D visualBounds,
            double outputScaleX, double outputScaleY, int physicalWidth, int physicalHeight) {
        assert outputScaleX > 0 : "Output scale must be positive";
        assert outputScaleY > 0 : "Output scale must be positive";
        assert physicalWidth > 0 : "Physical display width must be positive";
        assert physicalHeight > 0 : "Physical display height must be positive";

        boolean widthExceedsPhysicalDisplay = screenBounds.getWidth() * outputScaleX
                > physicalWidth + SCREEN_SIZE_TOLERANCE;
        boolean heightExceedsPhysicalDisplay = screenBounds.getHeight() * outputScaleY
                > physicalHeight + SCREEN_SIZE_TOLERANCE;
        double correctionX = widthExceedsPhysicalDisplay ? outputScaleX : NO_SCALE_CORRECTION;
        double correctionY = heightExceedsPhysicalDisplay ? outputScaleY : NO_SCALE_CORRECTION;
        return new Rectangle2D(visualBounds.getMinX() / correctionX, visualBounds.getMinY() / correctionY,
                visualBounds.getWidth() / correctionX, visualBounds.getHeight() / correctionY);
    }

    private static double calculateIntersectionArea(Rectangle2D firstBounds, Rectangle2D secondBounds) {
        double intersectionWidth = Math.max(0,
                Math.min(firstBounds.getMaxX(), secondBounds.getMaxX())
                        - Math.max(firstBounds.getMinX(), secondBounds.getMinX()));
        double intersectionHeight = Math.max(0,
                Math.min(firstBounds.getMaxY(), secondBounds.getMaxY())
                        - Math.max(firstBounds.getMinY(), secondBounds.getMinY()));
        return intersectionWidth * intersectionHeight;
    }
}
