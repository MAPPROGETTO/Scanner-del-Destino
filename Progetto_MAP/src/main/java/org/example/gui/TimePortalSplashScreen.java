package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TimePortalSplashScreen extends JWindow {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int CENTER_X = WIDTH / 2;
    private static final int CENTER_Y = HEIGHT / 2;
    private Timer animationTimer;
    private Timer closeTimer;
    private float portalRotation = 0f;
    private float portalPulse = 0f;
    private float tunnelDepth = 0f;
    private int fadeAlpha = 255;
    private boolean isClosing = false;

    private List<Particle> particles;
    private Random random;
    private String[] timeMessages = {
            "Inizializzazione portale temporale...",
            "Calibrazione flusso spazio-tempo...",
            "Sincronizzazione dimensionale...",
            "Apertura varco temporale...",
            "Viaggiando attraverso il tempo...",
            "Arrivo completato!"
    };
    private int currentMessageIndex = 0;
    private int messageTimer = 0;

    private Runnable afterCloseCallback;

    public TimePortalSplashScreen(Runnable afterSplash) {
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setBackground(new Color(0,0,0,0));
        this.afterCloseCallback = afterSplash;

        PortalPanel portalPanel = new PortalPanel();
        setContentPane(portalPanel);

        initializeParticles();
        startAnimation();
        setVisible(true);
    }

    private void initializeParticles() {
        particles = new ArrayList<>();
        random = new Random();
        for (int i = 0; i < 150; i++) {
            particles.add(new Particle());
        }
    }

    private void startAnimation() {
        animationTimer = new Timer(50, e -> {
            updateAnimation();
            repaint();
        });
        animationTimer.start();

        closeTimer = new Timer(8000, e -> isClosing = true);
        closeTimer.setRepeats(false);
        closeTimer.start();
    }

    private void updateAnimation() {
        portalRotation += 0.1f;
        portalPulse += 0.15f;
        tunnelDepth += 0.05f;

        messageTimer++;
        if (messageTimer > 25 && currentMessageIndex < timeMessages.length - 1) {
            currentMessageIndex++;
            messageTimer = 0;
        }

        for (Particle p : particles) {
            p.update();
        }

        if (isClosing && fadeAlpha > 0) {
            fadeAlpha -= 5;
            if (fadeAlpha <= 0) {
                closeSplash();
            }
        }
    }

    private void closeSplash() {
        if (animationTimer != null) animationTimer.stop();
        if (closeTimer != null) closeTimer.stop();
        setVisible(false);
        dispose();
        if (afterCloseCallback != null) SwingUtilities.invokeLater(afterCloseCallback);
    }

    private class PortalPanel extends JPanel {
        public PortalPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (fadeAlpha <= 0) {
                // Non disegnare più nulla dopo la dissolvenza
                return;
            }
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawSpaceBackground(g2d);
            drawTimeTunnel(g2d);
            drawMainPortal(g2d);
            drawParticles(g2d);
            drawLightEffects(g2d);
            drawMessage(g2d);
            drawProgressBar(g2d);

            if (isClosing) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha / 255f));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // reset
                closeSplash();
            }
        }
    }

    private void drawSpaceBackground(Graphics2D g2d) {
        Random starRandom = new Random(12345);
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < 100; i++) {
            int x = (int) (starRandom.nextFloat() * WIDTH);
            int y = (int) (starRandom.nextFloat() * HEIGHT);
            g2d.fillOval(x, y, 1, 1);
        }
    }

    private void drawTimeTunnel(Graphics2D g2d) {
        AffineTransform oldTransform = g2d.getTransform();
        g2d.translate(CENTER_X, CENTER_Y);
        g2d.rotate(portalRotation);

        for (int i = 0; i < 20; i++) {
            float scale = i * 0.3f + tunnelDepth % 6;
            int size = (int) (scale * 30);

            if (size > 0 && size < 600) {
                float alpha = 1.0f - (scale / 20f);
                if (alpha > 0) {
                    Color ringColor = new Color(0.3f + alpha * 0.4f, 0.1f + alpha * 0.6f, 0.8f + alpha * 0.2f, alpha * 0.3f);
                    g2d.setColor(ringColor);
                    g2d.setStroke(new BasicStroke(2f + alpha * 3f));
                    g2d.drawOval(-size/2, -size/2, size, size);
                }
            }
        }

        g2d.setTransform(oldTransform);
    }

    private void drawMainPortal(Graphics2D g2d) {
        AffineTransform oldTransform = g2d.getTransform();
        g2d.translate(CENTER_X, CENTER_Y);

        float pulseSize = 150 + (float) Math.sin(portalPulse) * 30;

        RadialGradientPaint outerGlow = new RadialGradientPaint(
                0, 0, pulseSize + 50,
                new float[]{0f, 0.7f, 1f},
                new Color[]{
                        new Color(100, 150, 255, 100),
                        new Color(50, 100, 255, 50),
                        new Color(0, 0, 0, 0)
                }
        );
        g2d.setPaint(outerGlow);
        g2d.fillOval(-(int)(pulseSize + 50), -(int)(pulseSize + 50),
                (int)(pulseSize + 50) * 2, (int)(pulseSize + 50) * 2);

        RadialGradientPaint portalGradient = new RadialGradientPaint(
                0, 0, pulseSize,
                new float[]{0f, 0.3f, 0.7f, 1f},
                new Color[]{
                        new Color(255, 255, 255, 200),
                        new Color(150, 200, 255, 150),
                        new Color(50, 100, 255, 100),
                        new Color(0, 50, 150, 50)
                }
        );
        g2d.setPaint(portalGradient);
        g2d.fillOval(-(int)pulseSize, -(int)pulseSize, (int)pulseSize * 2, (int)pulseSize * 2);

        g2d.setTransform(oldTransform);
    }

    private void drawParticles(Graphics2D g2d) {
        for (Particle p : particles) {
            p.draw(g2d);
        }
    }

    private void drawLightEffects(Graphics2D g2d) {
        Composite oldComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        for (int i = 0; i < 8; i++) {
            double angle = (portalRotation * 2 + i * Math.PI / 4);
            int x1 = CENTER_X + (int)(Math.cos(angle) * 80);
            int y1 = CENTER_Y + (int)(Math.sin(angle) * 80);
            int x2 = CENTER_X + (int)(Math.cos(angle) * 300);
            int y2 = CENTER_Y + (int)(Math.sin(angle) * 300);

            g2d.setColor(new Color(100, 150, 255));
            g2d.setStroke(new BasicStroke(3f));
            g2d.drawLine(x1, y1, x2, y2);
        }
        g2d.setComposite(oldComposite);
    }

    private void drawMessage(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(new Color(200, 220, 255));

        String message = timeMessages[currentMessageIndex];
        FontMetrics fm = g2d.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(message)) / 2;
        int y = HEIGHT - 150;

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(message, x + 2, y + 2);

        g2d.setColor(new Color(200, 220, 255));
        g2d.drawString(message, x, y);
    }

    private void drawProgressBar(Graphics2D g2d) {
        int barWidth = 300;
        int barHeight = 8;
        int barX = (WIDTH - barWidth) / 2;
        int barY = HEIGHT - 100;

        float progress = Math.min(1.0f, (currentMessageIndex + 1) / (float) timeMessages.length);

        g2d.setColor(new Color(100, 150, 255));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRect(barX, barY, barWidth, barHeight);

        LinearGradientPaint barGradient = new LinearGradientPaint(
                barX, barY, barX, barY + barHeight,
                new float[]{0f, 1f},
                new Color[]{new Color(150, 200, 255), new Color(50, 100, 255)}
        );
        g2d.setPaint(barGradient);
        g2d.fillRect(barX + 1, barY + 1, (int)((barWidth - 2) * progress), barHeight - 2);

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(Color.WHITE);
        String percentage = (int)(progress * 100) + "%";
        g2d.drawString(percentage, barX + barWidth + 10, barY + barHeight);
    }

    private class Particle {
        float x, y, z;
        float vx, vy, vz;
        Color color;
        float life;

        public Particle() {
            reset();
        }

        private void reset() {
            float angle = random.nextFloat() * 2 * (float)Math.PI;
            float radius = random.nextFloat() * 50 + 10;
            x = (float)Math.cos(angle) * radius;
            y = (float)Math.sin(angle) * radius;
            z = random.nextFloat() * 500 + 100;

            vx = (random.nextFloat() - 0.5f) * 2;
            vy = (random.nextFloat() - 0.5f) * 2;
            vz = -random.nextFloat() * 5 - 2;

            float hue = random.nextFloat() * 0.3f + 0.5f;
            color = new Color(Color.HSBtoRGB(hue, 0.8f, 1.0f));
            life = 1.0f;
        }

        public void update() {
            x += vx;
            y += vy;
            z += vz;
            life -= 0.01f;

            if (z <= 0 || life <= 0) {
                reset();
            }
        }

        public void draw(Graphics2D g2d) {
            if (z > 0) {
                float scale = 200f / z;
                int screenX = CENTER_X + (int)(x * scale);
                int screenY = CENTER_Y + (int)(y * scale);

                if (screenX >= 0 && screenX < WIDTH && screenY >= 0 && screenY < HEIGHT) {
                    int alpha = (int)(life * 255 * scale);
                    alpha = Math.max(0, Math.min(255, alpha));

                    Color particleColor = new Color(
                            color.getRed(), color.getGreen(), color.getBlue(), alpha
                    );
                    g2d.setColor(particleColor);

                    int size = Math.max(1, (int)(scale * 3));
                    g2d.fillOval(screenX - size/2, screenY - size/2, size, size);
                }
            }
        }
    }

    // Metodo statico per mostrare lo splash con callback
    public static void showWithCallback(Runnable afterSplash) {
        new TimePortalSplashScreen(afterSplash);
    }
}