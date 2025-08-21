package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SplashScreen extends JWindow {
    private Timer animationTimer;
    private Timer loadingTimer;
    private JProgressBar progressBar;
    private JLabel loadingLabel;
    private List<Particle> particles;
    private Random random;
    private int loadingProgress = 0;
    private String[] loadingMessages = {
            "Inizializzazione del mondo...",
            "Caricamento personaggi...",
            "Preparazione inventario...",
            "Generazione mappa...",
            "Calibrazione scanner...",
            "Sincronizzazione destino...",
            "Pronto all'avventura!"
    };
    private int messageIndex = 0;

    public SplashScreen() {
        initComponents();
        initParticles();
        startAnimations();
        setVisible(false);
    }

    private void initComponents() {
        setSize(1000, 800);
        setLocationRelativeTo(null);

        // Panel principale con sfondo personalizzato
        JPanel mainPanel = new AnimatedPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(26, 26, 26));

        // Panel centrale
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Titolo
        JLabel titleLabel = new JLabel("SCANNER DEL DESTINO");
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 32));
        titleLabel.setForeground(new Color(0, 255, 65));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sottotitolo
        JLabel subtitleLabel = new JLabel("Un'avventura testuale di Lewis nell'ignoto");
        subtitleLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Scanner circle (usando un JPanel personalizzato)
        ScannerPanel scannerPanel = new ScannerPanel();
        scannerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scannerPanel.setPreferredSize(new Dimension(150, 150));
        scannerPanel.setMaximumSize(new Dimension(150, 150));

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setBackground(new Color(40, 40, 40));
        progressBar.setForeground(new Color(0, 255, 65));
        progressBar.setBorderPainted(false);
        progressBar.setMaximumSize(new Dimension(300, 8));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Loading label
        loadingLabel = new JLabel("Inizializzazione...");
        loadingLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
        loadingLabel.setForeground(new Color(150, 150, 150));
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Versione
        JLabel versionLabel = new JLabel("v1.0.0");
        versionLabel.setFont(new Font("Consolas", Font.PLAIN, 10));
        versionLabel.setForeground(new Color(100, 100, 100));

        // Assemblaggio
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        centerPanel.add(scannerPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        centerPanel.add(progressBar);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(loadingLabel);
        centerPanel.add(Box.createVerticalGlue());

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(versionLabel);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void initParticles() {
        particles = new ArrayList<>();
        random = new Random();

        // Crea particelle iniziali
        for (int i = 0; i < 20; i++) {
            particles.add(new Particle(
                    random.nextInt(getWidth()),
                    random.nextInt(getHeight()),
                    random.nextDouble() * 2 + 1,
                    new Color(0, 255, 65, random.nextInt(100) + 50)
            ));
        }
    }

    private void startAnimations() {
        // Timer per animazione particelle
        animationTimer = new Timer(50, e -> {
            updateParticles();
            repaint();
        });
        animationTimer.start();

        // Timer per caricamento
        loadingTimer = new Timer(600, e -> {
            loadingProgress += 15;
            progressBar.setValue(Math.min(loadingProgress, 100));

            if (messageIndex < loadingMessages.length) {
                loadingLabel.setText(loadingMessages[messageIndex]);
                messageIndex++;
            }

            if (loadingProgress >= 100) {
                loadingTimer.stop();
                Timer closeTimer = new Timer(1000, evt -> {
                    animationTimer.stop();
                    dispose();
                    // Avvia il gioco principale
                    SwingUtilities.invokeLater(() -> new FinestraGioco());
                });
                closeTimer.setRepeats(false);
                closeTimer.start();
            }
        });
        loadingTimer.start();
    }

    private void updateParticles() {
        for (Particle particle : particles) {
            particle.update();

            // Riposiziona particelle che escono dallo schermo
            if (particle.y < 0) {
                particle.y = getHeight();
                particle.x = random.nextInt(getWidth());
            }
        }
    }

    // Classe per le particelle animate
    private static class Particle {
        double x, y, speed;
        Color color;

        Particle(double x, double y, double speed, Color color) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.color = color;
        }

        void update() {
            y -= speed;
        }
    }

    // Panel animato per lo sfondo
    private class AnimatedPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Sfondo gradiente
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(26, 26, 26),
                    getWidth(), getHeight(), new Color(45, 45, 45)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Disegna particelle
            for (Particle particle : particles) {
                g2d.setColor(particle.color);
                g2d.fillOval((int) particle.x, (int) particle.y, 3, 3);

                // Effetto luminoso
                g2d.setColor(new Color(0, 255, 65, 20));
                g2d.fillOval((int) particle.x - 2, (int) particle.y - 2, 7, 7);
            }

            g2d.dispose();
        }
    }

    // Panel per il cerchio scanner
    private static class ScannerPanel extends JPanel {
        private Timer rotationTimer;
        private double angle = 0;

        public ScannerPanel() {
            setOpaque(false);
            rotationTimer = new Timer(50, e -> {
                angle += 0.1;
                if (angle >= 2 * Math.PI) angle = 0;
                repaint();
            });
            rotationTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            // Cerchio esterno
            g2d.setColor(new Color(0, 255, 65));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(centerX - 60, centerY - 60, 120, 120);

            // Cerchio interno rotante
            g2d.setColor(new Color(0, 255, 65, 150));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(centerX - 40, centerY - 40, 80, 80);

            // Punto centrale pulsante
            int pulseSize = (int) (10 + 5 * Math.sin(angle * 3));
            g2d.setColor(new Color(0, 255, 65, 200));
            g2d.fillOval(centerX - pulseSize/2, centerY - pulseSize/2, pulseSize, pulseSize);

            // Linea rotante
            int lineEndX = centerX + (int) (35 * Math.cos(angle));
            int lineEndY = centerY + (int) (35 * Math.sin(angle));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(centerX, centerY, lineEndX, lineEndY);

            g2d.dispose();
        }

        public void stopAnimation() {
            if (rotationTimer != null) {
                rotationTimer.stop();
            }
        }
    }
}