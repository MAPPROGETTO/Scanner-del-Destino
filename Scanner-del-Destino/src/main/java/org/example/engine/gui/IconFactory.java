package org.example.engine.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
/**
 * Classe factory per creare icone personalizzate.
 * Le icone sono disegnate dinamicamente usando Java2D.
 *
 *
 * @version 1.0
 */
public class IconFactory implements Serializable {
    /**
     * Crea un'icona rappresentante un inventario (zaino/borsa).
     * @param size
     */
    public static ImageIcon createInventoryIcon(int size) {
        return createIcon(size, (g2d, s) -> {
            // Disegna zaino/borsa
            int padding = s / 8;
            int bagWidth = s - 2 * padding;
            int bagHeight = (int) (bagWidth * 0.8);

            // Corpo della borsa
            g2d.setColor(new Color(220, 220, 220));
            g2d.fillRoundRect(padding, padding + s/6, bagWidth, bagHeight, s/10, s/10);

            // Bordo
            g2d.setColor(new Color(0, 255, 65));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(padding, padding + s/6, bagWidth, bagHeight, s/10, s/10);

            // Chiusura superiore
            g2d.fillRect(padding + s/6, padding, bagWidth - s/3, s/8);
            g2d.drawRect(padding + s/6, padding, bagWidth - s/3, s/8);

            // Dettagli decorativi
            g2d.fillOval(padding + s/4, padding + s/3, s/12, s/12);
            g2d.fillOval(s - padding - s/4, padding + s/3, s/12, s/12);
        });
    }
    /**
     * Crea un'icona rappresentante una mappa.
     * @param size
     */
    public static ImageIcon createMapIcon(int size) {
        return createIcon(size, (g2d, s) -> {
            int padding = s / 8;

            // Mappa base
            g2d.setColor(new Color(220, 220, 220));
            g2d.fillRect(padding, padding, s - 2*padding, s - 2*padding);

            // Bordo
            g2d.setColor(new Color(0, 255, 65));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(padding, padding, s - 2*padding, s - 2*padding);

            // Piega
            g2d.setColor(new Color(180, 180, 180));
            int[] xPoints = {padding + s/4, padding + s/4, padding + s/3};
            int[] yPoints = {padding, padding + s/4, padding};
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(new Color(0, 255, 65));
            g2d.drawPolygon(xPoints, yPoints, 3);

            // Punto di interesse (rosso)
            g2d.setColor(Color.RED);
            g2d.fillOval(padding + s/3, padding + s/3, s/8, s/8);

            // Linee di percorso
            g2d.setColor(new Color(0, 255, 65));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(padding + s/6, padding + s/2, padding + s/2, padding + s/2);
            g2d.drawLine(padding + s/2, padding + s/2, s - padding - s/6, padding + 2*s/3);
        });
    }
    /**
     * Crea un'icona rappresentante un'azione di invio (send).
     * @param size
     */
    public static ImageIcon createSendIcon(int size) {
        return createIcon(size, (g2d, s) -> {
            int padding = s / 6;

            // Freccia
            g2d.setColor(new Color(0, 255, 65));
            g2d.setStroke(new BasicStroke(3));

            // Corpo freccia
            g2d.drawLine(padding, s/2, s - padding - s/4, s/2);

            // Punta freccia
            int[] xPoints = {s - padding - s/4, s - padding, s - padding - s/4};
            int[] yPoints = {s/2 - s/6, s/2, s/2 + s/6};
            g2d.fillPolygon(xPoints, yPoints, 3);

            // Linea di ritorno (stile enter)
            g2d.drawLine(padding, s/2, padding, s - padding);
            g2d.drawLine(padding, s - padding, padding + s/4, s - padding);
        });
    }
    /**
     * Metodo generico per creare un'icona disegnata dinamicamente.
     * @param size
     * @param drawer
     */
    private static ImageIcon createIcon(int size, IconDrawer drawer) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sfondo trasparente
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, size, size);
        g2d.setComposite(AlphaComposite.SrcOver);

        drawer.draw(g2d, size);
        g2d.dispose();

        return new ImageIcon(image);
    }

    @FunctionalInterface
    private interface IconDrawer {
        void draw(Graphics2D g2d, int size);
    }
    /**
     * Crea un'icona rappresentante un'azione di salto (skip).
     * @param size
     */
    public static ImageIcon createSkipIcon(int size) {
        return new ImageIcon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 255, 128));

                int w = getIconWidth();
                int h = getIconHeight();

                // Primo triangolo ">"
                Path2D.Double triangle1 = new Path2D.Double();
                triangle1.moveTo(x + 2, y);
                triangle1.lineTo(x + w / 2 - 2, y + h / 2);
                triangle1.lineTo(x + 2, y + h);
                triangle1.closePath();

                // Secondo triangolo ">"
                Path2D.Double triangle2 = new Path2D.Double();
                triangle2.moveTo(x + w / 2, y);
                triangle2.lineTo(x + w - 2, y + h / 2);
                triangle2.lineTo(x + w / 2, y + h);
                triangle2.closePath();

                g2.fill(triangle1);
                g2.fill(triangle2);
                g2.dispose();
            }
            /**
             * Larghezza dell'icona.
             * @return larghezza in pixel
             */
            @Override
            public int getIconWidth() {
                return size;
            }

            /**
             * Altezza dell'icona.
             * @return altezza in pixel
             */
            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }
}