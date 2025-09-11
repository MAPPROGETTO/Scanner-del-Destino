package org.example.database.seed;

import org.example.database.DatabaseManager;
import java.sql.*;

/**
 * Classe per l'inizializzazione delle tabelle del database con dati seed.
 * Contiene metodi per popolare le tabelle con dati iniziali se non sono già presenti.
 * Utilizza operazioni batch e transazioni per efficienza e integrità.
 * @version 1.0
 */
public final class InizializzaTabelle {
    private InizializzaTabelle() {}

    /**
     * Esegue l'inizializzazione delle tabelle del database.
     * Popola le tabelle con dati seed se non sono già presenti.
     * Utilizza operazioni batch e transazioni per efficienza e integrità.
     * @throws SQLException in caso di errori durante l'inizializzazione
     */
    public static void esegui() throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                seedOggetti(conn);
                seedScene(conn);
                seedFinaliSeMancano();   // top-up finale idempotente
                seedStanzaTesti(conn);
                conn.commit();
                System.out.println("[DB] Seed completato (INSERT-if-missing + batch).");
            } catch (SQLException e) {
                conn.rollback(); throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // ------------------------------------------------------------
    // OGGETTI  (INSERT IF MISSING + BATCH)
    // ------------------------------------------------------------
    /** Popola la tabella "oggetti" con dati seed se non sono già presenti.
     * Utilizza operazioni batch per efficienza.
     * @param c Connessione al database
     * @throws SQLException in caso di errori durante l'inserimento
     */
    private static void seedOggetti(Connection c) throws SQLException {
        final String sql = """
            INSERT INTO oggetti (codice, nome, descrizione, raccoglibile, usabile)
            SELECT ?, ?, ?, ?, ?
            WHERE NOT EXISTS (SELECT 1 FROM oggetti WHERE codice = ?)
        """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            addOggetto(ps,"chiave_matrimoniale","Chiave della camera","Una vecchia chiave arrugginita.",true,true);
            addOggetto(ps,"elica","Elica dello scanner","Sembra il cuore dello scanner.",true,false);
            addOggetto(ps,"schermo","Schermo danneggiato","Serve a visualizzare i dati dello scanner.",true,false);
            addOggetto(ps,"batteria","Batteria","Modulo di alimentazione.",true,true);
            addOggetto(ps,"documento","Documento Top Secret","Contiene il codice della stanza segreta.",true,true);
            ps.executeBatch();
        }
    }

    /** Aggiunge un oggetto al batch di inserimento.
     * @param ps PreparedStatement per l'inserimento
     * @param codice Codice univoco dell'oggetto
     * @param nome Nome dell'oggetto
     * @param desc Descrizione dell'oggetto
     * @param racc Se l'oggetto è raccoglibile
     * @param usa Se l'oggetto è usabile
     * @throws SQLException in caso di errori durante la preparazione del batch
     */
    private static void addOggetto(PreparedStatement ps, String codice, String nome, String desc, boolean racc, boolean usa) throws SQLException {
        ps.setString(1,codice);
        ps.setString(2,nome);
        ps.setString(3,desc);
        ps.setBoolean(4,racc);
        ps.setBoolean(5,usa);
        ps.setString(6,codice); // exists check
        ps.addBatch();
    }

    // ------------------------------------------------------------
    // SCENE (INIZIALI / EVENTO / PERICOLO / USO_OGGETTO) — INSERT IF MISSING + BATCH
    // ------------------------------------------------------------
    /** Popola la tabella "scene" con dati seed se non sono già presenti.
     * Utilizza operazioni batch per efficienza.
     * @param conn Connessione al database
     * @throws SQLException in caso di errori durante l'inserimento
     */
    private static void seedScene(Connection conn) throws SQLException {
        final String sql = """
            INSERT INTO scene (stanza, tipo, titolo, testo)
            SELECT ?, ?, ?, ?
            WHERE NOT EXISTS (
               SELECT 1 FROM scene
               WHERE stanza=? AND tipo=? AND COALESCE(titolo,'')=COALESCE(?, '')
            )
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Prologo
            addScene(ps, "intro_1", "INIZIALE", "Prologo 1",
                    "Una pioggia sottile batte contro le finestre dell’orfanotrofio. Lewis è piegato sul suo banco, intento a stringere un piccolo bullone. \"Ancora pochi ritocchi e sarai perfetto...\" sussurra allo Scanner Mnemonico. \n Michael entra nella stanza correndo: \"Lewis! La fiera della scienza inizia tra dieci minuti!\". Lewis alza lo sguardo, sorride nervosamente: \"Giusto il tempo di...\". Un lampo accecante attraversa il cielo.");
            addScene(ps, "intro_2", "INIZIALE", "Prologo 2",
                    "Alla fiera, lo Scanner si accende... ma qualcosa va storto. Fumo, scintille, il pubblico mormora. Dal fumo emerge una figura elegante, bombetta in testa e un ghigno sinistro. \"Prenderò questo, grazie.\" \nWilbur piomba sulla scena. \"Fermati!\" urla, ma l'uomo è già sparito. Wilbur si avvicina a Lewis: \"Sei tu Lewis? Ho bisogno del tuo aiuto. L'uomo con la bombetta ha rubato la mia macchina del tempo... e il tuo futuro.\"");
            addScene(ps, "intro_3", "INIZIALE", "Prologo 3",
                    "I due fuggono. Wilbur spiega: \"I pezzi del tuo Scanner sono sparsi ovunque... ma la parte più importante è nelle mani di quell’uomo. E c’è solo un posto dove può nascondersi: la Casa Abbandonata.\" \nLa notte cala mentre Lewis osserva la dimora decadente. \"Dunque... è qui che finisce tutto?\". Wilbur appoggia una mano sulla spalla dell’amico. \"No. È qui che inizia il tuo destino.\"");

            // Eventi item
            addScene(ps, "chiave_matrimoniale", "EVENTO", "Evento: chiave_matrimoniale",
                    "Hai raccolto una chiave arrugginita. Potrebbe aprire la porta della camera matrimoniale. Esplora le altre stanze\n");
            addScene(ps, "motore", "EVENTO", "Evento: motore",
                    "Un altro pezzo dello Scanner! Manca sempre meno... Esplora le altre stanze\n");
            addScene(ps, "elica", "EVENTO", "Evento: elica",
                    "Questo pezzo sembra essere il cuore dello Scanner. Ora è quasi completo. Esplora le altre stanze\n");
            addScene(ps, "batteria", "EVENTO", "Evento: batteria",
                    "Hai trovato un altro pezzo! Lo Scanner è quasi pronto per essere riattivato. Nel frattempo Lewis conserva la foto... Esplora le altre stanze\n");
            addScene(ps, "schermo", "EVENTO", "Evento: schermo",
                    "Il mio schermo, anche se è un po'rotto potrei ripararlo. Mi aiuta per visualizzare cosa riesce a captare lo Scanner. Esplora le altre stanze\n");
            addScene(ps, "documento", "EVENTO", "Evento: documento",
                    "Un documento misterioso. Potrebbe contenere qualcosa di importante... magari potrebbe aprire la stanza segreta? Esplora le altre stanze\n");

            // Pericolo / salvataggio balcone
            addScene(ps, "balcone", "PERICOLO", "Evento pericoloso: balcone",
                    "Appena metti piede sul balcone, senti un tremore improvviso. Il pavimento cede sotto i tuoi piedi: il balcone sta crollando! Devi aggrapparti a qualcosa per non cadere nel vuoto. Forse c'è una fune nelle vicinanze...\n");
            addScene(ps, "salvataggio_fune_balcone", "EVENTO", "Salvataggio con fune",
                    "Con prontezza afferri la fune e ti aggrappi con tutte le forze. Riesci a risalire e a metterti in salvo, il cuore che batte all'impazzata. Puoi continuare il tuo viaggio.\n");

            // Uso oggetto
            addScene(ps, "stanza_matrimoniale:chiave_matrimoniale", "EVENTO", "Uso oggetto",
                    "Hai usato la chiave arrugginita per aprire la porta della camera matrimoniale.\n");
            addScene(ps, "bagno:torcia", "USO_OGGETTO", "Uso oggetto",
                    "Hai acceso la torcia nel bagno. Un riflesso tra le piastrelle attira la tua attenzione è una vecchia chiave arrugginita.\n");
            addScene(ps, "stanza_dei_bambini:torcia", "USO_OGGETTO", "Uso oggetto",
                    "Hai acceso la torcia nella stanza dei bambini. Prendi un vecchio porta ritratti di una donna con due bambini... ti sembra di conoscerla, ma non capisci dove l'hai già vista. \nPer sbaglio la foto cade a terra e si rompe, rivelando al suo interno una BATTERIA coperta di polvere. Sembra ancora funzionare, potrebbe essere utile per il mio Scanner.\n");

            ps.executeBatch();
        }
    }

    /** Aggiunge una scena al batch di inserimento.
     * @param ps PreparedStatement per l'inserimento
     * @param stanzaKey Chiave della scena (es. Nome della stanza o evento specifico)
     * @param tipo Tipo di scena (INIZIALE, DEFAULT, EVENTO, etc.)
     * @param titolo Titolo della scena
     * @param testo Corpo del testo della scena
     * @throws SQLException in caso di errori durante la preparazione del batch
     */
    private static void addScene(PreparedStatement ps, String stanzaKey, String tipo, String titolo, String testo) throws SQLException {
        // valori
        ps.setString(1, stanzaKey);
        ps.setString(2, tipo);
        ps.setString(3, titolo);
        ps.setString(4, testo);
        // exists check
        ps.setString(5, stanzaKey);
        ps.setString(6, tipo);
        ps.setString(7, titolo);
        ps.addBatch();
    }

    // ------------------------------------------------------------
    // FINALI — INSERT IF MISSING + BATCH
    // ------------------------------------------------------------
    /** Popola la tabella "scene" con i finali se non sono già presenti.
     * Utilizza operazioni batch per efficienza.
     * @throws SQLException in caso di errori durante l'inserimento
     */
    public static void seedFinaliSeMancano() throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            final String sql = """
                INSERT INTO scene (stanza, tipo, titolo, testo)
                SELECT ?, ?, ?, ?
                WHERE NOT EXISTS (
                   SELECT 1 FROM scene
                   WHERE stanza=? AND tipo=? AND COALESCE(titolo,'')=COALESCE(?, '')
                )
            """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                addScene(ps, "finale_1",  "FINALE", "Epilogo I",   "Lewis entra nello sgabuzzino. L’'Uomo con la bombetta' è lì, seduto su uno sgabello.");
                addScene(ps, "finale_2",  "FINALE", "Epilogo II",  "\"Ci rivediamo, inventore... o dovrei dire... coinquilino?\"");
                addScene(ps, "finale_3",  "FINALE", "Epilogo III", "\"Michael...?\" mormora Lewis, incredulo.");
                addScene(ps, "finale_4",  "FINALE", "Epilogo IV",  "\"Sì, sono io. Tu mi hai tolto tutto, Lewis! La notte, il riposo... la mia carriera. Ora tocca a te perdere tutto.\"");
                addScene(ps, "finale_5",  "FINALE", "Epilogo V",   "Wilbur si fa avanti: \"Basta, Michael. È finita. Ridacci il pezzo.\"");
                addScene(ps, "finale_6",  "FINALE", "Epilogo VI",  "Michael si alza: \"Ah, Wilbur... sempre così pronto a fare l’eroe. Come tuo padre.\"");
                addScene(ps, "finale_7",  "FINALE", "Epilogo VII", "Lewis si gira lentamente: \"Mio... cosa?\"");
                addScene(ps, "finale_8",  "FINALE", "Epilogo VIII","Wilbur abbassa lo sguardo: \"Sì. Sono tuo figlio. Senza quello Scanner, io non nascerò mai. Aiutarti... era il mio destino.\"");
                addScene(ps, "finale_9",  "FINALE", "Epilogo IX",  "Lewis riattiva il portale. È tornato alla fiera della scienza.");
                addScene(ps, "finale_10", "FINALE", "Epilogo X",   "Guarda lo Scanner, poi Michael. Non lo userà per cercare sua madre. Ma per un’altra missione.");
                addScene(ps, "finale_11", "FINALE", "Epilogo XI",  "\"Michael! Tocca a te, entra in campo!\"");
                addScene(ps, "finale_12", "FINALE", "Epilogo XII", "\"Davvero...?\" chiede lui commosso.");
                addScene(ps, "finale_13", "FINALE", "Epilogo XIII","Lewis sorride: \"Sì. È tempo che tu viva la tua occasione.\"");
                addScene(ps, "finale_14", "FINALE", "Epilogo XIV", "La fiera esplode in applausi. Lewis alza lo sguardo. Un cartello sopra l’ingresso recita: 'Se vedi un bisogno... soddisfalo.'");
                addScene(ps, "finale_15", "FINALE", "Epilogo XV",  "\"Lo farò. Un giorno. E avrò una famiglia... tutta mia.\"");
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback(); throw e;
            }
        }
    }

    // ------------------------------------------------------------
    // STANZA_TESTI — INSERT IF MISSING + BATCH
    // ------------------------------------------------------------
    /** Popola la tabella "stanza_testi" con dati seed se non sono già presenti.
     * Utilizza operazioni batch per efficienza.
     * @param conn Connessione al database
     * @throws SQLException in caso di errori durante l'inserimento
     */
    private static void seedStanzaTesti(Connection conn) throws SQLException {
        final String sql = """
            INSERT INTO stanza_testi (nome_stanza, descrizione, osserva, osserva_aggiornata, completato)
            SELECT ?, ?, ?, ?, ?
            WHERE NOT EXISTS (SELECT 1 FROM stanza_testi WHERE nome_stanza=?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            insertStanza(ps, "salotto",
                    "--Salotto--\nTi trovi nel soggiorno. Attento, qui ci sono molti oggetti inutili... ma anche qualche sorpresa.\n" +
                            "Mentre ti guardi intorno, noti che dietro ad un vecchio divano c'è qualcosa di luccicante. " +
                            "Forse è un pezzo dello Scanner che mi serve ancora, sembra la mia ELICA.\n",
                    null,
                    "Completamento\nHai già preso tutto ciò che poteva servirti qui. È tempo di andare.\n",
                    false);

            insertStanza(ps, "cucina",
                    "--Cucina--\nUn odore stantio aleggia nella cucina. Alcuni utensili sono arrugginiti, ma potrebbero tornare utili.\n" +
                            "Mentre rovisti tra i cassetti, noti un vecchio SCHERMO rovinato. Sembra ancora funzionare, " +
                            "potrebbe essere utile per il mio Scanner.\n",
                    null,
                    "Completamento\nNiente più da cercare in cucina. È tempo di andare.\n",
                    false);

            insertStanza(ps, "bagno",
                    "--Bagno--\nLo specchio è incrinato, l’acqua non scorre. Ma qualcosa brilla tra le piastrelle rotte... \n" +
                            "Dovrei usare la TORCIA per vedere meglio.\n",
                    null,
                    "Completamento\nHai già preso tutto ciò che poteva servirti da qui.\n",
                    false);

            insertStanza(ps, "stanza_segreta",
                    "--Stanza segreta--\nUna stanza nascosta, piena di misteri, in lontananza un bancone attira la mia attenzione. " +
                            "Forse potrei utilizzarla per l'assemblaggio del mio scanner...\n",
                    null,
                    "Completamento\nHai trovato tutto ciò che potevi. Ora puoi tornare indietro.\n",
                    false);

            insertStanza(ps, "scale",
                    "--Scale--\nLe scale scricchiolano sotto i tuoi piedi. Ogni gradino ti avvicina alla verità.\n",
                    null, null, false);

            insertStanza(ps, "stanza_matrimoniale",
                    "--Stanza matrimoniale--\nUna volta fu una stanza d'amore. Ora è solo silenzio e polvere. \n" +
                            "Mentre esplori, noti un vecchio MOTORE. Sembra un pezzo dello Scanner che mi serve ancora.\n",
                    null,
                    "Completamento\nQui il tuo lavoro è finito. Lascia riposare il passato.\n",
                    false);

            insertStanza(ps, "balcone",
                    "--Balcone--\nDal balcone si vede tutta la città. Il vento sussurra segreti dimenticati.\n",
                    null, null, false);

            insertStanza(ps, "stanza_degli_ospiti",
                    "--Stanza degli ospiti--\nLetti vuoti, lenzuola scomposte. Chi è passato di qui? \n" +
                            "Una stanza molto disordinata, tanto da farti inciampare su qualcosa. Guardando meglio, " +
                            "noti che sotto il materasso capovolto c'è un DOCUMENTO per terra... con una scritta che dice 'Top Secret', " +
                            "forse apre la stanza segreta che Wilbur mi ha detto di cercare.\n",
                    null,
                    "Completamento\nNon c'è più nulla da fare qui. Torniamo indietro.\n",
                    false);

            insertStanza(ps, "stanza_dei_bambini",
                    "--Stanza dei bambini--\nGiochi rotti e disegni alle pareti. Il passato vive ancora qui. \n" +
                            "Purtroppo, non riesci a vedere bene, dovresti usare la TORCIA per illuminare meglio la stanza.\n",
                    null,
                    "Completamento\nI ricordi sono tutti qui. Non c'è altro da cercare.\n",
                    false);

            insertStanza(ps, "corridoio",
                    "--Corridoio--\nUn corridoio stretto e buio. Le ombre sembrano danzare sulle pareti.\n",
                    null, null, false);

            ps.executeBatch();
        }
    }

    /** Aggiunge una stanza al batch di inserimento.
     * @param ps PreparedStatement per l'inserimento
     * @param nomeStanza Nome univoco della stanza
     * @param descrizione Descrizione iniziale della stanza
     * @param osserva Testo mostrato quando si osserva la stanza per la prima volta
     * @param osservaAggiornata Testo mostrato quando si osserva la stanza dopo il completamento
     * @param completato Stato iniziale di completamento della stanza
     * @throws SQLException in caso di errori durante la preparazione del batch
     */
    private static void insertStanza(PreparedStatement ps, String nomeStanza, String descrizione,
                                     String osserva, String osservaAggiornata, boolean completato) throws SQLException {
        ps.setString(1, nomeStanza);
        ps.setString(2, descrizione);
        ps.setString(3, osserva);
        ps.setString(4, osservaAggiornata);
        ps.setBoolean(5, completato);
        ps.setString(6, nomeStanza); // exists check
        ps.addBatch();
    }
}
