package helpers;

import battleship.enumerators.ShipType;
import battleship.helpers.PlayerRespond;
import battleship.ships.Ship;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerRespondTest {

    @Test
    void test1() {
        Ship ship = Ship.createShip(ShipType.CRUISER);
        PlayerRespond respond = new PlayerRespond(ship, PlayerRespond.Status.HIT);
        assertEquals(respond.getShip(), ship);
        assertEquals(respond.getStatus(), PlayerRespond.Status.HIT);
    }

}
