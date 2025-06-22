package org.example.mappa;

import org.example.inventario.Inventario;
import org.example.story.Scena;

public class Stanza {
    private final String nome;
    private boolean luceAccesa;
    private boolean chiusa;
    private boolean haPezzo;
    private boolean eventoPericoloso;

    // Direzioni disponibili
    private boolean nord;
    private boolean sud;
    private boolean est;
    private boolean ovest;
    private final Scena scena;
    private boolean dialogoMostrato = false;
    private String oggetto; // opzionale, se vuoi gestire oggetti


    public Stanza(String nome, boolean luceAccesa, boolean chiusa, boolean haPezzo, boolean eventoPericoloso,
                  boolean nord, boolean sud, boolean est, boolean ovest, Scena scena, String oggetto) {
        this.nome = nome;
        this.luceAccesa = luceAccesa;
        this.chiusa = chiusa;
        this.haPezzo = haPezzo;
        this.eventoPericoloso = eventoPericoloso;
        this.nord = nord;
        this.sud = sud;
        this.est = est;
        this.ovest = ovest;
        this.scena = scena;
        this.oggetto = oggetto; // può essere null se non c'è un oggetto
    }

    public String descrizione() {
        StringBuilder sb = new StringBuilder("Ti trovi nella stanza: " + nome + ".\n");
        if (chiusa) sb.append("La stanza è chiusa.\n");
        if (!luceAccesa) sb.append("È buio, non riesci a vedere bene.\n");
        if (haPezzo) sb.append("Sembra esserci un pezzo importante qui.\n");
        if (eventoPericoloso) sb.append("Senti un pericolo imminente...\n");
        return sb.toString();
    }

    public String entra() {
        if (!dialogoMostrato) {
            dialogoMostrato = true;
            return scena.mostra();
        }
        return null;
    }

    public String prendiOggetto(Inventario inventario) {
        if (oggetto != null) {
            String messaggio = inventario.aggiungi(oggetto);
            oggetto = null; // Oggetto preso solo la prima volta
            return messaggio;
        }
        return null;
    }

    public boolean isChiusa() { return chiusa; }
    public boolean isLuceAccesa() { return luceAccesa; }
    public boolean hasPezzo() { return haPezzo; }
    public boolean isEventoPericoloso() { return eventoPericoloso; }

    public void apri() { this.chiusa = false; }
    public void accendiLuce() { this.luceAccesa = true; }
    public void raccogliPezzo() { this.haPezzo = false; }

    // Esempio: nella classe Stanza
    public boolean canGoNorth() { return nord; }
    public boolean canGoSouth() { return sud; }
    public boolean canGoEast()  { return est; }
    public boolean canGoWest()  { return ovest; }
    public String getNome() {return nome;}
    public Scena getScena() {return scena;}
    public int getIndice() {return scena != null ? scena.getIndice() : -1;}
    public String getOggetto() {return oggetto;}
}
