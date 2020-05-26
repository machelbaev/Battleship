package battleship.helpers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertController {

    private Alert alert;

    public Alert getAlert() {
        return alert;
    }

    /**
     * Method to show alert to user
     * @param message message that should be displayed on alert
     */
    public void showAlert(String message, ButtonType... buttons) {
        alert = new Alert(Alert.AlertType.NONE, message, buttons);
        alert.showAndWait();
    }

}
