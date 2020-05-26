package battleship.ships;

import battleship.enumerators.ShipType;

import java.io.Serializable;

public class Submarine extends Ship implements Serializable {

    /**
     * constructor
     */
    public Submarine() {
        length = 1;
    }

    /**
     * override of a getShipType
     */
    @Override
    public ShipType getShipType() {
        return ShipType.SUBMARINE;
    }

}
