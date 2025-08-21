package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class AnimatedPanel extends JPanel implements Serializable {
    private Timer animationTimer;
    private transient List<MatrixStyleUtils.MatrixChar> matrixChars;
    private transient List<MatrixStyleUtils.FloatingParticle> particles;

    public AnimatedPanel() {
        setOpaque(true);
        initEffects();
        startAnimation();
    }

    private void initEffects() {
        // Utilizza i metodi della classe utility per creare gli effetti
        matrixChars = MatrixStyleUtils.createMatrixChars(15, 800, 600);
        particles = MatrixStyleUtils.createFloatingParticles(30, 800, 600);
    }

    private void startAnimation() {
        animationTimer = new Timer(100, e -> {
            updateEffects();
            repaint();
        });
        animationTimer.start();
    }

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

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}