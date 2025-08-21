package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TitoliDiCodaPanel extends JPanel {
    private final List<String> titoli;
    private int yOffset;
    private Timer timer;

    public TitoliDiCodaPanel(List<String> titoli) {
        this.titoli = titoli;
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setFont(new Font("Monospaced", Font.PLAIN, 20));
    }

    public void startAnimation(Runnable onComplete) {
        yOffset = getHeight(); // parte da sotto la finestra
        int delay = 30;        // ms per frame
        int step = 2;          // velocità scroll
        timer = new Timer(delay, e -> {
            yOffset -= step;
            repaint();
            // quando tutto il testo è uscito dallo schermo
            int textHeight = titoli.size() * getFontMetrics(getFont()).getHeight();
            if (yOffset + textHeight < 0) {
                timer.stop();
                if (onComplete != null) onComplete.run();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getForeground());
        g.setFont(getFont());

        int lineHeight = g.getFontMetrics().getHeight();
        int y = yOffset;
        for (String riga : titoli) {
            int x = (getWidth() - g.getFontMetrics().stringWidth(riga)) / 2;
            g.drawString(riga, x, y);
            y += lineHeight + 5;
        }
    }
}
