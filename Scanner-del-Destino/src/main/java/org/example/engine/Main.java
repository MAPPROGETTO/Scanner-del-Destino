package org.example.engine;

import org.example.database.DatabaseBootstrap;
import org.example.engine.gui.GameMenuScreen;

import javax.swing.*;

/** * Classe principale per avviare il gioco.
 * * Inizializza e mostra il menu principale del gioco.
 * * @version 1.0
 *
 */

public class Main {
    public static void main(String[] args) {
        DatabaseBootstrap.ensureStarted(); // <— crea tabelle + popola se vuoto
        SwingUtilities.invokeLater(() -> {
            // Nessun salvataggio → mostra il menu principale
            new GameMenuScreen().setVisible(true);
        });
    }
}