package ships;

import battleship.enumerators.ShipType;
import battleship.game.ComputerOpponent;
import battleship.ships.Destroyer;
import battleship.ships.Ship;
import battleship.ships.Submarine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShipTest {

    private static Destroyer destroyer;
    private static ComputerOpponent computerOpponent;

    @BeforeAll
    public static void init() {
        Ship[][] ships = new Ship[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                ships[i][j] = Ship.createShip(ShipType.EMPTY);
            }
        }

        destroyer = new Destroyer();
        destroyer.setBowColumn(0);
        destroyer.setBowRow(0);
        destroyer.setHorizontal(true);
        ships[0][0] = destroyer;
        ships[0][1] = destroyer;

        computerOpponent = new ComputerOpponent();
        computerOpponent.ships = ships;
    }

    @Test
    void test1() {
        var ship = new Submarine();
        assertFalse(ship.okToPlaceShipAt(1, 0, true, computerOpponent));
    }

    @Test
    void test2() {
        destroyer.shootAt(0, 0);
        assertFalse(destroyer.isSunk());
        destroyer.shootAt(0, 1);
        assertTrue(destroyer.isSunk());
    }

    @Test
    void test3() {
        var coord = destroyer.getCoordinates();
        assertEquals(coord[0], 0);
        assertEquals(coord[1], 0);
        assertEquals(coord[2], 0);
        assertEquals(coord[3], 1);
    }

}
