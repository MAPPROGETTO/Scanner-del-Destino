package org.example.database.migration;

import org.example.database.DatabaseManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Classe per la creazione delle tabelle del database.
 * Contiene metodi per creare le tabelle necessarie se non esistono già.
 * @version 1.0
 */
public final class CreazioneDatabase {
    private CreazioneDatabase() {}
    /**
     * Crea le tabelle del database se non esistono già.
     * Le tabelle create sono: oggetti, scene, stanza_testi.
     * @throws SQLException in caso di errori durante la creazione delle tabelle
     */
    public static void creaTabelle() throws SQLException {
        try (Connection c = DatabaseManager.getConnection(); Statement st = c.createStatement()) {
            // -------- OGGETTI --------
            st.execute("""
                CREATE TABLE IF NOT EXISTS oggetti (
                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                  codice VARCHAR(64) UNIQUE NOT NULL,
                  nome VARCHAR(128) NOT NULL,
                  descrizione VARCHAR(1024),
                  raccoglibile BOOLEAN NOT NULL DEFAULT TRUE,
                  usabile BOOLEAN NOT NULL DEFAULT FALSE
                )
            """);
            // -------- SCENE --------
            st.execute("""
                CREATE TABLE IF NOT EXISTS scene (
                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                  stanza VARCHAR(128) NOT NULL,
                  tipo VARCHAR(32) NOT NULL,
                  titolo VARCHAR(128),
                  testo CLOB NOT NULL
                )
            """);
            // Indici
            st.execute("CREATE INDEX IF NOT EXISTS idx_scene_stanza_tipo ON scene(stanza, tipo)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_scene_titolo ON scene(titolo)");

            // Dedup prima di unique: elimina duplicati della tripla mantenendo l'id minore
            st.execute("""
                DELETE FROM scene s
                WHERE EXISTS (
                  SELECT 1 FROM scene d
                  WHERE d.stanza = s.stanza
                    AND d.tipo = s.tipo
                    AND COALESCE(d.titolo,'') = COALESCE(s.titolo,'')
                    AND d.id < s.id
                )
            """);
            // Vincolo UNIQUE sulla tripla (stanza,tipo,titolo) per evitare doppi
            st.execute("""
                ALTER TABLE scene
                ADD CONSTRAINT IF NOT EXISTS ux_scene_tripla
                UNIQUE(stanza, tipo, titolo)
            """);
            // -------- STANZA_TESTI --------
            st.execute("""
                CREATE TABLE IF NOT EXISTS stanza_testi (
                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                  nome_stanza VARCHAR(128) NOT NULL,
                  descrizione CLOB,
                  osserva CLOB,
                  osserva_aggiornata CLOB,
                  completato BOOLEAN DEFAULT FALSE,
                  CONSTRAINT ux_stanza UNIQUE (nome_stanza)
                )
            """);
        }
    }
}
