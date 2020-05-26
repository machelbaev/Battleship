package battleship.gameOver;

import battleship.Main;
import battleship.enumerators.GameMode;
import battleship.enumerators.Screens;
import battleship.helpers.Constants;
import battleship.helpers.NetworkManager;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.io.IOException;

public class GameOverController {

    @FXML
    private Text title;

    @FXML
    private Text opponentStat;

    @FXML
    private Text playerStat;

    /**
     * return to main menu
     */
    @FXML
    void backToMain() {
        Main.backToMain();
    }

    public void setWinTitle() {
        title.setText("You win! \nCongratulations");
    }

    public void setLoseTitle() {
        title.setText("You lost \nTry again!");
    }

    public void setPlayerStat(int value) {
        playerStat.setText("You did " + value + " shoots");
    }

    public void setOpponentStat(int value, String name) {
        opponentStat.setText(name + " did " + value + " shoots");
    }

}
