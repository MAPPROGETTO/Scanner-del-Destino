package org.example.utils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class SaveHelper {
    private SaveHelper() {}

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

    private static String labelSlot(int n) {
        return "Slot " + n + (GameSaver.esisteSalvataggio(n) ? " (occupato)" : " (libero)");
    }

    private static int parseSlot(String s) {
        // s è tipo "Slot 2 (occupato)"
        String digits = s.replaceAll("\\D+", "");
        try { return Integer.parseInt(digits); } catch (Exception e) { return -1; }
    }
}
