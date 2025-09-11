package org.example.gui;

import org.example.inventario.Inventario;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;
/**
 * Pannello per visualizzare l'inventario del giocatore.
 */
public class InventarioPanel extends JPanel implements Serializable{
    private JTextArea areaInventario;
    private Inventario inventario;
    /**
     * Costruttore del pannello inventario.
     * @param inventario
     */
    public InventarioPanel(Inventario inventario) {
        this.inventario = inventario;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 400));
        setBackground(new Color(30, 30, 30, 200));

        areaInventario = new JTextArea();
        areaInventario.setEditable(false);
        areaInventario.setLineWrap(true);
        areaInventario.setWrapStyleWord(true);
        areaInventario.setBackground(new Color(0, 0, 0, 200));
        areaInventario.setForeground(Color.GREEN);
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 15);
        areaInventario.setFont(emojiFont);

        //areaInventario.setFont(new Font("Consolas", Font.PLAIN, 14));
        areaInventario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 65, 100), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(areaInventario);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        aggiornaInventario();
        inventario.salvaSuFile();

    }
    private final File file = new File("inventario.txt");
    /**
     * Aggiorna l'area di testo con gli oggetti attuali nell'inventario.
     * Legge gli oggetti da un file di testo.
     * @throws IOException
     */
    public void aggiornaInventario() {
        if (!file.exists())
            return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            areaInventario.setText(" Nel inventario hai : \n\n"); // Pulisce l'area prima di aggiornare
            while ((line = reader.readLine()) != null) {
                areaInventario.append(line.trim() + "\n\n");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
