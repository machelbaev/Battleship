package battleship.ships;

import battleship.enumerators.ShipType;

import java.io.Serializable;

public class EmptySea extends Ship implements Serializable {

    /**
     * constructor
     */
    public EmptySea() {
        length = 1;
        hit = new boolean[1];
    }

    /**
     * override of a shootAt method
     */
    @Override
    public void shootAt(int row, int column) {
        hit[0] = true;
    }

    /**
     * override of a isSunk method
     */
    @Override
    public boolean isSunk() {
        return false;
    }

    /**
     * override of a getShipType
     */
    @Override
    public ShipType getShipType() {
        return ShipType.EMPTY;
    }

}
