package battleship.interfaces;

public interface BattleshipGameDelegate {

    /**
     * send attack data to another player
     * @param x coordinate
     * @param y coordinate
     * @param id player id who send this
     */
    void makeShoot(int x, int y, String id);

    /**
     * allow to make shots to another player
     * @param id player id who send the confirmation
     */
    void confirmRespond(String id);

}
