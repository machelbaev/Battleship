package battleship.helpers;

/**
 * styles that used in game
 */
public class Constants {

    // MARK: - network constants

    public static final String ok = "OK";

    public static final String full = "FULL";

    public static final String allowToMakeShoots = "allowToMakeShoots";

    public static final String checkHit = "checkHit";

    public static final String playerRespond = "PlayerRespond";

    public static final String makeShoot = "makeShoot";

    public static final String confirmRespond = "confirmRespond";

    public static final String gameOver = "gameOver";

    public static final String ready = "ready";

    public static final String first = "first";

    public static final String second = "second";

    public static final String username = "username";

    public static final String disconnect = "disconnect";

    // MARK: - style

    public static String getShipStyle() {
        return "-fx-background-color:  #0E3F5F";
    }

    public static String getPossibleShipStyle() {
        return "-fx-background-color: #0E3F5F; -fx-opacity: 0.5";
    }

    public static String getDisabledCellsStyle() {
        return "-fx-background-color:  #69A8B6; -fx-opacity: 0.5";
    }

    public static String getPointerStyle() {
        return "-fx-background-color: lightBlue; -fx-opacity: 0.7";
    }

    public static String getLogAreaStyle() {
        return "-fx-control-inner-background: #BEDDDF";
    }

    public static String getHitStyle() { return "-fx-background-color: #CF8556"; }

}
