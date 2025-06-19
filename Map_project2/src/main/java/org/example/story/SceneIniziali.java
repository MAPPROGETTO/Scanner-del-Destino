package org.example.story;

import org.example.gui.TimePortalSplashScreen;

import java.util.ArrayList;
import java.util.List;

public class SceneIniziali {

    public static List<Scena> creaScene() {
        List<Scena> scene = new ArrayList<>();

        Scena introduzione = new Scena(
                "Un'invenzione per il passato",
                "Lewis \u00E9 un brillante e giovanissimo inventore, orfano di circa 12 anni e vive in un orfanotrofio in stanza con un vero talento sportivo di nome Michael, un po' pi\u00F9 piccolo di lui, ma molto fragile e insicuro di se.\n"+
                          "Lewis sta creando uno scanner mnemonico per poter trovare, scavando nei ricordi, il giorno in cui sua madre l'abbandon\u00F2 per poterla riconoscere e ritrovarla con la convinzione che sia l'unica persona a volerlo davvero."
        );

        Scena fallimentoPresentazione = new Scena(
                "Un errore catastrofico",
                "Il giovane trover\u00E0 alcuni bug nel suo congegno facendo fallire la sua presentazione alla mostra della scienza scolastica dove verr\u00E0 deriso ed umiliato. \n" +
                        " \"incontrer\u00E0\" l' \"Uomo con la bombetta\", un uomo arrivato dal futuro con una macchina del tempo spaziale, per rubargli il congegno...\n"
        );

        Scena incontroWilbur = new Scena(
                "Un ragazzo misterioso",
                "In soccorso del giovane Lewis c'\u00E9 un altro ragazzo, proveniente dal futuro, di nome Wilbur che \u00E9 arrivato nel passato alla ricerca della sua macchina del tempo spaziale, rubatagli pochi giorni prima dall'\"Uomo con la bombetta\".\n" +
                        "Essendo Wilbur a conoscenza dei malvagi piani dell'\"Uomo con la bombetta\" decide di aiutare Lewis a ritrovare i pezzi dell'invenzione, in cambio lui stesso lo aiuter\u00E0 a trovare la macchina del tempo rubata.\n"
        );

        Scena ilViaggio = new Scena(
                "Il viaggio nel tempo",
                "Wilbur apre un portale per poter viaggiare nel tempo e ritornare nel futuro per poter recuperare l'invenzione di Lewis. Il viaggio inizia.....\n"
        );

        Scena loSchianto = new Scena(
                "Benvenuto nella nuova dimensione temporale!\nIl viaggio nel tempo \u00E9 stato completato con successo.\n\nLo schianto",
                "L'\"uomo con la bombetta\", riuscito nel suo piano malvagio di rubare l'invenzione, decide di ritornare nel futuro, ma un guasto " +
                        "alla sua macchina del tempo spaziale gli roviner\u00E0 i piani, facendolo schiantare e spargendo i pezzi dell'invenzione di Lewis in giro per la citt\u00E0.\n"
        );

        scene.add(introduzione);
        scene.add(fallimentoPresentazione);
        scene.add(incontroWilbur);
        scene.add(ilViaggio);
        scene.add(loSchianto);

        return scene;
    }
}