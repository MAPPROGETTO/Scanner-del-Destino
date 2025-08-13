package org.example.utils;

import org.example.gui.FinestraLettura;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public final class GameHelp {
    private GameHelp(){}

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

    private static String loadFromResource(String path) {
        InputStream is = GameHelp.class.getResourceAsStream(path);
        if (is == null) throw new IllegalStateException("Risorsa non trovata: " + path);
        try (Scanner sc = new Scanner(is, StandardCharsets.UTF_8.name())) {
            return sc.useDelimiter("\\A").next();
        }
    }
}
