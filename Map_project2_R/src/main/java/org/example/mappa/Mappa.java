package org.example.mappa;

import org.example.story.Scena;

import java.io.Serializable;
import java.util.List;

public class  Mappa implements Serializable {
    private final int larghezza;
    private final int altezza;
    private final Stanza[][] stanze;
    private static final long serialVersionUID = 1L;

    public Mappa(int altezza, int larghezza, List<Scena> scene) {
        this.larghezza = larghezza;
        this.altezza = altezza;
        this.stanze = new Stanza[altezza][larghezza];
        inizializza(scene);
    }

    private void inizializza(List<Scena> scene) {
        // Primo piano
        stanze[5][6] = new Stanza("Salotto", true, false, true, false, true, false, true, true, scene.get(6), "elica", null);
        stanze[2][6] = new Stanza("Cucina", true, false, true, false, false, true, false,false, scene.get(8), "schermo",null);
        stanze[5][10] = new Stanza("Bagno", false, false, true, false, false,false,false,true, scene.get(10), "chiave_matrimoniale", List.of("chiave matrimoniale"));
        stanze[2][2] = new Stanza("Stanza Segreta", false, true, false, false,false,false,true,false, scene.get(13), null, null);
        stanze[5][2] = new Stanza("Scale", true, false, false, true,false,false,true,false, scene.get(12), null, null);

        // Secondo piano
        stanze[3][9] = new Stanza("Stanza Matrimoniale", true, true, true, false, false, false, false, true, scene.get(16), "motore", List.of("chiave_matrimoniale"));
        stanze[0][1] = new Stanza("Balcone", true, false, false, true, false, true, false, false, scene.get(17), null, null);
        stanze[3][1] = new Stanza("Stanza dei Bambini", false, false, true, false, true, false, true, false, scene.get(18), "batteria", null);
        stanze[0][9] = new Stanza("Stanza degli ospiti", true, false, false, false, false, false, false, true, scene.get(20), "documento", List.of("chiave segreta"));
        stanze[6][5] = new Stanza("Scale", true, false, false, false, true, true, false, false, scene.get(12), null, null);
        stanze[3][5] = new Stanza("Corridoio", true, false, false, false, true, true, true, true, scene.get(19), null, null);
        stanze[0][5] = new Stanza("Corridoio", true, false, false, false, false, true, true, false, scene.get(19), null, null);
    }

    public boolean isValida(Posizione p) {
        int x = p.getX();
        int y = p.getY();
        boolean dentroMappa = y >= 0 && y < stanze.length && x >= 0 && x < stanze[0].length;
        boolean esisteStanza = dentroMappa && stanze[y][x] != null;

        System.out.println("Verifica posizione valida -> x=" + x + ", y=" + y +
                " | dentroMappa: " + dentroMappa + ", esisteStanza: " + esisteStanza);

        return esisteStanza;
    }

    public Stanza getStanza(Posizione posizione) {
        return stanze[posizione.getY()][posizione.getX()];
    }

    public String mostra(Posizione pos) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < altezza; y++) {
            for (int x = 0; x < larghezza; x++) {
                if (pos.getX() == x && pos.getY() == y) {
                    sb.append("P ");
                } else if (stanze[y][x] != null) {
                    sb.append("S ");
                } else {
                    sb.append(". ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    public int getLarghezza() {
        return larghezza;
    }

    public int getAltezza() {
        return altezza;
    }

    public int getScenaIndexAt(int x, int y) {
        if (y < 0 || y >= altezza || x < 0 || x >= larghezza) {
            return -1;
        }
        Stanza stanza = stanze[y][x];
        if (stanza == null || stanza.getScena() == null) {
            return -1;
        }
        return stanza.getScena().getIndice();
    }
}
