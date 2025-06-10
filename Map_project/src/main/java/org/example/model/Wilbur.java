package org.example.model;

public class Wilbur extends Personaggio {
    public Wilbur() {
        super("Wilbur", "Viaggiatore del tempo, venuto dal futuro con un segreto.", false);
    }

    @Override
    public void interagisci() {
        System.out.println("Wilbur: Abbiamo poco tempo, dobbiamo fermare l'Uomo con la Bombetta!");
    }
}