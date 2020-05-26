package battleship.interfaces;

import battleship.helpers.PlayerRespond;

public interface PlayerDelegate {

    /**
     * allow player to make shots
     */
    void allowToMakeShoots();

    /**
     * set unique id to player
     * @param id unique string
     */
    void setId(String id);

    /**
     * check is opponent hit player's ship
     * @param x coordinate
     * @param y coordinate
     */
    void checkHit(int x, int y, Completion completion);

    /**
     * get respond about attack and handle it
     * @param respond opponent's respond
     */
    void getRespond(PlayerRespond respond);

}
