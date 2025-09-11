package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Pannello per visualizzare i titoli di coda con animazione di scorrimento.
 * I titoli scorrono dal basso verso l'alto.
 * L'animazione termina quando tutti i titoli sono usciti dallo schermo.
 * È possibile passare una callback che viene eseguita al termine dell'animazione.
 *
 */
public class TitoliDiCodaPanel extends JPanel {
    private final List<String> titoli;
    private int yOffset;
    private Timer timer;

    /**
     * Costruttore del pannello dei titoli di coda.
     * @param titoli
     */
    public TitoliDiCodaPanel(List<String> titoli) {
        this.titoli = titoli;
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setFont(new Font("Monospaced", Font.PLAIN, 20));
    }

    /**
     * Avvia l'animazione di scorrimento dei titoli.
     * @param onComplete
     */
    public void startAnimation(Runnable onComplete) {
        yOffset = getHeight(); // parte da sotto la finestra
        int delay = 30;        // ms per frame
        int step = 4;          // velocità scroll
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

    /**
     *
     *  Override del metodo di disegno per visualizzare i titoli.
     * @param g Graphics
     */
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
