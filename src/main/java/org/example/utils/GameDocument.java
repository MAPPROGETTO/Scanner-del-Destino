package org.example.utils;

import org.example.gui.FinestraLettura;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public final class GameDocument {
    private GameDocument(){}

    /** Mostra il documento; sostituisce ${CODICE} nel testo con il codice segreto passato. */
    public static void showDocumento(Component parent, String codiceSegreto) {
        String testo;
        try {
            testo = loadFromResource("/documento.txt");
        } catch (Exception e) {
            // fallback se manca la risorsa
            testo = """
                    ═════════════ DOCUMENTO RITROVATO ═════════════

                    Frammenti di appunti sullo "Scanner del Destino".
                    Tra equazioni e scarabocchi si legge una sequenza:

                    Codice di sblocco: ${CODICE}

                    Annotazione: non far cadere la fune dal balcone.
                    ═══════════════════════════════════════════════
                    """;
        }

        if (codiceSegreto == null) codiceSegreto = "????";
        testo = testo.replace("${CODICE}", codiceSegreto);

        Window w = (parent instanceof Window) ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
        FinestraLettura dlg = new FinestraLettura(w, "Documento", testo);
        dlg.setVisible(true);
    }

    private static String loadFromResource(String path) {
        try (InputStream is = GameDocument.class.getResourceAsStream(path)) {
            if (is == null) throw new IllegalStateException("Risorsa non trovata: " + path);
            try (Scanner sc = new Scanner(is, StandardCharsets.UTF_8.name())) {
                return sc.useDelimiter("\\A").next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
