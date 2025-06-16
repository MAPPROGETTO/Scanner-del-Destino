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

    public void mostraInventario() {
        System.out.println("Inventario:");
        if (oggetti.isEmpty()) {
            System.out.println("  (vuoto)");
        } else {
            for (String item : oggetti) {
                System.out.println("  - " + item);
            }
        }
    }
    // Metodo per ottenere una rappresentazione testuale dell'inventario
    public String mostraInventarioTesto() {
        if (oggetti.isEmpty()) {
            return "  (vuoto)";
        }

        StringBuilder sb = new StringBuilder();
        for (String item : oggetti) {
            sb.append("  - ").append(item).append("\n");
        }
        return sb.toString();
    }
}
