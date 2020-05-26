package battleship.ships;

import battleship.game.ComputerOpponent;
import battleship.enumerators.ShipType;

import java.io.Serializable;

public abstract class Ship implements Serializable {

    /**
     * bow row
     */
    private int bowRow;

    /**
     * bow column
     */
    private int bowColumn;

    /**
     * length of the ship
     */
    protected int length;

    /**
     * shows whether the ship horizontal
     */
    private boolean horizontal;

    /**
     * hits in the ship
     */
    public boolean [] hit = new boolean[4];

    /**
     * getter of the length field
     * @return length of the ship
     */
    public int getLength() {
        return length;
    }

    /**
     * getter of the borRow field
     * @return bowRow
     */
    public int getBowRow() {
        return bowRow;
    }

    /**
     * setter of the bowRow field
     * @param bowRow bowRow
     */
    public void setBowRow(int bowRow) {
        this.bowRow = bowRow;
    }

    /**
     * getter of the bowColumn field
     * @return bowColumn
     */
    public int getBowColumn() {
        return bowColumn;
    }

    /**
     * setter of the bowColumn
     * @param bowColumn bowColumn
     */
    public void setBowColumn(int bowColumn) {
        this.bowColumn = bowColumn;
    }

    /**
     * shows whether the ship horizontal
     * @return true if the ship horizontal
     */
    public boolean isHorizontal() {
        return horizontal;
    }

    /**
     * set value for horizontal field
     * @param horizontal value
     */
    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * shows type of the ship
     * @return type of the ship
     */
    public abstract ShipType getShipType();

    /**
     * shows is it ok to place the ship here
     * @param row row
     * @param column column
     * @param horizontal ship orientation
     * @param ocean ocean instance
     * @return true if it is ok to place ship here, false otherwise
     */
    public boolean okToPlaceShipAt(int row, int column, boolean horizontal, ComputerOpponent ocean) {
        if (ocean.isOccupied(row, column)) return false;
        int leftColumn = horizontal ? column + getLength() : column + 1;
        int bottomRow = horizontal ? row + 1 : row + getLength();
        if (leftColumn > 10 || bottomRow > 10) return false;
        for (int i = 0; i < getLength(); i++) {
            if (isCloseToOthers(horizontal ? row : row + i, horizontal ? column + i : column, ocean)) return false;
        }
        placeShipAt(row, column, horizontal, ocean);
        return true;
    }

    /**
     * shows whether the cells around contains the ships parts
     * @param row row
     * @param column column
     * @param ocean ocean instance
     * @return true if there are other ships around the cell, false otherwise
     */
    private boolean isCloseToOthers(int row, int column, ComputerOpponent ocean) {
        if (ocean.isOccupied(Math.max(row - 1, 0) , Math.max(column - 1, 0))) return true;
        if (ocean.isOccupied(Math.max(row - 1, 0) , column)) return true;
        if (ocean.isOccupied(Math.max(row - 1, 0) , Math.min(column + 1, 9))) return true;
        if (ocean.isOccupied(row, Math.min(column + 1, 9))) return true;
        if (ocean.isOccupied(Math.min(row + 1, 9), Math.min(column + 1, 9))) return true;
        if (ocean.isOccupied(Math.min(row + 1, 9), column)) return true;
        if (ocean.isOccupied(Math.min(row + 1, 9), Math.max(column - 1, 0))) return true;
        return ocean.isOccupied(row, Math.max(column - 1, 0));
    }

    /**
     * places the ship at the selected location
     * @param row row
     * @param column column
     * @param horizontal ship orientation
     * @param ocean ocean instance
     */
    private void placeShipAt(int row, int column, boolean horizontal, ComputerOpponent ocean) {
        setBowColumn(column);
        setBowRow(row);
        setHorizontal(horizontal);
        if (horizontal) {
            for (int i = column; i < column + getLength(); ++i) {
                ocean.ships[row][i] = this;
            }
        } else {
            for (int i = row; i < row + getLength(); ++i) {
                ocean.ships[i][column] = this;
            }
        }
    }

    /**
     * shows did the user hit the sunken ship or not
     * @param row row
     * @param column column
     */
    public void shootAt(int row, int column) {
        if (isSunk()) {
            return;
        }
        if (horizontal) {
            hit[column - getBowColumn()] = true;
        } else {
            hit[row - getBowRow()] = true;
        }
    }

    /**
     * shows whether the ship sunken
     * @return true if the ship is sunken, false otherwise
     */
    public boolean isSunk() {
        for (int i = 0; i < getLength(); ++i) {
            if (!hit[i]) {
                return false;
            }
        }
        return true;
    }

    public static Ship createShip(ShipType type) {
        switch (type) {
            case BATTLESHIP:
                return new Battleship();
            case DESTROYER:
                return new Destroyer();
            case CRUISER:
                return new Cruiser();
            case SUBMARINE:
                return new Submarine();
            default:
                return new EmptySea();
        }
    }

    public int[] getCoordinates() {
        int x1 = getBowRow();
        int y1 = getBowColumn();
        int x2 = getBowRow() + (isHorizontal() ? 0 : getLength() - 1);
        int y2 = getBowColumn() + (isHorizontal() ? getLength() - 1 : 0);
        return new int[] { x1, y1, x2, y2 };
    }

    /**
     * override of a toString method
     */
    @Override
    public String toString() {
        return getShipType().toString().toLowerCase();
    }
}
