package org.example.story;

public class SceneLoader {

    private final SceneManager sceneManager;

    public SceneLoader(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void caricaTutteLeScene() {
        caricaSceneIniziali();
        caricaSceneDefault();
        caricaSceneEvento();
        caricaSceneCompletamento();
        caricaDialogoFinale();
        caricaSceneFinale();
    }

    private void caricaSceneIniziali() {
        sceneManager.aggiungiSceneIniziali(
                "Una pioggia sottile batte contro le finestre dell’orfanotrofio. Lewis è piegato sul suo banco, intento a stringere un piccolo bullone. \"Ancora pochi ritocchi e sarai perfetto...\" sussurra allo Scanner Mnemonico.",
                "Michael entra nella stanza correndo: \"Lewis! La fiera della scienza inizia tra dieci minuti!\". Lewis alza lo sguardo, sorride nervosamente: \"Giusto il tempo di...\". Un lampo accecante attraversa il cielo.",
                "Alla fiera, lo Scanner si accende... ma qualcosa va storto. Fumo, scintille, il pubblico mormora. Dal fumo emerge una figura elegante, bombetta in testa e un ghigno sinistro. \"Prenderò questo, grazie.\"",
                "Wilbur piomba sulla scena. \"Fermati!\" urla, ma l'uomo è già sparito. Wilbur si avvicina a Lewis: \"Sei tu Lewis? Ho bisogno del tuo aiuto. L'uomo con la bombetta ha rubato la mia macchina del tempo... e il tuo futuro.\"",
                "I due fuggono. Wilbur spiega: \"I pezzi del tuo Scanner sono sparsi ovunque... ma la parte più importante è nelle mani di quell’uomo. E c’è solo un posto dove può nascondersi: la Casa Abbandonata.\"",
                "La notte cala mentre Lewis osserva la dimora decadente. \"Dunque... è qui che finisce tutto?\". Wilbur appoggia una mano sulla spalla dell’amico. \"No. È qui che inizia il tuo destino.\""
        );
    }

    private void caricaSceneDefault() {
        sceneManager.aggiungiSceneDefault("soggiorno", "Ti trovi nel soggiorno. Attento, qui ci sono molti oggetti inutili... ma anche qualche sorpresa.");
        sceneManager.aggiungiSceneDefault("cucina", "Un odore stantio aleggia nella cucina. Alcuni utensili sono arrugginiti, ma potrebbero tornare utili.");
        sceneManager.aggiungiSceneDefault("bagno", "Lo specchio è incrinato, l’acqua non scorre. Ma qualcosa brilla tra le piastrelle rotte...");
        sceneManager.aggiungiSceneDefault("camera_matrimoniale", "Una volta fu una stanza d'amore. Ora è solo silenzio e polvere.");
        sceneManager.aggiungiSceneDefault("camera_ospiti", "Letti vuoti, lenzuola scomposte. Chi è passato di qui?");
        sceneManager.aggiungiSceneDefault("camera_bambini", "Giochi rotti e disegni alle pareti. Il passato vive ancora qui.");
        sceneManager.aggiungiSceneDefault("balcone", "Dal balcone si vede tutta la città. Il vento sussurra segreti dimenticati.");
        sceneManager.aggiungiSceneDefault("corridoio", "Ogni passo risuona tra le pareti. Qualcosa ti osserva.");
    }

    private void caricaSceneEvento() {
        sceneManager.aggiungiSceneEvento("chiave_matrimoniale", "Hai raccolto una chiave arrugginita. Potrebbe aprire la porta della camera matrimoniale.");
        sceneManager.aggiungiSceneEvento("motore", "Un altro pezzo dello Scanner! Manca sempre meno...");
        sceneManager.aggiungiSceneEvento("elica", "Questo pezzo sembra essere il cuore dello Scanner. Ora è quasi completo.");
        sceneManager.aggiungiSceneEvento("batteria", "Hai trovato un altro pezzo! Lo Scanner è quasi pronto per essere riattivato.");
        sceneManager.aggiungiSceneEvento("schermo", "Il mio schermo, anche se è un po'rotto potrei ripararlo. Mi aiuta per visualizzare cosa riesce a captare lo Scanner.");
        sceneManager.aggiungiSceneEvento("chiave_segreta", "Una chiave misteriosa. Potrebbe aprire qualcosa di importante... magari la stanza segreta?");
    }

    private void caricaSceneCompletamento() {
        sceneManager.aggiungiSceneCompletamento("bagno", "Hai già preso tutto ciò che poteva servirti da qui.");
        sceneManager.aggiungiSceneCompletamento("cucina", "Niente più da cercare in cucina. È tempo di andare.");
        sceneManager.aggiungiSceneCompletamento("camera_matrimoniale", "Qui il tuo lavoro è finito. Lascia riposare il passato.");
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
                "Wilbur abbassa lo sguardo: \"Sì. Sono tuo figlio. Senza quello Scanner, io non nascerò mai. Aiutarti... era il mio destino.\""
        );
    }

    private void caricaSceneFinale() {
        sceneManager.aggiungiSceneFinale(
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
