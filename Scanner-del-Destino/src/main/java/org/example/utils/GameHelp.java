package org.example.utils;

import org.example.gui.FinestraLettura;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Classe per mostrare le istruzioni del gioco in una finestra di lettura.
 * Il documento viene caricato da una risorsa di testo.
 * Il documento viene mostrato in una finestra modale.
 */
public final class GameHelp {
    private GameHelp(){}

    /**
     * Mostra le istruzioni del gioco.
     *
     * @param parent
     */
    public static void show(Component parent) {
        String testo;
        try {
            testo = loadFromResource("/istruzioni.txt");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Impossibile caricare le istruzioni.\n" + e.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Window w = parent instanceof Window ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
        FinestraLettura dlg = new FinestraLettura(w, "Istruzioni", testo);
        dlg.setVisible(true);
    }

    /**
     * Carica il contenuto di una risorsa di testo.
     * @param path
     * @return il contenuto della risorsa
     */
    private static String loadFromResource(String path) {
        InputStream is = GameHelp.class.getResourceAsStream(path);
        if (is == null) throw new IllegalStateException("Risorsa non trovata: " + path);
        try (Scanner sc = new Scanner(is, StandardCharsets.UTF_8.name())) {
            return sc.useDelimiter("\\A").next();
        }
    }
}
