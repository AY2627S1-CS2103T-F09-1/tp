package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.geometry.Rectangle2D;

public class ScreenBoundsCalculatorTest {

    private static final double SCALE = 1.5;
    private static final int PHYSICAL_WIDTH = 1280;
    private static final int PHYSICAL_HEIGHT = 720;

    // Equivalence partitions: each axis is reported either in physical or in logical pixels.
    @Test
    public void adjustVisualBoundsForOutputScale_physicalBoundsReportedAsLogical_correctsBounds() {
        Rectangle2D physicalBoundsReportedAsLogical = new Rectangle2D(0, 0, 1280, 720);

        Rectangle2D adjustedBounds = ScreenBoundsCalculator.adjustVisualBoundsForOutputScale(
                physicalBoundsReportedAsLogical, physicalBoundsReportedAsLogical, 1.5, 1.5, 1280, 720);

        assertEquals(new Rectangle2D(0, 0, 1280 / 1.5, 720 / 1.5), adjustedBounds);
    }

    @Test
    public void adjustVisualBoundsForOutputScale_logicalBoundsAlreadyScaled_keepsBounds() {
        Rectangle2D logicalBounds = new Rectangle2D(0, 0, 1280 / 1.5, 720 / 1.5);

        Rectangle2D adjustedBounds = ScreenBoundsCalculator.adjustVisualBoundsForOutputScale(
                logicalBounds, logicalBounds, 1.5, 1.5, 1280, 720);

        assertEquals(logicalBounds, adjustedBounds);
    }

    @Test
    public void adjustVisualBoundsForOutputScale_onlyHeightExceedsPhysicalDisplay_correctsHeightOnly() {
        Rectangle2D partlyPhysicalBounds = new Rectangle2D(0, 0, 1280 / 1.5, 720);

        Rectangle2D adjustedBounds = ScreenBoundsCalculator.adjustVisualBoundsForOutputScale(
                partlyPhysicalBounds, partlyPhysicalBounds, 1.5, 1.5, 1280, 720);

        assertEquals(new Rectangle2D(0, 0, 1280 / 1.5, 720 / 1.5), adjustedBounds);
    }

    @Test
    public void adjustVisualBoundsForOutputScale_onlyWidthExceedsPhysicalDisplay_correctsWidthOnly() {
        Rectangle2D partlyPhysicalBounds = new Rectangle2D(0, 0, 1280, 720 / 1.5);

        Rectangle2D adjustedBounds = ScreenBoundsCalculator.adjustVisualBoundsForOutputScale(
                partlyPhysicalBounds, partlyPhysicalBounds, 1.5, 1.5, 1280, 720);

        assertEquals(new Rectangle2D(0, 0, 1280 / 1.5, 720 / 1.5), adjustedBounds);
    }

    @Test
    public void adjustVisualBoundsForOutputScale_invalidScaleOrDisplaySize_throwsAssertionError() {
        Rectangle2D bounds = new Rectangle2D(0, 0, 1280, 720);

        assertThrows(AssertionError.class, () ->
                ScreenBoundsCalculator.adjustVisualBoundsForOutputScale(bounds, bounds, 0, 1, 1280, 720));
        assertThrows(AssertionError.class, () ->
                ScreenBoundsCalculator.adjustVisualBoundsForOutputScale(bounds, bounds, 1, 0, 1280, 720));
        assertThrows(AssertionError.class, () ->
                ScreenBoundsCalculator.adjustVisualBoundsForOutputScale(bounds, bounds, 1, 1, 0, 720));
        assertThrows(AssertionError.class, () ->
                ScreenBoundsCalculator.adjustVisualBoundsForOutputScale(bounds, bounds, 1, 1, 1280, 0));
    }

    // Boundary value analysis: the physical-display comparison allows a tolerance of exactly one pixel.
    @Test
    public void adjustVisualBoundsForOutputScale_widthAtToleranceBoundary_isNotCorrected() {
        double exactScale = 2;
        Rectangle2D screenAtTolerance = new Rectangle2D(
                0, 0, (PHYSICAL_WIDTH + 1) / exactScale, PHYSICAL_HEIGHT / exactScale);

        Rectangle2D adjustedBounds = ScreenBoundsCalculator.adjustVisualBoundsForOutputScale(
                screenAtTolerance, screenAtTolerance, exactScale, exactScale, PHYSICAL_WIDTH, PHYSICAL_HEIGHT);

        assertEquals(screenAtTolerance, adjustedBounds);
    }

    @Test
    public void adjustVisualBoundsForOutputScale_widthJustBeyondTolerance_isCorrected() {
        double justBeyondTolerance = (PHYSICAL_WIDTH + 1.01) / SCALE;
        Rectangle2D screen = new Rectangle2D(0, 0, justBeyondTolerance, PHYSICAL_HEIGHT / SCALE);

        Rectangle2D adjustedBounds = ScreenBoundsCalculator.adjustVisualBoundsForOutputScale(
                screen, screen, SCALE, SCALE, PHYSICAL_WIDTH, PHYSICAL_HEIGHT);

        assertEquals(justBeyondTolerance / SCALE, adjustedBounds.getWidth(), 1e-9);
        assertEquals(screen.getHeight(), adjustedBounds.getHeight(), 1e-9);
    }

