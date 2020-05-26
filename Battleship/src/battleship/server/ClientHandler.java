package battleship.server;

import battleship.helpers.Constants;
import battleship.helpers.PlayerRespond;
import battleship.interfaces.BattleshipGameDelegate;
import battleship.interfaces.Completion;
import battleship.interfaces.PlayerDelegate;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread implements PlayerDelegate {

    // ----------------------------------------------------------------------------------------
    // MARK: - fields
    // ----------------------------------------------------------------------------------------

    private final Socket clientSocket;
    private final Server server;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private boolean done;

    /**
     * If client connected to server than status is `OK`,
     * otherwise if server is full and client could not
     * connected than status is `FULL`
     */
    private final String status;

    // ----------------------------------------------------------------------------------------
    // MARK: - init
    // ----------------------------------------------------------------------------------------

    ClientHandler (Socket clientSocket, String status, Server server) {
        this.clientSocket = clientSocket;
        this.status = status;
        this.server = server;
        done = false;
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - getters
    // ----------------------------------------------------------------------------------------

    public String getUsername() {
        return username;
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - network
    // ----------------------------------------------------------------------------------------

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            username = in.readLine();
            out.println(status);

            while (!done) {
                handleMessages();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finish() {
        out.close();
        done = true;
        try {
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - messages
    // ----------------------------------------------------------------------------------------

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getMessage() throws IOException {
        return in.readLine();
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - player delegate
    // ----------------------------------------------------------------------------------------

    @Override
    public void allowToMakeShoots() {
        sendMessage(Constants.allowToMakeShoots);
    }

    @Override
    public void setId(String id) {
        // this method is not used
    }

    /**
     * `checkHit(int x, int y, Completion completion)` saves completion locally,
     * to use it from method `handleRespond(String message)` when player respond
     * will be available
     */
    private Completion completion;

    @Override
    public void checkHit(int x, int y, Completion completion) {
        sendMessage(Constants.checkHit + "," + x + "," + y);
        this.completion = completion;
    }

    @Override
    public void getRespond(PlayerRespond respond) {
        sendMessage(respond.toString());
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - support methods
    // ----------------------------------------------------------------------------------------

    private void handleRespond(String message) {
        PlayerRespond respond = new PlayerRespond(message);
        completion.execute(respond);
    }

    private void handleMessages() throws IOException {
        String line = getMessage();
        String[] arr = line.split(",");
        switch (arr[0]) {
            case Constants.ready:
                server.playerReady(this);
                break;
            case Constants.makeShoot:
                int x = Integer.parseInt(arr[1]);
                int y = Integer.parseInt(arr[2]);
                server.makeShoot(x, y, arr[3]);
                break;
            case Constants.confirmRespond:
                server.confirmRespond(arr[1]);
                break;
            case Constants.playerRespond:
                handleRespond(line);
            case Constants.username:
                username = arr[1];
                break;
            case Constants.gameOver:
                server.stopServer();
                break;
            case Constants.disconnect:
                server.stopServer("Stop game! OK?");
            default:
                break;
        }
    }

}
