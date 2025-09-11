package org.example.database.dao;

import org.example.database.DatabaseManager;

import java.sql.*;
import java.util.Optional;

/**
 * DAO per la tabella "stanza_testi".
 * Contiene testi narrativi associati a stanze specifiche.
 * Permette di arricchire l'esperienza di gioco con descrizioni dettagliate e osservazioni.
 * @version 1.0
 */
public class TabellaStanzaTesti {

    public static class StanzaTesti {
        private final String nomeStanza, descrizione, osserva, osservaAggiornata;
        private final boolean completato;

        /** Costruttore per StanzaTesti.
         * @param nomeStanza Nome della stanza (chiave primaria)
         * @param descrizione Descrizione generale della stanza
         * @param osserva Testo visualizzato quando il giocatore osserva la stanza
         * @param osservaAggiornata Testo visualizzato quando la stanza è stata completata
         * @param completato Flag che indica se la stanza è stata completata
         */
        public StanzaTesti(String nomeStanza, String descrizione, String osserva, String osservaAggiornata, boolean completato) {
            this.nomeStanza = nomeStanza;
            this.descrizione = descrizione;
            this.osserva = osserva;
            this.osservaAggiornata = osservaAggiornata;
            this.completato = completato;
        }
        public String descrizione(){ return descrizione; }
        public String osserva(){ return osserva; }
        public String osservaAggiornata(){ return osservaAggiornata; }
        public boolean completato(){ return completato; }
    }

    // Flag cache per evitare di controllare ogni volta
    private static volatile Boolean SCHEMA_OK = null;

    /** Controlla se lo schema della tabella è corretto.
     * Verifica la presenza delle colonne necessarie.
     * @return true se lo schema è corretto, false altrimenti
     */
    private static boolean schemaOk() {
        if (SCHEMA_OK != null) return SCHEMA_OK.booleanValue();
        boolean ok = false;
        final String q = """
            SELECT COUNT(*)
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE UPPER(TABLE_NAME)='STANZA_TESTI'
              AND UPPER(COLUMN_NAME) IN ('NOME_STANZA','DESCRIZIONE','OSSERVA','OSSERVA_AGGIORNATA','COMPLETATO')
        """;
        try (Connection c = DatabaseManager.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(q)) {
            if (rs.next()) {
                int count = rs.getInt(1);
                ok = (count >= 5); // tutte e 5 le colonne presenti
            }
        } catch (SQLException ignore) {}
        SCHEMA_OK = Boolean.valueOf(ok);
        return ok;
    }

    /** Cerca i testi associati a una stanza specifica.
     * @param stanza Nome della stanza da cercare
     * @return Optional contenente StanzaTesti se trovata, altrimenti vuoto
     */
    public static Optional<StanzaTesti> findByStanza(String stanza) {
        if (!schemaOk()) {
            return Optional.empty(); // evita qualsiasi query allo schema sbagliato
        }
        final String sql = "SELECT nome_stanza, descrizione, osserva, osserva_aggiornata, completato FROM stanza_testi WHERE nome_stanza = ? LIMIT 1";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, stanza);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new StanzaTesti(
                            rs.getString("nome_stanza"),
                            rs.getString("descrizione"),
                            rs.getString("osserva"),
                            rs.getString("osserva_aggiornata"),
                            rs.getBoolean("completato")
                    ));
                }
            }
        } catch (SQLException ignore) {
            // Se va in errore, spegni definitivamente questa via finché non migri
            SCHEMA_OK = Boolean.FALSE;
        }
        return Optional.empty();
    }
}
