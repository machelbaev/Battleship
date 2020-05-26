package battleship.ships;

import battleship.enumerators.ShipType;

import java.io.Serializable;

public class Battleship extends Ship implements Serializable {

    /**
     * constructor
     */
    public Battleship() {
        length = 4;
    }

    /**
     * override of a getShipType
     */
    @Override
    public ShipType getShipType() {
        return ShipType.BATTLESHIP;
    }
}
