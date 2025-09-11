package org.example.database;

import org.example.database.migration.CreazioneDatabase;
import org.example.database.seed.InizializzaTabelle;

/**
 * Classe per l'inizializzazione del database.
 * Si assicura che le tabelle siano create e popolate con i dati iniziali se necessario.
 * @version 1.0
 */
public final class DatabaseBootstrap {
    private static volatile boolean done = false;
    private DatabaseBootstrap() {}
    /**
     * Assicura che il database sia inizializzato.
     * Crea le tabelle se non esistono e popola i dati iniziali se la tabella 'scene' è vuota.
     * Questo metodo è thread-safe e viene eseguito solo una volta.
     */
    public static synchronized void ensureStarted() {
        if (done) return;
        try {
            CreazioneDatabase.creaTabelle();

            // Se la tabella 'scene' è vuota → fai seed completo (il tuo InizializzaTabelle ora TRUNCATE+INSERT)
            if (isEmpty("scene")) {
                InizializzaTabelle.esegui();
            } else {
                // DB già popolato → fai solo top-up dei finali in modalità "insert-if-missing"
                InizializzaTabelle.seedFinaliSeMancano();
            }

            done = true;
        } catch (Exception e) {
            throw new RuntimeException("Impossibile inizializzare il DB", e);
        }
    }
    /**
     * Controlla se una tabella è vuota.
     * @param table Nome della tabella da controllare
     * @return true se la tabella è vuota o non esiste, false altrimenti
     */
    private static boolean isEmpty(String table) {
        String sql = "SELECT COUNT(*) FROM " + table;
        try (var c = DatabaseManager.getConnection();
             var st = c.createStatement();
             var rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1) == 0;
        } catch (Exception e) {
            // Se fallisce (es. Tabella non esiste ancora) trattiamo come vuota
            return true;
        }
    }
}
