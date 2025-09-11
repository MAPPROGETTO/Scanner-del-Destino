package org.example.engine.gui;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.Random;

/** * Pannello animato con effetti in stile Matrix.
 * Utilizza la classe MatrixStyleUtils per creare e gestire gli effetti visivi.
 * @version 1.0
 */
public class AnimatedPanel extends JPanel implements Serializable {
    private Timer animationTimer;
    private transient List<MatrixStyleUtils.MatrixChar> matrixChars;
    private transient List<MatrixStyleUtils.FloatingParticle> particles;

    /** * Costruttore che inizializza il pannello e avvia l'animazione.
     * Imposta il pannello come opaco e configura gli effetti visivi.
     */
    public AnimatedPanel() {
        setOpaque(true);
        initEffects();
        startAnimation();
    }
    /** * Inizializza gli effetti visivi utilizzando la classe MatrixStyleUtils.
     * Crea caratteri in stile Matrix e particelle fluttuanti.
     */
    private void initEffects() {
        // Utilizza i metodi della classe utility per creare gli effetti
        matrixChars = MatrixStyleUtils.createMatrixChars(15, 800, 600);
        particles = MatrixStyleUtils.createFloatingParticles(30, 800, 600);
    }
    /** * Avvia il timer per l'animazione, aggiornando gli effetti e ridisegnando il pannello.
     * Il timer esegue un'azione ogni 100 millisecondi.
     */
    private void startAnimation() {
        animationTimer = new Timer(100, e -> {
            updateEffects();
            repaint();
        });
        animationTimer.start();
    }
    /** * Aggiorna gli effetti visivi, inclusi i caratteri Matrix e le particelle fluttuanti.
     * Implementa un movimento ondulatorio per le particelle.
     */
    private void updateEffects() {
        if (getWidth() <= 0 || getHeight() <= 0) return;
        // Crea un'istanza locale di Random
        Random random = new Random();

        // Aggiorna i caratteri Matrix
        MatrixStyleUtils.updateMatrixChars(matrixChars, getWidth(), getHeight());

        // Aggiorna particelle con movimento ondulatorio
        for (MatrixStyleUtils.FloatingParticle p : particles) {
            p.y -= p.speed;
            p.x += Math.sin(p.y * 0.01) * 0.5; // Movimento ondulatorio
            if (p.y < -10) {
                p.y = getHeight() + 10;
                p.x = random.nextInt(getWidth()); // usa random locale
            }
        }
    }

    /** * Sovrascrive il metodo paintComponent per disegnare gli effetti visivi.
     * Applica uno sfondo gradiente, disegna i caratteri Matrix e le particelle con effetto luminoso.
     * @param g Il contesto grafico utilizzato per il disegno.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Applica lo sfondo gradiente usando la utility
        MatrixStyleUtils.applyMatrixGradient(g2d, getWidth(), getHeight());

        // Disegna caratteri Matrix usando la utility
        MatrixStyleUtils.drawMatrixChars(g2d, matrixChars);

        // Disegna particelle con effetto luminoso
        for (MatrixStyleUtils.FloatingParticle p : particles) {
            g2d.setColor(p.color);
            g2d.fillOval((int) p.x, (int) p.y, 2, 2);

            // Effetto luminoso
            g2d.setColor(new Color(0, 255, 65, ((Color)p.color).getAlpha() / 4));
            g2d.fillOval((int) p.x - 1, (int) p.y - 1, 4, 4);
        }

        g2d.dispose();
    }
}