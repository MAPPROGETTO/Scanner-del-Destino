package org.example.story;

import org.example.database.dao.TabellaScene;
import org.example.database.dao.TabellaScene.TipoScene;
import org.example.database.dao.TabellaScene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility per accedere alle scene memorizzate nel database.
 * Le scene sono categorizzate per tipo (EVENTO, PERICOLO, USO_OGGETTO, INIZIALE, FINALE).
 * Fornisce metodi per recuperare scene specifiche basate su chiavi normalizzate.
 */
public final class ScenesDb {
    private ScenesDb(){}

    /**
     * Recupera una scena di tipo EVENTO basata sulla chiave fornita.
     * La chiave viene normalizzata (minuscolo, spazi sostituiti da underscore, rimozione accenti).
     * @param key Chiave della scena (es. nome della stanza o evento specifico)
     * @return Optional contenente la scena formattata se trovata, altrimenti vuoto
     */
    public static Optional<String> evento(String key) {
        try {
            return TabellaScene.getByStanzaAndTipo(normalize(key), TipoScene.EVENTO)
                    .map(sc -> format(sc.titolo(), sc.testo()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Recupera una scena di tipo USO_OGGETTO basata sulla stanza e l'oggetto forniti.
     * Cerca prima una scena specifica per la combinazione stanza:oggetto,
     * poi fa fallback a una scena di tipo EVENTO per la stessa chiave.
     * @param stanza Nome della stanza
     * @param oggetto Nome dell'oggetto
     * @return Optional contenente la scena formattata se trovata, altrimenti vuoto
     */
    public static Optional<String> usoOggetto(String stanza, String oggetto) {
        String raw = stanza + ":" + oggetto;
        String key = normalize(stanza) + ":" + normalize(oggetto);
        System.out.println("[USO_OGGETTO] raw=" + raw + "  normalized=" + key);

        try {
            // 1) cerca prima lo specifico USO_OGGETTO
            Optional<TabellaScene.Scene> r1 =
                    TabellaScene.getByStanzaAndTipo(key, TabellaScene.TipoScene.USO_OGGETTO);
            if (r1.isPresent()) {
                var sc = r1.get();
                return Optional.of(format(sc.titolo(), sc.testo()));
            }

            // 2) fallback: EVENTO (compat con seed tipo stanza_dei_bambini:torcia)
            Optional<TabellaScene.Scene> r2 =
                    TabellaScene.getByStanzaAndTipo(key, TabellaScene.TipoScene.EVENTO);
            return r2.map(sc -> format(sc.titolo(), sc.testo()));

        } catch (Exception e) {
            System.out.println("[USO_OGGETTO] Errore lookup key=" + key + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Normalizza una stringa per l'uso come chiave di ricerca nel database.
     * Rimuove spazi, converte in minuscolo, sostituisce caratteri accentati e rimuove caratteri non validi.
     * Esempio: " Stanza Segreta " -> "stanza_segreta"
     */
    private static String normalize(String s) {
        if (s == null) return "";
        String t = s.trim().toLowerCase(java.util.Locale.ROOT);
        t = java.text.Normalizer.normalize(t, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");    // rimuove accenti
        t = t.replace(' ', '_');            // spazi -> underscore
        t = t.replaceAll("[^a-z0-9_:\\-]", "");
        t = t.replaceAll("_+", "_");
        return t;
    }

    /** Confronta due stringhe ignorando il caso e gestendo i null.
     * @param titolo prima stringa
     * @param body seconda stringa
     * @return true se sono uguali (ignorando il caso), false altrimenti
     */
    private static String format(String titolo,String body){
        if (body==null) return "";
        if (titolo!=null && !titolo.isBlank()) return "— "+titolo+" —\n"+body;
        return body;
    }

    /** Recupera la sequenza di scene iniziali numerate (intro_1, intro_2, ...).
     * Si ferma alla prima scena mancante.
     * @return Lista di scene iniziali formattate
     */
    public static List<String> inizialiSequenza() {
        List<String> out = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Optional<TabellaScene.Scene> r =
                    TabellaScene.getByStanzaAndTipo("intro_" + i,
                            TabellaScene.TipoScene.INIZIALE);
            if (r.isPresent()) {
                var sc = r.get();
                // stesso formato usato altrove: "— Titolo —\nTesto"
                out.add(format(sc.titolo(), sc.testo()));
            } else {
                break; // ci fermiamo quando manca il prossimo
            }
        }
        return out;
    }

    /** Recupera la sequenza di scene finali numerate (finale_1, finale_2, ...).
     * Si ferma alla prima scena mancante.
     * @return Lista di scene finali formattate
     */
    public static List<String> finaliSequenza() {
        List<String> out = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String key = "finale_" + i;
            // cerchiamo esattamente tipo FINALE
            Optional<Scene> row = TabellaScene.getByStanzaAndTipo(normalize(key), TipoScene.FINALE);
            if (row.isPresent()) {
                Scene sc = row.get();
                out.add(format(sc.titolo(), sc.testo()));
            } else {
                break; // ci fermiamo alla prima mancante
            }
        }
        return out;
    }
}
