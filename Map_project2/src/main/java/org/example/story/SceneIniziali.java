package org.example.story;

import java.util.ArrayList;
import java.util.List;

public class SceneIniziali {

    public static List<Scena> creaScene() {
        List<Scena> scene = new ArrayList<>();

        Scena introduzione = new Scena(
                "introduzione",
                0,
                "Un'invenzione per il passato",
                "Lewis è un brillante e giovanissimo inventore, orfano di circa 12 anni e vive in un orfanotrofio in stanza con un vero talento sportivo di nome Michael, un po' più piccolo di lui, ma molto fragile e insicuro di se.\n"+
                        "Lewis sta creando uno scanner mnemonico per poter trovare, scavando nei ricordi, il giorno in cui sua madre l'abbandonò per poterla riconoscere e ritrovarla con la convinzione che sia l'unica persona a volerlo davvero."
        );

        Scena fallimentoPresentazione = new Scena(
                "fallimento_presentazione",
                1,
                "Un errore catastrofico",
                "Il giovane troverà alcuni bug nel suo congegno facendo fallire la sua presentazione alla mostra della scienza scolastica dove verrà deriso ed umiliato. \n" +
                        " \"incontrerà\" l' \"Uomo con la bombetta\", un uomo arrivato dal futuro con una macchina del tempo spaziale, per rubargli il congegno...\n"
        );

        Scena incontroWilbur = new Scena(
                "incontro_wilbur",
                2,
                "Un ragazzo misterioso",
                "In soccorso del giovane Lewis c'è un altro ragazzo, proveniente dal futuro, di nome Wilbur che è arrivato nel passato alla ricerca della sua macchina del tempo spaziale, rubatagli pochi giorni prima dall'\"Uomo con la bombetta\".\n" +
                        "Essendo Wilbur a conoscenza dei malvagi piani dell'\"Uomo con la bombetta\" decide di aiutare Lewis a ritrovare i pezzi dell'invenzione, in cambio lui stesso lo aiuterà a trovare la macchina del tempo rubata.\n"
        );

        Scena ilViaggio = new Scena(
                "il_viaggio",
                3,
                "Il viaggio nel tempo",
                "Wilbur apre un portale per poter viaggiare nel tempo e ritornare nel futuro per poter recuperare l'invenzione di Lewis. Il viaggio inizia.....\n"
        );

        Scena loSchianto = new Scena(
                "lo_schianto",
                4,
                "Benvenuto nella nuova dimensione temporale!\nIl viaggio nel tempo è stato completato con successo.\n\nLo schianto",
                "L'\"uomo con la bombetta\", riuscito nel suo piano malvagio di rubare l'invenzione, decide di ritornare nel futuro, ma un guasto " +
                        "alla sua macchina del tempo spaziale gli rovinerà i piani, facendolo schiantare e rompendo in tanti pezzi l'invenzione di Lewis. \n" +
                        "Wilbur e Lewis appena arrivati nel futuro, si ritrovano davanti a un panorama desolante, con la macchina del tempo distrutta e i pezzi sparsi in una casa abbandonata.\n" +
                        "I due ragazzi dovranno cercare di recuperare i pezzi dell'invenzione per poter tornare nel passato e fermare l'\"Uomo con la bombetta\".\n"+
                        "Ma non tutto è come sembra, e i due ragazzi dovranno affrontare molte sfide e pericoli per poter raggiungere il loro obiettivo.\n" +
                        "INIZIA L'AVVENTURA!"
        );

        Scena laCasaAbbandonata = new Scena(
                "la_casa_abbandonata",
                5,
                "SEI NELLA CASA ABBANDONATA",
                "Wilbur: \"Guarda questa casa abbandonata ha la porta aperta, sembra essere l'unico posto dove possiamo trovare i pezzi della tua invenzione.\""+
                        "\n\nLewis: \"Sembra che l'\"Uomo con la bombetta\" abbia lasciato dei pezzi della mia invenzione qui dentro, dobbiamo cercarli e metterli insieme per poter tornare nel passato e fermarlo.\""+
                        "\n\nWilbur: \"Dobbiamo fare attenzione, non sappiamo cosa ci aspetta qui dentro.\""
        );

        Scena soggiorno = new Scena(
                "soggiorno",
                6,
                "SEI NEL SOGGIORNO",
                "Wilbur: \"Siamo nel soggiorno, qui ci sono delle impronte che portano verso la cucina e guarda un po' proprio qui c'è lo shceletro del tuo scanner mnemonico. \""+
                        "\n\nLewis: \"Ottimo, guarda un po', sembra che in fondo alla stanza ci sia un pezzo della mia invenzione... è proprio la mia elica!!\""
        );

        Scena soggiorno2 = new Scena(
                "soggiorno_2",
                7,
                "",
                "Lewis: \"Ho recuperato un pezzo della mia invenzione, ma ne mancano tanti altri.\""+
                        "\n\nWilbur: \"Dobbiamo continuare a cercare, non possiamo fermarci ora... seguiamo le impronte.\""
        );

        Scena cucina = new Scena(
                "cucina",
                8,
                "SEI NELLA CUCINA",
                "Wilbur: \"Che hai trovato Lewis?\""+
                        "\n\nLewis: \"Niente di che, solo cianfrusaglie e robaccia vecchia.\""+
                        "\n\nWilbur: \"Non possiamo perdere tempo, dobbiamo trovare i pezzi della tua invenzione.\""+
                        "\n\nLewis: \"Hai ragione, dobbiamo continuare a cercare. Dovrei provare ad aprire il forno magari è lì che nascondono gli oggetti i cattivi!\""+
                        "\n\nWilbur: \"Buona idea!\""
        );

        Scena cucina2 = new Scena(
                "cucina_2",
                9,
                "",
                "Lewis: \"TROVATO! Te l'avevo detto che guardare film ci avrebbe aiutati.\""+
                        "\n\nWilbur: \"Cosa hai trovato?\""+
                        "\n\nLewis: \"Ho trovato un pezzo della mia invenzione, sembra essere lo schermo.\""+
                        "\n\nWilbur: \"Uno schermo?! Cosa ti serve uno schermo su uno scanner mnemonico?\""+
                        "\n\nLewis: \"Senza lo schermo non posso vedere i ricordi, è fondamentale per il funzionamento dello scanner.\""+
                        "\n\nWilbur: \"Vero hai ragione, basta blaterare continuamo a cercare. Ora però a quanto pare le impronte sono molto confusionarie...\""+
                        "\n\nLewis: \"Proviamo ad andare verso il bagno per vedere cosa riusciamo a trovare.\""
        );

        Scena bagno = new Scena(
                "bagno",
                10,
                "Nel momento in cui entrano nel bagno, Wilbur e Lewis notano che è tutto in disordine, come se qualcuno avesse cercato qualcosa in fretta. \n" +
                        "Ci sono medicinali sparsi ovunque e un odore strano nell'aria. Un forte botto fa spaventare i ragazzi. LA PORTA SI CHIUDE! Dovranno trovare un modo per uscire. \n",
                "Wilbur: \"Siamo nel bagno, qui non sembra esserci un pezzo della tua invenzione.\""+
                        "\n\nLewis: \"Mi sembra proprio così... aspetta, guarda lì!\""+
                        "\n\nWilbur: \"Cosa hai trovato?\""+
                        "\n\nLewis: \"Una chiave, con scritto \"STANZA MATRIMONIALE\". Dovrei prenderla! \""
        );

        Scena bagno2 = new Scena(
                "bagno_2",
                11,
                "",
                "\nWilbur: \"Forse potrebbe essere utile, magari ci sono altri pezzi della tua invenzione lì dentro.\"" +
                        "\n\nLewis: \"Sì, dobbiamo andare a cercare nella stanza matrimoniale, ma prima dobbiamo trovare un modo per uscire da qui.\"" +
                        "\n\nWilbur: \"Hai ragione, dobbiamo trovare un modo per aprire questa porta.\"" +
                        "\n\nLewis: \"Proviamo a usare la chiave che abbiamo trovato, magari funziona.\"" +
                        "\n\nWilbur: \"Buona idea, proviamoci subito!\""+
                        "\n\nLewis: \"Non funziona caspita!\""+
                        "\n\nWilbur: \"Dobbiamo trovare un altro modo per uscire. Possiamo provare a sfondarla...\""+
                        "\n\nLewis: \"No, non possiamo fare rumore, dobbiamo essere silenziosi. Proviamo a cercare un altro modo per uscire.\""+
                        "\n\nWilbur: \"Hai ragione, dobbiamo essere cauti. Proviamo a cercare un'altra via d'uscita.\""+
                        "\n\nLewis: \"Guarda in alto c'è una piccola finestra che si affaccia sul soggiorno potremmo provare a entrarci per uscire.\""+
                        "\n\nWilbur: \"Buona idea, proviamoci subito!\""+
                        "\n\nLewis: \"Ti faccio leva con le mani. \""+
                        "\n\nWilbur: \"Ok, ci provo!\""+
                        "\n\nLewis: \"Ok ora che sei dall'altra parte, apri la porta!\""+
                        "\n\nWilbur: \"Fatto! Ora possiamo continuare ad esplorare la casa. Andiamo al secondo piano. \""
        );

        Scena scale = new Scena(
                "scale",
                12,
                "",
                "Wilbur: \"Siamo arrivati alle scale, dobbiamo salire al secondo piano per cercare altri pezzi della tua invenzione.\""+
                        "\n\nLewis: \"Ok!\""
        );

        Scena stanzaSegreta = new Scena(
                "stanza_segreta",
                13,
                "",
                ""
        );

        Scena ilSecondoPiano = new Scena(
                "il_secondo_piano",
                14,
                "",
                ""
        );

        Scena ilPrimoPiano = new Scena(
                "il_primo_piano",
                15,
                "",
                ""
        );

        Scena cameraMatrimoniale = new Scena(
                "camera_matrimoniale",
                16,
                "",
                ""
        );

        Scena balcone = new Scena(
                "balcone",
                17,
                "",
                ""
        );

        Scena cameraBambini = new Scena(
                "camera_bambini",
                18,
                "",
                ""
        );

        Scena corridoio = new Scena(
                "corridoio",
                19,
                "",
                ""
        );

        Scena stanzaOspiti = new Scena(
                "stanza_ospiti",
                20,
                "",
                ""
        );

        scene.add(introduzione);
        scene.add(fallimentoPresentazione);
        scene.add(incontroWilbur);
        scene.add(ilViaggio);
        scene.add(loSchianto);
        scene.add(laCasaAbbandonata);
        scene.add(soggiorno);
        scene.add(soggiorno2);
        scene.add(cucina);
        scene.add(cucina2);
        scene.add(bagno);
        scene.add(bagno2);
        scene.add(scale);
        scene.add(stanzaSegreta);
        scene.add(ilSecondoPiano);
        scene.add(ilPrimoPiano);
        scene.add(cameraMatrimoniale);
        scene.add(balcone);
        scene.add(cameraBambini);
        scene.add(corridoio);
        scene.add(stanzaOspiti);

        return scene;
    }
}