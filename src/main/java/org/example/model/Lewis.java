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

    public String muoviEsploratore(String direzione) {
        Posizione nuovaPos = new Posizione(posizione.getX(), posizione.getY());
        Stanza stanzaCorrente = mappa.getStanza(posizione);

        switch (direzione.toLowerCase()) {
            case "nord": case "n": case "avanti": case "su": case "a nord":
                if (stanzaCorrente != null && stanzaCorrente.canGoNorth()) nuovaPos.muoviNord();
                else return "> Non puoi andare a nord!";
                break;
            case "sud": case "s": case "indietro": case "giù": case "a sud":
                if (stanzaCorrente != null && stanzaCorrente.canGoSouth()) nuovaPos.muoviSud();
                else return "> Non puoi andare a sud!";
                break;
            case "est": case "e": case "destra": case "a est":
                if (stanzaCorrente != null && stanzaCorrente.canGoEast()) nuovaPos.muoviEst();
                else return "> Non puoi andare a est!";
                break;
            case "ovest": case "o": case "sinistra": case "a ovest":
                if (stanzaCorrente != null && stanzaCorrente.canGoWest()) nuovaPos.muoviOvest();
                else return "> Non puoi andare a ovest!";
                break;
            default:
                return "> Direzione non valida.";
        }

        Stanza stanzaDestinazione = mappa.getStanza(nuovaPos);

        if (stanzaDestinazione == null) {
            return "> Non puoi andare in quella direzione.";
        }

        if (stanzaDestinazione.getNome().equalsIgnoreCase("Stanza Segreta") && stanzaDestinazione.isChiusa()) {
            if (!codiceSegretoSbloccato) {
                ultimaDirezioneTentata = direzione.toLowerCase();
                return "> La porta ha un tastierino numerico. Inserisci il codice per aprirla.";
            }
        }


        if (stanzaDestinazione.isChiusa()) {
            ultimaDirezioneTentata = direzione.toLowerCase();
            return "> La porta è chiusa. Usa una chiave per aprirla.";
        }

        posizione = nuovaPos;
        ultimaDirezioneTentata = null;
        return entraInStanza(stanzaDestinazione);
    }


    public String interpretaComandoDaGUI(String comando) {
        String[] parole = comando.trim().toLowerCase().split("\\s+");
        if (parole.length == 0) return "> Comando non valido.";

        String verbo = parole[0];
        String oggetto = parole.length > 1 ? comando.substring(verbo.length()).trim() : "";
        Stanza stanza = mappa.getStanza(posizione);
        InventarioPanel inventarioPanel = new InventarioPanel(inventario);

        if (verbo.equals("prendi") && oggetto.equals("fune") && eventoBalconeAttivo && stanza.getNome().equalsIgnoreCase("balcone")) {
            eventoBalconeAttivo = false; // disattiva il pericolo
            return sceneManager.getSceneEvento("salvataggio_fune_balcone")
                    .map(scena -> scena.mostra())
                    .orElse("> Prendi la fune e ti salvi!");
        }

        switch (verbo) {
            case "leggi":
                // normalizza l’oggetto
                String obj = oggetto.trim().toLowerCase();

                // Documento: apri finestra stile "Aiuto" e marca i flag
                if ((obj.equals("documento") || obj.contains("documento")) && inventario.contieneOggetto("documento")) {
                    // mostra il documento con il codice dinamico
                    Component parent = (mappaPanel != null) ? mappaPanel : null;
                    GameDocument.showDocumento(parent, this.CODICE_PORTA_SEGRETA);

                    // aggiorna i flag della trama
                    codiceSegretoLetto = true;

                    return "> Apri e leggi il documento.";
                }

                return "> Non c'è nulla da leggere su questo oggetto.";

            case "inserisci":
                if (oggetto.equals(CODICE_PORTA_SEGRETA)) {
                    codiceSegretoLetto = true;
                    codiceSegretoSbloccato = true;
                    Posizione posizioneSegreta = new Posizione(2, 2);
                    Stanza stanzaSegreta = mappa.getStanza(posizioneSegreta);
                    if (stanzaSegreta != null) {
                        stanzaSegreta.setChiusa(false);
                    }
                    return "> Codice corretto. La porta della stanza segreta si apre.";
                } else {
                    return "> Codice errato. Riprova.";
                }

            case "aiuto":
            case "help":
            case "?":
            case "comandi": {
                java.awt.Component parent = (mappaPanel != null) ? mappaPanel : null;
                org.example.utils.GameHelp.show(parent);
                return "Apro le istruzioni...";
            }


            case "esci":
            case "exit":
            {
                Window w = SwingUtilities.getWindowAncestor(mappaPanel);
                if (w instanceof FinestraGioco fg) {
                    fg.returnToMenu();
                    return "> Torni al menu principale.";
                }
                return "> Non riesco a tornare al menu (finestra non trovata).";
            }

            case "quit":
            case "chiudi":
            {
                org.example.utils.GameExit.confirmAndExit(
                        mappaPanel != null ? javax.swing.SwingUtilities.getWindowAncestor(mappaPanel) : null
                );
                return "> Uscita annullata.";
            }

            case "salva": {
                // 1) slot esplicito? (es: "salva slot2")
                Integer slotRichiesto = null;
                if (!oggetto.isEmpty()) {
                    String lower = oggetto.toLowerCase().replaceAll("\\s+", "");
                    Matcher m = Pattern.compile("^slot([1-3])$").matcher(lower);
                    if (m.find()) slotRichiesto = Integer.parseInt(m.group(1));
                }
                Integer slotDaUsare = slotRichiesto;

                // 2) se non specificato, proponi opzioni in base allo slot correntemente caricato
                if (slotDaUsare == null) {
                    Window w = SwingUtilities.getWindowAncestor(mappaPanel);
                    Integer slotCorrente = null;
                    if (w instanceof FinestraGioco fg) {
                        slotCorrente = fg.getSlotCaricatoCorrente();
                    }
                    slotDaUsare = SaveHelper.promptForSaveSlot(w, slotCorrente);
                    if (slotDaUsare == null) return "> Salvataggio annullato.";
                } else {
                    // se specificato ed è occupato (diverso da eventuale slot corrente), chiedi conferma
                    if (GameSaver.esisteSalvataggio(slotDaUsare)) {
                        Window w = SwingUtilities.getWindowAncestor(mappaPanel);
                        int ok = JOptionPane.showConfirmDialog(
                                w, "Lo slot " + slotDaUsare + " contiene già un salvataggio.\nVuoi sovrascriverlo?",
                                "Conferma sovrascrittura", JOptionPane.YES_NO_OPTION);
                        if (ok != JOptionPane.YES_OPTION) return "> Salvataggio annullato.";
                    }
                }

                // 3) costruisci DTO e salva
                StatoGioco stato = GameStateMapper.buildFrom(this, inventario, mappa, storyEngine);
                boolean ok = GameSaver.salvaPartita(stato, slotDaUsare);
                if (ok) {
                    // aggiorna lo slot attivo della sessione
                    Window w = SwingUtilities.getWindowAncestor(mappaPanel);
                    if (w instanceof FinestraGioco fg) {
                        fg.setSlotCaricatoCorrente(slotDaUsare);
                    }
                    return "> Partita salvata nello slot " + slotDaUsare + ".";
                } else {
                    return "> Errore durante il salvataggio.";
                }
            }


            case "prendi":
                if (oggetto.isEmpty()) return "> Cosa vuoi prendere?";

                String oggettoNormalizzato = Inventario.normalizza(oggetto);

                if (!stanza.oggettoMatches(oggettoNormalizzato)) {
                    return "> Non c'è questo oggetto da prendere qui.";
                }

                StringBuilder messaggio = new StringBuilder(inventario.aggiungi(oggetto));

                if (inventario.tuttiOggettiRaccolti()) {
                    // Sblocca la stanza segreta logicamente e fisicamente
                    Posizione posizioneSegreta = new Posizione(2, 2);
                    Posizione posizioneAdiacente = new Posizione(6, 2);

                    Stanza stanzaSegreta = mappa.getStanza(posizioneSegreta);
                    Stanza stanzaAdiacente = mappa.getStanza(posizioneAdiacente);

                    if (stanzaSegreta != null && stanzaAdiacente != null) {
                        stanzaSegreta.setEst(true); // permette accesso da est della stanza adiacente
                        stanzaAdiacente.setOvest(true); // permette accesso da ovest della stanza segreta
                        mappaPanel.controllaStanzaSegretaAperta(inventario);
                        System.out.println("DEBUG - stanzaSegreta: " + stanzaSegreta.getNome()+", chiusa: " + stanzaSegreta.isChiusa());
                        System.out.println("Nuova posizione: " + stanzaSegreta);
                        System.out.println("Stanza destinazione: " + (stanzaSegreta != null ? stanzaSegreta.getNome() : "null"));
                        System.out.println("Collegamento attivo? " + stanzaAdiacente.canGoWest()); // cambia in base alla direzione
                        System.out.println("DEBUG - stanzaAdiacente: " + stanzaAdiacente.getNome());
                        mappaPanel.aggiornaMappa(mappa, posizione.getX(), posizione.getY());
                    }
                }

                inventario.salvaSuFile();
                inventarioPanel.aggiornaInventario();

                String nomeEvento = stanza.getOggetto();
                sceneManager.getSceneEvento(nomeEvento)
                        .ifPresent(scena -> messaggio.append("\n").append(scena.mostra()));

                stanza.setOggetto(null); // rimuovi oggetto dalla stanza
                return sceneManager.getSceneEvento(nomeEvento).isPresent()
                        ? messaggio.toString()
                        : "> Hai preso: " + oggettoNormalizzato;

            case "lascia":
                if (oggetto.isEmpty()) return "> Cosa vuoi lasciare?";
                String msg = inventario.rimuovi(oggetto);
                inventario.salvaSuFile();
                inventarioPanel.aggiornaInventario();
                return msg;

            case "vai": case "sposta": case "muoviti":
                return muoviEsploratore(oggetto);

            case "sali":
                if (isScala(oggetto)) {
                    if (stanza != null && stanza.getNome().equalsIgnoreCase("scale")) {
                        spostamentoPiano(2, new Posizione(5, 6));
                        return "Hai usato le scale e sei salito al secondo piano!";
                    }
                    return "Non ci sono scale qui da usare.";
                }
                return "> Hai usato: " + oggetto;

            case "usa": case "utilizza": case "attiva":
                if (oggetto.isEmpty()) return "> Cosa vuoi usare?";
                String risultato = inventario.usa(oggetto, stanza, sceneManager);
                inventario.salvaSuFile();
                inventarioPanel.aggiornaInventario();
                return risultato;

            case "assembla":
            case "costruisci":
            case "monta":
                if (oggetto.contains("scanner") || oggetto.isEmpty()) {
                    Window w = SwingUtilities.getWindowAncestor(mappaPanel);
                    if (w instanceof FinestraGioco fg) {
                        fg.showScannerAssembly();
                        return "> Avvio assemblaggio...";
                    }
                    return "> Non riesco ad avviare il pannello di assemblaggio.";
                }
                // Java
                if (scannerAssemblato) {
                    // codice da eseguire se lo scanner è assemblato
                } else {
                    // codice da eseguire se NON è assemblato
                }

                return "> Cosa vuoi assemblare?";

            case "scendi": case "scendere":
                if (isScala(oggetto)) {
                    if (stanza != null && stanza.getNome().equalsIgnoreCase("Scale")) {
                        spostamentoPiano(1, new Posizione(6, 5));
                        return "Sei sceso al piano inferiore!";
                    }
                    return "Non ci sono scale qui da usare.";
                }
                return "> Hai usato: " + oggetto;

            default:
                return "> Comando sconosciuto.";
        }
    }


    private boolean isScala(String oggetto) {
        return oggetto.equals("scale") || oggetto.equals("le scale") || oggetto.contains("scala");
    }

    private String entraInStanza(Stanza stanza) {
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
    public int getPosizioneX() { return posizione.getX(); }
    public int getPosizioneY() { return posizione.getY(); }
    public Posizione getPosizione() { return posizione; }
    public void setMappaPanel(MappaPanel mappaPanel) { this.mappaPanel = mappaPanel; }
    public int getCurrentFloor() { return currentFloor; }
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