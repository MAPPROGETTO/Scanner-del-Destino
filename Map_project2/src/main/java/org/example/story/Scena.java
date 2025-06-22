package org.example.story;

public class Scena {
    private String nome;
    private int indice;
    private String titolo;
    private String descrizione;

    public Scena(String nome, int indice, String titolo, String descrizione) {
        this.nome =nome;
        this.indice = indice;
        this.titolo = titolo;
        this.descrizione = descrizione;
    }

    public String mostra() {
        StringBuilder sb = new StringBuilder();
        if (indice>6) {
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
}