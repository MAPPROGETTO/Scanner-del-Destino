package org.example.model;

import java.io.Serializable;

/**
 * Classe astratta che rappresenta un personaggio nel gioco.
 * Può essere un alleato o un nemico.
 * @version 1.0
 *
 */
public abstract class Personaggio implements Serializable {
    protected String nome;
    protected String descrizione;
    protected boolean isNemico;

    /**
     * Costruttore della classe Personaggio.
     * @param nome
     * @param descrizione
     * @param isNemico
     */
    public Personaggio(String nome, String descrizione, boolean isNemico) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.isNemico = isNemico;
    }

    public String getNome() {
        return nome;
    }
    @Override
    public String toString() {
        return nome + (isNemico ? " (Nemico)" : "") + ": " + descrizione;
    }
}
