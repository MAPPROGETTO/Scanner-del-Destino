package org.example.utils;

import org.example.model.StatoGioco;

import javax.swing.*;
import java.io.*;

public class GameSaver implements Serializable{

    private static final String CARTELLA_SALVATAGGIO = "salvataggi/";
    private static final int NUM_SLOT = 3;

    static {
        File dir = new File(CARTELLA_SALVATAGGIO);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.err.println("> Errore: impossibile creare la cartella di salvataggio.");
            }
        }
    }

    /**
     * Salva la partita nello slot indicato o nel primo disponibile.
     * Se tutti gli slot sono occupati, chiede se sovrascrivere lo slot 1.
     */
    public static boolean salvaPartita(StatoGioco stato, int slotForzato) {
        if (stato == null) {
            System.err.println("> Errore: stato di gioco nullo, impossibile salvare.");
            return false;
        }

        int slot = slotForzato > 0 ? slotForzato : getPrimoSlotDisponibile(true);

        if (slot == -1) {
            int scelta = JOptionPane.showConfirmDialog(null,
                    "Tutti gli slot sono pieni. Vuoi sovrascrivere lo slot 1?",
                    "Slot pieni",
                    JOptionPane.YES_NO_OPTION);

            if (scelta == JOptionPane.YES_OPTION) {
                slot = 1;
            } else {
                System.err.println("> Salvataggio annullato dall'utente.");
                return false;
            }
        }

        String nomeFile = CARTELLA_SALVATAGGIO + "slot" + slot + ".dat";

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeFile))) {
            oos.writeObject(stato);
            System.out.println("> Partita salvata su: " + nomeFile);
            return true;
        } catch (IOException e) {
            System.err.println("> Errore durante il salvataggio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Carica la partita da uno slot specifico.
     */
    public static StatoGioco caricaPartita(int slot) {
        String nomeFile = CARTELLA_SALVATAGGIO + "slot" + slot + ".dat";
        File file = new File(nomeFile);

        if (!file.exists() || file.length() == 0) {
            System.err.println("> Errore: il file di salvataggio non esiste o è vuoto.");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nomeFile))) {
            Object obj = ois.readObject();
            if (obj instanceof StatoGioco) {
                System.out.println("> Partita caricata da: " + nomeFile);
                return (StatoGioco) obj;
            } else {
                System.err.println("> Errore: il file non contiene uno StatoGioco valido.");
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("> Errore durante il caricamento: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Ritorna true se lo slot specificato esiste e contiene dati.
     */
    public static boolean esisteSalvataggio(int slot) {
        File file = new File(CARTELLA_SALVATAGGIO + "slot" + slot + ".dat");
        return file.exists() && file.length() > 0;
    }

    public static int trovaPrimoSlotLibero() {
        for (int slot = 1; slot <= NUM_SLOT; slot++) {
            if (!esisteSalvataggio(slot)) {
                return slot;
            }
        }
        return -1;
    }

    /**
     * Cerca il primo slot disponibile.
     * @param cercaSlotVuoti true = cerca slot liberi (per salvataggio), false = cerca slot occupati (per caricamento)
     * @return numero dello slot o -1 se nessuno disponibile
     */
    public static int getPrimoSlotDisponibile(boolean cercaSlotVuoti) {
        for (int i = 1; i <= NUM_SLOT; i++) {
            boolean esiste = esisteSalvataggio(i);
            if (cercaSlotVuoti && !esiste) return i;
            if (!cercaSlotVuoti && esiste) return i;
        }
        return -1;
    }
}