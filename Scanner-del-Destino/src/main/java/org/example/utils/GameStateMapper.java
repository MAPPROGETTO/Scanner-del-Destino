package org.example.utils;

import org.example.mappa.Posizione;
import org.example.mappa.Stanza;
import org.example.model.StatoGioco;
import org.example.model.Lewis;
import org.example.inventario.Inventario;
import org.example.mappa.Mappa;
import org.example.story.StoryEngine;

import java.util.*;

/**
 * Classe per convertire tra lo stato di gioco (StatoGioco) e gli oggetti di gioco attivi.
 * Usata per salvare e caricare la partita.
 * Le modifiche alla struttura di StatoGioco devono essere gestite qui.
 */
public final class GameStateMapper {

    private GameStateMapper() {}

    // --------- SALVATAGGIO ---------

    /**
     * Crea uno StatoGioco a partire dagli oggetti di gioco attivi.
     * @param lewis
     * @param inventario
     * @param mappa
     * @param storyEngine
     * @return $RETURN
     */
    public static StatoGioco buildFrom(Lewis lewis, Inventario inventario, Mappa mappa, StoryEngine storyEngine) {
        StatoGioco s = new StatoGioco();

        // Posizione / piano
        s.setPlayerX(lewis.getPosizioneX());
        s.setPlayerY(lewis.getPosizioneY());
        s.setCurrentFloor(lewis.getCurrentFloor());
        s.setStanzeVisitate(new HashSet<>(lewis.getStanzeVisitate()));
        s.setStanzeCompletate(new HashSet<>(lewis.getStanzeCompletate()));


        // Inventario (ID "puliti" senza emoji)
        List<String> ids = new ArrayList<>();
        for (String o : inventario.getOggetti()) {
            String id = o.contains(" ") ? o.substring(0, o.indexOf(" ")) : o;
            ids.add(id);
        }
        s.setInventarioOggetti(ids);

        // Story flags
        s.setSceneInizialiCompletate(storyEngine.isSceneInizialiCompletate());
        s.setCodiceSegretoLetto(lewis.isCodiceSegretoLetto());
        s.setCodiceSegretoSbloccato(lewis.isCodiceSegretoSbloccato());
        s.setScannerAssemblato(lewis.isScannerAssemblato());
        s.setBalconeCrollato(lewis.isBalconeCrollato());

        // Contenuto di ogni stanza (nome → oggetto o null)
        Map<String, String> oggettoPerStanza = new HashMap<>();
        for (var stanza : mappa.getTutteLeStanze()) {
            oggettoPerStanza.put(stanza.getNome(), stanza.getOggetto());
        }
        s.setOggettoPerStanza(oggettoPerStanza);

        // Elenco stanze senza oggetto
        Set<String> senzaOggetto = new HashSet<>();
        for (var stanza : mappa.getTutteLeStanze()) {
            if (stanza.getOggetto() == null) senzaOggetto.add(stanza.getNome());
        }
        s.setStanzeSenzaOggetto(senzaOggetto);

        Map<String, Boolean> chiusaPerStanza = new HashMap<>();
        for (var stanza : mappa.getTutteLeStanze()) {
            chiusaPerStanza.put(stanza.getNome(), stanza.isChiusa());
        }
        s.setStanzaChiusa(chiusaPerStanza);

        return s;
    }


    /**
     *     // --------- CARICAMENTO ---------
     * Applica uno StatoGioco agli oggetti di gioco attivi.
     * @param s
     * @param lewis
     * @param inventario
     * @param mappa
     * @param storyEngine
     */
    public static void applyTo(StatoGioco s, Lewis lewis, Inventario inventario, Mappa mappa, StoryEngine storyEngine) {
        // Story flags
        storyEngine.setSceneInizialiCompletate(s.isSceneInizialiCompletate());
        lewis.setCodiceSegretoLetto(s.isCodiceSegretoLetto());
        lewis.setCodiceSegretoSbloccato(s.isCodiceSegretoSbloccato());
        lewis.setScannerAssemblato(s.isScannerAssemblato());
        lewis.setStanzeVisitate(s.getStanzeVisitate());
        lewis.setStanzeCompletate(s.getStanzeCompletate());
        lewis.setBalconeCrollato(s.isBalconeCrollato());


        // Posizione/piano
        lewis.spostamentoPiano(s.getCurrentFloor(), new Posizione(s.getPlayerX(), s.getPlayerY()));

        // Inventario (autoritatvo)
        inventario.svuota();
        var items = s.getInventarioOggetti();
        if (items != null) {
            for (String id : items) inventario.aggiungi(id);
        }

        // Stato oggetti per stanza (fotografia 1:1)
        Map<String, String> mappaOggetti = s.getOggettoPerStanza();
        if (mappaOggetti != null) {
            for (var stanza : mappa.getTutteLeStanze()) {
                String wanted = mappaOggetti.get(stanza.getNome()); // può essere null
                stanza.setOggetto(wanted);
            }
        } else if (s.getStanzeSenzaOggetto() != null) {
            // fallback per vecchi salvataggi
            Set<String> set = s.getStanzeSenzaOggetto();
            for (var stanza : mappa.getTutteLeStanze()) {
                if (set.contains(stanza.getNome())) stanza.setOggetto(null);
            }
        }

        Map<String, Boolean> chiusaPerStanza = s.getStanzaChiusa();
        if (chiusaPerStanza != null) {
            for (var stanza : mappa.getTutteLeStanze()) {
                Boolean chiusa = chiusaPerStanza.get(stanza.getNome());
                if (chiusa != null) stanza.setChiusa(chiusa);
            }
        }

        Stanza stanzaSegreta = null;
        for (var st : mappa.getTutteLeStanze()) {
            if ("Stanza Segreta".equalsIgnoreCase(st.getNome())) {
                stanzaSegreta = st;
                break;
            }
        }
        if (stanzaSegreta != null) {
            // se NON hai inserito il codice → porta chiusa; se sì → porta aperta
            stanzaSegreta.setChiusa(!s.isCodiceSegretoSbloccato());
        }
    }
}