    @Test
    public void adjustVisualBoundsForOutputScale_offsetBounds_scalesOriginWithSize() {
        Rectangle2D screen = new Rectangle2D(0, 0, PHYSICAL_WIDTH, PHYSICAL_HEIGHT);
        Rectangle2D visualBounds = new Rectangle2D(150, 60, 1130, 660);

        Rectangle2D adjustedBounds = ScreenBoundsCalculator.adjustVisualBoundsForOutputScale(
                screen, visualBounds, SCALE, SCALE, PHYSICAL_WIDTH, PHYSICAL_HEIGHT);

        assertEquals(new Rectangle2D(150 / SCALE, 60 / SCALE, 1130 / SCALE, 660 / SCALE), adjustedBounds);
    }

    // Equivalence partitions of the screen-count check: exactly one of each versus any other combination.
    @Test
    public void canCorrectOutputScale_singleScreenAndDevice_returnsTrue() {
        assertTrue(ScreenBoundsCalculator.canCorrectOutputScale(1, 1));
    }

    @Test
    public void canCorrectOutputScale_zeroOrManyScreensOrDevices_returnsFalse() {
        assertFalse(ScreenBoundsCalculator.canCorrectOutputScale(0, 1));
        assertFalse(ScreenBoundsCalculator.canCorrectOutputScale(2, 1));
        assertFalse(ScreenBoundsCalculator.canCorrectOutputScale(1, 0));
        assertFalse(ScreenBoundsCalculator.canCorrectOutputScale(1, 2));
    }

    @Test
    public void findLargestOverlapIndex_windowSpansScreens_returnsScreenWithLargestOverlap() {
        Rectangle2D leftScreen = new Rectangle2D(0, 0, 1920, 1080);
        Rectangle2D rightScreen = new Rectangle2D(1920, 0, 1920, 1080);
        Rectangle2D mostlyRightWindow = new Rectangle2D(1800, 100, 800, 600);

        assertEquals(1, ScreenBoundsCalculator.findLargestOverlapIndex(
                mostlyRightWindow, List.of(leftScreen, rightScreen)));
        assertEquals(0, ScreenBoundsCalculator.findLargestOverlapIndex(
                mostlyRightWindow, List.of(rightScreen, leftScreen)));
    }

    @Test
    public void findLargestOverlapIndex_equalOrNoOverlap_returnsFirstScreen() {
        Rectangle2D leftScreen = new Rectangle2D(0, 0, 1920, 1080);
        Rectangle2D rightScreen = new Rectangle2D(1920, 0, 1920, 1080);
        Rectangle2D evenlySplitWindow = new Rectangle2D(1520, 100, 800, 600);
        Rectangle2D offScreenWindow = new Rectangle2D(5000, 5000, 800, 600);

        assertEquals(0, ScreenBoundsCalculator.findLargestOverlapIndex(
                evenlySplitWindow, List.of(leftScreen, rightScreen)));
        assertEquals(0, ScreenBoundsCalculator.findLargestOverlapIndex(
                offScreenWindow, List.of(leftScreen, rightScreen)));
    }

    @Test
    public void findLargestOverlapIndex_noScreens_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                ScreenBoundsCalculator.findLargestOverlapIndex(new Rectangle2D(0, 0, 800, 600), List.of()));
    }

    // Boundary: a window that only touches a screen edge has zero overlap, so the later screen must not win.
    @Test
    public void findLargestOverlapIndex_windowOnlyTouchesLaterScreenEdge_keepsFirstScreen() {
        Rectangle2D leftScreen = new Rectangle2D(0, 0, 1920, 1080);
        Rectangle2D rightScreen = new Rectangle2D(1920, 0, 1920, 1080);
        Rectangle2D windowEndingAtSharedEdge = new Rectangle2D(1120, 100, 800, 600);

        assertEquals(0, ScreenBoundsCalculator.findLargestOverlapIndex(
                windowEndingAtSharedEdge, List.of(leftScreen, rightScreen)));
    }

    @Test
    public void findLargestOverlapIndex_singleScreen_returnsZero() {
        Rectangle2D onlyScreen = new Rectangle2D(0, 0, 1920, 1080);

        assertEquals(0, ScreenBoundsCalculator.findLargestOverlapIndex(
                new Rectangle2D(5000, 5000, 10, 10), List.of(onlyScreen)));
    }

    // Boundary value analysis for clampCoordinate: below, at and above each end, and an empty range.
    @Test
    public void clampCoordinate_coordinateInsideRange_isUnchanged() {
        assertEquals(500, ScreenBoundsCalculator.clampCoordinate(500, 0, 1000));
    }

    @Test
    public void clampCoordinate_coordinateAtOrBeyondRangeEnds_isLimitedToRange() {
        assertEquals(0, ScreenBoundsCalculator.clampCoordinate(0, 0, 1000));
        assertEquals(0, ScreenBoundsCalculator.clampCoordinate(-1, 0, 1000));
        assertEquals(1000, ScreenBoundsCalculator.clampCoordinate(1000, 0, 1000));
        assertEquals(1000, ScreenBoundsCalculator.clampCoordinate(1001, 0, 1000));
    }

    @Test
    public void clampCoordinate_rangeIsEmpty_returnsMinimum() {
        assertEquals(100, ScreenBoundsCalculator.clampCoordinate(500, 100, -50));
        assertEquals(100, ScreenBoundsCalculator.clampCoordinate(-500, 100, -50));
    }
}
