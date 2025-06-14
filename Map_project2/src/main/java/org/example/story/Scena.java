package org.example.story;

import java.util.List;

public class Scena {
    private String titolo;
    private String descrizione;

    public Scena(String titolo, String descrizione) {
        this.titolo = titolo;
        this.descrizione = descrizione;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String mostra() {
        StringBuilder sb = new StringBuilder();
        sb.append(titolo).append("\n\n");
        sb.append(descrizione).append("\n\n");
        return sb.toString();
    }
}