package org.example.utils;

import java.util.List;
import java.util.Scanner;

public class GestoreInput {
    private final Scanner scanner;

    public GestoreInput() {
        scanner = new Scanner(System.in);
    }

    public int chiediSceltaUtente(List<String> opzioni) {
        int scelta = -1;

        while (scelta < 1 || scelta > opzioni.size()) {
            System.out.print("Scegli un'opzione (1-" + opzioni.size() + "): ");
            if (scanner.hasNextInt()) {
                scelta = scanner.nextInt();
                scanner.nextLine(); // pulisce buffer
            } else {
                System.out.println("Input non valido. Inserisci un numero.");
                scanner.nextLine(); // scarta input errato
            }
        }

        return scelta - 1; // ritorna l'indice reale (0-based)
    }

    public String leggiLinea() {
        return scanner.nextLine();
    }

    public void attendiInvio() {
        System.out.println("\n--- Premi INVIO per continuare ---");
        scanner.nextLine();
    }
}
