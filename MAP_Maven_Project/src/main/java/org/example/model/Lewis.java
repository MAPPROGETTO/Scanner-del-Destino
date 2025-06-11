package org.example.model;

import org.example.mappa.*;
import org.example.utils.GestoreInput;
import org.example.utils.Inventario;

public class Lewis extends Personaggio {
    private Mappa mappa;
    private GestoreInput input;
    private Inventario inventario;
    private Posizione posizione;

    public Lewis(Mappa mappa, GestoreInput input, Inventario inventario) {
        super("Lewis", "Un giovane inventore orfano...", false);
        this.mappa = mappa;
        this.input = input;
        this.inventario = inventario;
        this.posizione = new Posizione(2, 2); // centro mappa 5x5
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

        switch (direzione) {
            case "nord":
            case "n":
                nuovaPos.muoviNord();
                break;
            case "sud":
            case "s":
                nuovaPos.muoviSud();
                break;
            case "est":
            case "e":
                nuovaPos.muoviEst();
                break;
            case "ovest":
            case "o":
                nuovaPos.muoviOvest();
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
                return "Hai usato: " + oggetto; // da gestire meglio se vuoi
            default:
                return "Comando sconosciuto.";
        }
    }
    public String visualizzaMappa() {
        return mappa.mostra(posizione);
    }
    public String proseguiStoria() {
        return mappa.mostra(posizione);
    }
}
