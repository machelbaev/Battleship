package battleship.ships;

import battleship.enumerators.ShipType;

import java.io.Serializable;

public class Cruiser extends Ship implements Serializable {

    /**
     * constructor
     */
    public Cruiser() {
        length = 3;
    }

    /**
     * override of a getShipType
     */
    @Override
    public ShipType getShipType() {
        return ShipType.CRUISER;
    }
}
