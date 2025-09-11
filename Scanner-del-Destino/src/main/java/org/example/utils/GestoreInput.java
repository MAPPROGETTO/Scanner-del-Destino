package org.example.utils;

import org.example.engine.gui.FinestraGioco;
import org.example.engine.gui.InventarioPanel;
import org.example.engine.gui.MappaPanel;
import org.example.inventario.Inventario;
import org.example.mappa.Mappa;
import org.example.mappa.Posizione;
import org.example.mappa.Stanza;
import org.example.model.Lewis;
import org.example.model.StatoGioco;
import org.example.story.NarrationDbAdapter;
import org.example.story.SceneManager;
import org.example.story.ScenesDb;
import org.example.story.StoryEngine;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe che gestisce l'input dell'utente sia da console (testuale) che da GUI (comandi).
 * Implementa l'interfaccia InterfacciaInputUtente.
 * Contiene la logica di interpretazione dei comandi.
 * Usa gli oggetti di gioco (Lewis, Mappa, Inventario, SceneManager) per eseguire i comandi.
 * Permette di muovere l'esploratore, usare oggetti, prendere/lasciare oggetti, salvare/caricare.
 *
 */
public class GestoreInput implements Serializable {

    // ======= dipendenze =======
    private final Lewis lewis;
    private final Mappa mappa;
    private final Inventario inventario;
    private final MappaPanel mappaPanel;
    private final SceneManager sceneManager;
    private final StoryEngine storyEngine;

    // ======= stato comando =======
    private boolean inserimentoCodiceAttivo = false; // tastierino porta segreta
    private String ultimaDirezioneTentata = null;

    // ======= stato balcone (PERICOLO) =======
    private boolean eventoBalconeAttivo = false;
    private PericoloBalcone pericoloBalcone;

    // ======= porta segreta =======
    private boolean codiceSegretoLetto = false;
    private boolean codiceSegretoSbloccato = false;

    /**
     * Costruttore.
     * @param lewis
     * @param mappa
     * @param inventario
     * @param mappaPanel
     * @param sceneManager
     * @param storyEngine
     */
    public GestoreInput(Lewis lewis, Mappa mappa, Inventario inventario, MappaPanel mappaPanel, SceneManager sceneManager, StoryEngine storyEngine) {
        this.lewis = lewis;
        this.mappa = mappa;
        this.inventario = inventario;
        this.mappaPanel = mappaPanel;
        this.sceneManager = sceneManager;
        this.storyEngine = storyEngine;
    }

    // =============== MOVIMENTO ===============

    /**
     * Entra in una stanza e mostra il testo di ingresso.
     * @param direzione
     * @return testo di ingresso o messaggio di errore
     */
    public String muoviEsploratore(String direzione) {
        Posizione posAtt = lewis.getPosizione();
        if (posAtt == null) return "> Posizione non definita.";

        // Blocca QUALSIASI movimento se il pericolo balcone è attivo e sei sul balcone
        Stanza stanzaCorrente = mappa.getStanza(posAtt);
        if (stanzaCorrente != null
                && stanzaCorrente.getNome().equalsIgnoreCase("Balcone")
                && eventoBalconeAttivo
                && pericoloBalcone != null
                && pericoloBalcone.isAttivo()) {
            return "> Non puoi muoverti! Il balcone sta crollando — aggrappati alla fune!";
        }

        // Calcola nuova pos
        Posizione nuovaPos = new Posizione(posAtt.getX(), posAtt.getY());
        String d = direzione.toLowerCase(Locale.ITALY);
        switch (d) {
            case "nord": case "n": case "avanti": case "su": case "a nord":
                if (stanzaCorrente != null && stanzaCorrente.canGoNorth()) nuovaPos.muoviNord(); else return "> Non puoi andare a nord!";
                break;
            case "sud": case "s": case "indietro": case "giù": case "giu": case "a sud":
                if (stanzaCorrente != null && stanzaCorrente.canGoSouth()) nuovaPos.muoviSud(); else return "> Non puoi andare a sud!";
                break;
            case "est": case "e": case "destra": case "a est":
                if (stanzaCorrente != null && stanzaCorrente.canGoEast()) nuovaPos.muoviEst(); else return "> Non puoi andare a est!";
                break;
            case "ovest": case "o": case "sinistra": case "a ovest":
                if (stanzaCorrente != null && stanzaCorrente.canGoWest()) nuovaPos.muoviOvest(); else return "> Non puoi andare a ovest!";
                break;
            default:
                return "> Direzione non riconosciuta.";
        }

        Stanza stanzaDest = mappa.getStanza(nuovaPos);
        if (stanzaDest == null) return "> Non puoi andare in quella direzione.";

        // Se il balcone è crollato (flag persistente o stanza rimossa), impedisci di entrarci
        if (stanzaDest.getNome().equalsIgnoreCase("Balcone") && (isBalconeCrollato() || mappa.getStanzaByNome("Balcone") == null)) {
            return "> Il balcone è crollato: non puoi più raggiungerlo.";
        }

        // Tastierino stanza segreta
        if (stanzaDest.getNome().equalsIgnoreCase("Stanza Segreta") && stanzaDest.isChiusa()) {
            if (!codiceSegretoSbloccato) {
                ultimaDirezioneTentata = d;
                inserimentoCodiceAttivo = true;
                return "> La porta ha un tastierino numerico. Inserisci il codice per aprirla.";
            }
        }

        // Porte generiche chiuse
        if (stanzaDest.isChiusa()) {
            ultimaDirezioneTentata = d;
            return "> La porta è chiusa. Usa una chiave per aprirla.";
        }

        // Sposta
        lewis.setPosizione(nuovaPos.getX(), nuovaPos.getY());
        ultimaDirezioneTentata = null;

        // Testo ingresso
        return entraInStanza(stanzaDest);
    }

