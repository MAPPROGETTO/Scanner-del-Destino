package org.example.engine;

import org.example.gui.GameMenuScreen;

public class Main {
        public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Nessun salvataggio → mostra il menu principale
            new GameMenuScreen().setVisible(true);
        });
    }
}