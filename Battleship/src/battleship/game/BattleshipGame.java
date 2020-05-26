package battleship.game;

import battleship.interfaces.BattleshipGameDelegate;
import battleship.interfaces.Completion;
import battleship.interfaces.PlayerDelegate;
import battleship.helpers.PlayerRespond;

import java.util.UUID;

// class that handles communications between players in singleplayer mode

public class BattleshipGame implements BattleshipGameDelegate {

    private final PlayerDelegate player1;
    private final PlayerDelegate player2;

    /**
     * id of first player
     */
    private final String id1;

    public BattleshipGame(PlayerDelegate player1, PlayerDelegate player2) {
        this.player1 = player1;
        this.player2 = player2;

        id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();

        player1.setId(id1);
        player2.setId(id2);

        player1.allowToMakeShoots();
    }

    // Mark: - Battleship game delegate implementation

    @Override
    public void makeShoot(int x, int y, String id) {
        Completion completion = (respond) -> {
            if (id.equals(id1)) {
                player1.getRespond((PlayerRespond) respond[0]);
            } else {
                player2.getRespond((PlayerRespond) respond[0]);
            }
        };

        if (id.equals(id1))
            player2.checkHit(x, y, completion);
        else
            player1.checkHit(x, y, completion);

//        PlayerRespond respond = id.equals(id1) ? player2.checkHit(x, y) : player1.checkHit(x, y);
//        if (id.equals(id1)) {
//            player1.getRespond(respond);
//        } else {
//            player2.getRespond(respond);
//        }
    }

    @Override
    public void confirmRespond(String id) {
        if (id.equals(id1)) {
            player2.allowToMakeShoots();
        } else {
            player1.allowToMakeShoots();
        }
    }

}
