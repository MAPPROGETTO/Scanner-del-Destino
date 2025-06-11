package org.example.utils;
import java.util.ArrayList;
import java.util.List;

public class Inventario {
    private List<String> oggetti;

    public Inventario() {
        oggetti = new ArrayList<>();
    }

    public void aggiungi(String oggetto) {
        oggetti.add(oggetto);
    }
}