    // ============ COMANDI GUI ============

    /**
     * Mostra il testo di ingresso della stanza, se definito.
     *
     * @param comando
     * @return testo di ingresso o messaggio di errore
     */
    public String interpretaComandoDaGUI(String comando) {
        // 1) Pericolo balcone: intercetta PRIMA di tutto
        if (eventoBalconeAttivo && pericoloBalcone != null && pericoloBalcone.isAttivo()) {
            return pericoloBalcone.handle(comando); // ansia o salvataggio
        }

        String[] parole = comando.trim().toLowerCase().split("\\s+");
        if (parole.length == 0) return "> Comando non valido.";

        String verbo = parole[0];
        String oggetto = parole.length > 1 ? comando.substring(verbo.length()).trim() : "";
        Stanza stanza = mappa.getStanza(lewis.getPosizione());
        InventarioPanel inventarioPanel = new InventarioPanel(inventario);

        switch (verbo) {
            case "leggi": {
                String obj = oggetto.trim().toLowerCase();
                if ((obj.equals("documento") || obj.contains("documento")) && inventario.contieneOggetto("documento")) {
                    Component parent = (mappaPanel != null) ? mappaPanel : null;
                    org.example.utils.GameDocument.showDocumento(parent, lewis.getCodicePortaSegreta());
                    lewis.setCodiceSegretoLetto(true);
                    codiceSegretoLetto = true;
                    return "> Apri e leggi il documento.";
                }
                return "> Non c'è nulla da leggere su questo oggetto.";
            }

            case "inserisci": {
                if (!inserimentoCodiceAttivo) return "> Non c'è nessun tastierino da usare in questo momento.";
                if (oggetto.equals(lewis.getCodicePortaSegreta())) {
                    lewis.setCodiceSegretoLetto(true);
                    lewis.setCodiceSegretoSbloccato(true);
                    codiceSegretoSbloccato = true;

                    Posizione posSegreta = new Posizione(2, 2);
                    Stanza stanzaSegreta = mappa.getStanza(posSegreta);
                    if (stanzaSegreta != null) stanzaSegreta.setChiusa(false);

                    if (mappaPanel != null) {
                        mappaPanel.apriStanzaSegreta();
                        mappaPanel.controllaStanzaSegretaAperta(inventario);
                    }

                    inserimentoCodiceAttivo = false;

                    if (ultimaDirezioneTentata != null) {
                        Posizione pos = lewis.getPosizione();
                        Posizione targetPos = new Posizione(pos.getX(), pos.getY());
                        switch (ultimaDirezioneTentata) {
                            case "nord": case "n": case "avanti": case "su": case "a nord": targetPos.muoviNord(); break;
                            case "sud":  case "s": case "indietro": case "giu": case "giù": case "a sud": targetPos.muoviSud(); break;
                            case "est":  case "e": case "destra": case "a est": targetPos.muoviEst(); break;
                            case "ovest":case "o": case "sinistra": case "a ovest": targetPos.muoviOvest(); break;
                            default: /* niente */
                        }

                        Stanza stanzaDest = mappa.getStanza(targetPos);
                        if (stanzaDest != null && !stanzaDest.isChiusa()) {
                            lewis.setPosizione(targetPos.getX(), targetPos.getY());
                            ultimaDirezioneTentata = null;

                            if (mappaPanel != null) {
                                mappaPanel.aggiornaMappa(mappa, lewis.getPosizione().getX(), lewis.getPosizione().getY());
                            }
                            String testoIngresso = entraInStanza(stanzaDest);
                            return "> Codice corretto. La porta della stanza segreta si apre.\n" + testoIngresso;
                        }
                    }

                    ultimaDirezioneTentata = null;
                    return "> Codice corretto. La porta della stanza segreta si apre.";
                } else {
                    return "> Codice errato. Riprova.";
                }
            }

            case "aiuto": case "help": case "?": case "comandi": {
                Component parent = (mappaPanel != null) ? mappaPanel : null;
                org.example.utils.GameHelp.show(parent);
                return "Apro le istruzioni...";
            }

            case "esci": case "exit": {
                Window w = SwingUtilities.getWindowAncestor(mappaPanel);
                if (w instanceof org.example.engine.gui.FinestraGioco fg) {
                    fg.returnToMenu();
                    return "> Torni al menu principale.";
                }
                return "> Non riesco a tornare al menu (finestra non trovata).";
            }

            case "quit": case "chiudi": {
                org.example.utils.GameExit.confirmAndExit(
                        mappaPanel != null ? SwingUtilities.getWindowAncestor(mappaPanel) : null
                );
                return "> Uscita annullata.";
            }

            case "salva": {
                Integer slotRichiesto = null;
                if (!oggetto.isEmpty()) {
                    String lower = oggetto.toLowerCase().replaceAll("\\s+", "");
                    Matcher m = Pattern.compile("^slot([1-3])$").matcher(lower);
                    if (m.find()) slotRichiesto = Integer.parseInt(m.group(1));
                }
                Integer slotDaUsare = slotRichiesto;

                if (slotDaUsare == null) {
                    Window w = SwingUtilities.getWindowAncestor(mappaPanel);
                    Integer slotCorrente = null;
                    if (w instanceof org.example.engine.gui.FinestraGioco fg) {
                        slotCorrente = fg.getSlotCaricatoCorrente();
                    }
                    slotDaUsare = org.example.utils.SaveHelper.promptForSaveSlot(w, slotCorrente);
                    if (slotDaUsare == null) return "> Salvataggio annullato.";
                } else {
                    if (org.example.utils.GameSaver.esisteSalvataggio(slotDaUsare)) {
                        Window w = SwingUtilities.getWindowAncestor(mappaPanel);
                        int ok = JOptionPane.showConfirmDialog(
                                w, "Lo slot " + slotDaUsare + " contiene già un salvataggio.\nVuoi sovrascriverlo?",
                                "Conferma sovrascrittura", JOptionPane.YES_NO_OPTION);
                        if (ok != JOptionPane.YES_OPTION) return "> Salvataggio annullato.";
                    }
                }

                StatoGioco stato = org.example.utils.GameStateMapper.buildFrom(lewis, inventario, mappa, storyEngine);
                boolean ok = org.example.utils.GameSaver.salvaPartita(stato, slotDaUsare);
                if (ok) {
                    Window w = SwingUtilities.getWindowAncestor(mappaPanel);
                    if (w instanceof org.example.engine.gui.FinestraGioco fg) fg.setSlotCaricatoCorrente(slotDaUsare);
                    return "> Partita salvata nello slot " + slotDaUsare + ".";
                } else {
                    return "> Errore durante il salvataggio.";
                }
            }

            case "prendi": {
                if (stanza == null) return "> Non c'è nulla da prendere qui.";
                if (oggetto.isEmpty()) return "> Cosa vuoi prendere?";
                String oggettoNormalizzato = Inventario.normalizza(oggetto);
                if (!stanza.oggettoMatches(oggettoNormalizzato)) return "> Non c'è questo oggetto da prendere qui.";

                StringBuilder messaggio = new StringBuilder(inventario.aggiungi(oggetto));
                stanza.setOggetto(null);
                try { stanza.raccogliPezzo(); } catch (Throwable ignored) {}

                if (inventario.tuttiOggettiRaccolti()) {
                    Posizione posizioneSegreta = new Posizione(2, 2);
                    Posizione posizioneAdiacente = new Posizione(6, 2);
                    Stanza stanzaSegreta = mappa.getStanza(posizioneSegreta);
                    Stanza stanzaAdiacente = mappa.getStanza(posizioneAdiacente);
                    if (stanzaSegreta != null && stanzaAdiacente != null) {
                        stanzaSegreta.setEst(true);
                        stanzaAdiacente.setOvest(true);
                        if (mappaPanel != null) mappaPanel.controllaStanzaSegretaAperta(inventario);
                    }
                }

                if (mappaPanel != null) {
                    try { mappaPanel.aggiornaMappa(mappa, lewis.getPosizione().getX(), lewis.getPosizione().getY()); } catch (Throwable ignored) {}
                }

                Optional<String> ev = ScenesDb.evento(oggettoNormalizzato);
                ev.ifPresent(s -> messaggio.append("\n").append(s));
                if (ev.isEmpty()) sceneManager.getSceneEvento(oggettoNormalizzato).ifPresent(sc -> messaggio.append("\n").append(sc.mostra()));

                return sceneManager.getSceneEvento(oggettoNormalizzato).isPresent()
                        ? messaggio.toString()
                        : (messaggio.isEmpty() ? ("> Hai preso: " + oggettoNormalizzato) : messaggio.toString());
            }

            case "vai":
            case "sposta":
            case "muoviti":
                return muoviEsploratore(oggetto);

            case "sali": {
                if (isScala(oggetto)) {
                    if (stanza != null && stanza.getNome().equalsIgnoreCase("scale")) {
                        lewis.spostamentoPiano(2, new Posizione(5, 6));
                        if (mappaPanel != null) mappaPanel.cambiaPiano(2, 5, 6);
                        return "Hai usato le scale e sei salito al secondo piano!";
                    }
                    return "Non ci sono scale qui da usare.";
                }
                return "> Hai usato: " + oggetto;
            }

            case "usa":
            case "utilizza":
            case "attiva": {
                if (oggetto.isEmpty()) return "> Cosa vuoi usare?";
                if (stanza != null) {
                    Optional<String> uso = ScenesDb.usoOggetto(stanza.getNome(), oggetto);
                    if (uso.isPresent()) {
                        inventario.usa(oggetto, stanza, sceneManager);
                        inventario.salvaSuFile();
                        try { new InventarioPanel(inventario).aggiornaInventario(); } catch (Throwable ignored) {}
                        return uso.get();
                    }
                }
                String risultato = inventario.usa(oggetto, stanza, sceneManager);
                inventario.salvaSuFile();
                try { new InventarioPanel(inventario).aggiornaInventario(); } catch (Throwable ignored) {}
                return risultato;
            }

            case "assembla":
            case "costruisci":
            case "monta": {
                if (oggetto.contains("scanner") || oggetto.isEmpty()) {
                    Window w = SwingUtilities.getWindowAncestor(mappaPanel);
                    if (w instanceof FinestraGioco fg) { fg.showScannerAssembly(); return "> Avvio assemblaggio..."; }
                    return "> Non riesco ad avviare il pannello di assemblaggio.";
                }
                return "> Cosa vuoi assemblare?";
            }

            case "scendi":
            case "scendere": {
                if (isScala(oggetto)) {
                    if (stanza != null && stanza.getNome().equalsIgnoreCase("scale")) {
                        lewis.spostamentoPiano(1, new Posizione(6, 5));
                        if (mappaPanel != null) mappaPanel.cambiaPiano(1, 6, 5);
                        return "Sei sceso al piano inferiore!";
                    }
                    return "Non ci sono scale qui da usare.";
                }
                return "> Hai usato: " + oggetto;
            }

            default:
                return "> Comando sconosciuto.";
        }
    }

