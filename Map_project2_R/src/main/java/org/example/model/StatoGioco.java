package org.example.model;

import org.example.inventario.Inventario;
import org.example.mappa.Mappa;
import org.example.story.StoryEngine;

import java.io.Serializable;

public class StatoGioco implements Serializable {
    private static final long serialVersionUID = 1L;

    private Lewis lewis;
    private Inventario inventario;
    private Mappa mappa;
    private StoryEngine storyEngine;

    public StatoGioco(Lewis lewis, Inventario inventario, Mappa mappa, StoryEngine storyEngine) {
        this.lewis = lewis;
        this.inventario = inventario;
        this.mappa = mappa;
        this.storyEngine = storyEngine;
    }

    public Lewis getLewis() {
        return lewis;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public Mappa getMappa() {
        return mappa;
    }

    public StoryEngine getStoryEngine() {
        return storyEngine;
    }
}
