package org.example.mappa;

import org.example.story.Scena;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta la mappa del gioco con le stanze e le loro connessioni.
 * Ogni stanza ha proprietà come nome, direzioni accessibili, stato della luce,
 * se è chiusa, se contiene oggetti, eventi pericolosi e una scena narrativa associata.
 * La mappa gestisce la validità delle posizioni e fornisce metodi per ottenere
 * le stanze e visualizzare la mappa.
 * Implementa Serializable per permettere la serializzazione dell'oggetto.
 */
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

    /**
     * Inizializza le stanze della mappa con le loro proprietà specifiche.
     * Ogni stanza viene creata con il suo nome, stato della luce,
     * se è chiusa, se contiene oggetti, eventi pericolosi,
     * le direzioni accessibili, la scena associata e gli oggetti presenti.
     * @param scene
     */
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

    /**
     * Restituisce la stanza alla posizione specificata.
     * @param posizione
     * @return
     */
    public Stanza getStanza(Posizione posizione) {
        return stanze[posizione.getY()][posizione.getX()];
    }

    /**
     * Restituisce una lista di tutte le stanze presenti nella mappa.
     *
     */
    public List<Stanza> getTutteLeStanze() {
        List<Stanza> out = new ArrayList<>();
        for (int y = 0; y < altezza; y++) {
            for (int x = 0; x < larghezza; x++) {
                Stanza s = stanze[y][x];
                if (s != null) out.add(s);
            }
        }
        return out;
    }

    /**
     * Cerca una stanza per nome (case-insensitive)
     * @param nome
     */
    public Stanza getStanzaByNome(String nome) {
        if (nome == null) return null;
        for (Stanza s : getTutteLeStanze()) {
            if (s != null && s.getNome() != null && s.getNome().equalsIgnoreCase(nome)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Cerca una stanza per nome (case-insensitive)
     * @param nome
     */
    public Posizione posizioneDellaStanza(String nome) {
        if (nome == null) return null;
        for (int y = 0; y < altezza; y++) {
            for (int x = 0; x < larghezza; x++) {
                Stanza s = stanze[y][x];
                if (s != null && s.getNome() != null && s.getNome().equalsIgnoreCase(nome)) {
                    return new Posizione(x, y);
                }
            }
        }
        return null;
    }
}
