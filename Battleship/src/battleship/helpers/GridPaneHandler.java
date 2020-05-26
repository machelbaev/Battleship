package battleship.helpers;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * class that helps to handle grid pane
 */
public class GridPaneHandler {

    /**
     * field dimension
     */
    private static final int SIDE = 10;

    /**
     * shows selected cell on grid
     */
    private final Pane pointer;

    /**
     * grid pane to handle
     */
    private final GridPane gridPane;

    public GridPaneHandler(GridPane gridPane) {
        this.gridPane = gridPane;
        pointer = new Pane();
        pointer.setStyle(Constants.getPointerStyle());
    }

    /**
     * horizontal value from 0 to 9 according to mouse position
     * @param position mouse x position
     * @return value from 0 to 9
     */
    public int calculateHorizontalPosition(double position) {
        double cellWidth = gridPane.getWidth() / SIDE;
        for (int i = 0; i < SIDE; i++) {
            if (position > i * cellWidth && position <= cellWidth * (i + 1)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * vertical value from 0 to 9 according to mouse position
     * @param position mouse y position
     * @return value from 0 to 9
     */
    public int calculateVerticalPosition(double position) {
        double cellHeight = gridPane.getHeight() / SIDE;
        for (int i = 0; i < SIDE; i++) {
            if (position > i * cellHeight && position <= cellHeight * (i + 1)) {
                return i;
            }
        }
        return 0;
    }

    public void addPointer(int x, int y) {
        removePointer();
        gridPane.add(pointer, x, y);
    }

    public void removePointer() {
        gridPane.getChildren().remove(pointer);
    }

}
