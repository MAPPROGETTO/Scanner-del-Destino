/**
 * Classe Lewis - rappresenta il personaggio principale controllato dal giocatore.
 * Gestisce il movimento, l'inventario, le interazioni con la mappa e le scene.
 */
package org.example.model;

import org.example.gui.MappaPanel;
import org.example.inventario.Inventario;
import org.example.gui.InventarioPanel;
import org.example.mappa.*;
import org.example.story.*;
import org.example.utils.*;

public class Lewis extends Personaggio {
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
    private final String CODICE_PORTA_SEGRETA = "4913";

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
        if (stanzaDestinazione.getNome().equalsIgnoreCase("Stanza Segreta") && !codiceSegretoLetto) {
            ultimaDirezioneTentata = direzione.toLowerCase();
            return "> La porta ha un tastierino numerico. Inserisci il codice per aprirla.";
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
                if (oggetto.contains("chiave") && inventario.contieneOggetto("chiave_segreta")) {
                    codiceSegretoLetto = true;
                    return "> Giri la chiave e sul retro leggi un codice: '4913'. Potrebbe essere utile.";
                }
                return "> Non c'è nulla da leggere su questo oggetto.";

            case "inserisci":
                if (oggetto.equals(CODICE_PORTA_SEGRETA)) {
                    codiceSegretoLetto = true;
                    return "> Codice corretto. La porta della stanza segreta si apre.";
                } else {
                    return "> Codice errato. Riprova.";
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
                    Posizione posizioneSegreta = new Posizione(3, 2);
                    Posizione posizioneAdiacente = new Posizione(6, 2);

                    Stanza stanzaSegreta = mappa.getStanza(posizioneSegreta);
                    Stanza stanzaAdiacente = mappa.getStanza(posizioneAdiacente);

                    if (stanzaSegreta != null && stanzaAdiacente != null) {
                        stanzaSegreta.setChiusa(false);
                        stanzaSegreta.setEst(true); // permette accesso da est della stanza adiacente
                        stanzaAdiacente.setOvest(true); // permette accesso da ovest della stanza segreta
                        mappaPanel.controllaStanzaSegretaAperta(inventario);
                        System.out.println("DEBUG - stanzaSegreta: " + stanzaSegreta.getNome()+", chiusa: " + stanzaSegreta.isChiusa());
                        System.out.println("DEBUG - stanzaAdiacente: " + stanzaAdiacente.getNome());
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

        if (stanza.getOggetto() == null) {
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