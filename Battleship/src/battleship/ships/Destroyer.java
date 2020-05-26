package battleship.ships;

import battleship.enumerators.ShipType;

import java.io.Serializable;

public class Destroyer extends Ship implements Serializable {

    /**
     * constructor
     */
    public Destroyer() {
        length = 2;
    }

    /**
     * override of a getShipType
     */
    @Override
    public ShipType getShipType() {
        return ShipType.DESTROYER;
    }

}
