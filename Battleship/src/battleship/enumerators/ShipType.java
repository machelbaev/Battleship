package battleship.enumerators;

public enum ShipType {
    BATTLESHIP(4, 1), CRUISER(3, 2), DESTROYER(2, 3), SUBMARINE(1, 4), EMPTY;

    /**
     * size of the ship
     */
    private int size;

    /**
     * available number to place ship on board
     */
    private int number;

    ShipType(int size, int number) {
        this.size = size;
        this.number = number;
    }

    ShipType() {}

    public int getSize() {
        return size;
    }

    /**
     * set initial values to play again
     */
    public void prepareForReuse() {
        switch (this) {
            case BATTLESHIP:
                number = 1;
                break;
            case CRUISER:
                number = 2;
                break;
            case DESTROYER:
                number = 3;
                break;
            default:
                number = 4;
                break;
        }
    }

    /**
     * decrease number of available ships
     */
    public void decreaseNumber() {
        number--;
    }

    /**
     * shows is current ship available to place on board
     * @return true if user can place ship of this type, false otherwise
     */
    public boolean isEnabled() {
        return number > 0;
    }

    /**
     * custom enumeration of ships
     * @return index of the ship
     */
    public int getIndex() {
        switch (this) {
            case BATTLESHIP:
                return 0;
            case CRUISER:
                return 1;
            case DESTROYER:
                return 2;
            default:
                return 3;
        }
    }

    /**
     * Ship at index
     * @param ind index
     * @return ship type
     */
    public static ShipType shipAt(int ind) {
        switch (ind % 4) {
            case 0:
                return BATTLESHIP;
            case 1:
                return CRUISER;
            case 2:
                return DESTROYER;
            default:
                return SUBMARINE;
        }
    }

    /**
     * Text title at ships layout screen
     * @return title
     */
    public String getTitle() {
        switch (this) {
            case BATTLESHIP:
                return number + " battleship" + (number != 1 ? "s" : "");
            case DESTROYER:
                return number + " destroyer" + (number != 1 ? "s" : "");
            case CRUISER:
                return number + " cruiser" + (number != 1 ? "s" : "");
            default:
                return number + " submarine" + (number != 1 ? "s" : "");
        }
    }

    /**
     * Number of all ships of this type
     * @return number
     */
    public int getAllNumber() {
        switch (this) {
            case BATTLESHIP:
                return 1;
            case CRUISER:
                return 2;
            case DESTROYER:
                return 3;
            default:
                return 4;
        }
    }

    /**
     * Method to get ship type using ship name
     * @param name ship name
     * @return ship type
     */
    public static ShipType getShipType(String name) {
        switch (name) {
            case "battleship":
                return BATTLESHIP;
            case "cruiser":
                return CRUISER;
            case "destroyer":
                return DESTROYER;
            case "submarine":
                return SUBMARINE;
            default:
                return EMPTY;
        }
    }
}
