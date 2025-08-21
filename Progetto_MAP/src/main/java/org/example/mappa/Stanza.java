/**
 * Classe Stanza - rappresenta una stanza della mappa.
 * Ogni stanza può essere chiusa o aperta, contenere oggetti, eventi pericolosi,
 * direzioni accessibili e una scena narrativa associata.
 */
package org.example.mappa;

import org.example.story.Scena;

import java.io.Serializable;
import java.util.List;

public class Stanza implements Serializable{
    private final String nome;
    private boolean luceAccesa;
    private boolean chiusa;
    private boolean haPezzo;
    private boolean eventoPericoloso;
    private static final long serialVersionUID = 1L;

    // Direzioni disponibili
    private boolean nord;
    private boolean sud;
    private boolean est;
    private boolean ovest;

    private Scena scena; // RIMOSSO 'final' per permettere l'assegnazione
    private boolean dialogoMostrato = false;
    private String oggetto; // opzionale, se vuoi gestire oggetti
    private List<String> aliasOggetto;

    public Stanza(String nome, boolean luceAccesa, boolean chiusa, boolean haPezzo, boolean eventoPericoloso,
                  boolean nord, boolean sud, boolean est, boolean ovest, Scena scena, String oggetto, List<String> aliasOggetto) {
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
        this.aliasOggetto = aliasOggetto;
    }

    // Getter/Setter

    public boolean isChiusa() { return chiusa; }
    public boolean isLuceAccesa() { return luceAccesa; }
    public boolean hasPezzo() { return haPezzo; }
    public boolean isEventoPericoloso() { return eventoPericoloso; }
    public boolean provaAdAprire() {return !chiusa;}

    public boolean canGoNorth() { return nord; }
    public boolean canGoSouth() { return sud; }
    public boolean canGoEast()  { return est; }
    public boolean canGoWest()  { return ovest; }

    public String getNome() { return nome; }
    public Scena getScena() { return scena; }
    public void setScena(Scena scena) { this.scena = scena; }
    public int getIndice() { return scena != null ? scena.getIndice() : -1; }
    public String getOggetto() { return oggetto; }
    public List<String> getAliasOggetto() { return aliasOggetto; }

    public boolean oggettoMatches(String input) {
        if (oggetto == null) return false;
        if (oggetto.equalsIgnoreCase(input)) return true;
        if (aliasOggetto == null) return false;
        return aliasOggetto.stream().anyMatch(a -> a.equalsIgnoreCase(input));
    }

    /**
     * Tenta di aprire la stanza usando un oggetto specifico.
     * @param oggetto oggetto da usare come chiave
     * @return true se la stanza si apre, false altrimenti
     */
    public boolean apriCon(String oggetto) {
        if (!chiusa) return false;
        if (aliasOggetto == null || aliasOggetto.isEmpty()) return false;

        String oggettoPulito = oggetto.replaceAll("[^a-zA-Z0-9_ ]", "").trim().toLowerCase();
        for (String chiave : aliasOggetto) {
            String chiavePulita = chiave.replaceAll("[^a-zA-Z0-9_ ]", "").trim().toLowerCase();
            if (oggettoPulito.equals(chiavePulita)) {
                chiusa = false;
                System.out.println("Porta aperta con: " + chiave);
                return true;
            }
        }
        System.out.println("'" + oggetto + "' non apre la porta.");
        return false;
    }

    public void setChiusa(boolean chiusa) {this.chiusa = chiusa;}
    public void setOggetto(String oggetto) {this.oggetto = oggetto;}
    public void setSud(boolean sud) {this.sud = sud;}
    public void setEst(boolean est) {this.est = est;}
    public void setOvest(boolean ovest) {this.ovest = ovest;}
    public void setNord(boolean nord) {this.nord = nord;}
    public void apri() { this.chiusa = false; }
    public void accendiLuce() { this.luceAccesa = true; }
    public void raccogliPezzo() { this.haPezzo = false; }

    public void resetDialogo() { this.dialogoMostrato = false; } // opzionale, se vuoi rivisualizzare
}