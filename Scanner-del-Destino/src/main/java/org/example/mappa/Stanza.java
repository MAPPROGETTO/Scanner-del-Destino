/**
 * Classe Stanza - rappresenta una stanza della mappa.
 * Ogni stanza può essere chiusa o aperta, contenere oggetti, eventi pericolosi,
 * direzioni accessibili e una scena narrativa associata.
 */
package org.example.mappa;

import org.example.story.Scena;

import java.io.Serializable;
import java.util.List;

/**
 * Rappresenta una stanza della mappa con proprietà come nome, stato della luce,
 * se è chiusa, se contiene oggetti, eventi pericolosi, le direzioni accessibili,
 * la scena associata e gli oggetti presenti.
 * Implementa Serializable per permettere la serializzazione dell'oggetto.
 */
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

    /**
     * Controlla se la stanza è chiusa.
     * @return true se la stanza è chiusa, false altrimenti
     */
    public boolean isChiusa() { return chiusa; }

    /**
     * Controlla se la luce è accesa.
     * @return true se la luce è accesa, false altrimenti
     */
    public boolean isLuceAccesa() { return luceAccesa; }

    /**
     * Controlla se è possibile andare a nord.
     * @return true se è possibile andare in quella direzione, false altrimenti
     */
    public boolean canGoNorth() { return nord; }

    /**
     * Controlla se è possibile andare a sud.
     * @return true se è possibile andare in quella direzione, false altrimenti
     */
    public boolean canGoSouth() { return sud; }

    /**
     * Controlla se è possibile andare a est.
     * @return true se è possibile andare in quella direzione, false altrimenti
     */
    public boolean canGoEast()  { return est; }

    /**
     * Controlla se è possibile andare a ovest.
     * @return true se è possibile andare in quella direzione, false altrimenti
     */
    public boolean canGoWest()  { return ovest; }
    /**
     * Restituisce il nome della stanza.
     * @return nome della stanza
     */
    public String getNome() { return nome; }

    /**
     * Restituisce la scena associata alla stanza.
     * @return scena della stanza
     */
    public Scena getScena() { return scena; }

    /**
     * Imposta la scena associata alla stanza.
     * @param scena
     */
    public void setScena(Scena scena) { this.scena = scena; }

    public String getOggetto() { return oggetto; }

    /**
     * Controlla se l'ingresso corrisponde all'oggetto della stanza o ai suoi alias.
     * @param input
     */
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
    /**
     * Tenta di aprire la porta usando un oggetto specifico.
     * @param oggetto
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

    /**
     * Imposta lo stato di chiusura della stanza.
     * @param chiusa
     */
    public void setChiusa(boolean chiusa) {this.chiusa = chiusa;}

    public void setOggetto(String oggetto) {this.oggetto = oggetto;}
    /**
     * Imposta la direzione est della stanza.
     * @param est
     */
    public void setEst(boolean est) {this.est = est;}

    /**
     * Imposta la direzione ovest della stanza.
     * @param ovest
     */
    public void setOvest(boolean ovest) {this.ovest = ovest;}
    /**
     * Accende la luce della stanza.
     */
    public void accendiLuce() { this.luceAccesa = true; }

    /**
     * Raccoglie il pezzo presente nella stanza.
     */
    public void raccogliPezzo() { this.haPezzo = false; }
}