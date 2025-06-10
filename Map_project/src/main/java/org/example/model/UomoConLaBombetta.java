package org.example.model;

public class UomoConLaBombetta extends Personaggio {
    public UomoConLaBombetta() {
        super("Uomo con la Bombetta", "Misterioso ladro dal futuro, assetato di vendetta.", true);
    }

    @Override
    public void interagisci() {
        System.out.println("Uomo con la Bombetta: Porterò via la tua invenzione... e il tuo futuro!");
    }
}