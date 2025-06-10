package org.example.story;

public class Missione {

    public enum Stato {
        NON_INIZIATA,
        IN_CORSO,
        COMPLETATA
    }

    private String titolo;
    private String obiettivo;
    private Stato stato;

    public Missione(String nome, String obiettivo) {
        this.titolo = nome;
        this.obiettivo = obiettivo;
        this.stato = Stato.NON_INIZIATA;
    }

    public void mostraDettagli() {
        System.out.println("Missione: " + titolo);
        System.out.println("Obiettivo: " + obiettivo);
        System.out.println("   Stato: " + stato.name().replace('_', ' '));
    }

    public String getTitolo() {
        return titolo;
    }

    public String getObiettivo() {
        return obiettivo;
    }

    public Stato getStato() {
        return stato;
    }

    public void avvia() {
        if (stato == Stato.NON_INIZIATA) {
            stato = Stato.IN_CORSO;
        }
    }

    public void completa() {
        stato = Stato.COMPLETATA;
    }

    public boolean isCompletata() {
        return stato == Stato.COMPLETATA;
    }


}