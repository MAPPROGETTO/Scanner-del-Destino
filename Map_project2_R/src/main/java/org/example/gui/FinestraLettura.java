package org.example.gui;

import javax.swing.*;
import java.awt.*;

public class FinestraLettura extends JDialog {

    public FinestraLettura(Window parent, String titolo, String testo) {
        super(parent, titolo, ModalityType.MODELESS);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(parent);

        JTextArea area = new JTextArea(testo);
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setLineWrap(false);
        area.setWrapStyleWord(false);

        // (Opzionale) stile Matrix, se hai MatrixStyleUtils:
        try {
            area.setForeground(MatrixStyleUtils.MATRIX_GREEN);
            area.setBackground(new Color(18, 18, 18));
        } catch (Throwable ignore) {}

        JScrollPane scroll = new JScrollPane(area);
        setContentPane(scroll);
    }
}
