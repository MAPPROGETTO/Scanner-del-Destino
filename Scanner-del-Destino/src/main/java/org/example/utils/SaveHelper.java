package org.example.utils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe di utilità per la gestione del salvataggio del gioco.
 * Fornisce un metodo per chiedere all'utente dove salvare la partita.
 * Permette di scegliere tra sovrascrivere lo slot corrente, usare il primo slot libero,
 * scegliere uno slot specifico, o annullare l'operazione.
 * Gestisce la conferma di sovrascrittura se lo slot scelto è già occupato.
 * @version 1.0
 */
public final class SaveHelper {
    private SaveHelper() {}

    /**
     * Mostra una finestra di dialogo per chiedere all'utente dove salvare la partita.
     * Le opzioni sono:
     * - Sovrascrivi lo slot corrente (se fornito)
     * - Usa il primo slot libero (se disponibile)
     * - Scegli uno slot specifico
     * - Annulla
     * Se lo slot scelto è già occupato, chiede conferma per sovrascriverlo.
     * @param parent
     * @param slotCorrente
     * @return lo slot scelto (1-3) o null se l'operazione è stata annullata
     */
    public static Integer promptForSaveSlot(Component parent, Integer slotCorrente) {
        // calcola primo libero
        int primoLibero = GameSaver.getPrimoSlotDisponibile(true); // true = cerca liberi

        // Opzioni dinamiche
        List<String> options = new ArrayList<>();
        List<Integer> codes = new ArrayList<>();

        if (slotCorrente != null && slotCorrente > 0) {
            options.add("Sovrascrivi slot " + slotCorrente);
            codes.add(slotCorrente);
        }
        if (primoLibero != -1) {
            options.add("Usa primo slot libero (slot " + primoLibero + ")");
            codes.add(primoLibero);
        }
        options.add("Scegli slot...");
        codes.add(0);
        options.add("Annulla");
        codes.add(-1);

        int choice = JOptionPane.showOptionDialog(
                parent,
                "Dove vuoi salvare?",
                "Salvataggio",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options.toArray(),
                options.get(0)
        );
        if (choice < 0) return null;

        int code = codes.get(choice);
        if (code == -1) return null; // annulla

        if (code > 0) {
            // scelto “sovrascrivi slot corrente” o “primo libero”
            if (GameSaver.esisteSalvataggio(code) && (slotCorrente == null || !slotCorrente.equals(code))) {
                int ok = JOptionPane.showConfirmDialog(parent,
                        "Lo slot " + code + " contiene già un salvataggio.\nVuoi sovrascriverlo?",
                        "Conferma sovrascrittura", JOptionPane.YES_NO_OPTION);
                if (ok != JOptionPane.YES_OPTION) return null;
            }
            return code;
        }

        // “Scegli slot...”
        Object sel = JOptionPane.showInputDialog(
                parent,
                "Seleziona slot:",
                "Selezione Slot",
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{
                        labelSlot(1), labelSlot(2), labelSlot(3)
                },
                labelSlot(1)
        );
        if (sel == null) return null;

        int slotScelto = parseSlot(sel.toString());
        if (slotScelto <= 0) return null;

        if (GameSaver.esisteSalvataggio(slotScelto)) {
            int ok = JOptionPane.showConfirmDialog(parent,
                    "Lo slot " + slotScelto + " contiene già un salvataggio.\nVuoi sovrascriverlo?",
                    "Conferma sovrascrittura", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return null;
        }
        return slotScelto;
    }

    /**
     *  Ritorna l'etichetta per uno slot, indicando se è occupato o libero.
     * @param n
     * @return l'etichetta dello slot
     */
    private static String labelSlot(int n) {
        return "Slot " + n + (GameSaver.esisteSalvataggio(n) ? " (occupato)" : " (libero)");
    }

    /**
     * Estrae il numero di slot da una stringa del tipo "Slot N (occupato)" o "Slot N (libero)".
     * @param s
     * @return il numero di slot o -1 se non valido
     * @exception NumberFormatException se la stringa non contiene un numero valido
     */
    private static int parseSlot(String s) {
        // s è tipo "Slot 2 (occupato)"
        String digits = s.replaceAll("\\D+", "");
        try { return Integer.parseInt(digits); } catch (Exception e) { return -1; }
    }
}
