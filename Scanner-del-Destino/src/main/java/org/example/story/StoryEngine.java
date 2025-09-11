package org.example.story;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Gestisce la progressione della storia, incluse le scene iniziali e il dialogo finale con il boss.
 * Utilizza SceneManager per accedere alle scene e tiene traccia dell'indice corrente per ogni tipo di scena.
 * Implementa Serializable per permettere la serializzazione dell'oggetto.
 *
 */
public class StoryEngine implements Serializable {

    private final SceneManager sceneManager;
    private int indiceScenaIniziale = 0;
    private int indiceDialogoFinale = 0;
    private int indiceFinale = 0;
    private static final long serialVersionUID = 1L;
    private boolean sceneInizialiCompletate = false;

    /** Costruttore per StoryEngine.
     * @param sceneManager Il gestore delle scene da utilizzare.
     */
    public StoryEngine(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    // === Scene iniziali ===
    /**
     * Ritorna la prossima scena iniziale se disponibile.
     * Avanza l'indice della scena iniziale.
     * @return Optional contenente la prossima scena iniziale o vuoto se non ci sono più scene.
     */
    public Optional<Scena> prossimaScenaIniziale() {
        List<Scena> iniziali = sceneManager.getSceneIniziali();
        if (indiceScenaIniziale < iniziali.size()) {
            return Optional.of(iniziali.get(indiceScenaIniziale++));
        }
        return Optional.empty();
    }

    /**
     * Ritorna la scena iniziale precedente se disponibile.
     * Decrementa l'indice della scena iniziale.
     * @return Optional contenente la scena iniziale precedente o vuoto se non ci sono scene precedenti.
     */
    public Optional<Scena> precedenteScenaIniziale() {
        List<Scena> iniziali = sceneManager.getSceneIniziali();
        System.out.println(indiceScenaIniziale);
        if (indiceScenaIniziale > 1) { // >1 perché dopo prossimaScenaIniziale() l'indice è già avanzato
            indiceScenaIniziale -=2;
            
            System.out.println(indiceScenaIniziale);
            return Optional.of(iniziali.get(indiceScenaIniziale++));
        }
        return Optional.empty();
    }

    /**
     * Verifica se ci sono altre scene iniziali disponibili.
     * @return true se ci sono altre scene iniziali, false altrimenti.
     */
    public boolean haAltreSceneIniziali() {
        return indiceScenaIniziale < sceneManager.getSceneIniziali().size();
    }
    // === Dialogo con il boss ===
    /**
     * Ritorna la prossima battuta del dialogo con il boss se disponibile.
     * Avanza l'indice del dialogo finale.
     * @return Optional contenente la prossima battuta del dialogo con il boss o vuoto se non ci sono più battute.
     */
    public Optional<Scena> prossimaBattutaBoss() {
        List<Scena> dialogo = sceneManager.getDialogoFinale();
        if (indiceDialogoFinale < dialogo.size()) {
            return Optional.of(dialogo.get(indiceDialogoFinale++));
        }
        return Optional.empty();
    }

    /**
     * Ritorna la prossima scena finale se disponibile.
     * Avanza l'indice della scena finale.
     * @return Optional contenente la prossima scena finale o vuoto se non ci sono più scene.
     */
    public Optional<Scena> precedenteBattutaBoss() {
        List<Scena> dialogo = sceneManager.getDialogoFinale();
        if (indiceDialogoFinale > 1) {
            indiceDialogoFinale -= 2;
            return Optional.of(dialogo.get(indiceDialogoFinale++));
        }
        return Optional.empty();
    }

    /**
     * Verifica se ci sono altre battute del dialogo con il boss disponibili.
     * @return true se ci sono altre battute del dialogo con il boss, false altrimenti.
     */
    public boolean puoiTornareIndietroDialogo() {
        return indiceDialogoFinale > 1;
    }


    /**
     * Verifica se ci sono altre scene finali disponibili.
     * @return true se ci sono altre scene finali, false altrimenti.
     */
    public void resetDialogoFinale() {
        indiceDialogoFinale = 0;
    }

    /** Ritorna la prossima scena finale se disponibile.
     * Avanza l'indice della scena finale.
     * @return Optional contenente la prossima scena finale o vuoto se non ci sono più scene.
     */
    public boolean puoiTornareIndietro() {
        return indiceScenaIniziale > 1;
    }

    /** Verifica se ci sono altre scene finali disponibili.
     * @return true se ci sono altre scene finali, false altrimenti.
     */
    public boolean puoiTornareIndietroFinale() {
        return indiceFinale > 1;
    }

    /** Ritorna la prossima scena finale se disponibile.
     * Avanza l'indice della scena finale.
     * @return Optional contenente la prossima scena finale o vuoto se non ci sono più scene.
     */
    public boolean isSceneInizialiCompletate() { return sceneInizialiCompletate; }
    public void setSceneInizialiCompletate(boolean v) { this.sceneInizialiCompletate = v; }
    public void completaSceneIniziali() { this.sceneInizialiCompletate = true; }
    public SceneManager getSceneManager() {return sceneManager;}
}