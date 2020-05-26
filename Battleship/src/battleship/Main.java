package battleship;

import battleship.enumerators.GameMode;
import battleship.enumerators.ProgramType;
import battleship.enumerators.Screens;
import battleship.enumerators.ShipType;
import battleship.helpers.NetworkManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    /**
     * all stages
     */
    private static Map<Screens, Stage> stages;

    /**
     * all controllers
     */
    private static Map<Screens, Object> controllers;

    /**
     * instance of this class
     * it is needed to get resources
     */
    private static Main instance;

    /**
     * index of current screen
     */
    private static Screens current;

    /**
     * game mode
     */
    private static GameMode mode;

    /**
     * screens' paths
     */
    private static final Map<Screens, String> screens = new HashMap<>() {{
        put(Screens.MENU, "/battleship/menu/MainMenu.fxml");
        put(Screens.LAYOUT, "/battleship/layout/ShipsLayout.fxml");
        put(Screens.GAME, "/battleship/game/GameScreen.fxml");
        put(Screens.GAME_OVER, "/battleship/gameOver/GameOver.fxml");
        put(Screens.SERVER, "/battleship/server/ServerGUI.fxml");
        put(Screens.CONNECT_TO_SERVER, "/battleship/connect/ConnectToServer.fxml");
    }};

    private static ProgramType programType;

    public static Object getController(Screens screen) {
        return controllers.get(screen);
    }

    public static Stage getStage(Screens screen) {
        return stages.get(screen);
    }

    public static void setMode(GameMode mode) {
        Main.mode = mode;
    }

    public static GameMode getMode() {
        return mode;
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        prepareForGame();
    }

    /**
     * prepare scenes and controllers
     */
    private static void prepareForGame() {
        current = programType == ProgramType.CLIENT ? Screens.MENU : Screens.SERVER;
        stages = new HashMap<>();
        controllers = new HashMap<>();
        for (var screen : screens.entrySet()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(instance.getClass().getResource(screen.getValue()));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stages.put(screen.getKey(), stage);
            controllers.put(screen.getKey(), loader.getController());
        }
        stages.get(current).setTitle(current == Screens.MENU ? "battleship" : "server");
        stages.get(current).show();
    }

    /**
     * hides the current screen and shows the screen at index
     * @param screen screen that should be shown
     */
    public static void setPane(Screens screen) {
        stages.get(current).hide();
        stages.get(screen).show();
        current = screen;
    }

    /**
     * renew scenes, controller and some variables
     */
    public static void backToMain() {
        stages.get(current).hide();
        ShipType.BATTLESHIP.prepareForReuse();
        ShipType.CRUISER.prepareForReuse();
        ShipType.DESTROYER.prepareForReuse();
        ShipType.SUBMARINE.prepareForReuse();
        prepareForGame();
    }

    public static void main(String[] args) {
        mode = GameMode.SINGLE;
        programType = ProgramType.CLIENT;
        if (args.length > 0 && args[0].equals("Server")) {
            programType = ProgramType.SERVER;
        }
        launch(args);
    }
}
