package org.example.inventario;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vdurmont.emoji.EmojiParser;
import org.example.mappa.Stanza;
import org.example.story.SceneManager;

/**
 * Classe che gestisce l'inventario del giocatore.
 * Permette di aggiungere, rimuovere e usare oggetti,
 * e di salvare/caricare l'inventario da un file di testo.
 * Gli oggetti sono rappresentati come stringhe.
 * Implementa Serializable per permettere la serializzazione.
 */
public class Inventario implements Serializable{
    private final List<String> oggetti = new ArrayList<>();
    private final File file = new File("inventario.txt");
    private static final long serialVersionUID = 1L;

    /**
     * Costruttore che carica l'inventario da file se esiste.
     * Se il file non esiste, l'inventario parte vuoto.
     * Inizialmente contiene la torcia.
     */
    public Inventario() {
        caricaDaFile();
    }

    /**
     *  Mappa di alias per normalizzare i nomi degli oggetti.
     *  Permette di riconoscere vari nomi per lo stesso oggetto.
     */
    private static final Map<String, String> ALIAS = Map.of(
            "chiave vecchia", "chiave_matrimoniale",
            "chiave_vecchia", "chiave_matrimoniale",
            "chiave", "chiave_matrimoniale",
            "DOCUMENTO", "documento",
            "foglio", "documento",
            "vecchio motore", "motore",
            "VECCHIO_MOTORE", "motore",
            "vecchio_motore", "motore",
            "VECCHIO MOTORE", "motore",
            "vecchio MOTORE", "motore"
    );

    /**
     * Normalizza il nome dell'oggetto usando la mappa di alias.
     * Se il nome non è presente nella mappa, ritorna il nome in minuscolo.
     * Usa toLowerCase per rendere la ricerca case-insensitive.
     * @param nomeOggetto
     */
    public static String normalizza(String nomeOggetto)
    {
        return ALIAS.getOrDefault(nomeOggetto.toLowerCase(), nomeOggetto.toLowerCase());
    }

    /**
     * Svuota completamente l'inventario e aggiorna il file di conseguenza.
     * Usato per inizializzare una nuova partita.
     */
    public void svuota() {
        oggetti.clear();
        salvaInventarioCompleto(); // aggiorna il file coerentemente
    }

    /**
     * Inizializza una nuova partita, svuotando l'inventario e aggiungendo la torcia.
     * Aggiorna il file di conseguenza.
     * Usato all'inizio di una nuova partita.
     * Aggiorna il file di conseguenza.
     */
    public void inizializzaNuovaPartita() {
        oggetti.clear();
        salvaInventarioCompleto();
        aggiungi("torcia");
    }

    /**
     * Aggiunge un oggetto all'inventario se non è già presente.
     *  Ritorna un messaggio di conferma o di errore.
     * @param oggetto
     */
    public String aggiungi(String oggetto) {
        String nomeNormalizzato = normalizza(oggetto);

        String elemento = switch (nomeNormalizzato) {
            case "chiave_matrimoniale" -> nomeNormalizzato + " " + EmojiParser.parseToUnicode(":old_key:");
            case "documento" -> nomeNormalizzato + " " + EmojiParser.parseToUnicode(":page_facing_up:");
            case "elica" -> nomeNormalizzato + " " + EmojiParser.parseToUnicode(":cyclone:");
            case "schermo" -> nomeNormalizzato + " " + EmojiParser.parseToUnicode(":desktop_computer:");
            case "motore" -> nomeNormalizzato + " " + EmojiParser.parseToUnicode(":gear:");
            case "batteria" -> nomeNormalizzato + " " + EmojiParser.parseToUnicode(":battery:");
            case "torcia" -> nomeNormalizzato + " " + EmojiParser.parseToUnicode(":flashlight:");
            default -> null;
        };

        if (elemento == null) {
            return "> Oggetto non riconosciuto.";
        }

        if (oggetti.contains(elemento)) {
            return "> L'oggetto \"" + oggetto + "\" è già presente nell'inventario.";
        }
        else {
            oggetti.add(elemento);
            salvaInventarioCompleto(); // Salva l'inventario aggiornato
            return "> Hai aggiunto: " + elemento + " all'inventario.";
        }

    }

