package org.example.story;
import java.io.Serializable;
import java.util.*;

/**
 * Gestisce le scene della storia, inclusi caricamento, accesso e organizzazione.
 * Le scene sono categorizzate in iniziali, default, eventi, completamenti, uso oggetti e finali.
 * Implementa Serializable per permettere la serializzazione dell'oggetto.
 *
 */
public class SceneManager implements Serializable {

    private final List<Scena> sceneIniziali = new ArrayList<>();
    private final Map<String, Scena> sceneDefault = new HashMap<>();
    private final Map<String, Scena> sceneEvento = new HashMap<>();
    private final Map<String, Scena> sceneCompletamento = new HashMap<>();
    private final Map<String, Scena> sceneUsoOggetti = new HashMap<>();
    private final List<Scena> dialogoFinale = new ArrayList<>();
    private final List<Scena> sceneFinale = new ArrayList<>();

    private int indiceIniziale = 1;
    private int indiceGlobale = 1;
    private static final long serialVersionUID = 1L;

    // === Metodi di caricamento ===

    /**
         * Aggiunge una scena di tipo INIZIALE.
     * La chiave è la descrizione in minuscolo.
     * @param descrizioni Descrizione della stanza
     */
    public void aggiungiSceneIniziali(String... descrizioni) {
        for (String desc : descrizioni) {
            sceneIniziali.add(new Scena("ScenaIniziale" + indiceIniziale, indiceIniziale, "Introduzione", desc, Scena.TipoScena.INIZIALE));
            indiceIniziale++;
        }
    }

    // === Metodi di accesso ===


    public Optional<Scena> getSceneUsoOggetto(String stanza, String oggetto) {
        return Optional.ofNullable(sceneUsoOggetti.get(stanza + ":" + oggetto));
    }


    public List<Scena> getSceneIniziali() {
        return Collections.unmodifiableList(sceneIniziali);
    }

    public Optional<Scena> getSceneDefault(String stanza) {
        return Optional.ofNullable(sceneDefault.get(stanza.toLowerCase()));
    }

    public Optional<Scena> getSceneEvento(String evento) {
        return Optional.ofNullable(sceneEvento.get(evento.toLowerCase()));
    }

    public Optional<Scena> getSceneCompletamento(String stanza) {
        return Optional.ofNullable(sceneCompletamento.get(stanza.toLowerCase()));
    }

    public List<Scena> getDialogoFinale() {
        return Collections.unmodifiableList(dialogoFinale);
    }

    public List<Scena> getSceneFinale() {
        return Collections.unmodifiableList(sceneFinale);
    }

    /** Ritorna una lista di scene mappate alle posizioni della mappa.
     * Le posizioni senza scena sono impostate a null.
     * @return Lista di scene per la mappa
     */
    public List<Scena> getScenePerMappa() {
        List<Scena> scene = new ArrayList<>(Collections.nCopies(21, null));

        scene.set(6, sceneDefault.get("salotto"));
        scene.set(8, sceneDefault.get("cucina"));
        scene.set(10, sceneDefault.get("bagno"));
        scene.set(12, sceneDefault.get("scale"));
        scene.set(13, sceneDefault.get("stanza segreta"));
        scene.set(16, sceneDefault.get("stanza matrimoniale"));
        scene.set(17, sceneDefault.get("balcone"));
        scene.set(18, sceneDefault.get("stanza dei bambini"));
        scene.set(19, sceneDefault.get("corridoio"));
        scene.set(20, sceneDefault.get("stanza degli ospiti"));


        return scene;
    }

    // Aggiunge le scene finali (riusa il contenitore esistente: sceneFinale)
    /**
     * Aggiunge scene finali numerate.
     * La chiave è "Finale" + numero.
     * @param descrizioni Descrizioni delle scene finali
     */
    public void aggiungiSceneFinali(String... descrizioni) {
        int i = 1;
        for (String desc : descrizioni) {
            if (desc == null || desc.isBlank()) continue;
            sceneFinale.add(new Scena("Finale" + i, indiceGlobale++, "Finale", desc, Scena.TipoScena.FINALE));
            i++;
        }
    }
}
