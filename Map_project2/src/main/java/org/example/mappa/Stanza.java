package org.example.mappa;

import org.example.story.Scena;

public class Stanza {
    private final String nome;
    private boolean luceAccesa;
    private boolean chiusa;
    private boolean haPezzo;
    private boolean eventoPericoloso;

    // Direzioni disponibili
    private final boolean nord;
    private final boolean sud;
    private final boolean est;
    private final boolean ovest;

    private Scena scena; // RIMOSSO 'final' per permettere l'assegnazione
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
        this.oggetto = oggetto;
    }

    // Getter/Setter

    public boolean isChiusa() { return chiusa; }
    public boolean isLuceAccesa() { return luceAccesa; }
    public boolean hasPezzo() { return haPezzo; }
    public boolean isEventoPericoloso() { return eventoPericoloso; }

    public boolean canGoNorth() { return nord; }
    public boolean canGoSouth() { return sud; }
    public boolean canGoEast()  { return est; }
    public boolean canGoWest()  { return ovest; }

    public String getNome() { return nome; }
    public Scena getScena() { return scena; }
    public void setScena(Scena scena) { this.scena = scena; }
    public int getIndice() { return scena != null ? scena.getIndice() : -1; }
    public String getOggetto() { return oggetto; }


    public void setOggetto(String oggetto) {this.oggetto = oggetto;}
    public void apri() { this.chiusa = false; }
    public void accendiLuce() { this.luceAccesa = true; }
    public void raccogliPezzo() { this.haPezzo = false; }

    public void resetDialogo() { this.dialogoMostrato = false; } // opzionale, se vuoi rivisualizzare
}
