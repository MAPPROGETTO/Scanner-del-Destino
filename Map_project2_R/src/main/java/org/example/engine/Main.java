package org.example.engine;
import org.example.gui.GameMenuScreen;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new GameMenuScreen().setVisible(true);
        });
    }
}