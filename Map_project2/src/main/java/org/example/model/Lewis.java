package org.example.model;

import org.example.gui.MappaPanel;
import org.example.mappa.*;
import org.example.utils.GestoreInput;
import org.example.utils.GestoreInputGUI;
import org.example.utils.Inventario;

public class Lewis extends Personaggio {
    private Mappa mappa;
    private GestoreInput input;
    private Inventario inventario;
    private MappaPanel mappaPanel;
    private Posizione posizione;
    private GestoreInputGUI inputGUI;
    private int currentFloor = 1;

    public Lewis(Mappa mappa, GestoreInput input, Inventario inventario) {
        super("Lewis", "Un giovane inventore orfano...", false);
        this.mappa = mappa;
        this.input = input;
        this.inventario = inventario;
        this.posizione = new Posizione(6, 5); // centro mappa 5x5
    }

    public Lewis(Mappa mappa, GestoreInputGUI inputGUI, Inventario inventario) {
        super("Lewis", "Un giovane inventore orfano...", false);
        this.mappa = mappa;
        this.inputGUI = inputGUI;
        this.inventario = inventario;
        this.posizione = new Posizione(6, 5); // centro mappa 5x5
    }

    @Override
    public void interagisci() {
        System.out.println("Lewis: Devo ritrovare i pezzi del mio scanner mnemonico...");
    }

    public void esplora() {
        boolean continua = true;

        System.out.println("Benvenuto nell'esplorazione! Scrivi i comandi per giocare. (scrivi 'esci' per terminare)");

        while (continua) {
            mappa.mostra(posizione);
            System.out.print("> ");
            String comando = input.leggiLinea().trim().toLowerCase();

            if (comando.equals("esci") || comando.equals("q")) {
                continua = false;
                System.out.println("Arrivederci!");
                continue;
            }

            interpretaComandoDaGUI(comando);
        }
    }

    private String muoviEsploratore(String direzione) {
        Posizione nuovaPos = new Posizione(posizione.getX(), posizione.getY());
        Stanza stanzaCorrente = mappa.getStanza(posizione);

        System.out.println("Stanza corrente: " + (stanzaCorrente != null ? stanzaCorrente.getNome() : "null"));

        switch (direzione) {
            case "nord":
            case "n":
                if (stanzaCorrente != null && stanzaCorrente.canGoNorth()) {
                    System.out.println("Muovendo " + posizione.getX() + "..."+ posizione.getY());
                    nuovaPos.muoviNord();
                } else {
                    System.out.println("Muovendo " + posizione.getX() + "..."+ posizione.getY());
                    return "Non puoi andare a nord!";
                }
                break;
            case "sud":
            case "s":
                if (stanzaCorrente != null && stanzaCorrente.canGoSouth()) {
                    nuovaPos.muoviSud();
                } else {
                    return "Non puoi andare a sud!";
                }
                break;
            case "est":
            case "e":
                if (stanzaCorrente != null && stanzaCorrente.canGoEast()) {
                    nuovaPos.muoviEst();
                } else {
                    return "Non puoi andare a est!";
                }
                break;
            case "ovest":
            case "o":
                if (stanzaCorrente != null && stanzaCorrente.canGoWest()) {
                    nuovaPos.muoviOvest();
                } else {
                    return "Non puoi andare a ovest!";
                }
                break;
            default:
                return "Direzione non valida: " + direzione;
        }

        if (mappa.isValida(nuovaPos)) {
            posizione = nuovaPos;
            return "Sei andato a " + direzione;
        } else {
            return "Non puoi andare in quella direzione!";
        }
    }

    public String interpretaComandoDaGUI(String comando) {
        String[] parole = comando.trim().toLowerCase().split("\\s+");
        if (parole.length == 0) return "Comando non valido.";

        String verbo = parole[0];
        String oggetto = parole.length > 1 ? comando.substring(verbo.length()).trim() : "";

        switch (verbo) {
            case "prendi":
                if (!oggetto.isEmpty()) {
                    inventario.aggiungi(oggetto);
                    return "Hai preso: " + oggetto;
                }
                return "Prendi cosa?";
            case "vai":
                return muoviEsploratore(oggetto); // già presente in Lewis
            case "usa":
                if (oggetto.equals("scale")) {
                    Stanza stanzaCorrente = mappa.getStanza(posizione);
                    if (stanzaCorrente != null && stanzaCorrente.getNome().equalsIgnoreCase("Scale")) {
                        // Esempio: sali al secondo piano (aggiorna coordinate e piano secondo la tua mappa)
                        spostamentoPiano(2, new Posizione(5, 6));
                        return "Hai usato le scale e sei salito al secondo piano!";
                    } else {
                        return "Non ci sono scale qui da usare.";
                    }
                }
                return "Hai usato: " + oggetto;
            case "scendi":
                if (oggetto.equals("scale")) {
                    Stanza stanzaCorrente = mappa.getStanza(posizione);
                    if (stanzaCorrente != null && stanzaCorrente.getNome().equalsIgnoreCase("Scale")) {
                        // Esempio: scendi al piano inferiore (aggiorna coordinate e piano secondo la tua mappa)
                        spostamentoPiano(1, new Posizione(6, 5));
                        return "Sei sceso al piano inferiore!";
                    } else {
                        return "Non ci sono scale qui da usare.";
                    }
                }
                return "Hai usato: " + oggetto;
            default:
                return "Comando sconosciuto.";
        }
    }
    public String visualizzaMappa() {
        return mappa.mostra(posizione);
    }

    public Mappa getMappa() {
        return mappa;
    }
    public int getPosizioneX() {
        return posizione.getX();
    }
    public int getPosizioneY() {return posizione.getY();}
    public Posizione getPosizione() {return posizione;}
    public void setMappaPanel(MappaPanel mappaPanel) {
        this.mappaPanel = mappaPanel;
    }
    public int getCurrentFloor() {
        return currentFloor;
    }

    public void spostamentoPiano(int nuovoPiano, Posizione nuovaPosizione) {
        this.currentFloor = nuovoPiano;
        this.posizione = nuovaPosizione;
        if (mappaPanel != null) {
            mappaPanel.cambiaPiano(nuovoPiano, nuovaPosizione.getX(), nuovaPosizione.getY());
        }
    }
}
