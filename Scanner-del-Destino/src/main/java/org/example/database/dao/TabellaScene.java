package org.example.database.dao;

import org.example.database.DatabaseManager;

import java.sql.*;
import java.util.*;

/**
 * DAO per la tabella "scene".
 * Le scene sono testi narrativi associati a stanze e tipi specifici.
 * Permettono di arricchire l'esperienza di gioco con descrizioni, eventi e dialoghi.
 * @version 1.0
 */
public class TabellaScene {

    // ---- Tipi di scena supportati ----
    public enum TipoScene { INIZIALE, DEFAULT, EVENTO, PERICOLO, COMPLETAMENTO, DIALOGO, USO_OGGETTO, FINALE }

    // ---- Modello Scena ----
    /** Rappresenta una scena con attributi: stanza, tipo, titolo e testo.
     */
    public static class Scene {
        private final String stanza;
        private final TipoScene tipo;
        private final String titolo;
        private final String testo;

        public Scene(String stanza, TipoScene tipo, String titolo, String testo) {
            this.stanza = stanza;
            this.tipo = tipo;
            this.titolo = titolo;
            this.testo = testo;
        }

        public String stanza() { return stanza; }
        public TipoScene tipo() { return tipo; }
        public String titolo() { return titolo; }
        public String testo() { return testo; }
    }

    // =====================================================================================
    //  Metodi usati dagli adapter (NarrationDbAdapter / ScenesDb)
    // =====================================================================================

    /** Ritorna una scena specifica basata sulla stanza e il tipo.
     * @param stanza Chiave della scena (es. nome della stanza o evento specifico)
     * @param tipo Tipo di scena (INIZIALE, DEFAULT, EVENTO, etc.)
     * @return Optional contenente la scena se trovata, altrimenti vuoto
     */
    public static Optional<Scene> getByStanzaAndTipo(String stanza, TipoScene tipo) {
        final String sql = "SELECT titolo, testo FROM scene WHERE stanza = ? AND tipo = ? LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, stanza);
            ps.setString(2, tipo.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String titolo = rs.getString("titolo");
                    String testo  = rs.getString("testo");
                    return Optional.of(new Scene(stanza, tipo, titolo, testo));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
