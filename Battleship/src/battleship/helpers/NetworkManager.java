package battleship.helpers;

import battleship.Main;
import battleship.game.GameController;
import battleship.interfaces.BattleshipGameDelegate;
import battleship.interfaces.Completion;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.net.Socket;

public class NetworkManager implements BattleshipGameDelegate {

    // ----------------------------------------------------------------------------------------
    // MARK: - fields
    // ----------------------------------------------------------------------------------------

    private Socket clientSocket;

    private PrintWriter out;

    private BufferedReader in;

    private boolean connected;

    private GameController gameController;

    private AlertController alertController;

    // ----------------------------------------------------------------------------------------
    // MARK: - singleton
    // ----------------------------------------------------------------------------------------

    private static final NetworkManager instance;

    private NetworkManager() {
        alertController = new AlertController();
    }

    static {
        instance = new NetworkManager();
    }

    public static NetworkManager getInstance() {
        return instance;
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - setters
    // ----------------------------------------------------------------------------------------

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - methods to work with network
    // ----------------------------------------------------------------------------------------

    public Response startConnection(String ip, int port, String username) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // successfully connected as a first player
        out.println(username);
        connected = true;
        String line = in.readLine();
        if ("OK".equals(line)) {
            return new Response(Response.ResponseStatus.SUCCESS, Constants.ok);
        }

        // server is full
        connected = false;
        return new Response(Response.ResponseStatus.ERROR, Constants.full);
    }

    public void disconnect() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - messages
    // ----------------------------------------------------------------------------------------

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public void getMessage(Completion completion) {
        new Thread(() -> {
            try {
                String message = in.readLine();
                Platform.runLater(() -> completion.execute(message));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - game
    // ----------------------------------------------------------------------------------------

    public void startGame() {
        new Thread(() -> {
            while (connected) {
                try {
                    handleMessage();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    private void handleMessage() throws IOException {
        String line = in.readLine();
        String[] arr = line.split(",");
        switch (arr[0]) {
            case Constants.allowToMakeShoots:
                gameController.allowToMakeShoots();
                break;
            case Constants.checkHit:
                int x = Integer.parseInt(arr[1]);
                int y = Integer.parseInt(arr[2]);
                gameController.checkHit(x, y, (respond) -> sendMessage(respond[0].toString()));
                break;
            case Constants.playerRespond:
                gameController.getRespond(new PlayerRespond(line));
                break;
            case Constants.disconnect:
                Platform.runLater(()->{
                    alertController.showAlert(arr[1], ButtonType.OK);
                    Main.backToMain();
                    try {
                        NetworkManager.getInstance().disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            default:
                break;
        }
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - battle ship game delegate
    // ----------------------------------------------------------------------------------------

    @Override
    public void makeShoot(int x, int y, String id) {
        sendMessage(Constants.makeShoot + "," + x + "," + y + "," + id);
    }

    @Override
    public void confirmRespond(String id) {
        sendMessage(Constants.confirmRespond + "," + id);
    }

}
