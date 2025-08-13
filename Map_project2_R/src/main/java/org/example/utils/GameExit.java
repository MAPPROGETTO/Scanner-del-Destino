package org.example.utils;

import javax.swing.*;
import java.awt.*;

public final class GameExit {
    private GameExit() {}

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
