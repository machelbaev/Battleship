package enumerators;

import battleship.enumerators.ShipType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShipTypeTest {

    ShipType ship = ShipType.CRUISER;

    @Test
    void test1() {
        assertEquals(ship.toString(), "CRUISER");
    }

    @Test
    void test2() {
        assertEquals(ship.getSize(), 3);
    }

    @Test
    void test3() {
        ship.decreaseNumber();
        assertTrue(ship.isEnabled());
        ship.decreaseNumber();
        assertFalse(ship.isEnabled());
    }

    @Test
    void test4() {
        int index = ship.getIndex();
        assertEquals(index, 1);
        assertEquals(ShipType.shipAt(index), ship);
    }

    @Test
    void test5() {
        assertEquals(ship.getTitle(), "0 cruisers");
    }

    @Test
    void test6() {
        ship.prepareForReuse();
        assertTrue(ship.isEnabled());
    }

}
