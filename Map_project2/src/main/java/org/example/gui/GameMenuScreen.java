package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public class GameMenuScreen extends JFrame implements KeyListener {
    // Componenti UI
    private JButton newGameButton;
    private JButton loadGameButton;
    private JButton exitButton;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private AnimatedPanel mainPanel;

    // Particelle e caratteri per animazioni
    private List<MatrixStyleUtils.FloatingParticle> particles;
    private List<MatrixStyleUtils.MatrixChar> matrixChars;

    public GameMenuScreen() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();

        // Timer per animare l'effetto Matrix
        Timer matrixTimer = new Timer(100, e -> {
            MatrixStyleUtils.updateFloatingParticles(particles, getWidth(), getHeight());
            MatrixStyleUtils.updateMatrixChars(matrixChars, getWidth(), getHeight());
            mainPanel.repaint();
        });
        matrixTimer.start();
    }

    private void initializeComponents() {
        // Inizializza particelle e caratteri Matrix
        particles = MatrixStyleUtils.createFloatingParticles(20, 1000, 800);
        matrixChars = MatrixStyleUtils.createMatrixChars(30, 1000, 800);

        // Titolo principale
        titleLabel = new JLabel("GAME MENU", SwingConstants.CENTER);
        titleLabel.setFont(MatrixStyleUtils.COURIER_TITLE);
        titleLabel.setForeground(MatrixStyleUtils.MATRIX_GREEN);

        // Sottotitolo
        subtitleLabel = new JLabel("Seleziona un'opzione per continuare", SwingConstants.CENTER);
        subtitleLabel.setFont(MatrixStyleUtils.COURIER_FONT);
        subtitleLabel.setForeground(MatrixStyleUtils.MATRIX_GREEN_TRANSPARENT);

        // Pulsanti del menu utilizzando MatrixStyleUtils
        newGameButton = MatrixStyleUtils.createStyledButton("NUOVA PARTITA", MatrixStyleUtils.MATRIX_GREEN);
        loadGameButton = MatrixStyleUtils.createStyledButton("CARICA PARTITA", MatrixStyleUtils.MATRIX_GREEN_TRANSPARENT);
        exitButton = MatrixStyleUtils.createStyledButton("ESCI", MatrixStyleUtils.DANGER_COLOR);

        // Panel principale con effetti Matrix avanzati
        mainPanel = new AnimatedPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                // Applica sfondo gradiente Matrix
                MatrixStyleUtils.applyAlternativeGradient(g2d, getWidth(), getHeight());

                // Disegna particelle fluttuanti
                MatrixStyleUtils.drawFloatingParticles(g2d, particles);

                // Disegna caratteri Matrix animati
                MatrixStyleUtils.drawMatrixChars(g2d, matrixChars);

                // Effetto Matrix aggiuntivo (statico)
                MatrixStyleUtils.drawMatrixEffect(g2d, getWidth(), getHeight());

                g2d.dispose();
            }
        };

        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(MatrixStyleUtils.BACKGROUND_COLOR);
    }

    private void setupLayout() {
        // Panel del titolo
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(50, 10, 30, 10));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Panel dei pulsanti utilizzando il metodo della utility
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 50, 0));

        // Aggiunta pulsanti con spaziatura
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(createButtonWithSpacing(newGameButton));
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(createButtonWithSpacing(loadGameButton));
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(createButtonWithSpacing(exitButton));
        buttonPanel.add(Box.createVerticalGlue());

        // Assemblaggio finale
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Panel con bordo Matrix utilizzando la utility
        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.setBackground(Color.BLACK);
        borderPanel.setBorder(MatrixStyleUtils.createThickMatrixBorder());
        borderPanel.add(mainPanel, BorderLayout.CENTER);

        add(borderPanel);
    }

    private JPanel createButtonWithSpacing(JButton button) {
        // Utilizza il metodo della utility per creare panel con bordo Matrix
        JPanel panel = MatrixStyleUtils.createPanelWithMatrixBorder();
        panel.add(button);
        return panel;
    }

    private void setupEventHandlers() {
        newGameButton.addActionListener(e -> newGame());

        loadGameButton.addActionListener(e -> loadGame());

        exitButton.addActionListener(e -> exitGame());
    }

    private void setupFrame() {
        setTitle("Game Menu - Matrix Style");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        addKeyListener(this);
        setFocusable(true);

        // Icona della finestra (opzionale)
        try {
            // setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        } catch (Exception e) {
            // Icona non trovata, usa default
        }
    }

    // Metodi di gestione azioni
    private void newGame() {
        // Utilizza l'animazione della utility
        MatrixStyleUtils.animateButton(newGameButton, MatrixStyleUtils.MATRIX_GREEN);
        dispose();
        new SplashScreen().setVisible(true); // Apri la schermata di caricamento
    }

    private void loadGame() {
        // Utilizza l'animazione della utility
        MatrixStyleUtils.animateButton(loadGameButton, MatrixStyleUtils.MATRIX_GREEN_TRANSPARENT);

        // Simulazione dialog di caricamento
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleziona file di salvataggio");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "File di salvataggio (*.sav)", "sav"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Caricando partita: " + fileChooser.getSelectedFile().getName(),
                    "Carica Partita",
                    JOptionPane.INFORMATION_MESSAGE);

            // Qui inserire la logica per caricare la partita
        }
    }

    private void exitGame() {
        // Utilizza l'animazione della utility
        MatrixStyleUtils.animateButton(exitButton, MatrixStyleUtils.DANGER_COLOR);

        int choice = JOptionPane.showConfirmDialog(this,
                "Sei sicuro di voler uscire?",
                "Conferma Uscita",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.NO_OPTION) {
            return; // Non uscire se l'utente sceglie NO
        }

        System.exit(0);
    }

    // Implementazione KeyListener per navigazione da tastiera
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

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}