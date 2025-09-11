package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TimePortalSplashScreen
 * Una schermata di splash animata che simula un portale temporale con effetti visivi.
 * Include un tunnel temporale, particelle luminose, effetti di luce e un messaggio di caricamento.
 * Dopo un certo periodo, la schermata si dissolve e chiama un callback.
 * Basato su Java Swing.
 * @version 1.0
 */
public class TimePortalSplashScreen extends JWindow {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int CENTER_X = WIDTH / 2;
    private static final int CENTER_Y = HEIGHT / 2;

    // Configurabili
    private static final int MIN_SHOW_MS = 6000;   // tempo minimo visibile
    private static final int MAX_SHOW_MS = 15000;  // timeout di sicurezza

    private Timer animationTimer;
    private Timer minTimer;
    private Timer maxTimer;

    private float portalRotation = 0f;
    private float portalPulse = 0f;
    private float tunnelDepth = 0f;

    private List<Particle> particles;
    private Random random;

    private final String[] timeMessages = {
            "Inizializzazione portale temporale...",
            "Calibrazione flusso spazio-tempo...",
            "Sincronizzazione dimensionale...",
            "Apertura varco temporale...",
            "Viaggiando attraverso il tempo...",
            "Arrivo completato!"
    };
    private int currentMessageIndex = 0;
    private int messageTimer = 0;

    // Stato per chiudere solo quando PRONTO & TEMPO MINIMO trascorso
    private volatile boolean workerDone = false;
    private volatile boolean minElapsed = false;

    // Lavoro in background (DB, risorse, ecc.)
    private SwingWorker<Void, Void> initWorker;

    // callback finale
    private final Runnable afterCloseCallback;

