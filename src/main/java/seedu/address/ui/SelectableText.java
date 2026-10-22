package seedu.address.ui;

import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Text;

/**
 * Displays read-only text that users can select and copy.
 */
public class SelectableText extends TextArea {

    private static final double HEIGHT_BUFFER = 2;

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
        setPrefColumnCount(1);
        setPrefRowCount(1);
        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);
        getStyleClass().add("selectable-text");

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

        double horizontalInsets = snappedLeftInset() + snappedRightInset();
        double availableWidth = width - horizontalInsets;
        double wrappingWidth = isWrapText() && Double.isFinite(availableWidth) && availableWidth > 0
                ? availableWidth
                : 0;
        measuringText.setWrappingWidth(wrappingWidth);

        double verticalInsets = snappedTopInset() + snappedBottomInset();
        return Math.ceil(measuringText.getLayoutBounds().getHeight()) + verticalInsets + HEIGHT_BUFFER;
    }

    private void requestPreferredSizeLayout() {
        requestLayout();
        Parent parent = getParent();
        if (parent != null) {
            parent.requestLayout();
        }
    }

    private void forwardScrollToListView(ScrollEvent event) {
        Parent ancestor = getParent();
        while (ancestor != null && !(ancestor instanceof ListView<?>)) {
            ancestor = ancestor.getParent();
        }
        if (ancestor instanceof ListView<?> listView) {
            Parent virtualFlow = (Parent) listView.lookup(".virtual-flow");
            if (virtualFlow != null) {
                virtualFlow.getChildrenUnmodifiable().stream()
                        .filter(ScrollBar.class::isInstance)
                        .map(ScrollBar.class::cast)
                        .filter(scrollBar -> scrollBar.getOrientation() == Orientation.VERTICAL)
                        .filter(ScrollBar::isVisible)
                        .findFirst()
                        .ifPresent(scrollBar -> scrollVertically(scrollBar, event));
            }
        }
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
