package battleship.server;

import battleship.helpers.Constants;
import battleship.helpers.PlayerRespond;
import battleship.interfaces.BattleshipGameDelegate;
import battleship.interfaces.Completion;
import battleship.interfaces.PlayerDelegate;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Server extends Thread implements BattleshipGameDelegate {

    // ----------------------------------------------------------------------------------------
    // MARK: - fields
    // ----------------------------------------------------------------------------------------

    private final int port;

    private boolean isRunning;

    private final List<ClientHandler> clients;

    private final List<PlayerDelegate> players;

    private String id1;

    private Completion stopServerCompletion;

    // ----------------------------------------------------------------------------------------
    // MARK: - init
    // ----------------------------------------------------------------------------------------

    public Server(int port, Completion stopServerCompletion) {
        this.port = port;
        this.stopServerCompletion = stopServerCompletion;
        isRunning = true;
        clients = new ArrayList<>();
        players = new ArrayList<>();
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - server work
    // ----------------------------------------------------------------------------------------

    public void run() {
        try {
            ServerSocket s = new ServerSocket(port);
            while (isRunning) {
                Socket incoming = s.accept();
                String status = clients.size() < 2 ? Constants.ok : Constants.full;
                ClientHandler client = new ClientHandler(incoming, status, this);
                client.start();

                if (clients.size() == 2) {
                    client.finish();
                } else {
                    clients.add(client);
                }
            }
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        for (ClientHandler client : clients) {
            client.finish();
        }

        clients.clear();
        players.clear();

        // disable stop button and enable start button
        stopServerCompletion.execute();

        isRunning = false;
        try {
            // socket to stop the server
            Socket fake = new Socket("localhost", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(Constants.disconnect + "," + message);
        }

        stopServer();
    }

    public boolean isServerRunning() {
        return isRunning;
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - handle players
    // ----------------------------------------------------------------------------------------

    public void playerReady(PlayerDelegate player) {
        players.add(player);
        if (players.size() == 2) {
            id1 = UUID.randomUUID().toString();
            String id2 = UUID.randomUUID().toString();

            var player1 = (ClientHandler) players.get(0);
            var player2 = (ClientHandler) players.get(1);

            player2.sendMessage(id2 + "," + Constants.first + "," + player1.getUsername());
            player1.sendMessage(id1 + "," + Constants.second + "," + player2.getUsername());
        }
    }

    // ----------------------------------------------------------------------------------------
    // MARK: - battle ship game delegate
    // ----------------------------------------------------------------------------------------

    @Override
    public void makeShoot(int x, int y, String id) {
        Completion completion = (respond) -> {
            if (id.equals(id1)) {
                players.get(0).getRespond((PlayerRespond) respond[0]);
            } else {
                players.get(1).getRespond((PlayerRespond) respond[0]);
            }
        };

        if (id.equals(id1))
            players.get(1).checkHit(x, y, completion);
        else
            players.get(0).checkHit(x, y, completion);
    }

    @Override
    public void confirmRespond(String id) {
        if (id.equals(id1)) {
            players.get(1).allowToMakeShoots();
        } else {
            players.get(0).allowToMakeShoots();
        }
    }
}
