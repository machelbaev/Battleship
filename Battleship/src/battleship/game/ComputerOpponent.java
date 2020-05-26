package battleship.game;

import battleship.interfaces.BattleshipGameDelegate;
import battleship.interfaces.Completion;
import battleship.interfaces.PlayerDelegate;
import battleship.enumerators.ShipType;
import battleship.helpers.PlayerRespond;
import battleship.ships.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class ComputerOpponent implements PlayerDelegate {

    /**
     * side of the field
     */
    private static final int SIDE = 10;

    /**
     * unique player id
     */
    private String id;

    private BattleshipGameDelegate delegate;

    public Ship[][] ships;

    /**
     * positions on opponent's field that available to shot
     */
    private final List<Integer> availablePositions;

    /**
     * instance of Random
     */
    private static final Random rnd = new Random();

    public void setDelegate(BattleshipGameDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * constructor
     */
    public ComputerOpponent() {
        ships = new Ship[SIDE][SIDE];
        availablePositions = new ArrayList<>();

        for (int i = 0; i < SIDE; ++i) {
            for (int j = 0; j < SIDE; ++j) {
                ships[i][j] = new EmptySea();
                availablePositions.add(i*SIDE + j);
            }
        }
        placeAllShipsRandomly();
    }

    /**
     * places all ships randomly
     */
    private void placeAllShipsRandomly() {
        Ship[] ships = new Ship[SIDE];
        for (int i = 0; i < ships.length; i++) {
            if (i == 0) {
                ships[i] = new Battleship();
            } else if (i == 1 || i == 2) {
                ships[i] = new Cruiser();
            } else if (i < 6) {
                ships[i] = new Destroyer();
            } else {
                ships[i] = new Submarine();
            }
        }
        for (Ship ship : ships) {
            boolean shipPlaced = false;
            while (!shipPlaced) {
                int row = rnd.nextInt(SIDE);
                int column = rnd.nextInt(SIDE);
                boolean isHorizontal = rnd.nextBoolean();
                shipPlaced = ship.okToPlaceShipAt(row, column, isHorizontal, this);
            }
        }
    }

    /**
     * checks whether the cell contain a ship part
     * @param row row
     * @param column column
     * @return whether the cell contain a ship part
     */
    public boolean isOccupied(int row, int column) {
        return !ships[row][column].getClass().equals(EmptySea.class);
    }

    /**
     * Remove cells around ship from available if ship was sunk
     * @param ship ship
     */
    private void surroundShip(Ship ship) {
        var coordinates = ship.getCoordinates();
        int x1 = coordinates[0];
        int y1 = coordinates[1];
        int x2 = coordinates[2];
        int y2 = coordinates[3];

        for (int i = x1; i <= min(x2 + 1, SIDE - 1); i++) {
            removeAvailablePosition(i, y1 - 1);
        }
        for (int i = y1; i <= min(y2 + 1, SIDE - 1); i++) {
            removeAvailablePosition(x2 + 1, i);
        }
        for (int i = max(x1 - 1, 0); i <= x2; i++) {
            removeAvailablePosition(i, y2 + 1);
        }
        for (int i = max(y1 - 1, 0); i <= y2; i++) {
            removeAvailablePosition(x1 - 1, i);
        }
    }

    /**
     * remove position from list of available to shot positions
     * @param x coordinate
     * @param y coordinate
     */
    private void removeAvailablePosition(int x, int y) {
        if (x >= 0 && x < SIDE && y >= 0 && y < SIDE) {
            int position = availablePositions.indexOf(x * SIDE + y);
            if (position > 0)
                availablePositions.remove(position);
        }
    }

    private void makeShot() {
        int shotPosition = rnd.nextInt(availablePositions.size());
        int shotCoords = availablePositions.get(shotPosition);
        availablePositions.remove(availablePositions.indexOf(shotCoords));
        int x = shotCoords / SIDE;
        int y = shotCoords % SIDE;
        delegate.makeShoot(x, y, id);
    }

    // Mark: - Player delegate implementation

    @Override
    public void allowToMakeShoots() {
        makeShot();
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void checkHit(int x, int y, Completion completion) {
        ships[x][y].shootAt(x, y);
        var status = ships[x][y].getShipType() == ShipType.EMPTY ? PlayerRespond.Status.MISS : PlayerRespond.Status.HIT;
//        return new PlayerRespond(ships[x][y], status);
        completion.execute(new PlayerRespond(ships[x][y], status));
    }

    @Override
    public void getRespond(PlayerRespond respond) {
        if (respond.getShip().isSunk()) {
            surroundShip(respond.getShip());
        }

        if (respond.getStatus() == PlayerRespond.Status.HIT) {
            allowToMakeShoots();
        } else {
            delegate.confirmRespond(id);
        }
    }
}