    // ============ USA OGGETTO ============
    /**
     * Usa un oggetto dall'inventario.
     * @param oggetto
     * @return testo di risultato o messaggio di errore
     */
    public String usaOggetto(String oggetto) {
        Stanza stanza = mappa.getStanza(lewis.getPosizione());

        if (ultimaDirezioneTentata == null) {
            String risultato = inventario.usa(oggetto, stanza, sceneManager);
            inventario.salvaSuFile();
            if (mappaPanel != null) {
                mappaPanel.aggiornaMappa(mappa, lewis.getPosizione().getX(), lewis.getPosizione().getY());
            }
            return risultato;
        }

        if (!inventario.contieneOggetto(oggetto)) return "> Non hai questo oggetto nel tuo inventario.";

        Posizione pos = lewis.getPosizione();
        Posizione targetPos = new Posizione(pos.getX(), pos.getY());
        switch (ultimaDirezioneTentata) {
            case "nord": case "n": case "avanti": case "su": case "a nord": targetPos.muoviNord(); break;
            case "sud":  case "s": case "indietro": case "giu": case "giù": case "a sud": targetPos.muoviSud(); break;
            case "est":  case "e": case "destra": case "a est": targetPos.muoviEst(); break;
            case "ovest":case "o": case "sinistra": case "a ovest": targetPos.muoviOvest(); break;
            default: ultimaDirezioneTentata = null; return "> Nessuna porta da aprire in quella direzione.";
        }

        Stanza stanzaDest = mappa.getStanza(targetPos);
        if (stanzaDest == null || !stanzaDest.isChiusa()) { ultimaDirezioneTentata = null; return "> Non c'è una porta chiusa da aprire."; }

        if (stanzaDest.apriCon(oggetto)) {
            lewis.setPosizione(targetPos.getX(), targetPos.getY());
            ultimaDirezioneTentata = null;
            if (mappaPanel != null) mappaPanel.aggiornaMappa(mappa, lewis.getPosizione().getX(), lewis.getPosizione().getY());
            return "> Usi la chiave. La porta si apre.\n" + entraInStanza(stanzaDest);
        } else {
            return "> Questo oggetto non apre la porta.";
        }
    }