    /**
     * Usa un oggetto dell'inventario in una stanza specifica.
     * Gestisce l'uso della torcia per illuminare la stanza
     * e l'uso della chiave per aprire porte (supporto).
     * @param nomeOggetto
     * @param stanza
     * @param sceneManager
     */
    public String usa(String nomeOggetto, Stanza stanza, SceneManager sceneManager) {
        String nomeOggettoTrim = normalizza(nomeOggetto.trim());

        // --- TORCIA: illumina la stanza se ce l'hai ---
        if (nomeOggettoTrim.equals("torcia")) {
            if (stanza.isLuceAccesa()) {
                return "> La stanza è già illuminata, non serve usare la torcia.";
            }
            if (!contieneOggetto("torcia")) {
                return "> Non hai la torcia nell'inventario.";
            }
            stanza.accendiLuce();
            Optional<String> usoDb = org.example.story.ScenesDb.usoOggetto(stanza.getNome(), "torcia");
            if (usoDb.isPresent()) {
                return usoDb.get();
            }

            StringBuilder msg = new StringBuilder("Hai usato: torcia. Ora la stanza è illuminata!");
            sceneManager.getSceneUsoOggetto(stanza.getNome(), "torcia")
                    .ifPresent(scena -> msg.append("\n").append(scena.getDescrizione()));
            return msg.toString();
        }

        if (nomeOggettoTrim.equals("chiave_matrimoniale")) {
            if (!contieneOggetto("chiave_matrimoniale")) {
                return "> Non hai la chiave_matrimoniale nell'inventario.";
            }
            return "> Puoi usare la chiave su una porta chiusa.\n" +
                    "  Suggerimento: prova prima a muoverti verso la porta (es. 'nord'),\n" +
                    "  poi digita 'usa chiave_matrimoniale' per aprirla.";
        }

        // --- TUTTI GLI ALTRI OGGETTI: non si usano direttamente ---
        return "> Questo oggetto non può essere usato direttamente.";
    }


    /**
     * Rimuove un oggetto dall'inventario se presente.
     * Aggiorna il file di conseguenza.
     * @param oggetto
     * @return
     */
    public String rimuovi(String oggetto) {
        String normalizzato = normalizza(oggetto);
        boolean rimosso = oggetti.removeIf(o -> {
            String nome = o.contains(" ") ? o.substring(0, o.indexOf(" ")) : o;
            return nome.equalsIgnoreCase(normalizzato);
        });
        if (rimosso) {
            salvaInventarioCompleto();
            return "> Hai rimosso: " + oggetto + " dall'inventario.";
        } else {
            return "> L'oggetto \"" + oggetto + "\" non è presente nell'inventario.";
        }
    }

    /**
     * Salva l'inventario completo sul file, sovrascrivendo il contenuto precedente.
     * Usato per operazioni di svuotamento o inizializzazione.
     * Aggiorna il file coerentemente.
     * Usa UTF-8 per supportare caratteri speciali (emoji).
     * @exception IOException in caso di errori di scrittura.
     */
    private void salvaInventarioCompleto() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"))) {
            for (String o : oggetti) {
                writer.write(o);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica l'inventario da file se esiste.
     * Usa UTF-8 per supportare caratteri speciali (emoji).
     * Se il file non esiste, l'inventario rimane vuoto.
     * @exception IOException in caso di errori di lettura.
     */
    public void caricaDaFile() {
        if (!file.exists())
            return;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            oggetti.clear(); // Pulisce l'inventario prima di caricare
            while ((line = reader.readLine()) != null) {
                // Rimuove spazi bianchi all'inizio e alla fine della riga
                oggetti.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Aggiunge gli oggetti attuali al file senza duplicati.
     * @exception IOException in caso di errori di scrittura.
     * Usato per aggiungere nuovi oggetti senza sovrascrivere quelli esistenti.
     * Aggiorna il file coerentemente.
     */
    public void salvaSuFile () {
        List<String> oggettoPresente = new ArrayList<>();

        // Carica gli oggetti già presenti nel file per evitare duplicati
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    oggettoPresente.add(line.trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Aggiunge solo gli oggetti che non sono già presenti nel file
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"))) {
            for (String o : oggetti) {
                if (!oggettoPresente.contains(o)) {
                    writer.write(o);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Controlla se tutti gli oggetti necessari per aprire la stanza segreta sono stati raccolti.
     *  Sostituisci i nomi con quelli effettivamente necessari per aprire la stanza segreta.
     * @return
     */
    public boolean tuttiOggettiRaccolti() {
        // Sostituisci i nomi con quelli effettivamente necessari per aprire la stanza segreta
        return oggetti.stream().anyMatch(o -> o.startsWith("chiave_matrimoniale")) &&
                oggetti.stream().anyMatch(o -> o.startsWith("documento")) &&
                oggetti.stream().anyMatch(o -> o.startsWith("elica")) &&
                oggetti.stream().anyMatch(o -> o.startsWith("schermo")) &&
                oggetti.stream().anyMatch(o -> o.startsWith("motore")) &&
                oggetti.stream().anyMatch(o -> o.startsWith("batteria"));
    }

    /**
     * Ritorna la lista completa degli oggetti nell'inventario.
     */
    public List<String> getOggetti() {return oggetti;}

    /**
     * Controlla se un oggetto è presente nell'inventario.
     * Usa il nome normalizzato per la ricerca.
     *
     * @param nomeOggetto
     */
    public boolean contieneOggetto(String nomeOggetto) {
        String nomePulito = nomeOggetto.trim().toLowerCase();
        return oggetti.stream().anyMatch(o -> {
            String nome = o.contains(" ") ? o.substring(0, o.indexOf(" ")) : o;
            return nome.equalsIgnoreCase(nomePulito);
        });
    }

}