package org.example.story;

import java.util.List;

public class Scena {
    private String titolo;
    private String descrizione;
    private List<String> opzioni;

    public Scena(String titolo, String descrizione, List<String> opzioni) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.opzioni = opzioni;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public List<String> getOpzioni() {
        return opzioni;
    }

    public String mostra() {
        StringBuilder sb = new StringBuilder();
        sb.append(titolo).append("\n\n");
        sb.append(descrizione).append("\n\n");
        sb.append("Opzioni:\n");
        for (int i = 0; i < opzioni.size(); i++) {
            sb.append((i + 1)).append(". ").append(opzioni.get(i)).append("\n");
        }
        return sb.toString();
    }
}