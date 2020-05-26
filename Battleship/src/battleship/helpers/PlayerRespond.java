package battleship.helpers;

import battleship.enumerators.ShipType;
import battleship.ships.Ship;

public class PlayerRespond {

    /**
     * shot result
     */
    public enum Status {
        HIT, MISS
    }

    // MARK: - fields

    /**
     * cell that was attacked
     */
    private final Ship ship;

    /**
     * shot result
     */
    private final Status status;

    // MARK: - init

    public PlayerRespond(Ship ship, Status status) {
        this.ship = ship;
        this.status = status;
    }

    public PlayerRespond(String str) {
        String[] data = str.split(",");
        status = data[1].equals("HIT") ? Status.HIT : Status.MISS;
        ship = Ship.createShip(ShipType.getShipType(data[2]));
        ship.setBowRow(Integer.parseInt(data[3]));
        ship.setBowColumn(Integer.parseInt(data[4]));
        ship.setHorizontal(Boolean.parseBoolean(data[5]));
        for (int i = 0; i < ship.getLength(); i++) {
            ship.hit[i] = Boolean.parseBoolean(data[6 + i]);
        }
    }

    // MARK: - getters

    public Ship getShip() {
        return ship;
    }

    public Status getStatus() {
        return status;
    }

    // MARK: - toString

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("PlayerRespond," +
                status + "," +
                ship.toString() + "," +
                ship.getBowRow() + "," +
                ship.getBowColumn() + "," +
                ship.isHorizontal() + ",");

        for (int i = 0; i < ship.getLength(); i++) {
            result.append(ship.hit[i]).append(i == ship.getLength() - 1 ? "" : ",");
        }

        return result.toString();
    }
}
