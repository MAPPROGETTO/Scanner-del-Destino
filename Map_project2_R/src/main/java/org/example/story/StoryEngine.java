package org.example.story;

import java.util.List;
import java.util.Optional;

public class StoryEngine {

    private final SceneManager sceneManager;
    private int indiceScenaIniziale = 0;
    private int indiceDialogoFinale = 0;
    private int indiceFinale = 0;

    public StoryEngine(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    // === Scene iniziali ===
    public Optional<Scena> prossimaScenaIniziale() {
        List<Scena> iniziali = sceneManager.getSceneIniziali();
        if (indiceScenaIniziale < iniziali.size()) {
            return Optional.of(iniziali.get(indiceScenaIniziale++));
        }
        return Optional.empty();
    }

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

    public boolean haAltreSceneIniziali() {
        return indiceScenaIniziale < sceneManager.getSceneIniziali().size();
    }

    public void resetSceneIniziali() {
        indiceScenaIniziale = 0;
    }

    // === Dialogo con il boss ===
    public Optional<Scena> prossimaBattutaBoss() {
        List<Scena> dialogo = sceneManager.getDialogoFinale();
        if (indiceDialogoFinale < dialogo.size()) {
            return Optional.of(dialogo.get(indiceDialogoFinale++));
        }
        return Optional.empty();
    }

    public void resetDialogoFinale() {
        indiceDialogoFinale = 0;
    }

    public boolean haAltreBattuteBoss() {
        return indiceDialogoFinale < sceneManager.getDialogoFinale().size();
    }

    // === Epilogo ===
    public Optional<Scena> prossimaScenaFinale() {
        List<Scena> finale = sceneManager.getSceneFinale();
        if (indiceFinale < finale.size()) {
            return Optional.of(finale.get(indiceFinale++));
        }
        return Optional.empty();
    }

    public void resetFinale() {
        indiceFinale = 0;
    }

    public boolean haAltreSceneFinali() {
        return indiceFinale < sceneManager.getSceneFinale().size();
    }

    public boolean puoiTornareIndietro() {
        return indiceScenaIniziale > 1;
    }
}
