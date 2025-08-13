package org.example.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatoGioco implements Serializable {
    private static final long serialVersionUID = 2L;

    // Posizione del giocatore
    private String stanzaCorrenteId;
    private int playerX;
    private int playerY;
    private int currentFloor;
    private boolean codiceSegretoLetto;
    private boolean codiceSegretoSbloccato;

    // Inventario (solo ID oggetti, non istanze Swing)
    private List<String> inventarioOggetti;
    private Set<String> stanzeSenzaOggetto;

    // Stato delle stanze
    private Map<String, Boolean> stanzaChiusa;
    private Map<String, Boolean> stanzaLuceAccesa;
    private Map<String, Boolean> stanzaHaPezzo;
    private Map<String, String> oggettoPerStanza; // chiave = nome stanza, valore = nome oggetto (o null)

    // Stato della storia
    private boolean sceneInizialiCompletate;

    // Getters e Setters
    public String getStanzaCorrenteId() { return stanzaCorrenteId; }
    public void setStanzaCorrenteId(String stanzaCorrenteId) { this.stanzaCorrenteId = stanzaCorrenteId; }

    public int getPlayerX() { return playerX; }
    public void setPlayerX(int playerX) { this.playerX = playerX; }

    public int getPlayerY() { return playerY; }
    public void setPlayerY(int playerY) { this.playerY = playerY; }

    public boolean isCodiceSegretoLetto() { return codiceSegretoLetto; }
    public void setCodiceSegretoLetto(boolean v) { this.codiceSegretoLetto = v; }

    public boolean isCodiceSegretoSbloccato() { return codiceSegretoSbloccato; }
    public void setCodiceSegretoSbloccato(boolean v) { this.codiceSegretoSbloccato = v; }

    public List<String> getInventarioOggetti() { return inventarioOggetti; }
    public void setInventarioOggetti(List<String> inventarioOggetti) { this.inventarioOggetti = inventarioOggetti; }
    public Set<String> getStanzeSenzaOggetto() { return stanzeSenzaOggetto; }
    public void setStanzeSenzaOggetto(Set<String> stanzeSenzaOggetto) { this.stanzeSenzaOggetto = stanzeSenzaOggetto; }

    public Map<String, Boolean> getStanzaChiusa() { return stanzaChiusa; }
    public void setStanzaChiusa(Map<String, Boolean> stanzaChiusa) { this.stanzaChiusa = stanzaChiusa; }
    public Map<String, String> getOggettoPerStanza() { return oggettoPerStanza; }
    public void setOggettoPerStanza(Map<String, String> oggettoPerStanza) { this.oggettoPerStanza = oggettoPerStanza; }

    public int getCurrentFloor() { return currentFloor; }
    public void setCurrentFloor(int currentFloor) { this.currentFloor = currentFloor; }

    public Map<String, Boolean> getStanzaLuceAccesa() { return stanzaLuceAccesa; }
    public void setStanzaLuceAccesa(Map<String, Boolean> stanzaLuceAccesa) { this.stanzaLuceAccesa = stanzaLuceAccesa; }

    public Map<String, Boolean> getStanzaHaPezzo() { return stanzaHaPezzo; }
    public void setStanzaHaPezzo(Map<String, Boolean> stanzaHaPezzo) { this.stanzaHaPezzo = stanzaHaPezzo; }

    public boolean isSceneInizialiCompletate() { return sceneInizialiCompletate; }
    public void setSceneInizialiCompletate(boolean sceneInizialiCompletate) { this.sceneInizialiCompletate = sceneInizialiCompletate; }
}
