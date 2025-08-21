package org.example.utils;

import java.io.Serializable;
import java.util.List;

public class GestoreInputGUI implements InterfacciaInputUtente, Serializable {
    private String ultimaLineaInserita = "";

    public void setUltimaLineaInserita(String input) {
        this.ultimaLineaInserita = input;
    }

    @Override
    public int chiediSceltaUtente(List<String> opzioni) {
        try {
            int scelta = Integer.parseInt(ultimaLineaInserita);
            return Math.max(0, Math.min(opzioni.size() - 1, scelta - 1));
        } catch (NumberFormatException e) {
            return -1; // da gestire a livello superiore
        }
    }

    @Override
    public String leggiLinea() {
        return ultimaLineaInserita;
    }

    @Override
    public void attendiInvio() {
        // Può essere un JOptionPane.showMessageDialog o ignorato se la GUI lo gestisce altrove
    }
}
