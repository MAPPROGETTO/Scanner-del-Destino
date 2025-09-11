package org.example.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Classe per gestire l'uscita dal gioco con conferma.
 */
public final class GameExit {
    private GameExit() {}

    /**
     * Mostra una finestra di conferma per uscire dal gioco.
     * @param parent
     */
    public static void confirmAndExit(Component parent) {
        int choice = JOptionPane.showConfirmDialog(
                parent,
                "Sei sicuro di voler uscire?",
                "Conferma Uscita",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
