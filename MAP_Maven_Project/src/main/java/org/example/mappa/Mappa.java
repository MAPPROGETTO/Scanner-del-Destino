package org.example.mappa;

public class Mappa {
    private final int larghezza;
    private final int altezza;
    private final char[][] griglia;

    public Mappa(int larghezza, int altezza) {
        this.larghezza = larghezza;
        this.altezza = altezza;
        griglia = new char[altezza][larghezza];
        inizializza();
    }

    private void inizializza() {
        for (int y = 0; y < altezza; y++) {
            for (int x = 0; x < larghezza; x++) {
                griglia[y][x] = '.';
            }
        }
    }

    public String mostra(Posizione pos) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < altezza; y++) {
            for (int x = 0; x < larghezza; x++) {
                if (pos.getX() == x && pos.getY() == y) {
                    sb.append("P ");
                } else {
                    sb.append(griglia[y][x]).append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean isValida(Posizione pos) {
        int x = pos.getX();
        int y = pos.getY();
        return x >= 0 && y >= 0 && x < larghezza && y < altezza;
    }
}
