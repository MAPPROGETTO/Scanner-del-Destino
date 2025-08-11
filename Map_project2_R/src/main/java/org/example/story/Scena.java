package org.example.story;

import java.io.Serializable;

public class Scena implements Serializable {
    public enum TipoScena {
        INIZIALE, DEFAULT, EVENTO, COMPLETAMENTO, FINALE, DIALOGO, USO_OGGETTO
    }
    private String nome;
    private int indice;
    private String titolo;
    private String descrizione;
    private TipoScena tipo;
    private static final long serialVersionUID = 1L;


    public Scena(String nome, int indice, String titolo, String descrizione, TipoScena tipo) {
        this.nome = nome;
        this.indice = indice;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return String.format("[%s #%d] %s - %s", tipo, indice, titolo, descrizione);
    }
    /**
     * Mostra la scena in un formato leggibile.
     * Se è una scena iniziale, mostra il nome della scena.
     * Altrimenti, mostra titolo e descrizione con separatori.
     */

    public String mostra() {
        StringBuilder sb = new StringBuilder();
        if (tipo != TipoScena.INIZIALE) {
            sb.append("--------------------------------------------------------------------------\n");
            sb.append(titolo).append("\n");
            sb.append(descrizione).append("\n\n");
        } else {
            sb.append("Scena: ").append(nome).append("\n\n");
            sb.append(titolo).append("\n");
            sb.append(descrizione).append("\n\n");
        }
        return sb.toString();
    }


    public String getTitolo() {return titolo;}
    public String getDescrizione() {return descrizione;}
    public int getIndice() {return indice;}
    public String getNome() {return nome;}
    public TipoScena getTipo() {return tipo;}
}