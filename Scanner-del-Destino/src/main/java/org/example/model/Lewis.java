/**
 * Classe Lewis - rappresenta il personaggio principale controllato dal giocatore.
 * Gestisce il movimento, l'inventario, le interazioni con la mappa e le scene.
 */
package org.example.model;

import org.example.engine.gui.MappaPanel;
import org.example.inventario.Inventario;
import org.example.mappa.*;
import org.example.story.*;
import org.example.utils.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe che rappresenta il personaggio principale "Lewis".
 * Gestisce la posizione, l'inventario, le interazioni con la mappa e le scene.
 * @version 2.0
 */
public class Lewis extends Personaggio implements Serializable {
    private Mappa mappa;
    private Inventario inventario;
    private MappaPanel mappaPanel;
    private Posizione posizione;
    private int currentFloor = 1;
    private SceneManager sceneManager;
    // --- Command Service per spostare qui la logica dei comandi (in utils.GestoreInput)
    private GestoreInput commandService;

    private String ultimaDirezioneTentata = null;
    private boolean balconeCrollato;

    private boolean codiceSegretoLetto = false;
    private boolean codiceSegretoSbloccato = false;
    private final String CODICE_PORTA_SEGRETA = "4913";
    private static final long serialVersionUID = 1L;
    private boolean scannerAssemblato = false;
    // --- NUOVO ---
    private Set<String> stanzeVisitate = new HashSet<>();
    private Set<String> stanzeCompletate = new HashSet<>();
    /**
     *  Ritorna l'insieme delle stanze visitate.
     * @return stanze visitate
     */
    public Set<String> getStanzeVisitate() { return stanzeVisitate; }
    /**
     * Ritorna l'insieme delle stanze completate.
     * @return stanze completate
     */
    public Set<String> getStanzeCompletate() { return stanzeCompletate; }

    /**
     * Imposta l'insieme delle stanze visitate.
     * @param s
     */
    public void setStanzeVisitate(Set<String> s) { stanzeVisitate = (s == null)? new HashSet<>() : s; }

    /**
     * Imposta l'insieme delle stanze completate.
     * @param s
     */
    public void setStanzeCompletate(Set<String> s) { stanzeCompletate = (s == null)? new HashSet<>() : s; }

    /**
     * Costruttore della classe Lewis.
     * @param mappa
     * @param inventario
     * @param sceneManager
     */
    public Lewis(Mappa mappa, Inventario inventario, SceneManager sceneManager) {
        super("Lewis", "Un giovane inventore orfano...", false);
        this.mappa = mappa;
        this.inventario = inventario;
        this.posizione = new Posizione(6, 5);
        this.sceneManager = sceneManager;
    }

    /**
     * Gestisce lo spostamento di Lewis tra i piani della mappa.
     * @param nuovoPiano
     * @param nuovaPosizione
     */
    public void spostamentoPiano(int nuovoPiano, Posizione nuovaPosizione) {
        this.currentFloor = nuovoPiano;
        this.posizione = nuovaPosizione;
        if (mappaPanel != null) {
            mappaPanel.cambiaPiano(nuovoPiano, nuovaPosizione.getX(), nuovaPosizione.getY());
        }
    }

    /**
     * Gestisce il movimento di Lewis nella direzione specificata.
     * @param direzione
     * @return Messaggio di esito del movimento.
     */
    public String muoviEsploratore(String direzione) {
        if (commandService == null) return "> Servizio comandi non inizializzato.";
        return commandService.muoviEsploratore(direzione);
    }

    /**
     * Interpreta un comando ricevuto dalla GUI.
     * @param comando
     * @return Risposta al comando.
     */
    public String interpretaComandoDaGUI(String comando) {
        if (commandService == null) return "> Servizio comandi non inizializzato.";
        return commandService.interpretaComandoDaGUI(comando);
    }

    public String usaOggetto(String oggetto) {
        if (commandService == null) return "> Servizio comandi non inizializzato.";
        return commandService.usaOggetto(oggetto);
    }


    public Mappa getMappa() { return mappa; }

    /**
     * Ritorna la posizione corrente di Lewis.
     * @return posizione X
     */
    public int getPosizioneX() { return posizione.getX(); }

    /**
     * Ritorna la posizione corrente di Lewis.
     * @return posizione Y
     */
    public int getPosizioneY() { return posizione.getY(); }

    /**
     * Ritorna la posizione corrente di Lewis.
     * @return posizione (oggetto Posizione)
     */
    public Posizione getPosizione() { return posizione; }
    public void setMappaPanel(MappaPanel mappaPanel) { this.mappaPanel = mappaPanel; }
    public int getCurrentFloor() { return currentFloor; }
    public void setPosizione(int x, int y) {
        this.posizione = new Posizione(x, y);
        if (mappaPanel != null) {
            mappaPanel.aggiornaMappa(mappa, x, y);
        }
    }

    /**
     * Imposta il servizio di gestione dei comandi.
     * @param s
     */
    public void setCommandService(GestoreInput s) {this.commandService = s;}

    /**
     * Ritorna il codice della porta segreta.
     * @return codice della porta segreta
     */
    public String getCodicePortaSegreta() { return CODICE_PORTA_SEGRETA; }

    /**
     * Ritorna l'ultima direzione tentata da Lewis.
     *
     * @return ultima direzione tentata
     */
    public String getUltimaDirezioneTentata() { return ultimaDirezioneTentata; }

    /**
     * Ritorna se il codice segreto è stato letto.
     * @return true se il codice segreto è stato letto, false altrimenti
     */
    public boolean isCodiceSegretoLetto() { return codiceSegretoLetto; }

    /**
     * Imposta se il codice segreto è stato letto.
     * @param v
     */
    public void setCodiceSegretoLetto(boolean v) { this.codiceSegretoLetto = v; }

    /**
     * Ritorna se il codice segreto è stato sbloccato.
     * @return true se il codice segreto è stato sbloccato, false altrimenti
     */
    public boolean isCodiceSegretoSbloccato() { return codiceSegretoSbloccato; }

    /**
     * Imposta se il codice segreto è stato sbloccato.
     * @param v
     */
    public void setCodiceSegretoSbloccato(boolean v) { this.codiceSegretoSbloccato = v; }

    /**
     * Ritorna se lo scanner è stato assemblato.
     * @return true se lo scanner è stato assemblato, false altrimenti
     */
    public boolean isScannerAssemblato() { return scannerAssemblato; }

    /**
     * Imposta se lo scanner è stato assemblato.
     * @param v
     */
    public void setScannerAssemblato(boolean v) { scannerAssemblato = v; }

    public boolean isBalconeCrollato(){ return balconeCrollato; }
    public void setBalconeCrollato(boolean v){ balconeCrollato = v; }

}