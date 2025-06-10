package org.example.model;

public class Michael extends Personaggio {
    public Michael() {
        super("Michael", "Compagno di stanza di Lewis, fragile ma promettente atleta.", false);
    }

    @Override
    public void interagisci() {
        System.out.println("Michael: Perché mi hai trascurato, Lewis?");
    }
}
