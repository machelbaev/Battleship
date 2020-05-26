package battleship.server;

import battleship.Main;
import battleship.enumerators.Screens;
import battleship.interfaces.Completion;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ServerGUIController {

    @FXML
    Button startButton;

    @FXML
    Button stopButton;

    @FXML
    TextField textField;

    Server server;

    private boolean firstTime;

    @FXML
    void initialize() {
        startButton.setDisable(true);
        stopButton.setDisable(true);
        textField.textProperty().addListener((observable, oldValue, newValue) -> textChanged(newValue));
        firstTime = true;
    }

    void textChanged(String value) {
        if (server != null && server.isServerRunning())
            return;

        int port;
        try {
            port = Integer.parseInt(value);
            if (port > 5000 || port < 1025)
                throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            port = -1;
        }
        startButton.setDisable(port == -1);
    }

    @FXML
    void runServer() {

        if (firstTime) {
            Main.getStage(Screens.SERVER).setOnCloseRequest(e -> {
                stopServer();
            });
            firstTime = false;
        }

        int port = Integer.parseInt(textField.getText());
        Completion completion = (a)->{
            Platform.runLater(()->{
                textChanged(textField.getText());
                stopButton.setDisable(true);
            });
        };
        server = new Server(port, completion);
        server.start();

        stopButton.setDisable(false);
        startButton.setDisable(true);
    }

    @FXML
    void stopServer() {
        server.stopServer("Server was stopped");
    }

}
