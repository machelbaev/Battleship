package battleship.menu;

import battleship.Main;
import battleship.enumerators.GameMode;
import battleship.enumerators.Screens;
import battleship.layout.ShipsLayoutController;
import javafx.fxml.FXML;

public class MainMenuController {

    @FXML
    private void startSingleplayer() {
        Main.setMode(GameMode.SINGLE);
        openLayoutScreen(false);
    }

    @FXML
    private void startMultiplayer() {
        Main.setMode(GameMode.MULTIPLAYER);
        openLayoutScreen(true);
    }

    private void openLayoutScreen(boolean showItems) {
        Main.setPane(Screens.LAYOUT);
        var layoutController = ((ShipsLayoutController) Main.getController(Screens.LAYOUT));
        layoutController.getFindServerButton().setVisible(showItems);
        layoutController.getStatus().setVisible(showItems);
        layoutController.setup();
    }

}