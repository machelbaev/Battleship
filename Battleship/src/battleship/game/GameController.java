package battleship.game;

import battleship.Main;
import battleship.enumerators.GameMode;
import battleship.enumerators.Screens;
import battleship.gameOver.GameOverController;
import battleship.helpers.*;
import battleship.enumerators.ShipType;
import battleship.interfaces.BattleshipGameDelegate;
import battleship.interfaces.Completion;
import battleship.interfaces.PlayerDelegate;
import battleship.ships.EmptySea;
import battleship.ships.Ship;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class GameController implements PlayerDelegate {

    // ----------------------------------------------------------------------------------------
    // MARK: - fields
    // ----------------------------------------------------------------------------------------

    private Ship[][] myShips;

    private Pane[][] myPanes;

    private Pane[][] opponentPanes;

    private static final int SIDE = 10;

    private GridPaneHandler gridDelegate;

    private BattleshipGameDelegate gameDelegate;

    private String id;

    private boolean isAllowedToShot;

    private int shotsFired;

    private int opponentShotsFired;

    private Point shotCell;

    private int[] myShipsStat;

    private int[] opShipsStat;

    private List<Character> alphabet;

    private Logger myLogger;

    private Logger opLogger;

    private String opponentName;

    private Text[] myShipLabels;

    private Text[] opShipLabels;

    // ----------------------------------------------------------------------------------------
    // MARK: - FXML fields
    // ----------------------------------------------------------------------------------------

    @FXML private GridPane myGrid;

    @FXML private GridPane opponentGrid;

    @FXML private TextArea logArea;

    @FXML private Text myShots;

    @FXML private Text opponentShots;

    @FXML private Text myBattleship;

    @FXML private Text myDestroyers;

    @FXML private Text myCruisers;

    @FXML private Text mySubmarines;

    @FXML private Text opBattleship;

    @FXML private Text opCruisers;

    @FXML private Text opDestroyers;

    @FXML private Text opSubmarines;

    @FXML private TextField rowInput;

    @FXML private TextField columnInput;

    // ----------------------------------------------------------------------------------------
    // MARK: - init
    // ----------------------------------------------------------------------------------------

    @FXML
    public void initialize() {
        logArea.setWrapText(true);
        logArea.setStyle(Constants.getLogAreaStyle());
        logArea.setEditable(false);

        myLogger = new Logger("You", logArea);
        opLogger = new Logger("Opponent", logArea);
        opponentName = "Opponent";

        myShipsStat = new int[]{ 1, 2, 3, 4 };
        opShipsStat = new int[]{ 1, 2, 3, 4 };

        shotsFired = 0;
        opponentShotsFired = 0;

        alphabet = new ArrayList<>();
        for (char i = 'a'; i <= 'j'; i++) {
            alphabet.add(i);
        }

        myPanes = new Pane[SIDE][SIDE];
        opponentPanes = new Pane[SIDE][SIDE];

        gridDelegate = new GridPaneHandler(opponentGrid);

        if (Main.getMode() == GameMode.SINGLE) {
            var opponent = new ComputerOpponent();
            gameDelegate = new BattleshipGame(this, opponent);
            opponent.setDelegate(gameDelegate);
            isAllowedToShot = true;
        } else {
            gameDelegate = NetworkManager.getInstance();
            NetworkManager.getInstance().setGameController(this);
            isAllowedToShot = false;
        }

        myShipLabels = new Text[] {myBattleship, myCruisers, myDestroyers, mySubmarines};
        opShipLabels = new Text[] {opBattleship, opCruisers, opDestroyers, opSubmarines};
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - setters
    // ----------------------------------------------------------------------------------------

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
        opLogger = new Logger(opponentName, logArea);
    }

    public void setup() {
        Main.getStage(Screens.GAME).setOnCloseRequest(e -> {
            e.consume();
            finishGame();
        });
    }

    /**
     * arrange player ships on board
     * @param myShips player ships
     */
    public void setMyShips(Ship[][] myShips) {
        this.myShips = new Ship[SIDE][SIDE];
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (myShips[i][j] != null && myShips[i][j].getShipType() != ShipType.EMPTY)
                    addShipCellAt(i, j);
                else
                    myShips[i][j] = new EmptySea();
                this.myShips[i][j] = myShips[i][j];
            }
        }
    }

    /**
     * setter for field shotsFires
     * @param shotsFired number of shots
     */
    private void setShotsFired(int shotsFired) {
        this.shotsFired = shotsFired;
        myShots.setText("Number of shots: " + shotsFired);
    }

    /**
     * setter for field opponentShotsFired
     * @param opponentShotsFired number of shots
     */
    private void setOpponentShotsFired(int opponentShotsFired) {
        this.opponentShotsFired = opponentShotsFired;
        opponentShots.setText("Number of shots: " + opponentShotsFired);
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - getters
    // ----------------------------------------------------------------------------------------

    private Ship getMyShip(int x, int y) {
        return myShips[x][y];
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - buttons' handlers
    // ----------------------------------------------------------------------------------------

    @FXML
    private void makeShotFromButton() {
        int x, y;
        if (rowInput.getText().length() == 1 && alphabet.contains((rowInput.getText().charAt(0)))) {
            x = alphabet.indexOf((rowInput.getText().charAt(0)));
        } else {
            myLogger.invalidAction();
            return;
        }
        try {
            y = Integer.parseInt(columnInput.getText());
            if (y > 0 && y < 11)
                makeShot(y - 1, x);
            else
                throw new IllegalArgumentException();
        } catch (IllegalArgumentException ex) {
            myLogger.invalidAction();
        }
    }

    @FXML
    private void finishGame() {
        if (Main.getMode() == GameMode.SINGLE)
            Main.backToMain();
        else
            NetworkManager.getInstance().sendMessage(Constants.disconnect);
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - mouse handlers
    // ----------------------------------------------------------------------------------------

    /**
     * mouse move event handler, adds pointer if possible
     * @param event mouse event
     */
    @FXML
    private void mouseMoved(MouseEvent event) {
        gridDelegate.removePointer();

        int x = gridDelegate.calculateHorizontalPosition(event.getX());
        int y = gridDelegate.calculateVerticalPosition(event.getY());

        if (opponentPanes[x][y] != null)
            return;

        gridDelegate.addPointer(x, y);
    }

    /**
     * Mouse exit from grid pane event handler
     */
    @FXML
    void mouseExit() {
        gridDelegate.removePointer();
    }

    /**
     * Mouse click handler, player make a shot if possible
     * @param event mouse event
     */
    @FXML
    void cellSelected(MouseEvent event) {
        int x = gridDelegate.calculateHorizontalPosition(event.getX());
        int y = gridDelegate.calculateVerticalPosition(event.getY());
        makeShot(x, y);
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - logging
    // ----------------------------------------------------------------------------------------

    /**
     * send data to logger according to shot status
     * @param logger logger
     * @param status player respond status that has 2 states: miss and hit
     * @param ship ship at cell
     */
    private void printLog(Logger logger, PlayerRespond.Status status, Ship ship, Point point) {
        if (status == PlayerRespond.Status.HIT) {
            if (ship.isSunk())
                logger.sink(ship.toString(), point);
            else
                logger.hit(point);
        } else {
            logger.missShot(point);
        }
    }

    /**
     * send message to logger to print it in log area
     * @param message message that should be printed
     */
    public void printLog(String message) {
        logArea.setText(message + "\n");
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - support methods
    // ----------------------------------------------------------------------------------------

    private void sinkMyShip(ShipType type) {
        sinkShip(type, myShipsStat, myShipLabels);
    }

    private void sinkOpShip(ShipType type) {
        sinkShip(type, opShipsStat, opShipLabels);
    }

    /**
     * change info about sank ships at info grid
     * @param type ship that was sunk
     * @param shipsStat ship statistics
     * @param shipLabels ship labels
     */
    private void sinkShip(ShipType type, int[] shipsStat, Text[] shipLabels) {
        int i = type.getIndex();
        shipsStat[i]--;
        shipLabels[i].setText(shipsStat[i] + "/" + type.getAllNumber());
        if (shipsStat[i] == 0)
            checkIsEnd(shipsStat);
    }

    /**
     * check is the game over
     * @param shipsStat ship statistics
     */
    private void checkIsEnd(int[] shipsStat) {
        for (var stat : shipsStat) {
            if (stat != 0)
                return;
        }

        if (Main.getMode() == GameMode.MULTIPLAYER) {
            try {
                NetworkManager.getInstance().sendMessage(Constants.gameOver);
                NetworkManager.getInstance().disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        var gameOverController = ((GameOverController) Main.getController(Screens.GAME_OVER));
        if (Arrays.equals(shipsStat, myShipsStat)) {
            gameOverController.setLoseTitle();
            ++opponentShotsFired;
        } else {
            gameOverController.setWinTitle();
        }
        gameOverController.setPlayerStat(shotsFired);
        gameOverController.setOpponentStat(opponentShotsFired, opponentName);
        Main.setPane(Screens.GAME_OVER);
    }

    /**
     * Place ship at cell
     * @param x coordinate
     * @param y coordinate
     */
    private void addShipCellAt(int x, int y) {
        Pane pane = new Pane();
        pane.setStyle(Constants.getShipStyle());
        myGrid.add(pane, x, y);
        myPanes[x][y] = pane;
    }

    /**
     * make cells around the ship disabled if the ship was sunk
     * @param ship ship
     * @param pane grid pane
     */
    private void surroundShip(Ship ship, GridPane pane) {
        var coordinates = ship.getCoordinates();
        int x1 = coordinates[0];
        int y1 = coordinates[1];
        int x2 = coordinates[2];
        int y2 = coordinates[3];

        for (int i = x1; i <= min(x2 + 1, SIDE - 1); i++) {
            markField(pane, PlayerRespond.Status.MISS, i, y1 - 1);
        }
        for (int i = y1; i <= min(y2 + 1, SIDE - 1); i++) {
            markField(pane, PlayerRespond.Status.MISS, x2 + 1, i);
        }
        for (int i = max(x1 - 1, 0); i <= x2; i++) {
            markField(pane, PlayerRespond.Status.MISS, i, y2 + 1);
        }
        for (int i = max(y1 - 1, 0); i <= y2; i++) {
            markField(pane, PlayerRespond.Status.MISS, x1 - 1, i);
        }
    }

    /**
     * Change field cell color during the game
     * @param grid grid pane
     * @param status shot status
     * @param x coordinate
     * @param y coordinate
     */
    private void markField(GridPane grid, PlayerRespond.Status status, int x, int y) {
        if (x >= 0 && x < SIDE && y >= 0 && y < SIDE) {
            var panes = grid.equals(myGrid) ? myPanes : opponentPanes;
            if (panes[x][y] != null) {
                grid.getChildren().remove(panes[x][y]);
            }
            Pane pane = new Pane();
            if (status == PlayerRespond.Status.MISS) {
                pane.setStyle(Constants.getDisabledCellsStyle());
            } else {
                pane.setStyle(Constants.getHitStyle());
            }
            grid.add(pane, x, y);
            panes[x][y] = pane;
        }
    }

    /**
     * player makes a shot
     * @param x coordinate
     * @param y coordinate
     */
    private void makeShot(int x, int y) {
        if (!isAllowedToShot) {
            myLogger.opponentsTurn();
            return;
        }
        if (opponentPanes[x][y] != null) {
            myLogger.notAllowedShoot(new Point(x, y));
            return;
        }

        setShotsFired(shotsFired + 1);
        isAllowedToShot = false;
        shotCell = new Point(x, y);
        gameDelegate.makeShoot(x, y, id);
    }

    // ----------------------------------------------------------------------------------------
    // Mark: - Player delegate implementation
    // ----------------------------------------------------------------------------------------

    @Override
    public void allowToMakeShoots() {
        isAllowedToShot = true;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void checkHit(int x, int y, Completion completion) {
        var ship = getMyShip(x, y);
        ship.shootAt(x, y);
        var status = ship.getShipType() == ShipType.EMPTY ? PlayerRespond.Status.MISS : PlayerRespond.Status.HIT;

        // change ui from main thread
        Platform.runLater(()->{
            printLog(opLogger, status, ship, new Point(x, y));
            if (ship.isSunk()) {
                surroundShip(getMyShip(x, y), myGrid);
                sinkMyShip(ship.getShipType());
            }
            markField(myGrid, status, x, y);
            setOpponentShotsFired(opponentShotsFired + 1);
        });

        var resp = new PlayerRespond(ship, status);
        completion.execute(resp);
    }

    @Override
    public void getRespond(PlayerRespond respond) {
        int x = shotCell.getX();
        int y = shotCell.getY();

        PlayerRespond.Status status = respond.getStatus();
        Ship ship = respond.getShip();

        // change ui from main thread
        Platform.runLater(()->{
            markField(opponentGrid, status, x, y);
            if (ship.isSunk()) {
                surroundShip(ship, opponentGrid);
                sinkOpShip(ship.getShipType());
            }

            printLog(myLogger, status, ship, shotCell);
        });

        if (status == PlayerRespond.Status.HIT) {
            allowToMakeShoots();
        } else {
            gameDelegate.confirmRespond(id);
        }

    }
}