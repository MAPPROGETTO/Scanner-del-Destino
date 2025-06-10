package org.example.story;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SceneIniziali {

    public static List<Scena> creaScene() {
        List<Scena> scene = new ArrayList<>();

        Scena introduzione = new Scena(
                "Un'invenzione per il passato",
                "Lewis, un giovane inventore orfano, sta cercando di costruire uno scanner mnemonico per ritrovare il volto di sua madre.\n" +
                        "Nel frattempo, l'orfanotrofio è in fermento per la mostra della scienza...",
                Arrays.asList(
                        "Continua a lavorare al progetto tutta la notte",
                        "Parla con Michael della sua ansia per la gara"
                )
        );

        Scena fallimentoPresentazione = new Scena(
                "Un errore catastrofico",
                "Il giorno della mostra arriva. Ma lo scanner si blocca! Lewis viene deriso e umiliato...",
                Arrays.asList(
                        "Scappa dall'auditorium",
                        "Cerca di riparare subito il dispositivo"
                )
        );

        Scena incontroWilbur = new Scena(
                "Un ragazzo misterioso",
                "Nel caos, un ragazzo strano di nome Wilbur appare da una porta luminosa... dice di venire dal futuro!",
                Arrays.asList(
                        "Fuggi, è troppo strano!",
                        "Ascolta cosa ha da dire"
                )
        );

        scene.add(introduzione);
        scene.add(fallimentoPresentazione);
        scene.add(incontroWilbur);

        return scene;
    }
}