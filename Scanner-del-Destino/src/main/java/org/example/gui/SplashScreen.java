package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SplashScreen con animazioni personalizzate e progress bar.
 * Mostra un titolo, sottotitolo, cerchio scanner animato,
 * particelle animate, barra di caricamento e messaggi di stato.
 * Al termine del caricamento, avvia la finestra principale del gioco.
 * @version 1.0.0
 */
public class SplashScreen extends JWindow {
    // --- Timers UI ---
    private Timer animationTimer;

    // (sostituisce il vecchio loadingTimer)
    private Timer minShowTimer;   // <<< durata minima
    private Timer maxShowTimer;   // <<< timeout di sicurezza

    // --- UI ---
    private JProgressBar progressBar;
    private JLabel loadingLabel;
    private List<Particle> particles;
    private Random random;

    // --- Stato ---
    private final String[] loadingMessages = {
            "Inizializzazione del mondo...",
            "Caricamento personaggi...",
            "Preparazione inventario...",
            "Generazione mappa...",
            "Calibrazione scanner...",
            "Sincronizzazione destino...",
            "Pronto all'avventura!"
    };
    // Durata minima e massima (regolabili)
    private static final int MIN_SHOW_MS = 6000;    // <<< almeno 6s visibile
    private static final int MAX_SHOW_MS = 15000;   // <<< al massimo 15s

    private volatile boolean workerDone = false;
    private volatile boolean minElapsed = false;

    // Worker in background (usa un Thread interno)  <<< THREAD QUI
    private LoaderWorker loaderWorker;

    /**
     * Costruttore della SplashScreen.
     * Inizializza componenti, particelle e avvia le animazioni.
     * La finestra è inizialmente invisibile.
     */
    public SplashScreen() {
        initComponents();
        initParticles();
        startAnimations();

        // Mostra la finestra (se preferisci, lascia false e gestisci da fuori)
        setVisible(false); // << lasciato come nel tuo codice
    }

    /**
     * Inizializza i componenti grafici della SplashScreen.
     * Configura layout, colori, font e aggiunge i vari elementi.
     * Imposta dimensioni e posizione della finestra.
     */
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

        // Scanner circle
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