    /**
     * controlla se il testo indica una scala
     * @param testo
     * @return true se è una scala
     */
    private boolean isScala(String testo) {
        if (testo == null) return false;
        String t = testo.toLowerCase(Locale.ITALY);
        return t.equals("scala") || t.equals("scale") || t.contains("scala") || t.contains("scale");
    }

    // ============ INGRESSO STANZA ============
    /**
     * Mostra il testo di ingresso della stanza, se definito.
     * @param stanza
     * @return testo di ingresso o messaggio di errore
     */
    private String entraInStanza(Stanza stanza) {
        if (stanza == null) return "";

        // Se il balcone è già crollato (flag o stanza inesistente), blocca ingresso e rimuovi fisicamente
        if (stanza.getNome().equalsIgnoreCase("Balcone") && (isBalconeCrollato() || mappa.getStanzaByNome("Balcone") == null)) {
            return "> Il balcone è crollato: non puoi più entrare di lì.";
        }

        // Stato PERICOLO: entra nel balcone e attiva la sequenza (blocco movimento attivo altrove)
        if (stanza.getNome().equalsIgnoreCase("Balcone")) {
            if (!eventoBalconeAttivo) {
                eventoBalconeAttivo = true;
                pericoloBalcone = new PericoloBalcone(); // avanza solo su errore
                pericoloBalcone.start();
            }
            StringBuilder t = new StringBuilder();
            t.append("> Il pavimento scricchiola sotto i tuoi piedi!\n");
            t.append("> Hai pochi istanti! Guardati intorno per trovare qualcosa con cui salvarti...");
            return t.toString();
        }

        StringBuilder testo = new StringBuilder();

        // DB-first
        String fromDb = NarrationDbAdapter.testoIngressoDaDb(stanza);
        if (!fromDb.isBlank()) {
            testo.append(fromDb);
        } else {
            // Fallback legacy
            if (stanza.getScena() == null) sceneManager.getSceneDefault(stanza.getNome()).ifPresent(stanza::setScena);
            if (stanza.getOggetto() == null
                    && !stanza.getNome().equalsIgnoreCase("scale")
                    && !stanza.getNome().equalsIgnoreCase("corridoio")
                    && !stanza.getNome().equalsIgnoreCase("stanza segreta")) {
                sceneManager.getSceneCompletamento(stanza.getNome()).ifPresent(sc -> {
                    if (testo.length() > 0) testo.append("\n");
                    testo.append(sc.mostra());
                });
            } else {
                sceneManager.getSceneDefault(stanza.getNome()).ifPresent(sc -> {
                    if (testo.length() > 0) testo.append("\n");
                    testo.append(sc.mostra());
                });
            }
        }

        if (stanza.getNome().equalsIgnoreCase("Stanza Segreta") && !lewis.isScannerAssemblato()) {
            Window w = SwingUtilities.getWindowAncestor(mappaPanel);
            if (w instanceof FinestraGioco fg) {
                fg.appendOutput("----------------------------------------------------");
                fg.appendOutput("> Sul banco ci sono i componenti dello Scanner...");
                fg.appendOutput("> Digita 'assembla scanner' per iniziare il montaggio.");
            }
        }
        return testo.toString();
    }
    /** Metodo alternativo con nome diverso per chiarezza semantica.
     * @param stanza
     * @return testo di ingresso o messaggio di errore
     */
    public String entraNellaStanza(Stanza stanza) { return entraInStanza(stanza); }

