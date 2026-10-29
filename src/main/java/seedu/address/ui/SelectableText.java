package seedu.address.ui;

import java.util.Optional;

import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

/**
 * Displays read-only text that users can select and copy.
 */
public class SelectableText extends TextArea {

    private static final String STYLE_CLASS = "selectable-text";
    private static final double HEIGHT_BUFFER = 2;
    private static final int PREFERRED_COLUMN_COUNT = 20;

    private final Text measuringText = new Text();
    private double lineSpacing;

    /**
     * Creates a transparent, wrapping text control that grows with its content.
     */
    public SelectableText() {
        setEditable(false);
        setWrapText(true);
        setFocusTraversable(false);
        setMinWidth(0);
        setMaxWidth(Double.MAX_VALUE);
        setPrefColumnCount(PREFERRED_COLUMN_COUNT);
        setPrefRowCount(1);
        // Matches the TextArea skin so each measured line is as tall as the rendered line.
        measuringText.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);
        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);
        getStyleClass().add(STYLE_CLASS);

        textProperty().addListener(observable -> requestPreferredSizeLayout());
        fontProperty().addListener(observable -> requestPreferredSizeLayout());
        focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (!isFocused) {
                deselect();
            }
        });
        addEventFilter(ScrollEvent.SCROLL, this::forwardScrollToListView);
    }

    /**
     * Sets the space between wrapped lines.
     *
     * @param lineSpacing The line spacing in pixels.
     */
    public void setLineSpacing(double lineSpacing) {
        assert lineSpacing >= 0 : "Line spacing must not be negative";
        this.lineSpacing = lineSpacing;
        requestPreferredSizeLayout();
    }

    /**
     * Returns the space between wrapped lines.
     */
    public double getLineSpacing() {
        return lineSpacing;
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

    @Override
    protected double computePrefHeight(double width) {
        measuringText.setFont(getFont());
        measuringText.setText(getText().isEmpty() ? " " : getText());
        measuringText.setLineSpacing(lineSpacing);

        measuringText.setWrappingWidth(calculateWrappingWidth(width));

        double verticalInsets = snappedTopInset() + snappedBottomInset();
        return Math.ceil(measuringText.getLayoutBounds().getHeight()) + verticalInsets + HEIGHT_BUFFER;
    }

    /**
     * Returns the width available for text inside {@code width}, or 0 (no wrapping) when wrapping is off or
     * the width is not a usable positive number.
     */
    private double calculateWrappingWidth(double width) {
        double availableWidth = width - (snappedLeftInset() + snappedRightInset());
        boolean canWrapWithin = isWrapText() && Double.isFinite(availableWidth) && availableWidth > 0;
        return canWrapWithin ? availableWidth : 0;
    }

    private void requestPreferredSizeLayout() {
        requestLayout();
        Parent parent = getParent();
        if (parent != null) {
            parent.requestLayout();
        }
    }

    private void forwardScrollToListView(ScrollEvent event) {
        findAncestorListView()
                .flatMap(SelectableText::findVisibleVerticalScrollBar)
                .ifPresent(scrollBar -> scrollVertically(scrollBar, event));
    }

    private Optional<ListView<?>> findAncestorListView() {
        Parent ancestor = getParent();
        while (ancestor != null && !(ancestor instanceof ListView<?>)) {
            ancestor = ancestor.getParent();
        }
        return Optional.ofNullable((ListView<?>) ancestor);
    }

    private static Optional<ScrollBar> findVisibleVerticalScrollBar(ListView<?> listView) {
        if (!(listView.lookup(UiPart.VIRTUAL_FLOW_SELECTOR) instanceof Parent virtualFlow)) {
            return Optional.empty();
        }
        return virtualFlow.getChildrenUnmodifiable().stream()
                .filter(ScrollBar.class::isInstance)
                .map(ScrollBar.class::cast)
                .filter(scrollBar -> scrollBar.getOrientation() == Orientation.VERTICAL)
                .filter(ScrollBar::isVisible)
                .findFirst();
    }

    private void scrollVertically(ScrollBar scrollBar, ScrollEvent event) {
        if (event.getDeltaY() < 0) {
            scrollBar.increment();
            event.consume();
        } else if (event.getDeltaY() > 0) {
            scrollBar.decrement();
            event.consume();
        }
    }
}
