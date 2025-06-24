package org.example.inventario;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.vdurmont.emoji.EmojiParser;
import org.example.mappa.Stanza;
import org.example.story.SceneLoader;
import org.example.story.SceneManager;

public class Inventario {
    private final List<String> oggetti = new ArrayList<>();
    private final File file = new File("inventario.txt");

    public Inventario() {
        svuotaFile();
        caricaDaFile();
        aggiungi("torcia"); // Aggiunge la torcia all'inizio
    }

    public String aggiungi(String oggetto) {
        String elemento = switch (oggetto) {
            case "chiave_matrimoniale" -> oggetto + " " + EmojiParser.parseToUnicode(":old_key:");
            case "chiave_segreta" -> oggetto + " " + EmojiParser.parseToUnicode(":key:");
            case "elica" -> oggetto + " " + EmojiParser.parseToUnicode(":cyclone:");
            case "schermo" -> oggetto + " " + EmojiParser.parseToUnicode(":desktop_computer:");
            case "motore" -> oggetto + " " + EmojiParser.parseToUnicode(":gear:");
            case "batteria" -> oggetto + " " + EmojiParser.parseToUnicode(":battery:");
            case "torcia" -> oggetto + " " + EmojiParser.parseToUnicode(":flashlight:");
            default -> null;
        };

        if (elemento == null) {
            System.out.println("Oggetto non riconosciuto.");
            return elemento;
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

    // Metodo per usare un oggetto dall'inventario anche se ha un'emoji
    /*public String usa(String nomeOggetto, Stanza stanza) {
        String nomeOggettoTrim = nomeOggetto.trim();
        if (nomeOggettoTrim.equalsIgnoreCase("torcia")) {
            if (stanza.isLuceAccesa()) {
                return "> La stanza è già illuminata, non serve usare la torcia.";
            }
            boolean presente = oggetti.stream().anyMatch(o -> {
                String nome = o.contains(" ") ? o.substring(0, o.indexOf(" ")) : o;
                return nome.equalsIgnoreCase("torcia");
            });
            if (presente) {
                stanza.accendiLuce();
                return "Hai usato: torcia. Ora la stanza è illuminata!";
            } else {
                return "> Non hai la torcia nell'inventario.";
            }
        }
        // Logica standard per altri oggetti
        return oggetti.stream()
                .filter(o -> {
                    String nome = o.contains(" ") ? o.substring(0, o.indexOf(" ")) : o;
                    return nome.equalsIgnoreCase(nomeOggettoTrim);
                })
                .findFirst()
                .map(o -> "Hai usato: " + o)
                .orElse("> Non hai questo oggetto nell'inventario.");
    }
*/

    public String usa(String nomeOggetto, Stanza stanza, SceneManager sceneManager) {
        String nomeOggettoTrim = nomeOggetto.trim();
        if (nomeOggettoTrim.equalsIgnoreCase("torcia")) {
            if (stanza.isLuceAccesa()) {
                return "> La stanza è già illuminata, non serve usare la torcia.";
            }
            boolean presente = oggetti.stream().anyMatch(o -> {
                String nome = o.contains(" ") ? o.substring(0, o.indexOf(" ")) : o;
                return nome.equalsIgnoreCase("torcia");
            });
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
        boolean rimosso = oggetti.removeIf(o -> {
            String nome = o.contains(" ") ? o.substring(0, o.indexOf(" ")) : o;
            return nome.equalsIgnoreCase(oggetto.trim());
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

    public List<String> getOggetti() {return oggetti;}
}