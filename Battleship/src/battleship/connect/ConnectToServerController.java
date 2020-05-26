package battleship.connect;

import battleship.Main;
import battleship.enumerators.Screens;
import battleship.helpers.AlertController;
import battleship.helpers.NetworkManager;
import battleship.helpers.Response;
import battleship.layout.ShipsLayoutController;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ConnectToServerController {

    @FXML
    private TextField hostTextField;

    @FXML
    private TextField portTextField;

    @FXML
    private TextField nameTextField;

    private AlertController alertController;

    @FXML
    void initialize() {
        alertController = new AlertController();
    }

    /**
     * Method to connect to server
     */
    @FXML
    private void connect() {
        if (hostTextField.getText().isEmpty()
                || portTextField.getText().isEmpty()
                || nameTextField.getText().isEmpty()) {
            alertController.showAlert("Some fields are empty. Please, input missing data!", ButtonType.OK);
            return;
        }

        if (nameTextField.getText().length() < 3) {
            alertController.showAlert("Username must contain at least 3 symbols!", ButtonType.OK);
            return;
        }

        try {
            int port = Integer.parseInt(portTextField.getText());
            var response = NetworkManager.getInstance().startConnection(hostTextField.getText(), port, nameTextField.getText());
            if (response.getStatus() == Response.ResponseStatus.SUCCESS) {
                ((ShipsLayoutController) Main.getController(Screens.LAYOUT)).shouldEnableNextButton();
                ((ShipsLayoutController) Main.getController(Screens.LAYOUT)).getFindServerButton().setDisable(true);
                cancel(); // return to layout screen
            } else {
                alertController.showAlert("Server is full. Try to find another server or create a new one!", ButtonType.OK);
            }
        } catch (IOException | NumberFormatException ex) {
            alertController.showAlert("Server with these port and host does not exist! Try again!", ButtonType.OK);
        }
    }

    /**
     * Method to close current window and return to layout screen
     */
    @FXML
    private void cancel() {
        Main.setPane(Screens.LAYOUT);
    }

}