        // --- Avvio del Worker in background + timers di durata ---
        startBackgroundLoading(); // <<< THREAD + timers
    }
    /**
     * Inizializza le particelle animate.
     * Crea una lista di particelle con posizioni, velocità e colori casuali.
     * Le particelle verranno animate nel pannello principale.
     */
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
    /**
     * Avvia le animazioni della SplashScreen.
     * Configura timer per animazioni particelle e caricamento.
     * Al termine del caricamento, chiude la SplashScreen e avvia la finestra principale del gioco.
     *
     */
    private void startAnimations() {
        // Timer per animazione particelle
        animationTimer = new Timer(50, e -> {
            updateParticles();
            repaint();
        });
        animationTimer.start();
    }

    // ---------------- THREAD + controllo durata ----------------

    private void startBackgroundLoading() {
        // 1) Worker BACKGROUND (usa un thread separato)
        loaderWorker = new LoaderWorker();
        // Aggiorna progress bar con setProgress del worker
        loaderWorker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    int p = (int) evt.getNewValue();
                    progressBar.setValue(p);
                }
            }
        });
        loaderWorker.execute();

        // 2) Durata minima
        minShowTimer = new Timer(MIN_SHOW_MS, e -> {
            minElapsed = true;
            tryClose();
        });
        minShowTimer.setRepeats(false);
        minShowTimer.start();

        // 3) Timeout massimo di sicurezza
        maxShowTimer = new Timer(MAX_SHOW_MS, e -> {
            // se qualcosa va storto, chiudiamo comunque
            closeAndLaunch();
        });
        maxShowTimer.setRepeats(false);
        maxShowTimer.start();
    }

    private void tryClose() {
        if (workerDone && minElapsed) {
            closeAndLaunch();
        }
    }

    private void closeAndLaunch() {
        if (!isVisible()) {
            // se non era stata resa visibile qui, chiudi comunque risorse e lancia gioco
            stopAndDispose();
            SwingUtilities.invokeLater(FinestraGioco::new);
            return;
        }
        stopAndDispose();
        SwingUtilities.invokeLater(FinestraGioco::new);
    }

    private void stopAndDispose() {
        if (animationTimer != null) animationTimer.stop();
        if (minShowTimer != null) minShowTimer.stop();
        if (maxShowTimer != null) maxShowTimer.stop();
        setVisible(false);
        dispose();
    }

    /**
     * Worker che simula/effettua il caricamento reale:
     * - In doInBackground() fai init DB H2, preload risorse, lettura scene, ecc.
     * - Usa publish(...) per messaggi, setProgress(...) per la barra.
     */
    private class LoaderWorker extends SwingWorker<Void, String> {
        @Override
        protected Void doInBackground() throws Exception {
            // Esempio di step reali: inizializzazione DB H2
            // (sostituisci/integra con ciò che serve davvero al tuo progetto)
            // THREAD REALE: questo metodo NON gira sull'EDT.
            setProgress(2);
            publish(loadingMessages[0]); // "Inizializzazione del mondo..."
            org.example.database.DatabaseBootstrap.ensureStarted();

            // Se hai altri step (preload risorse, DAO, cache, ecc.), pubblicali:
            step(18, 1); // "Caricamento personaggi..."
            step(32, 2); // "Preparazione inventario..."
            step(48, 3); // "Generazione mappa..."
            step(65, 4); // "Calibrazione scanner..."
            step(82, 5); // "Sincronizzazione destino..."

            // Ultimo step
            step(100, 6); // "Pronto all'avventura!"
            return null;
        }

        private void step(int progress, int messageIdx) throws InterruptedException {
            // piccolo ritardo per percepire l'avanzamento (facoltativo)
            Thread.sleep(350); // regola a piacere
            setProgress(Math.min(100, Math.max(0, progress)));
            if (messageIdx >= 0 && messageIdx < loadingMessages.length) {
                publish(loadingMessages[messageIdx]);
            }
        }

        @Override
        protected void process(List<String> chunks) {
            // Eseguito sull'EDT: aggiorna l'etichetta con l'ultimo messaggio
            if (!chunks.isEmpty()) {
                String last = chunks.get(chunks.size() - 1);
                loadingLabel.setText(last);
            }
        }

        @Override
        protected void done() {
            // Worker terminato
            workerDone = true;
            tryClose();
        }
    }

    // ---------------- PARTICELLE / RENDER ----------------
    /**
     * Aggiorna la posizione delle particelle.
     * Le particelle si muovono verso l'alto e vengono riposizionate quando escono dallo schermo.
     */
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

    /**
     * Classe per le particelle animate
     * Rappresenta una particella animata.
     * Contiene posizione, velocità e colore.
     * Fornisce un metodo per aggiornare la posizione.
     */
    private static class Particle {
        double x, y, speed;
        Color color;
        /**
         * Costruttore della particella.
         * Inizializza posizione, velocità e colore.
         * @param x
         * @param y
         * @param speed
         * @param color
         */
        Particle(double x, double y, double speed, Color color) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.color = color;
        }
        /**
         * Aggiorna la posizione della particella.
         * Si muove verso l'alto in base alla velocità.
         */
        void update() {
            y -= speed;
        }
    }

    /**
     * Panel con sfondo animato e particelle.
     * Disegna uno sfondo a gradiente e particelle animate con effetto luminoso.
     * Estende JPanel e sovrascrive il metodo paintComponent per il rendering personalizzato.
     */
    private class AnimatedPanel extends JPanel {
        /**
         * Sovrascrive il metodo paintComponent per disegnare lo sfondo e le particelle.
         * Utilizza rendering antialiasing per migliorare la qualità grafica.
         * @param g
         */
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

    /**
     * Panel con animazione del cerchio scanner.
     * Disegna cerchi concentrici, un punto centrale pulsante e una linea rotante.
     * Estende JPanel e utilizza un Timer per l'animazione.
     * Fornisce un metodo per fermare l'animazione.
     */    private static class ScannerPanel extends JPanel {
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
        /**
         * Sovrascrive il metodo paintComponent per disegnare l'animazione del cerchio scanner.
         * Disegna cerchi, un punto centrale pulsante e una linea rotante.
         * Utilizza rendering antialiasing per migliorare la qualità grafica.
         * @param g Il contesto grafico per il disegno.
         */
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
    }
}
