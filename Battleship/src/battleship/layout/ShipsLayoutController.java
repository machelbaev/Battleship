package battleship.layout;

import battleship.*;
import battleship.enumerators.GameMode;
import battleship.enumerators.Screens;
import battleship.enumerators.ShipType;
import battleship.game.GameController;
import battleship.helpers.*;
import battleship.interfaces.Completion;
import battleship.ships.EmptySea;
import battleship.ships.Ship;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.IOException;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class ShipsLayoutController {

    // ----------------------------------------------------------------------------------------
    // MARK: - fields
    // ----------------------------------------------------------------------------------------

    /**
     * board dimensions
     */
    private final static int SIDE = 10;

    private boolean locatingShips;

    /**
     * coordinate of ship bow when it is locating
     */
    private Point highlightedCell;

    /**
     * panes of gridPane
     */
    private Pane[][] ocean;

    /**
     * type of current ship
     */
    private ShipType currentShip = ShipType.BATTLESHIP;

    /**
     * cell that shows possible ship location
     */
    private Pane[] possibleSelectedCells;

    private boolean locationAllowed;

    private Ship[][] ships;

    private GridPaneHandler delegate;

    private Text[] labels;

    private boolean layoutIsDone;

    private AlertController alertController;

    // ----------------------------------------------------------------------------------------
    // MARK: - FXML fields
    // ----------------------------------------------------------------------------------------

    @FXML
    private GridPane gridPane;

    @FXML
    private Text battleshipLabel;

    @FXML
    private Text destroyerLabel;

    @FXML
    private Text cruiserLabel;

    @FXML
    private Text submarineLabel;

    @FXML
    private Button nextButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button findServerButton;

    @FXML
    private Text status;

    // ----------------------------------------------------------------------------------------
    // MARK: - init
    // ----------------------------------------------------------------------------------------

    @FXML
    void initialize() {
        ocean = new Pane[SIDE][SIDE];
        ships = new Ship[SIDE][SIDE];
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                ships[i][j] = new EmptySea();
            }
        }

        delegate = new GridPaneHandler(gridPane);
        locatingShips = false;
        locationAllowed = true;

        possibleSelectedCells = new Pane[4];
        labels = new Text[] { battleshipLabel, cruiserLabel, destroyerLabel, submarineLabel };

        selectLabel(battleshipLabel);
        layoutIsDone = false;

        alertController = new AlertController();
        status.setText("Status: disconnected");
    }

    private void setUpPossibleCell(int ind) {
        possibleSelectedCells[ind] = new Pane();
        possibleSelectedCells[ind].setStyle(Constants.getPossibleShipStyle());
    }

    public void setup() {
        Main.getStage(Screens.LAYOUT).setOnCloseRequest(e -> {
            if (NetworkManager.getInstance() != null) {
                NetworkManager.getInstance().sendMessage(Constants.disconnect);
                try {
                    NetworkManager.getInstance().disconnect();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - setters
    // ----------------------------------------------------------------------------------------

    private void setLocatingShips(boolean value) {
        locatingShips = value;
        removeButton.setDisable(!value);
    }

    /**
     * put ship value in array and mark ship on grid pane
     * @param x coordinate
     * @param y coordinate
     * @param ship shit
     */
    private void setShips(int x, int y, Ship ship) {
        ships[x][y] = ship;
        Pane pane = new Pane();
        if (ship.getShipType() == ShipType.EMPTY) {
            pane.setStyle(Constants.getDisabledCellsStyle());
        } else {
            pane.setStyle(Constants.getShipStyle());
        }
        gridPane.add(pane, x, y);
        ocean[x][y] = pane;
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - getters
    // ----------------------------------------------------------------------------------------

    public Button getFindServerButton() {
        return findServerButton;
    }

    public Text getStatus() {
        return status;
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - mouse handlers
    // ----------------------------------------------------------------------------------------

    /**
     * show pointer if cell is empty or possible ship location if locatingShips == true
     * @param event mouse event
     */
    @FXML
    private void mouseEnteredCell(MouseEvent event) {
        clearGridPane();
        int x = delegate.calculateHorizontalPosition(event.getX());
        int y = delegate.calculateVerticalPosition(event.getY());

        if (!checkAvailability(x, y))
            return;

        if (locatingShips) {
            if (y == highlightedCell.getY()) {
                if (x - highlightedCell.getX() < 0 && highlightedCell.getX() + 1 >= currentShip.getSize()) {
                    for (int i = 1; i < currentShip.getSize(); i++) {
                        setUpPossibleCell(i - 1);
                        gridPane.add(possibleSelectedCells[i - 1], highlightedCell.getX() - i,
                                highlightedCell.getY());
                    }
                } else if (x - highlightedCell.getX() > 0 &&
                        SIDE - highlightedCell.getX() >= currentShip.getSize()) {
                    for (int i = 1; i < currentShip.getSize(); i++) {
                        setUpPossibleCell(i - 1);
                        gridPane.add(possibleSelectedCells[i - 1], highlightedCell.getX() + i,
                                highlightedCell.getY());
                    }
                }
            } else if (x == highlightedCell.getX()) {
                if (y - highlightedCell.getY() < 0 && highlightedCell.getY() + 1 >= currentShip.getSize()) {
                    for (int i = 1; i < currentShip.getSize(); i++) {
                        setUpPossibleCell(i - 1);
                        gridPane.add(possibleSelectedCells[i - 1], highlightedCell.getX(),
                                highlightedCell.getY() - i);
                    }
                } else if (y - highlightedCell.getY() > 0 &&
                        SIDE - highlightedCell.getY() >= currentShip.getSize()) {
                    for (int i = 1; i < currentShip.getSize(); i++) {
                        setUpPossibleCell(i - 1);
                        gridPane.add(possibleSelectedCells[i - 1], highlightedCell.getX(),
                                highlightedCell.getY() + i);
                    }
                }
            }
        } else {
            if (ocean[x][y] == null)
                delegate.addPointer(x, y);
        }
    }

    /**
     * remove pointer and possible cells if mouse exited the grid
     */
    @FXML
    private void mouseExitedFromGrid() {
        clearGridPane();
    }

    /**
     * select cell or place ship on mouse click
     * @param event mouse event
     */
    @FXML
    private void mouseClicked(MouseEvent event) {
        if (!locationAllowed )
            return;

        int y = delegate.calculateVerticalPosition(event.getY());
        int x = delegate.calculateHorizontalPosition(event.getX());

        if (!checkAvailability(x, y) || ocean[x][y] != null)
            return;

        if (locatingShips) {
            Ship ship = ships[highlightedCell.getX()][highlightedCell.getY()];
            if (y == highlightedCell.getY()) {
                if (x - highlightedCell.getX() < 0 && highlightedCell.getX() + 1 >= currentShip.getSize()) {
                    for (int i = 1; i < currentShip.getSize(); i++) {
                        setShips(highlightedCell.getX() - i, highlightedCell.getY(), ship);
                    }
                    placeShip(highlightedCell.getX() - currentShip.getSize() + 1, y, ship);
                } else if (x - highlightedCell.getX() > 0 &&
                        SIDE - highlightedCell.getX() >= currentShip.getSize()) {
                    for (int i = 1; i < currentShip.getSize(); i++) {
                        setShips(highlightedCell.getX() + i, highlightedCell.getY(), ship);
                    }
                    placeShip(highlightedCell.getX() + currentShip.getSize() - 1, y, ship);
                }
            } else if (x == highlightedCell.getX()) {
                if (y - highlightedCell.getY() < 0 && highlightedCell.getY() + 1 >= currentShip.getSize()) {
                    for (int i = 1; i < currentShip.getSize(); i++) {
                        setShips(highlightedCell.getX(), highlightedCell.getY() - i, ship);
                    }
                    placeShip(x, highlightedCell.getY() - currentShip.getSize() + 1, ship);
                } else if (y - highlightedCell.getY() > 0 &&
                        SIDE - highlightedCell.getY() >= currentShip.getSize()) {
                    for (int i = 1; i < currentShip.getSize(); i++) {
                        setShips(highlightedCell.getX(), highlightedCell.getY() + i, ship);
                    }
                    placeShip(x, highlightedCell.getY() + currentShip.getSize() - 1, ship);
                }
            }
        } else {
            setLocatingShips(true);
            highlightedCell = new Point(x, y);
            Ship ship = Ship.createShip(currentShip);
            setShips(x, y, ship);
            if (currentShip == ShipType.SUBMARINE)
                placeShip(x, y, ship);
        }
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - buttons' handlers
    // ----------------------------------------------------------------------------------------

    /**
     * change current ship type if new ship was selected
     * @param event mouse event
     */
    @FXML
    private void shipTypeSelected(MouseEvent event) {
        var previousType = currentShip;
        if (event.getSource().equals(battleshipLabel)) {
            currentShip = ShipType.BATTLESHIP;
        } else if (event.getSource().equals(destroyerLabel)) {
            currentShip = ShipType.DESTROYER;
        } else if (event.getSource().equals(cruiserLabel)) {
            currentShip = ShipType.CRUISER;
        } else {
            currentShip = ShipType.SUBMARINE;
        }
        if (previousType != currentShip && locatingShips) {
            removeShip();
        }
        selectLabel((Text) event.getSource());
    }

    /**
     * remove ship that player started to arrange
     */
    @FXML
    private void removeShip() {
        if (locatingShips) {
            setLocatingShips(false);
            int x = highlightedCell.getX();
            int y = highlightedCell.getY();
            gridPane.getChildren().remove(ocean[x][y]);
            highlightedCell = null;
            ocean[x][y] = null;
            clearGridPane();
        }
    }

    @FXML
    private void findServer() {
        Main.setPane(Screens.CONNECT_TO_SERVER);
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - screens navigation
    // ----------------------------------------------------------------------------------------

    /**
     * open the main menu and finish current game
     */
    @FXML
    private void goBack() {
        Main.backToMain();

        if (NetworkManager.getInstance() != null) {
            try {
                NetworkManager.getInstance().sendMessage(Constants.disconnect);
                NetworkManager.getInstance().disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * open the game screen
     */
    @FXML
    private void openGameScreen() {
        nextButton.setDisable(true);
        findServerButton.setDisable(true);
        if (Main.getMode() == GameMode.SINGLE) {
            Main.setPane(Screens.GAME);
            ((GameController) Main.getController(Screens.GAME)).setMyShips(ships);
        } else {
            NetworkManager.getInstance().sendMessage(Constants.ready);
            Completion completion = (message) -> {
                String[] data = ((String) message[0]).split(",");
                if (alertController.getAlert() != null)
                    alertController.getAlert().hide();
                String alertMessage = "Your opponent is " + data[2] + ".\nYou go " + data[1] + ".";

                Main.setPane(Screens.GAME);
                // setup game controller
                var gameController = ((GameController) Main.getController(Screens.GAME));
                gameController.initialize();
                gameController.setup();
                gameController.setMyShips(ships);
                gameController.setId(data[0]);
                gameController.printLog(alertMessage);
                gameController.setOpponentName(data[2]);
                NetworkManager.getInstance().startGame();

                if (data[1].equals(Constants.first))
                    gameController.allowToMakeShoots();

            };
            status.setText("Status: waiting opponent...");
            NetworkManager.getInstance().getMessage(completion);
        }
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - support methods
    // ----------------------------------------------------------------------------------------

    /**
     * check is ship can be placed from highlighted cell to cell at (x, y)
     * @param x coordinate
     * @param y coordinate
     * @return true if ship can be placed, false otherwise
     */
    private boolean checkAvailability(int x, int y) {
        if (locatingShips) {
            if (y == highlightedCell.getY()) {
                if (x - highlightedCell.getX() < 0 && highlightedCell.getX() + 1 >= currentShip.getSize()) {
                    for (int i = 1; i < currentShip.getSize(); i++) {
                        if (ocean[highlightedCell.getX() - i][highlightedCell.getY()] != null)
                            return false;
                    }
                } else if (x - highlightedCell.getX() > 0 &&
                        SIDE - highlightedCell.getX() >= currentShip.getSize()) {
                    for (int i = 1; i < currentShip.getSize(); i++) {
                        if (ocean[highlightedCell.getX() + i][highlightedCell.getY()] != null)
                            return false;
                    }
                }
            } else if (x == highlightedCell.getX()) {
                if (y - highlightedCell.getY() < 0 && highlightedCell.getY() + 1 >= currentShip.getSize()) {
                    for (int i = 1; i < currentShip.getSize(); i++) {
                        if (ocean[highlightedCell.getX()][highlightedCell.getY() - i] != null)
                            return false;
                    }
                } else if (y - highlightedCell.getY() > 0 &&
                        SIDE - highlightedCell.getY() >= currentShip.getSize()) {
                    for (int i = 1; i < currentShip.getSize(); i++) {
                        if (ocean[highlightedCell.getX()][highlightedCell.getY() + i] != null)
                            return false;
                    }
                }
            }
        }
        return ocean[x][y] == null;
    }

    private void clearGridPane() {
        delegate.removePointer();
        for (Pane possibleSelectedCell : possibleSelectedCells) {
            gridPane.getChildren().removeAll(possibleSelectedCell);
        }
    }

    /**
     * make cells around places ship disabled
     */
    private void surroundShip(int x1, int y1, int x2, int y2) {
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if (y1 > y2) {
            int temp = y1;
            y1 = y2;
            y2 = temp;
        }

        for (int i = x1; i <= min(x2 + 1, SIDE - 1); i++) {
            markCellAsDisabled(i, y1 - 1);
        }
        for (int i = y1; i <= min(y2 + 1, SIDE - 1); i++) {
            markCellAsDisabled(x2 + 1, i);
        }
        for (int i = max(x1 - 1, 0); i <= x2; i++) {
            markCellAsDisabled(i, y2 + 1);
        }
        for (int i = max(y1 - 1, 0); i <= y2; i++) {
            markCellAsDisabled(x1 - 1, i);
        }
    }

    private void markCellAsDisabled(int x, int y) {
        if (x >= 0 && x < SIDE && y >= 0 && y < SIDE && ocean[x][y] == null) {
            setShips(x, y, new EmptySea());
        }
    }

    /**
     * handles the ship placing
     * @param lastX ship's last x coordinate
     * @param lastY ship's last y coordinate
     * @param ship ship to place
     */
    private void placeShip(int lastX, int lastY, Ship ship) {
        setLocatingShips(false);
        currentShip.decreaseNumber();
        labels[currentShip.getIndex()].setText(currentShip.getTitle());
        surroundShip(highlightedCell.getX(), highlightedCell.getY(), lastX, lastY);

        ship.setHorizontal(lastY != highlightedCell.getY());
        ship.setBowRow(Math.min(highlightedCell.getX(), lastX));
        ship.setBowColumn(Math.min(highlightedCell.getY(), lastY));

        if (!currentShip.isEnabled()) {
            int ind = currentShip.getIndex();
            labels[ind].setDisable(true);
            labels[ind].setFill(Color.GRAY);
            ind++;
            while (!ShipType.shipAt(ind).equals(currentShip)) {
                if (ShipType.shipAt(ind).isEnabled()) {
                    currentShip = ShipType.shipAt(ind);
                    selectLabel(labels[ind % 4]);
                    ind++;
                    break;
                }
                ind++;
            }
            if (ShipType.shipAt(ind).equals(currentShip)) {
                layoutIsDone = true;
                shouldEnableNextButton();
                selectLabel(null);
                locationAllowed = false;
            }
        }
    }

    private void selectLabel(Text text) {
        for (Text label : labels) {
            label.setFont(Font.font("SF Pro Text Regular", FontWeight.SEMI_BOLD, 25));
        }
        if (text != null)
            text.setFont(Font.font("SF Pro Text Regular", FontWeight.SEMI_BOLD, 38));
    }

    public void shouldEnableNextButton() {
        if (Main.getMode() == GameMode.SINGLE) {
            nextButton.setDisable(!layoutIsDone);
        } else {
            if (layoutIsDone
                    && NetworkManager.getInstance() != null
                    && NetworkManager.getInstance().isConnected()) {
                nextButton.setDisable(false);
            }

            if (NetworkManager.getInstance() != null && NetworkManager.getInstance().isConnected()) {
                status.setText("Status: connected");
            }
        }
    }

}
