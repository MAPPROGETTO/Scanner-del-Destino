package org.example.inventario;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.vdurmont.emoji.EmojiParser;
public class Inventario {
    private final List<String> oggetti = new ArrayList<>();
    private final File file = new File("inventario.txt");

    public Inventario() {
        caricaDaFile();
    }

    public String aggiungi(String oggetto) {
        String elemento = switch (oggetto) {
            case "chiave" -> oggetto + " " + EmojiParser.parseToUnicode(":old_key:");
            case "torcia" -> oggetto + " " + EmojiParser.parseToUnicode(":flashlight:");
            case "cacciavite" -> oggetto + " " + EmojiParser.parseToUnicode(":wrench:");
            case "boullone" -> oggetto + " " + EmojiParser.parseToUnicode(":nut_and_bolt:");
            case "martello" -> oggetto + " " + EmojiParser.parseToUnicode(":hammer:");
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
            return "> Hai aggiunto: " + elemento + " all'inventario.";
        }

    }
    public boolean controlOgetto(String oggetto) {
        return oggetti.contains(oggetto);
    }

    public void caricaDaFile() {
        if (!file.exists())
            return;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                oggetti.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void salvaSuFile () {
        List<String> oggettoPresente = new ArrayList<>();

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
}