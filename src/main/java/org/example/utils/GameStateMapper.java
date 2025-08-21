package org.example.utils;

import org.example.mappa.Posizione;
import org.example.mappa.Stanza;
import org.example.model.StatoGioco;
import org.example.model.Lewis;
import org.example.inventario.Inventario;
import org.example.mappa.Mappa;
import org.example.story.StoryEngine;

import java.util.*;

public final class GameStateMapper {

    private GameStateMapper() {}

    // --------- SALVATAGGIO ---------
    public static StatoGioco buildFrom(Lewis lewis, Inventario inventario, Mappa mappa, StoryEngine storyEngine) {
        StatoGioco s = new StatoGioco();
        s.setPlayerX(lewis.getPosizioneX());
        s.setPlayerY(lewis.getPosizioneY());
        s.setCurrentFloor(lewis.getCurrentFloor());
        // salva gli ID "puliti": estrai prima dello spazio (per rimuovere le emoji)
        List<String> ids = new ArrayList<>();
        for (String o : inventario.getOggetti()) {
            String id = o.contains(" ") ? o.substring(0, o.indexOf(" ")) : o;
            ids.add(id);
        }
        s.setInventarioOggetti(ids);
        s.setSceneInizialiCompletate(storyEngine.isSceneInizialiCompletate());
        s.setCodiceSegretoLetto(lewis.isCodiceSegretoLetto());
        s.setCodiceSegretoSbloccato(lewis.isCodiceSegretoSbloccato());
        s.setScannerAssemblato(lewis.isScannerAssemblato());

        // NUOVO: fotografa il contenuto di ogni stanza (nome → oggetto o null)
        Map<String, String> oggettoPerStanza = new HashMap<>();
        for (var stanza : mappa.getTutteLeStanze()) {
            oggettoPerStanza.put(stanza.getNome(), stanza.getOggetto()); // può essere null
        }
        s.setOggettoPerStanza(oggettoPerStanza);

        // NUOVO: stanze senza oggetto
        Set<String> senzaOggetto = new HashSet<>();
        for (var stanza : mappa.getTutteLeStanze()) {
            if (stanza.getOggetto() == null) {
                senzaOggetto.add(stanza.getNome());
            }
        }
        s.setStanzeSenzaOggetto(senzaOggetto);
        return s;
    }

    // --------- CARICAMENTO ---------
    public static void applyTo(StatoGioco s, Lewis lewis, Inventario inventario, Mappa mappa, StoryEngine storyEngine) {
        // storia
        storyEngine.setSceneInizialiCompletate(s.isSceneInizialiCompletate());

        // piano + posizione
        // usa l'API già esistente: sposta piano E posizione in un colpo
        lewis.spostamentoPiano(s.getCurrentFloor(), new Posizione(s.getPlayerX(), s.getPlayerY()));
        lewis.setCodiceSegretoLetto(s.isCodiceSegretoLetto());
        lewis.setCodiceSegretoSbloccato(s.isCodiceSegretoSbloccato());
        lewis.setScannerAssemblato(s.isScannerAssemblato());

        // NEW: forza lo stato della porta della Stanza Segreta in base al flag
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

        // inventario
        inventario.svuota();
        var items = s.getInventarioOggetti();
        if (items != null) {
            for (String id : items) inventario.aggiungi(id);
        }

        // 4) stato delle stanze – ripristino 1:1
        Map<String, String> mappaOggetti = s.getOggettoPerStanza();
        if (mappaOggetti != null) {
            for (var stanza : mappa.getTutteLeStanze()) {
                String wanted = mappaOggetti.get(stanza.getNome()); // può essere null
                stanza.setOggetto(wanted);
            }
        } else {
            // fallback (vecchi salvataggi): rimuovi oggetti dalle stanze segnate
            if (s.getStanzeSenzaOggetto() != null) {
                Set<String> set = s.getStanzeSenzaOggetto();
                for (var stanza : mappa.getTutteLeStanze()) {
                    if (set.contains(stanza.getNome())) stanza.setOggetto(null);
                }
            }
        }
    }
}
