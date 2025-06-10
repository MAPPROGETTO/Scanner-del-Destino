package org.example.gui;

import javax.swing.*;
import java.awt.*;

public class MappaPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.GREEN);
        g.fillRect(20, 20, 100, 100);
        g.setColor(Color.BLACK);
        g.drawString("Mappa", 60, 80);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 0); // larghezza fissa
    }
}
