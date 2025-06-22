package org.example.model;

import org.example.gui.MappaPanel;
import org.example.inventario.Inventario;
import org.example.gui.InventarioPanel;
import org.example.mappa.*;
import org.example.story.*;
import org.example.utils.*;
import java.util.Optional;

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

    public Lewis(Mappa mappa, GestoreInput input, Inventario inventario, SceneManager sceneManager) {
        super("Lewis", "Un giovane inventore orfano...", false);
        this.mappa = mappa;
        this.input = input;
        this.inventario = inventario;
        this.posizione = new Posizione(6, 5);
        this.sceneManager = sceneManager;
        this.storyEngine = new StoryEngine(sceneManager);
    }

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

    private String muoviEsploratore(String direzione) {
        Posizione nuovaPos = new Posizione(posizione.getX(), posizione.getY());
        Stanza stanzaCorrente = mappa.getStanza(posizione);

        switch (direzione) {
            case "nord": case "n": case "avanti":
                if (stanzaCorrente != null && stanzaCorrente.canGoNorth()) nuovaPos.muoviNord();
                else return "> Non puoi andare a nord!";
                break;
            case "sud": case "s": case "indietro":
                if (stanzaCorrente != null && stanzaCorrente.canGoSouth()) nuovaPos.muoviSud();
                else return "> Non puoi andare a sud!";
                break;
            case "est": case "e": case "destra":
                if (stanzaCorrente != null && stanzaCorrente.canGoEast()) nuovaPos.muoviEst();
                else return "> Non puoi andare a est!";
                break;
            case "ovest": case "o": case "sinistra":
                if (stanzaCorrente != null && stanzaCorrente.canGoWest()) nuovaPos.muoviOvest();
                else return "> Non puoi andare a ovest!";
                break;
            default:
                return "> Direzione non valida: " + direzione;
        }

        if (mappa.isValida(nuovaPos)) {
            posizione = nuovaPos;
            Stanza nuovaStanza = mappa.getStanza(posizione);
            String dialogo = entraInStanza(nuovaStanza);
            return dialogo != null ? dialogo : "> Sei andato a " + direzione;
        }
        return "> Non puoi andare in quella direzione!";
    }

    public String interpretaComandoDaGUI(String comando) {
        String[] parole = comando.trim().toLowerCase().split("\\s+");
        if (parole.length == 0) return "> Comando non valido.";

        String verbo = parole[0];
        String oggetto = parole.length > 1 ? comando.substring(verbo.length()).trim() : "";
        Stanza stanza = mappa.getStanza(posizione);
        InventarioPanel inventarioPanel = new InventarioPanel(inventario);

        switch (verbo) {
            case "prendi":
                if (stanza == null || stanza.getOggetto() == null || !stanza.getOggetto().equalsIgnoreCase(oggetto)) {
                    return "> Non c'è questo oggetto da prendere qui.";
                }
                if (oggetto.isEmpty()) return "> Cosa vuoi prendere?";

                StringBuilder messaggio = new StringBuilder(inventario.aggiungi(oggetto));
                inventario.salvaSuFile();
                inventarioPanel.aggiornaInventario();

                String nomeEvento = stanza.getOggetto();
                sceneManager.getSceneEvento(nomeEvento).ifPresent(scena -> messaggio.append("\n").append(scena.mostra()));
                return sceneManager.getSceneEvento(nomeEvento).isPresent() ? messaggio.toString() : "> Hai preso: " + oggetto;

            case "lascia":
                if (oggetto.isEmpty()) return "> Cosa vuoi lasciare?";
                String msg = inventario.rimuovi(oggetto);
                inventario.salvaSuFile();
                inventarioPanel.aggiornaInventario();
                return msg;

            case "vai": case "sposta": case "muoviti":
                return muoviEsploratore(oggetto);

            case "usa": case "sali":
                if (isScala(oggetto)) {
                    if (stanza != null && stanza.getNome().equalsIgnoreCase("Scale")) {
                        spostamentoPiano(2, new Posizione(5, 6));
                        return "Hai usato le scale e sei salito al secondo piano!";
                    }
                    return "Non ci sono scale qui da usare.";
                }
                return "> Hai usato: " + oggetto;

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
        return oggetto.equals("scale") || oggetto.equals("scala") || oggetto.contains("scala");
    }

    private String entraInStanza(Stanza stanza) {
        if (stanza == null) return "";

        // Assicura che la stanza abbia la scena giusta
        if (stanza.getScena() == null) {
            sceneManager.getSceneDefault(stanza.getNome()).ifPresent(stanza::setScena);
        }

        StringBuilder testo = new StringBuilder();

        // Mostra scena collegata alla stanza (se non già mostrata)
        String dialogoStanza = stanza.entra();
        if (dialogoStanza != null) {
            testo.append(dialogoStanza);
        }

        // Se è la "Casa Abbandonata", innesca scene iniziali
        if (stanza.getNome().equalsIgnoreCase("Casa Abbandonata")) {
            storyEngine.resetSceneIniziali();
            storyEngine.prossimaScenaIniziale().ifPresent(scena -> {
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
}
