package org.example.story;

import java.io.Serializable;

/**
 * Rappresenta una scena della storia con attributi come nome, indice, titolo, descrizione e tipo.
 * Il tipo di scena può essere INIZIALE, DEFAULT, EVENTO, COMPLETAMENTO, DIALOGO o USO_OGGETTO.
 * Implementa Serializable per permettere la serializzazione dell'oggetto.
 *
 */
public class Scena implements Serializable {
    public enum TipoScena {
        INIZIALE, DEFAULT, EVENTO, COMPLETAMENTO, DIALOGO, USO_OGGETTO, FINALE
    }
    private String nome;
    private int indice;
    private String titolo;
    private String descrizione;
    private TipoScena tipo;
    private static final long serialVersionUID = 1L;


    /**
     * Costruttore per la classe Scena.
     * @param nome
     * @param indice
     * @param titolo
     * @param descrizione
     * @param tipo
     */
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

    /**
     * Getter per il titolo, il nome e la descrizione.
     * @return titolo, nome, descrizione
     */
    public String getTitolo() {return titolo;}
    public String getDescrizione() {return descrizione;}
    public String getNome() {return nome;}
}