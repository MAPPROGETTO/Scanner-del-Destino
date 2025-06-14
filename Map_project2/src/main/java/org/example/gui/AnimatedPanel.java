package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimatedPanel extends JPanel {
    private Timer animationTimer;
    private List<MatrixChar> matrixChars;
    private List<FloatingParticle> particles;
    private Random random;

    public AnimatedPanel() {
        setOpaque(true);
        random = new Random();
        initEffects();
        startAnimation();
    }

    private void initEffects() {
        matrixChars = new ArrayList<>();
        particles = new ArrayList<>();

        // Crea caratteri Matrix
        for (int i = 0; i < 15; i++) {
            matrixChars.add(new MatrixChar(
                    random.nextInt(800),
                    random.nextInt(600),
                    random.nextDouble() * 2 + 1
            ));
        }

        // Crea particelle fluttuanti
        for (int i = 0; i < 30; i++) {
            particles.add(new FloatingParticle(
                    random.nextInt(800),
                    random.nextInt(600),
                    random.nextDouble() * 1.5 + 0.5
            ));
        }
    }

    private void startAnimation() {
        animationTimer = new Timer(100, e -> {
            updateEffects();
            repaint();
        });
        animationTimer.start();
    }

    private void updateEffects() {
        // Aggiorna caratteri Matrix
        for (MatrixChar ch : matrixChars) {
            ch.update();
            if (ch.y > getHeight()) {
                ch.y = -50;
                ch.x = random.nextInt(getWidth());
            }
        }

        // Aggiorna particelle
        for (FloatingParticle p : particles) {
            p.update();
            if (p.y < -10) {
                p.y = getHeight() + 10;
                p.x = random.nextInt(getWidth());
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sfondo gradiente
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(26, 26, 26),
                getWidth(), getHeight(), new Color(45, 45, 45)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Disegna caratteri Matrix
        g2d.setFont(new Font("Consolas", Font.PLAIN, 12));
        for (MatrixChar ch : matrixChars) {
            g2d.setColor(new Color(0, 255, 65, ch.alpha));
            g2d.drawString(ch.character, (int) ch.x, (int) ch.y);
        }

        // Disegna particelle
        for (FloatingParticle p : particles) {
            g2d.setColor(new Color(0, 255, 65, p.alpha));
            g2d.fillOval((int) p.x, (int) p.y, 2, 2);

            // Effetto luminoso
            g2d.setColor(new Color(0, 255, 65, p.alpha / 4));
            g2d.fillOval((int) p.x - 1, (int) p.y - 1, 4, 4);
        }

        g2d.dispose();
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    // Classe per caratteri Matrix
    private class MatrixChar {
        double x, y, speed;
        String character;
        int alpha;
        private final String chars = "01アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン";

        MatrixChar(double x, double y, double speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.character = String.valueOf(chars.charAt(random.nextInt(chars.length())));
            this.alpha = random.nextInt(150) + 50;
        }

        void update() {
            y += speed;
            // Cambia carattere occasionalmente
            if (random.nextInt(100) < 5) {
                character = String.valueOf(chars.charAt(random.nextInt(chars.length())));
            }
        }
    }

    // Classe per particelle fluttuanti
    private class FloatingParticle {
        double x, y, speed;
        int alpha;

        FloatingParticle(double x, double y, double speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.alpha = random.nextInt(100) + 50;
        }

        void update() {
            y -= speed;
            x += Math.sin(y * 0.01) * 0.5; // Movimento ondulatorio
        }
    }
}