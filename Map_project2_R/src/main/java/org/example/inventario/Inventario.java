package org.example.inventario;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vdurmont.emoji.EmojiParser;
import org.example.mappa.Stanza;
import org.example.story.SceneManager;

public class Inventario implements Serializable{
    private final List<String> oggetti = new ArrayList<>();
    private final File file = new File("inventario.txt");
    private static final long serialVersionUID = 1L;

    public Inventario() {
        svuotaFile();
        caricaDaFile();
        aggiungi("torcia"); // Aggiunge la torcia all'inizio
    }

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
            // aggiungi altri alias se necessario
    );

    public static String normalizza(String nomeOggetto)
    {
        return ALIAS.getOrDefault(nomeOggetto.toLowerCase(), nomeOggetto.toLowerCase());
    }

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

    public String usa(String nomeOggetto, Stanza stanza, SceneManager sceneManager) {
        String nomeOggettoTrim = normalizza(nomeOggetto.trim());
        if (nomeOggettoTrim.equalsIgnoreCase("torcia")) {
            if (stanza.isLuceAccesa()) {
                return "> La stanza è già illuminata, non serve usare la torcia.";
            }
            boolean presente = contieneOggetto("torcia");
            if (presente) {
                stanza.accendiLuce();
                StringBuilder messaggio = new StringBuilder("Hai usato: torcia. Ora la stanza è illuminata!");
                sceneManager.getSceneUsoOggetto(stanza.getNome(), "torcia")
                        .ifPresent(scena -> messaggio.append("\n").append(scena.getDescrizione()));
                return messaggio.toString();
            } else {
                return "> Non hai la torcia nell'inventario.";
            }
        }
        // Logica standard per altri oggetti...
        return oggetti.stream()
                .filter(o -> {
                    String nome = o.contains(" ") ? o.substring(0, o.indexOf(" ")) : o;
                    return nome.equalsIgnoreCase(nomeOggettoTrim);
                })
                .findFirst()
                .map(o -> "Hai usato: " + o)
                .orElse("> Non hai questo oggetto nell'inventario.");
    }

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

    // Sovrascrive completamente il file con la lista aggiornata
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

    private void svuotaFile() {
        try (PrintWriter writer = new PrintWriter(file)) {
            // Scrittura vuota = file svuotato
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean tuttiOggettiRaccolti() {
        // Sostituisci i nomi con quelli effettivamente necessari per aprire la stanza segreta
        return oggetti.stream().anyMatch(o -> o.startsWith("chiave_matrimoniale")) &&
                oggetti.stream().anyMatch(o -> o.startsWith("documento")) &&
                oggetti.stream().anyMatch(o -> o.startsWith("elica")) &&
                oggetti.stream().anyMatch(o -> o.startsWith("schermo")) &&
                oggetti.stream().anyMatch(o -> o.startsWith("motore")) &&
                oggetti.stream().anyMatch(o -> o.startsWith("batteria"));
    }

    public List<String> getOggetti() {return oggetti;}

    public boolean contieneOggetto(String nomeOggetto) {
        String nomePulito = nomeOggetto.trim().toLowerCase();
        return oggetti.stream().anyMatch(o -> {
            String nome = o.contains(" ") ? o.substring(0, o.indexOf(" ")) : o;
            return nome.equalsIgnoreCase(nomePulito);
        });
    }

}