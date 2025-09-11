package org.example.story;

import org.example.database.dao.TabellaStanzaTesti;
import org.example.database.dao.TabellaScene;
import org.example.database.dao.TabellaScene.TipoScene;
import org.example.database.dao.TabellaScene.Scene;
import org.example.mappa.Stanza;

import java.util.Locale;
import java.util.Optional;

/**
 * Adapter per ottenere testi narrativi dal database.
 * Integra le tabelle "stanza_testi" e "scene" per fornire descrizioni dinamiche delle stanze.
 * Segue una logica di priorità per scegliere il testo più appropriato in base allo stato della stanza.
 * @version 1.0
 */
public final class NarrationDbAdapter {

    private NarrationDbAdapter() {}

    /**
     * Priorità:
     * 1) stanza_testi(nome_stanza, stato): stato = "COMPLETATA" se stanza completata, altrimenti "DEFAULT"
     * 2) scene(stanza, tipo=DEFAULT)
     * 3) scene(stanza, tipo=INIZIALE)
     * 4) "" per lasciare il fallback al SceneManager
     */
    public static String testoIngressoDaDb(Stanza stanza) {
        if (stanza == null) return "";
        String key = normalize(stanza.getNome());

        // 1) stanza_testi: usa schema ricco (descrizione/osserva/osserva_aggiornata/completato)
        try {
            Optional<TabellaStanzaTesti.StanzaTesti> stOpt = TabellaStanzaTesti.findByStanza(key);
            if (stOpt.isPresent()) {
                TabellaStanzaTesti.StanzaTesti st = stOpt.get();
                boolean considerCompletata = st.completato()
                        || (stanza.getOggetto() == null
                        && !equalsIgnoreCase(stanza.getNome(), "scale")
                        && !equalsIgnoreCase(stanza.getNome(), "corridoio")
                        && !equalsIgnoreCase(stanza.getNome(), "stanza segreta"));

                if (considerCompletata) {
                    if (notBlank(st.osservaAggiornata())) return st.osservaAggiornata();
                    if (notBlank(st.osserva())) return st.osserva();
                    if (notBlank(st.descrizione())) return st.descrizione();
                } else {
                    if (notBlank(st.osserva())) return st.osserva();
                    if (notBlank(st.descrizione())) return st.descrizione();
                }
            }
        } catch (Exception ignored) {}

        // 2) scene DEFAULT
        try {
            Optional<TabellaScene.Scene> def = TabellaScene.getByStanzaAndTipo(key, TabellaScene.TipoScene.DEFAULT);
            if (def.isPresent()) {
                Scene sc = def.get();
                return format(sc.titolo(), sc.testo());
            }
        } catch (Exception ignored) {}

        // 3) scene INIZIALE
        try {
            Optional<TabellaScene.Scene> init = TabellaScene.getByStanzaAndTipo(key, TipoScene.INIZIALE);
            if (init.isPresent()) {
                Scene sc = init.get();
                return format(sc.titolo(), sc.testo());
            }
        } catch (Exception ignored) {}

        // 4) niente DB → fallback al SceneManager
        return "";
    }

    /** Normalizza una stringa per l'uso come chiave di ricerca nel database.
     * Rimuove spazi, converte in minuscolo e sostituisce caratteri accentati.
     * Esempio: " Stanza Segreta " -> "stanza_segreta"
     */
    private static String normalize(String s) {
        if (s == null) return "";
        String k = s.trim().toLowerCase(Locale.ITALY);
        k = k.replace('à','a').replace('è','e').replace('é','e')
                .replace('ì','i').replace('ò','o').replace('ù','u');
        k = k.replaceAll("\\s+","_");
        return k;
    }
    /** Confronta due stringhe ignorando il caso e gestendo i null.
     * @param a prima stringa
     * @param b seconda stringa
     * @return true se sono uguali (ignorando il caso), false altrimenti
     */
    private static boolean equalsIgnoreCase(String a, String b) {
        return a == null ? b == null : a.equalsIgnoreCase(b);
    }

    /** Controlla se una stringa non è null e non è vuota o composta solo da spazi.
     * @param s la stringa da controllare
     * @return true se la stringa contiene caratteri non spazi, false altrimenti
     */
    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    /** Formattta il testo con il titolo (se presente).
     * @param titolo il titolo della scena (può essere null o vuoto)
     * @param body il corpo del testo (può essere null)
     * @return il testo formattato
     */
    private static String format(String titolo, String body) {
        if (body == null) return "";
        if (titolo != null && !titolo.isBlank()) {
            return "— " + titolo + " —\n" + body;
        }
        return body;
    }
}
