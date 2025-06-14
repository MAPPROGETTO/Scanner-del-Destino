package org.example.model;

public abstract class Personaggio {
    protected String nome;
    protected String descrizione;
    protected boolean isNemico;

    public Personaggio(String nome, String descrizione, boolean isNemico) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.isNemico = isNemico;
    }

    public String getNome() {
        return nome;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public boolean isNemico() {
        return isNemico;
    }
    public abstract void interagisci();

    @Override
    public String toString() {
        return nome + (isNemico ? " (Nemico)" : "") + ": " + descrizione;
    }
}
