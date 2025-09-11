package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe per la gestione della connessione al database H2.
 * Fornisce un metodo statico per ottenere una connessione al database.
 * @version 1.0
 */
public final class DatabaseManager {
    private static final String JDBC_URL =
            "jdbc:h2:file:./scannerdb;" +
                    "MODE=MySQL;" +
                    "DATABASE_TO_LOWER=TRUE;" +
                    "CASE_INSENSITIVE_IDENTIFIERS=TRUE;" +
                    "DB_CLOSE_DELAY=-1;" +      // non chiudere l’engine a fine connessione
                    "PAGE_SIZE=4096;" +
                    "CACHE_SIZE=65536;" +
                    "TRACE_LEVEL_FILE=0";
    private static final String USER = "sa";
    private static final String PASS = "";

    private DatabaseManager() {}

    /**
     * Ottiene una connessione al database H2.
     * @return Connessione al database
     * @throws SQLException in caso di errori di connessione
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASS);
    }
}