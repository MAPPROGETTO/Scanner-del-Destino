package org.example.engine.gui;

import org.example.model.StatoGioco;
import org.example.utils.GameSaver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Schermata del menu principale del gioco in stile Matrix.
 * Presenta opzioni per iniziare una nuova partita, caricare una partita salvata o uscire dal gioco.
 * Utilizza effetti visivi in stile Matrix per un'esperienza immersiva.
 * @version 1.0
 */
public class GameMenuScreen extends JFrame implements KeyListener, Serializable {
    private JButton newGameButton;
    private JButton loadGameButton;
    private JButton exitButton;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private AnimatedPanel mainPanel;

    private transient List<MatrixStyleUtils.FloatingParticle> particles;
    private transient List<MatrixStyleUtils.MatrixChar> matrixChars;
    /**
     * Costruttore che inizializza la schermata del menu di gioco.
     * Configura i componenti, il layout e gli handler degli eventi.
     * Avvia un timer per aggiornare gli effetti visivi in stile Matrix.
     * Imposta le proprietà della finestra principale.
     * @see MatrixStyleUtils
     * @see AnimatedPanel
     * @see Timer
     *  @see KeyListener
     *  @see JFrame
     */
    public GameMenuScreen() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();

        Timer matrixTimer = new Timer(100, e -> {
            MatrixStyleUtils.updateFloatingParticles(particles, getWidth(), getHeight());
            MatrixStyleUtils.updateMatrixChars(matrixChars, getWidth(), getHeight());
            mainPanel.repaint();
        });
        matrixTimer.start();
    }
    /**
     * Inizializza i componenti della schermata del menu di gioco.
     * Crea etichette, pulsanti e il pannello principale con effetti animati.
     * Utilizza la classe MatrixStyleUtils per applicare stili e creare effetti visivi.
     * @see MatrixStyleUtils
     * @see AnimatedPanel
     *
     */
    private void initializeComponents() {
        particles = MatrixStyleUtils.createFloatingParticles(20, 1000, 800);
        matrixChars = MatrixStyleUtils.createMatrixChars(30, 1000, 800);

        titleLabel = new JLabel("GAME MENU", SwingConstants.CENTER);
        titleLabel.setFont(MatrixStyleUtils.COURIER_TITLE);
        titleLabel.setForeground(MatrixStyleUtils.MATRIX_GREEN);

        subtitleLabel = new JLabel("Seleziona un'opzione per continuare", SwingConstants.CENTER);
        subtitleLabel.setFont(MatrixStyleUtils.COURIER_FONT);
        subtitleLabel.setForeground(MatrixStyleUtils.MATRIX_GREEN_TRANSPARENT);

        newGameButton = MatrixStyleUtils.createStyledButton("NUOVA PARTITA", MatrixStyleUtils.MATRIX_GREEN);
        loadGameButton = MatrixStyleUtils.createStyledButton("CARICA PARTITA", MatrixStyleUtils.MATRIX_GREEN_TRANSPARENT);
        exitButton = MatrixStyleUtils.createStyledButton("ESCI", MatrixStyleUtils.DANGER_COLOR);

        mainPanel = new AnimatedPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                MatrixStyleUtils.applyAlternativeGradient(g2d, getWidth(), getHeight());
                MatrixStyleUtils.drawFloatingParticles(g2d, particles);
                MatrixStyleUtils.drawMatrixChars(g2d, matrixChars);
                MatrixStyleUtils.drawMatrixEffect(g2d, getWidth(), getHeight());

                g2d.dispose();
            }
        };

        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(MatrixStyleUtils.BACKGROUND_COLOR);
    }
    /**
     * Configura il layout della schermata del menu di gioco.
     *  Utilizza BorderLayout e BoxLayout per organizzare i componenti.
     *  Applica bordi e spaziature per migliorare l'aspetto visivo.
     */
    private void setupLayout() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(50, 10, 30, 10));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 50, 0));

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(createButtonWithSpacing(newGameButton));
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(createButtonWithSpacing(loadGameButton));
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(createButtonWithSpacing(exitButton));
        buttonPanel.add(Box.createVerticalGlue());

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.setBackground(Color.BLACK);
        borderPanel.setBorder(MatrixStyleUtils.createThickMatrixBorder());
        borderPanel.add(mainPanel, BorderLayout.CENTER);

        add(borderPanel);
    }
    /**
     * Crea un pannello che contiene un pulsante con spaziatura e bordo in stile Matrix.
     * Utilizza la classe MatrixStyleUtils per applicare il bordo.
     * @param button
     * @return Un JPanel contenente il pulsante con spaziatura.
     */
    private JPanel createButtonWithSpacing(JButton button) {
        JPanel panel = MatrixStyleUtils.createPanelWithMatrixBorder();
        panel.add(button);
        return panel;
    }
    /**
     * Configura gli handler degli eventi per i pulsanti della schermata del menu di gioco.
     * Associa azioni specifiche ai pulsanti per iniziare una nuova partita, caricare una partita salvata o uscire dal gioco.
     * Utilizza lambda expressions per semplificare la gestione degli eventi.
     * @see JButton
     * @see ActionListener
     * @see MatrixStyleUtils
     * @see SplashScreen
     */
    private void setupEventHandlers() {
        newGameButton.addActionListener(e -> newGame());
        loadGameButton.addActionListener(e -> loadGame());
        exitButton.addActionListener(e -> exitGame());
    }
    /**
     * Configura le proprietà della finestra principale della schermata del menu di gioco.
     *  Imposta il titolo, le dimensioni, la posizione e il comportamento di chiusura.
     *  Imposta la finestra come non ridimensionabile e aggiunge un KeyListener per gestire gli input da tastiera.
     */
    private void setupFrame() {
        setTitle("Game Menu - Matrix Style");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        addKeyListener(this);
        setFocusable(true);
    }
    /**
     * Avvia una nuova partita.
     * Anima il pulsante "Nuova Partita" e chiude la schermata del menu.
     * Apre la schermata di splash per iniziare una nuova partita.
     * @see SplashScreen
     * @see MatrixStyleUtils
     * @see JButton
     * @see JFrame
     */
    private void newGame() {
        MatrixStyleUtils.animateButton(newGameButton, MatrixStyleUtils.MATRIX_GREEN);
        dispose();

        new SplashScreen().setVisible(true);
    }
    /**
     * Carica una partita salvata.
     * Anima il pulsante "Carica Partita" e verifica la presenza di salvataggi.
     * Se sono presenti salvataggi, mostra un dialog per scegliere lo slot da caricare.
     * Carica la partita selezionata in un thread separato per evitare il blocco dell'interfaccia utente.
     * In caso di successo, apre la finestra di gioco con lo stato caricato.
     * In caso di errore, mostra un messaggio di errore.
     */
    private void loadGame() {
        MatrixStyleUtils.animateButton(loadGameButton, MatrixStyleUtils.MATRIX_GREEN_TRANSPARENT);

        List<Integer> occupati = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            if (GameSaver.esisteSalvataggio(i)) occupati.add(i);
        }
        if (occupati.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nessuna partita salvata trovata.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Dialog per scegliere slot
        Object[] options = occupati.stream().map(i -> "Slot " + i).toArray();
        int choice = JOptionPane.showOptionDialog(
                this,
                "Scegli lo slot da caricare:",
                "Carica Partita",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        if (choice < 0) return; // annullato
        final int slot = occupati.get(choice);

        new SwingWorker<StatoGioco, Void>() {
            @Override
            protected StatoGioco doInBackground() {
                return GameSaver.caricaPartita(slot);
            }

            @Override
            protected void done() {
                try {
                    StatoGioco s = get();
                    if (s == null) {
                        JOptionPane.showMessageDialog(GameMenuScreen.this, "Errore nel caricamento della partita.", "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    FinestraGioco gioco = FinestraGioco.createLoadedGameWindow(s,slot);
                    gioco.setVisible(true);
                    dispose();

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(GameMenuScreen.this, "Errore nel recupero della partita.", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }

        }.execute();
    }

    /**
     * Esce dal gioco.
     * Anima il pulsante "Esci" e mostra una conferma prima di chiudere l'applicazione.
     * Utilizza la classe GameExit per gestire la conferma e l'uscita.
     */
    private void exitGame() {
        MatrixStyleUtils.animateButton(exitButton, MatrixStyleUtils.DANGER_COLOR);
        org.example.utils.GameExit.confirmAndExit(this);
    }

    /**
     * Gestisce gli eventi di pressione dei tasti.
     * Permette di avviare una nuova partita, caricare una partita salvata o uscire dal gioco tramite i tasti 1, 2 e 3 (o ESC).
     * @param e L'evento di pressione del tasto.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_1:
                newGame();
                break;
            case KeyEvent.VK_2:
                loadGame();
                break;
            case KeyEvent.VK_3:
            case KeyEvent.VK_ESCAPE:
                exitGame();
                break;
        }
    }

    /**
     * Gestisce gli eventi di digitazione dei tasti.
     * @param e the event to be processed
     */
    @Override public void keyTyped(KeyEvent e) {}
    /**
     * Gestisce gli eventi di rilascio dei tasti.
     * @param e the event to be processed
     */
    @Override public void keyReleased(KeyEvent e) {}
}