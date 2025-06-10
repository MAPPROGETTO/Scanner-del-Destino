package org.example.engine;

import org.example.gui.FinestraGioco;
import org.example.gui.SplashScreen;

import java.io.Serializable;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new SplashScreen();
        });
    }
}