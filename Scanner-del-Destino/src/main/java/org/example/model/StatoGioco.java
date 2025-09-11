package org.example.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Classe che rappresenta lo stato del gioco, inclusi la posizione del giocatore,
 * l'inventario, lo stato delle stanze e lo stato della storia.
 * Permette di salvare e caricare lo stato del gioco.
 * @version 2.0
 *
 */
public class StatoGioco implements Serializable {
    private static final long serialVersionUID = 2L;

    // Posizione del giocatore
    private int playerX;
    private int playerY;
    private int currentFloor;
    private boolean codiceSegretoLetto;
    private boolean codiceSegretoSbloccato;
    private boolean scannerAssemblato;
    private boolean balconeCrollato;

    // Inventario (solo ID oggetti, non istanze Swing)
    private List<String> inventarioOggetti;
    private Set<String> stanzeSenzaOggetto;
    private Set<String> stanzeVisitate;
    private Set<String> stanzeCompletate;


    // Stato delle stanze
    private Map<String, Boolean> stanzaChiusa;
    private Map<String, String> oggettoPerStanza; // chiave = nome stanza, valore = nome oggetto (o null)

    // Stato della storia
    private boolean sceneInizialiCompletate;

    /**
     * Ritorna la coordinata X del giocatore.
     * @return playerX
     */
    public int getPlayerX() { return playerX; }

    /**
     * Imposta la coordinata X del giocatore.
     * @param playerX
     */
    public void setPlayerX(int playerX) { this.playerX = playerX; }

    /**
     * Ritorna la coordinata Y del giocatore.
     * @return playerY
     */
    public int getPlayerY() { return playerY; }

    /**
     * Imposta la coordinata Y del giocatore.
     * @param playerY
     */
    public void setPlayerY(int playerY) { this.playerY = playerY; }

    /**
     * Controlla se il codice segreto è stato letto.
     * @return codiceSegretoLetto
     */
    public boolean isCodiceSegretoLetto() { return codiceSegretoLetto; }

    /**
     * Imposta se il codice segreto è stato letto.
     * @param v
     */
    public void setCodiceSegretoLetto(boolean v) { this.codiceSegretoLetto = v; }

    /**
     * Controlla se il codice segreto è stato sbloccato.
     * @return codiceSegretoSbloccato
     */
    public boolean isCodiceSegretoSbloccato() { return codiceSegretoSbloccato; }

    /**
     * Imposta se il codice segreto è stato sbloccato.
     * @param v
     */
    public void setCodiceSegretoSbloccato(boolean v) { this.codiceSegretoSbloccato = v; }

    public boolean isBalconeCrollato() { return balconeCrollato; }
    public void setBalconeCrollato(boolean balconeCrollato) { this.balconeCrollato = balconeCrollato; }

    /**
     * Controlla se lo scanner è stato assemblato.
     * @return scannerAssemblato
     */
    public boolean isScannerAssemblato() { return scannerAssemblato; }

    /**
     * Imposta se lo scanner è stato assemblato.
     * @param scannerAssemblato
     */
    public void setScannerAssemblato(boolean scannerAssemblato) { this.scannerAssemblato = scannerAssemblato; }

    /**
     * Ritorna l'inventario del giocatore (lista di ID oggetti).
     * @return inventarioOggetti
     */
    public List<String> getInventarioOggetti() { return inventarioOggetti; }

    /**
     * Imposta l'inventario del giocatore (lista di ID oggetti).
     * @param inventarioOggetti
     */
    public void setInventarioOggetti(List<String> inventarioOggetti) { this.inventarioOggetti = inventarioOggetti; }

    /**
     * Ritorna l'insieme delle stanze senza oggetto.
     * @return stanzeSenzaOggetto
     */
    public Set<String> getStanzeSenzaOggetto() { return stanzeSenzaOggetto; }

    /**
     * Imposta l'insieme delle stanze senza oggetto.
     * @param stanzeSenzaOggetto
     */
    public void setStanzeSenzaOggetto(Set<String> stanzeSenzaOggetto) { this.stanzeSenzaOggetto = stanzeSenzaOggetto; }

    /**
     * Ritorna l'insieme delle stanze visitate .
     * @return stanzeVisitate
     */
    public Set<String> getStanzeVisitate() { return stanzeVisitate; }

    /**
     * Imposta l'insieme delle stanze visitate.
     * @param stanzeVisitate
     */
    public void setStanzeVisitate(Set<String> stanzeVisitate) { this.stanzeVisitate = stanzeVisitate; }

    /**
     * Ritorna l'insieme delle stanze completate.
     * @return stanzeCompletate
     */
    public Set<String> getStanzeCompletate() { return stanzeCompletate; }

    /**
     * Imposta l'insieme delle stanze completate.
     * @param stanzeCompletate
     */
    public void setStanzeCompletate(Set<String> stanzeCompletate) { this.stanzeCompletate = stanzeCompletate; }

    /**
     * Ritorna lo stato di chiusura di ogni stanza.
     * @return stanzaChiusa
     */
    public Map<String, Boolean> getStanzaChiusa() { return stanzaChiusa; }

    /**
     * Imposta lo stato di chiusura di ogni stanza.
     * @param stanzaChiusa
     */
    public void setStanzaChiusa(Map<String, Boolean> stanzaChiusa) { this.stanzaChiusa = stanzaChiusa; }

    /**
     * Ritorna l'oggetto associato a ogni stanza.
     * @return oggettoPerStanza
     */

    public Map<String, String> getOggettoPerStanza() { return oggettoPerStanza; }

    /**
     * Imposta l'oggetto associato a ogni stanza.
     * @param oggettoPerStanza
     */
    public void setOggettoPerStanza(Map<String, String> oggettoPerStanza) { this.oggettoPerStanza = oggettoPerStanza; }

    /**
     * Ritorna il piano corrente del giocatore.
     * @return currentFloor
     */
    public int getCurrentFloor() { return currentFloor; }

    /**
     * Imposta il piano corrente del giocatore.
     *
     * @param currentFloor
     */
    public void setCurrentFloor(int currentFloor) { this.currentFloor = currentFloor; }

    /**
     * Controlla se le scene iniziali sono state completate.
     * @return true se le scene iniziali sono state completate, false altrimenti.
     */
    public boolean isSceneInizialiCompletate() { return sceneInizialiCompletate; }

    /**
     * Imposta se le scene iniziali sono state completate.
     * @param sceneInizialiCompletate
     */
    public void setSceneInizialiCompletate(boolean sceneInizialiCompletate) { this.sceneInizialiCompletate = sceneInizialiCompletate; }
}
