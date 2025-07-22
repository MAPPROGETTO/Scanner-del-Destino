package org.example.utils;

import java.util.List;
import java.util.Scanner;

public class GestoreInput implements InterfacciaInputUtente {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public int chiediSceltaUtente(List<String> opzioni) {
        int scelta = -1;
        while (scelta < 1 || scelta > opzioni.size()) {
            System.out.print("Scegli un'opzione (1-" + opzioni.size() + "): ");
            if (scanner.hasNextInt()) {
                scelta = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Input non valido.");
                scanner.nextLine();
            }
        }
        return scelta - 1;
    }

    @Override
    public String leggiLinea() {
        return scanner.nextLine();
    }

    @Override
    public void attendiInvio() {
        System.out.println("\n--- Premi INVIO per continuare ---");
        scanner.nextLine();
    }
}
