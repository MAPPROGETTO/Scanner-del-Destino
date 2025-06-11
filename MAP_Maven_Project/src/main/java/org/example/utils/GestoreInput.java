package org.example.utils;

import java.util.Scanner;

public class GestoreInput {
    private final Scanner scanner;

    public GestoreInput() {
        scanner = new Scanner(System.in);
    }

    public String leggiLinea() {
        return scanner.nextLine();
    }
}
