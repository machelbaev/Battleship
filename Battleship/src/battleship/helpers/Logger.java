package battleship.helpers;

import battleship.enumerators.ShipType;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.List;

public class Logger {

    /**
     * player name
     */
    private final String player;

    /**
     * text area to write logs
     */
    private final TextArea logArea;

    private List<Character> alphabet;

    public Logger(String name, TextArea logArea) {
        this.logArea = logArea;
        this.player = name;

        alphabet = new ArrayList<>();
        for (char i = 'a'; i <= 'j'; i++) {
            alphabet.add(i);
        }
    }

    /**
     * write log in text area
     * @param message message to be written
     */
    private void setText(String message, Point shotCell) {
        String text = logArea.getText();
        logArea.setText(text + player + ": " + message + " at "
                + coordinatesToString(shotCell.getX(), shotCell.getY()) + "!\n");
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    private void setText(String message) {
        String text = logArea.getText();
        logArea.setText(text + player + ": " + message + "\n");
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    public void notAllowedShoot(Point shotCell) {
        setText("Can't shoot", shotCell);
    }

    public void invalidAction() {
        setText("Invalid action!");
    }

    public void opponentsTurn() {
        String text = logArea.getText();
        logArea.setText(text + "It is opponent's turn!\n");
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    public void missShot(Point shotCell) {
        setText("Miss shot", shotCell);
    }

    public void hit(Point shotCell) {
        setText("Hit a ship", shotCell);
    }

    public void sink(String ship, Point shotCell) {
        setText("Sank a " + ship, shotCell);
    }

    private String coordinatesToString(int x, int y) {
        return "(" + alphabet.get(y) + ", " + (x + 1) + ")";
    }

}
