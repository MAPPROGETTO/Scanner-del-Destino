/**
 * Classe Lewis - rappresenta il personaggio principale controllato dal giocatore.
 * Gestisce il movimento, l'inventario, le interazioni con la mappa e le scene.
 */
package org.example.model;

import org.example.gui.FinestraGioco;
import org.example.gui.MappaPanel;
import org.example.inventario.Inventario;
import org.example.gui.InventarioPanel;
import org.example.mappa.*;
import org.example.story.*;
import org.example.utils.*;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lewis extends Personaggio implements Serializable {
    private Mappa mappa;
    private GestoreInput input;
    private GestoreInputGUI inputGUI;
    private Inventario inventario;
    private MappaPanel mappaPanel;
    private Posizione posizione;
    private int currentFloor = 1;
    private SceneManager sceneManager;
    private StoryEngine storyEngine;
    private String ultimaDirezioneTentata = null;
    private boolean eventoBalconeAttivo = false; // aggiungi questa variabile
    private boolean codiceSegretoLetto = false;
    private boolean codiceSegretoSbloccato = false;
    private final String CODICE_PORTA_SEGRETA = "4913";
    private static final long serialVersionUID = 1L;
    private boolean scannerAssemblato = false;

    public Lewis(Mappa mappa, GestoreInputGUI inputGUI, Inventario inventario, SceneManager sceneManager) {
        super("Lewis", "Un giovane inventore orfano...", false);
        this.mappa = mappa;
        this.inputGUI = inputGUI;
        this.inventario = inventario;
        this.posizione = new Posizione(6, 5);
        this.sceneManager = sceneManager;
        this.storyEngine = new StoryEngine(sceneManager);
    }

    @Override
    public void interagisci() {
        System.out.println("Lewis: Devo ritrovare i pezzi del mio scanner mnemonico...");
    }



    private boolean isScala(String oggetto) {
        return oggetto.equals("scale") || oggetto.equals("le scale") || oggetto.contains("scala");
    }

    public String entraInStanza(Stanza stanza) {
        if (stanza == null) return "";

        // Assicura che la stanza abbia la scena giusta
        if (stanza.getScena() == null) {
            sceneManager.getSceneDefault(stanza.getNome()).ifPresent(stanza::setScena);
        }

        StringBuilder testo = new StringBuilder();

        // Dentro il metodo entraInStanza(Stanza stanza)
        if (stanza.getNome().equalsIgnoreCase("balcone")) {
            sceneManager.getEventoPericolosoPerStanza(stanza.getNome()).ifPresent(scena -> {
                if (testo.length() > 0) testo.append("\n");
                testo.append(scena.mostra());
                eventoBalconeAttivo = true;
            });
        }

        if (stanza.getOggetto() == null && !stanza.getNome().equalsIgnoreCase("scale") && !stanza.getNome().equalsIgnoreCase("corridoio") && !stanza.getNome().equalsIgnoreCase("stanza segreta")) {
            // Se non c'è un oggetto, mostra la scena di completamento (se esiste)
            sceneManager.getSceneCompletamento(stanza.getNome()).ifPresent(scena -> {
                if (testo.length() > 0) testo.append("\n");
                testo.append(scena.mostra());
            });
        } else {
            // Se c'è ancora l'oggetto, mostra la scena di default (se esiste)
            sceneManager.getSceneDefault(stanza.getNome()).ifPresent(scena -> {
                if (testo.length() > 0) testo.append("\n");
                testo.append(scena.mostra());
            });
        }
        if (stanza.getNome().equalsIgnoreCase("Stanza Segreta") && !scannerAssemblato) {
            Window w = SwingUtilities.getWindowAncestor(mappaPanel);
            if (w instanceof FinestraGioco fg) {
                fg.appendOutput("----------------------------------------------------");
                fg.appendOutput("> Sul banco ci sono i componenti dello Scanner...");
                fg.appendOutput("> Digita 'assembla scanner' per iniziare il montaggio.");
            }
        }

        return testo.toString();
    }


    public void spostamentoPiano(int nuovoPiano, Posizione nuovaPosizione) {
        this.currentFloor = nuovoPiano;
        this.posizione = nuovaPosizione;
        if (mappaPanel != null) {
            mappaPanel.cambiaPiano(nuovoPiano, nuovaPosizione.getX(), nuovaPosizione.getY());
        }
    }

    public Mappa getMappa() { return mappa; }

    public Posizione getPosizione() { return posizione; }
    public void setMappaPanel(MappaPanel mappaPanel) { this.mappaPanel = mappaPanel; }
    public int getCurrentFloor() { return currentFloor; }
    public int getPosizioneX() { return posizione.getX(); }
    public int getPosizioneY() { return posizione.getY(); }
    public void setPosizione(int x, int y) {
        this.posizione = new Posizione(x, y);
        if (mappaPanel != null) {
            mappaPanel.aggiornaMappa(mappa, x, y);
        }
    }
    public String getCodicePortaSegreta() { return CODICE_PORTA_SEGRETA; }
    public String getUltimaDirezioneTentata() { return ultimaDirezioneTentata; }
    public boolean isCodiceSegretoLetto() { return codiceSegretoLetto; }
    public void setCodiceSegretoLetto(boolean v) { this.codiceSegretoLetto = v; }

    public boolean isCodiceSegretoSbloccato() { return codiceSegretoSbloccato; }
    public void setCodiceSegretoSbloccato(boolean v) { this.codiceSegretoSbloccato = v; }
    public boolean isScannerAssemblato() { return scannerAssemblato; }
    public void setScannerAssemblato(boolean v) { scannerAssemblato = v; }




    public String usaOggetto(String oggetto) {
        Stanza stanza = mappa.getStanza(posizione);

        if (ultimaDirezioneTentata == null) {
            String risultato = inventario.usa(oggetto, stanza, sceneManager);
            inventario.salvaSuFile();
            if (mappaPanel != null) {
                mappaPanel.aggiornaMappa(mappa, posizione.getX(), posizione.getY());
            }
            return risultato;
        }

        if (!inventario.contieneOggetto(oggetto)) {
            return "> Non hai questo oggetto nel tuo inventario.";
        }

        Posizione targetPos = new Posizione(posizione.getX(), posizione.getY());
        switch (ultimaDirezioneTentata) {
            case "nord": case "n": case "avanti": case "su": case "a nord":
                targetPos.muoviNord(); break;
            case "sud": case "s": case "indietro": case "giù": case "a sud":
                targetPos.muoviSud(); break;
            case "est": case "e": case "destra": case "a est":
                targetPos.muoviEst(); break;
            case "ovest": case "o": case "sinistra": case "a ovest":
                targetPos.muoviOvest(); break;
            default:
                return "> Direzione memorizzata non valida.";
        }

        Stanza stanzaDest = mappa.getStanza(targetPos);
        if (stanzaDest == null || !stanzaDest.isChiusa()) {
            ultimaDirezioneTentata = null;
            return "> Non c'è una porta chiusa da aprire.";
        }

        if (stanzaDest.apriCon(oggetto)) {
            posizione = targetPos;
            ultimaDirezioneTentata = null;
            return "> Usi la chiave. La porta si apre.\n" + entraInStanza(stanzaDest);
        } else {
            return "> Questo oggetto non apre la porta.";
        }
    }

}