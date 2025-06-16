package org.example.mappa;

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


    public Stanza(String nome, boolean luceAccesa, boolean chiusa, boolean haPezzo, boolean eventoPericoloso,
                  boolean nord, boolean sud, boolean est, boolean ovest) {
        this.nome = nome;
        this.luceAccesa = luceAccesa;
        this.chiusa = chiusa;
        this.haPezzo = haPezzo;
        this.eventoPericoloso = eventoPericoloso;
        this.nord = nord;
        this.sud = sud;
        this.est = est;
        this.ovest = ovest;
    }

    public String descrizione() {
        StringBuilder sb = new StringBuilder("Ti trovi nella stanza: " + nome + ".\n");
        if (chiusa) sb.append("La stanza è chiusa.\n");
        if (!luceAccesa) sb.append("È buio, non riesci a vedere bene.\n");
        if (haPezzo) sb.append("Sembra esserci un pezzo importante qui.\n");
        if (eventoPericoloso) sb.append("Senti un pericolo imminente...\n");
        return sb.toString();
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
}