    // ======= PERICOLO BALCONE: state machine on-error =======
    /** Quando entri nel balcone, parte un conto alla rovescia (gestito altrove).
     * Qui gestiamo solo la sequenza di messaggi e la risoluzione.
     * Il giocatore deve digitare "prendi fune" per salvarsi.
     * Ogni altro comando fa avanzare la sequenza di ansia.
     * Se il giocatore non si salva in tempo, perde.
     */
    private class PericoloBalcone {
        private boolean attivo = false;
        private boolean risolto = false;
        private int step = -1;
        private final String[] messaggi = new String[] {
                "Devo muovermi, sto per crollare!",
                "Oddio... sto per cadere...",
                "Devo aggrapparmi a qualcosa... forse a una fune!",
                "...ALLORA è così difficile prendere una fune... Maledizione digita \"prendi fune\". Grazie!"
        };

        void start(){ attivo = true; step = -1; }
        boolean isAttivo(){ return attivo; }

        /** Gestisce l'ingresso dell'utente durante il pericolo.
         * Se l'ingresso è "prendi fune" o "afferra fune", si salva.
         * Altrimenti, avanza la sequenza di ansia.
         * @param input
         * @return messaggio di stato o di salvataggio
         */
        String handle(String input) {
            if (risolto) return "> Sei già al sicuro.";
            String norm = (input == null) ? "" : input.trim().toLowerCase();

            // Risoluzione
            if (norm.equals("prendi fune") || norm.equals("afferra fune")) {
                risolto = true;
                attivo = false;
                eventoBalconeAttivo = false;

                // prova a persistere un flag su Lewis, se esiste
                try { Lewis.class.getMethod("setBalconeCrollato", boolean.class).invoke(lewis, true); } catch (Throwable ignored) {}

                // sposta in "Stanza dei Bambini"
                Stanza bambini = mappa.getStanzaByNome("Stanza dei Bambini");
                StringBuilder sb = new StringBuilder();
                sb.append("> Ti aggrappi alla fune!\n");

                if (bambini != null) {
                    Posizione p = mappa.posizioneDellaStanza("Stanza dei Bambini");
                    if (p != null) lewis.setPosizione(p.getX(), p.getY());

                    if (mappaPanel != null) {
                        try {
                            mappaPanel.aggiornaMappa(mappa, lewis.getPosizione().getX(), lewis.getPosizione().getY());
                            mappaPanel.repaint();
                        } catch (Throwable ignored) {}
                    }

                    String testo = entraInStanza(bambini);
                    sb.append("> Il balcone crolla alle tue spalle!\n");
                    sb.append(testo);
                } else {
                    sb.append("> Ti salvi, ma non trovo 'Stanza dei Bambini' nella mappa!");
                }
                return sb.toString();
            }

            // Errore: avanza al messaggio successivo (ripeti ultimo solo su ulteriori errori)
            if (step < messaggi.length - 1) step++;
            String msg = messaggi[Math.max(0, step)];
            return "> " + msg;
        }
    }

    // ======= helper flag balcone crollato su Lewis (facoltativo, via reflection) =======
    /** Controlla se il balcone è crollato, usando un ipotetico flag su Lewis.
     * Se il metodo non esiste o fallisce, torna false.
     * @return true se il balcone è crollato
     */
    private boolean isBalconeCrollato() {
        try {
            Object res = Lewis.class.getMethod("isBalconeCrollato").invoke(lewis);
            if (res instanceof Boolean b) return b;
        } catch (Throwable ignored) {}
        return false;
    }
}
