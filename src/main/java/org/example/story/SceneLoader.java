package org.example.story;

import java.io.Serializable;

public class SceneLoader implements Serializable {

    private final SceneManager sceneManager;

    public SceneLoader(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void caricaTutteLeScene() {
        caricaSceneIniziali();
        caricaSceneDefault();
        caricaSceneEvento();
        caricaSceneCompletamento();
        caricaSceneUsoOggetto();
        caricaDialogoFinale();
    }

    private void caricaSceneIniziali() {
        sceneManager.aggiungiSceneIniziali(
                "Una pioggia sottile batte contro le finestre dell’orfanotrofio. Lewis è piegato sul suo banco, intento a stringere un piccolo bullone. \"Ancora pochi ritocchi e sarai perfetto...\" sussurra allo Scanner Mnemonico. \n Michael entra nella stanza correndo: \"Lewis! La fiera della scienza inizia tra dieci minuti!\". Lewis alza lo sguardo, sorride nervosamente: \"Giusto il tempo di...\". Un lampo accecante attraversa il cielo.",
                "Alla fiera, lo Scanner si accende... ma qualcosa va storto. Fumo, scintille, il pubblico mormora. Dal fumo emerge una figura elegante, bombetta in testa e un ghigno sinistro. \"Prenderò questo, grazie.\" \nWilbur piomba sulla scena. \"Fermati!\" urla, ma l'uomo è già sparito. Wilbur si avvicina a Lewis: \"Sei tu Lewis? Ho bisogno del tuo aiuto. L'uomo con la bombetta ha rubato la mia macchina del tempo... e il tuo futuro.\"",
                "I due fuggono. Wilbur spiega: \"I pezzi del tuo Scanner sono sparsi ovunque... ma la parte più importante è nelle mani di quell’uomo. E c’è solo un posto dove può nascondersi: la Casa Abbandonata.\" \nLa notte cala mentre Lewis osserva la dimora decadente. \"Dunque... è qui che finisce tutto?\". Wilbur appoggia una mano sulla spalla dell’amico. \"No. È qui che inizia il tuo destino.\""
        );
    }

    private void caricaSceneDefault() {
        sceneManager.aggiungiSceneDefault("Salotto", "Ti trovi nel soggiorno. Attento, qui ci sono molti oggetti inutili... ma anche qualche sorpresa.\nMentre ti guardi intorno, noti che dietro ad un vecchio divano c'è qualcosa di luccicante. Forse è un pezzo dello Scanner che mi serve ancora, sembra la mia ELICA.");
        sceneManager.aggiungiSceneDefault("Cucina", "Un odore stantio aleggia nella cucina. Alcuni utensili sono arrugginiti, ma potrebbero tornare utili.\nMentre rovisti tra i cassetti, noti un vecchio SCHERMO rovinato. Sembra ancora funzionare, potrebbe essere utile per il mio Scanner.");
        sceneManager.aggiungiSceneDefault("Bagno", "Lo specchio è incrinato, l’acqua non scorre. Ma qualcosa brilla tra le piastrelle rotte... \nDovrei usare la TORCIA per vedere meglio.");
        sceneManager.aggiungiSceneDefault("Stanza Segreta", "Una stanza nascosta, piena di misteri, in lontananza un bancone attira la mia attenzione. Forse potrei utilizzarla per l'assemblaggio del mio scanner...");
        sceneManager.aggiungiSceneDefault("Scale", "Le scale scricchiolano sotto i tuoi piedi. Ogni gradino ti avvicina alla verità.");

        sceneManager.aggiungiSceneDefault("Stanza Matrimoniale", "Una volta fu una stanza d'amore. Ora è solo silenzio e polvere. \nMentre esplori, noti un vecchio MOTORE. Sembra un pezzo dello Scanner che mi serve ancora.");
        sceneManager.aggiungiSceneDefault("balcone", "Dal balcone si vede tutta la città. Il vento sussurra segreti dimenticati.");
        sceneManager.aggiungiSceneDefault("Stanza degli ospiti", "Letti vuoti, lenzuola scomposte. Chi è passato di qui? \nUna stanza molto disordinata, tanto da farti inciampare su qualcosa. Guardando meglio, noti che è sotto il materasso capovolto c'è un DOCUMENTO per terra ... con una scritta che dice 'Top Secret', forse apre la stanza segreta che Wilbur mi ha detto di cercare.");
        sceneManager.aggiungiSceneDefault("Stanza dei Bambini", "Giochi rotti e disegni alle pareti. Il passato vive ancora qui. \nPurtroppo, non riesci a vedere bene, dovresti usare la TORCIA per illuminare meglio la stanza.");
        sceneManager.aggiungiSceneDefault("corridoio", "Un corridoio stretto e buio. Le ombre sembrano danzare sulle pareti.");
    }

    private void caricaSceneEvento() {
        sceneManager.aggiungiSceneEvento("chiave_matrimoniale", "Hai raccolto una chiave arrugginita. Potrebbe aprire la porta della camera matrimoniale. Esplora le altre stanze");
        sceneManager.aggiungiSceneEvento("motore", "Un altro pezzo dello Scanner! Manca sempre meno... Esplora le altre stanze");
        sceneManager.aggiungiSceneEvento("elica", "Questo pezzo sembra essere il cuore dello Scanner. Ora è quasi completo. Esplora le altre stanze");
        sceneManager.aggiungiSceneEvento("batteria", "Hai trovato un altro pezzo! Lo Scanner è quasi pronto per essere riattivato. Nel frattempo Lewis conserva la foto... Esplora le altre stanze");
        sceneManager.aggiungiSceneEvento("schermo", "Il mio schermo, anche se è un po'rotto potrei ripararlo. Mi aiuta per visualizzare cosa riesce a captare lo Scanner. Esplora le altre stanze");
        sceneManager.aggiungiSceneEvento("documento", "Un documento misterioso. Potrebbe contenere qualcosa di importante... magari potrebbe aprire la stanza segreta? Esplora le altre stanze");
        sceneManager.aggiungiSceneEvento(
                "evento_pericoloso_balcone",
                "Appena metti piede sul balcone, senti un tremore improvviso. Il pavimento cede sotto i tuoi piedi: il balcone sta crollando! Devi aggrapparti a qualcosa per non cadere nel vuoto. Forse c'è una fune nelle vicinanze..."
        );

// Evento salvataggio: prendi fune
        sceneManager.aggiungiSceneEvento(
                "salvataggio_fune_balcone",
                "Con prontezza afferri la fune e ti aggrappi con tutte le forze. Riesci a risalire e a metterti in salvo, il cuore che batte all'impazzata. Puoi continuare il tuo viaggio."
        );
    }

    private void caricaSceneCompletamento() {
        sceneManager.aggiungiSceneCompletamento("Bagno", "Hai già preso tutto ciò che poteva servirti da qui.");
        sceneManager.aggiungiSceneCompletamento("Cucina", "Niente più da cercare in cucina. È tempo di andare.");
        sceneManager.aggiungiSceneCompletamento("Stanza matrimoniale", "Qui il tuo lavoro è finito. Lascia riposare il passato.");
        sceneManager.aggiungiSceneCompletamento("Stanza degli ospiti", "Non c'è più nulla da fare qui. Torniamo indietro.");
        sceneManager.aggiungiSceneCompletamento("Stanza dei bambini", "I ricordi sono tutti qui. Non c'è altro da cercare.");
        sceneManager.aggiungiSceneCompletamento("Salotto", "Hai già preso tutto ciò che poteva servirti qui. È tempo di andare.");
        sceneManager.aggiungiSceneCompletamento("Stanza segreta", "Hai trovato tutto ciò che potevi. Ora puoi tornare indietro.");
    }

    private void caricaSceneUsoOggetto() {
        sceneManager.aggiungiSceneUsoOggetto(
                "Stanza Matrimoniale",
                "chiave_matrimoniale",
                new Scena(
                        "chiave_matrimoniale",
                        0,
                        null,
                        "Hai usato la chiave arrugginita per aprire la porta della camera matrimoniale.",
                        Scena.TipoScena.USO_OGGETTO
                )
        );
        // Esempio per la torcia in diverse stanze
        sceneManager.aggiungiSceneUsoOggetto(
                "Salotto",
                "torcia",
                new Scena(
                        "Salotto:torcia",
                        0,
                        null,
                        "Hai acceso la torcia nel salotto. Ora noti un oggetto brillante sotto il divano... sembra l'elica del mio scanner.",
                        Scena.TipoScena.USO_OGGETTO
                )
        );
        sceneManager.aggiungiSceneUsoOggetto(
                "Bagno",
                "torcia",
                new Scena(
                        "Bagno:torcia",
                        0,
                        null,
                        "Hai acceso la torcia nel bagno. Un riflesso tra le piastrelle attira la tua attenzione è una vecchia chiave arrugginita.",
                        Scena.TipoScena.USO_OGGETTO
                )
        );
        sceneManager.aggiungiSceneUsoOggetto(
                "Stanza dei Bambini",
                "torcia",
                new Scena(
                        "Stanza dei Bambini:torcia",
                        0,
                        null,
                        "Hai acceso la torcia nella stanza dei bambini. Prendi un vecchio porta ritratti di una donna con due bambini... ti sembra di conoscerla, ma non capisci dove l'hai già vista. \nPer sbaglio la foto cade a terra e si rompe, rivelando al suo interno una BATTERIA coperta di polvere. Sembra ancora funzionare, potrebbe essere utile per il mio Scanner.",
                        Scena.TipoScena.USO_OGGETTO
                )
        );
        sceneManager.aggiungiSceneUsoOggetto(
                "Stanza Segreta",
                "chiave_segreta",
                new Scena(
                        "chiave_segreta",
                        0,
                        null,
                        "Hai inserito la batteria nello Scanner. Senti un ronzio di energia.",
                        Scena.TipoScena.USO_OGGETTO
                )
        );
    }

    private void caricaDialogoFinale() {
        sceneManager.aggiungiDialogoFinale(
                "Lewis entra nello sgabuzzino. L’'Uomo con la bombetta' è lì, seduto su uno sgabello.",
                "\"Ci rivediamo, inventore... o dovrei dire... coinquilino?\"",
                "\"Michael...?\" mormora Lewis, incredulo.",
                "\"Sì, sono io. Tu mi hai tolto tutto, Lewis! La notte, il riposo... la mia carriera. Ora tocca a te perdere tutto.\"",
                "Wilbur si fa avanti: \"Basta, Michael. È finita. Ridacci il pezzo.\"",
                "Michael si alza: \"Ah, Wilbur... sempre così pronto a fare l’eroe. Come tuo padre.\"",
                "Lewis si gira lentamente: \"Mio... cosa?\"",
                "Wilbur abbassa lo sguardo: \"Sì. Sono tuo figlio. Senza quello Scanner, io non nascerò mai. Aiutarti... era il mio destino.\"",
                "Lewis riattiva il portale. È tornato alla fiera della scienza.",
                "Guarda lo Scanner, poi Michael. Non lo userà per cercare sua madre. Ma per un’altra missione.",
                "\"Michael! Tocca a te, entra in campo!\"",
                "\"Davvero...?\" chiede lui commosso.",
                "Lewis sorride: \"Sì. È tempo che tu viva la tua occasione.\"",
                "La fiera esplode in applausi. Lewis alza lo sguardo. Un cartello sopra l’ingresso recita: 'Se vedi un bisogno... soddisfalo.'",
                "\"Lo farò. Un giorno. E avrò una famiglia... tutta mia.\""
        );
    }
}