    /**
     * Costruttore
     * Crea e mostra la schermata di splash.
     * @param afterSplash
     */
    public TimePortalSplashScreen(Runnable afterSplash) {
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setBackground(new Color(0, 0, 0, 0));
        this.afterCloseCallback = afterSplash;

        PortalPanel portalPanel = new PortalPanel();
        setContentPane(portalPanel);

        initializeParticles();

        // === BACKGROUND WORKER (NON blocca la GUI) ===
        initWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Metti qui il lavoro "lento"
                org.example.database.DatabaseBootstrap.ensureStarted();
                return null;
            }
            @Override
            protected void done() {
                workerDone = true;
                tryClose(); // prova a chiudere se è passato anche il minimo tempo
            }
        };
        initWorker.execute();

        // === TIMERS DI CONTROLLO DURATA ===
        // Mostra almeno MIN_SHOW_MS
        minTimer = new Timer(MIN_SHOW_MS, e -> {
            minElapsed = true;
            tryClose(); // se il worker è già finito, ora chiudiamo
        });
        minTimer.setRepeats(false);
        minTimer.start();

        // Sicurezza: comunque chiudi entro MAX_SHOW_MS
        maxTimer = new Timer(MAX_SHOW_MS, e -> closeSplash());
        maxTimer.setRepeats(false);
        maxTimer.start();

        // === ANIMAZIONE ===
        startAnimation();
        setVisible(true);
    }

    private void tryClose() {
        // Chiudi solo quando: lavoro in background FINITO + minimo tempo TRASCORSO
        if (workerDone && minElapsed) {
            closeSplash();
        }
    }

    /**
     *  Inizializza le particelle luminose.
     *  Crea un certo numero di particelle con posizioni e velocità casuali.
     *  Le particelle si muovono verso il centro del portale e si dissolvono con la distanza.
     */
    private void initializeParticles() {
        particles = new ArrayList<>();
        random = new Random();
        for (int i = 0; i < 150; i++) {
            particles.add(new Particle());
        }
    }

    /**
     * Avvia l'animazione della schermata di splash.
     * Utilizza un Timer per aggiornare lo stato dell'animazione e ridisegnare la schermata.
     * Dopo un certo periodo, inizia la chiusura della schermata.
     */
    private void startAnimation() {
        animationTimer = new Timer(50, e -> {
            updateAnimation();
            repaint();
        });
        animationTimer.start();
    }

    /**
     * Aggiorna lo stato dell'animazione.
     * Aggiorna la rotazione del portale, l'effetto di pulsazione, la profondità del tunnel,
     * il messaggio di caricamento e le particelle.
     * Gestisce anche la dissolvenza della schermata quando in chiusura.
     * Nota: Questo metodo viene chiamato periodicamente dal Timer di animazione.
     */
    private void updateAnimation() {
        portalRotation += 0.1f;
        portalPulse += 0.15f;
        tunnelDepth += 0.05f;

        // Avanza i messaggi a tempo (decorativo)
        messageTimer++;
        if (messageTimer > 25 && currentMessageIndex < timeMessages.length - 1) {
            currentMessageIndex++;
            messageTimer = 0;
        }

        for (Particle p : particles) {
            p.update();
        }
    }

    /**
     * Chiude la schermata di splash.
     * Ferma i timer, nasconde e distrugge la finestra.
     * Chiama il callback passato al costruttore dopo la chiusura.
     * Nota: Questo metodo viene chiamato quando l'animazione di chiusura è completa.
     */
    private void closeSplash() {
        if (!isVisible()) return; // evita doppie chiusure
        if (animationTimer != null) animationTimer.stop();
        if (minTimer != null) minTimer.stop();
        if (maxTimer != null) maxTimer.stop();
        setVisible(false);
        dispose();
        if (afterCloseCallback != null) {
            SwingUtilities.invokeLater(afterCloseCallback);
        }
    }


    /**
     * Pannello principale della schermata di splash.
     * Gestisce il disegno di tutti gli elementi visivi.
     * Override del metodo paintComponent per disegnare lo sfondo, il tunnel temporale,
     * il portale, le particelle, gli effetti di luce, il messaggio e la barra di progresso.
     * Gestisce anche la dissolvenza della schermata quando in chiusura.
     */
    private class PortalPanel extends JPanel {
        public PortalPanel() {
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawTimeTunnel(g2d);
            drawMainPortal(g2d);
            drawParticles(g2d);
            drawLightBeams(g2d);
            drawMessage(g2d);
            drawProgressBar(g2d);
        }
    }

    /**
     * Disegna il tunnel temporale con anelli concentrici che si muovono verso il centro.
     * Gli anelli ruotano e si dissolvono con la distanza.
     * @param g2d
     */
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
                    Color ringColor = new Color(
                            0.35f + alpha * 0.35f,
                            0.20f + alpha * 0.35f,
                            0.95f,
                            alpha * 0.3f
                    );
                    g2d.setColor(ringColor);
                    g2d.setStroke(new BasicStroke(2f + alpha * 3f));
                    g2d.drawOval(-size / 2, -size / 2, size, size);
                }
            }
        }
        g2d.setTransform(oldTransform);
    }

    /**
     * Disegna il portale principale con un effetto di pulsazione.
     * Utilizza gradienti radiali per creare un effetto di luce e profondità.
     * @param g2d
     */
    private void drawMainPortal(Graphics2D g2d) {
        AffineTransform oldTransform = g2d.getTransform();
        g2d.translate(CENTER_X, CENTER_Y);

        float pulseSize = 150 + (float) Math.sin(portalPulse) * 30;

        RadialGradientPaint outerGlow = new RadialGradientPaint(
                0, 0, pulseSize + 50,
                new float[]{0f, 0.6f, 1f},
                new Color[]{
                        new Color(120, 170, 255, 120),
                        new Color(70, 130, 255, 60),
                        new Color(0, 0, 0, 0)
                }
        );
        g2d.setPaint(outerGlow);
        g2d.fillOval(-(int) (pulseSize + 50), -(int) (pulseSize + 50),
                (int) (pulseSize + 50) * 2, (int) (pulseSize + 50) * 2);

        RadialGradientPaint portalGradient = new RadialGradientPaint(
                0, 0, pulseSize,
                new float[]{0f, 0.3f, 0.7f, 1f},
                new Color[]{
                        new Color(255, 255, 255, 220),
                        new Color(170, 210, 255, 160),
                        new Color(80, 130, 255, 110),
                        new Color(40, 80, 200, 70)
                }
        );
        g2d.setPaint(portalGradient);
        g2d.fillOval(-(int) pulseSize, -(int) pulseSize, (int) pulseSize * 2, (int) pulseSize * 2);

        g2d.setTransform(oldTransform);
    }

    /**
     *  Disegna le particelle luminose che si muovono verso il centro del portale.
     *  Le particelle si dissolvono con la distanza e hanno colori variabili.
     * @param g2d
     */
    private void drawParticles(Graphics2D g2d) {
        for (Particle p : particles) {
            p.draw(g2d);
        }
    }

    /**
     * Disegna gli effetti di luce che si irradiano dal portale.
     * Gli effetti di luce ruotano con il portale e hanno un effetto di trasparenza.
     *
     * @param g2d
     */
    private void drawLightBeams(Graphics2D g2d) {
        Composite oldComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.28f));
        for (int i = 0; i < 8; i++) {
            double angle = (portalRotation * 2 + i * Math.PI / 4);
            int x1 = CENTER_X + (int) (Math.cos(angle) * 80);
            int y1 = CENTER_Y + (int) (Math.sin(angle) * 80);
            int x2 = CENTER_X + (int) (Math.cos(angle) * 300);
            int y2 = CENTER_Y + (int) (Math.sin(angle) * 300);

            g2d.setColor(new Color(120, 170, 255));
            g2d.setStroke(new BasicStroke(3f));
            g2d.drawLine(x1, y1, x2, y2);
        }
        g2d.setComposite(oldComposite);
    }

    /**
     * Disegna il messaggio di caricamento centrato nella parte inferiore della schermata.
     * Utilizza un font leggibile e un'ombra per migliorare la visibilità.
     * @param g2d
     */
    private void drawMessage(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String message = timeMessages[currentMessageIndex];

        FontMetrics fm = g2d.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(message)) / 2;
        int y = HEIGHT - 150;

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(message, x + 2, y + 2);

        g2d.setColor(new Color(220, 235, 255));
        g2d.drawString(message, x, y);
    }

    /**
     * Disegna una barra di progresso sotto il messaggio di caricamento.
     * La barra si riempie in base al messaggio corrente.
     * @param g2d
     */
    private void drawProgressBar(Graphics2D g2d) {
        int barWidth = 300;
        int barHeight = 8;
        int barX = (WIDTH - barWidth) / 2;
        int barY = HEIGHT - 100;

        float progress = Math.min(1.0f, (currentMessageIndex + 1) / (float) timeMessages.length);

        g2d.setColor(new Color(120, 170, 255));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRect(barX, barY, barWidth, barHeight);

        LinearGradientPaint barGradient = new LinearGradientPaint(
                barX, barY, barX, barY + barHeight,
                new float[]{0f, 1f},
                new Color[]{new Color(170, 210, 255), new Color(70, 130, 255)}
        );
        g2d.setPaint(barGradient);
        g2d.fillRect(barX + 1, barY + 1, (int) ((barWidth - 2) * progress), barHeight - 2);

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(Color.WHITE);
        String percentage = (int) (progress * 100) + "%";
        g2d.drawString(percentage, barX + barWidth + 10, barY + barHeight);
    }


    /**
     * Classe interna per rappresentare una particella luminosa.
     * Ogni particella ha una posizione 3D, velocità, colore e durata di vita.
     * Le particelle si muovono verso il centro del portale e si dissolvono con la distanza.
     * Le particelle vengono rigenerate quando escono dallo schermo o terminano la loro vita.
     */
    private class Particle {
        float x, y, z;
        float vx, vy, vz;
        Color color;
        float life;

        public Particle() { reset(); }

        /**
         * Reimposta la particella con una nuova posizione, velocità, colore e durata di vita.
         * La posizione iniziale è casuale intorno al centro dello schermo.
         * La velocità è diretta verso il centro con una componente casuale.
         */
        private void reset() {
            float angle = random.nextFloat() * 2 * (float) Math.PI;
            float radius = random.nextFloat() * 50 + 10;
            x = (float) Math.cos(angle) * radius;
            y = (float) Math.sin(angle) * radius;
            z = random.nextFloat() * 500 + 100;

            vx = (random.nextFloat() - 0.5f) * 2;
            vy = (random.nextFloat() - 0.5f) * 2;
            vz = -random.nextFloat() * 5 - 2;

            float hue = random.nextFloat() * 0.3f + 0.55f;
            color = new Color(Color.HSBtoRGB(hue, 0.8f, 1.0f));
            life = 1.0f;
        }

        /**
         * Aggiorna la posizione e lo stato della particella.
         * La particella si muove in base alla sua velocità.
         * La durata di vita diminuisce nel tempo.
         * Se la particella esce dallo schermo o termina la sua vita, viene reimpostata.
         */
        public void update() {
            x += vx;
            y += vy;
            z += vz;
            life -= 0.01f;
            if (z <= 0 || life <= 0) reset();
        }

        /**
         *  Disegna la particella sullo schermo.
         *  La posizione 3D viene proiettata in 2D.
         *  La trasparenza della particella dipende dalla sua distanza e durata di vita.
         *  La particella viene disegnata come un piccolo cerchio.
         * @param g2d
         */
        public void draw(Graphics2D g2d) {
            if (z > 0) {
                float scale = 200f / z;
                int screenX = CENTER_X + (int) (x * scale);
                int screenY = CENTER_Y + (int) (y * scale);
                if (screenX >= 0 && screenX < WIDTH && screenY >= 0 && screenY < HEIGHT) {
                    int alpha = (int) (life * 255 * Math.min(1f, scale));
                    alpha = Math.max(0, Math.min(255, alpha));
                    Color particleColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
                    g2d.setColor(particleColor);
                    int size = Math.max(1, (int) (scale * 3));
                    g2d.fillOval(screenX - size / 2, screenY - size / 2, size, size);
                }
            }
        }
    }

    /**
     * Mostra la schermata di splash con un callback.
     * Crea una nuova istanza di TimePortalSplashScreen e la mostra.
     * @param afterSplash
     */
    public static void showWithCallback(Runnable afterSplash) {
        new TimePortalSplashScreen(afterSplash);
    }
}
