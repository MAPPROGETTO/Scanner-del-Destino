package org.example.story;
import java.util.*;

public class SceneManager {

    private final List<Scena> sceneIniziali = new ArrayList<>();
    private final Map<String, Scena> sceneDefault = new HashMap<>();
    private final Map<String, Scena> sceneEvento = new HashMap<>();
    private final Map<String, Scena> sceneCompletamento = new HashMap<>();
    private final List<Scena> dialogoFinale = new ArrayList<>();
    private final List<Scena> sceneFinale = new ArrayList<>();

    private int indiceIniziale = 1;
    private int indiceGlobale = 1;
    private boolean sceneDefaultCaricate = false;

    // === Metodi di caricamento ===

    public void aggiungiSceneIniziali(String... descrizioni) {
        for (String desc : descrizioni) {
            sceneIniziali.add(new Scena("ScenaIniziale" + indiceIniziale, indiceIniziale, "Introduzione", desc, Scena.TipoScena.INIZIALE));
            indiceIniziale++;
        }
    }

    public void aggiungiSceneDefault(String stanza, String descrizione) {
        sceneDefault.put(stanza.toLowerCase(), new Scena(stanza, indiceGlobale++, "Luogo: " + stanza, descrizione, Scena.TipoScena.DEFAULT));
    }

    public void aggiungiSceneEvento(String evento, String descrizione) {
        sceneEvento.put(evento.toLowerCase(), new Scena(evento, indiceGlobale++, "Evento: " + evento, descrizione, Scena.TipoScena.EVENTO));
    }

    public void aggiungiSceneCompletamento(String stanza, String descrizione) {
        sceneCompletamento.put(stanza.toLowerCase(), new Scena("Fine_" + stanza, indiceGlobale++, "Completamento", descrizione, Scena.TipoScena.COMPLETAMENTO));
    }

    public void aggiungiDialogoFinale(String... battute) {
        int i = 1;
        for (String battuta : battute) {
            dialogoFinale.add(new Scena("Dialogo" + i, indiceGlobale++, "Dialogo", battuta, Scena.TipoScena.DIALOGO));
            i++;
        }
    }

    public void aggiungiSceneFinale(String... scene) {
        int i = 1;
        for (String testo : scene) {
            sceneFinale.add(new Scena("Finale" + i, indiceGlobale++, "Finale", testo, Scena.TipoScena.FINALE));
            i++;
        }
    }

    // === Metodi di accesso ===

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


    // === Utility ===

    public void reset() {
        sceneIniziali.clear();
        sceneDefault.clear();
        sceneEvento.clear();
        sceneCompletamento.clear();
        dialogoFinale.clear();
        sceneFinale.clear();
        indiceIniziale = 1;
        indiceGlobale = 1;
    }


    public boolean haCaricatoSceneDefault() {
        return sceneDefaultCaricate;
    }
}
