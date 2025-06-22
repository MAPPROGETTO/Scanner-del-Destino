package org.example.engine;
import org.example.gui.GameMenuScreen;
import org.example.gui.SplashScreen;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new GameMenuScreen().setVisible(true);
        });
    }
}