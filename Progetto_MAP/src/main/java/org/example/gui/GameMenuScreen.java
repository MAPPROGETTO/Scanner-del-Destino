package org.example.gui;

import org.example.inventario.Inventario;
import org.example.mappa.Mappa;
import org.example.model.Lewis;
import org.example.model.StatoGioco;
import org.example.story.SceneLoader;
import org.example.story.SceneManager;
import org.example.story.StoryEngine;
import org.example.utils.GameSaver;
import org.example.utils.GestoreInputGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameMenuScreen extends JFrame implements KeyListener, Serializable {
    private JButton newGameButton;
    private JButton loadGameButton;
    private JButton exitButton;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private AnimatedPanel mainPanel;

    private transient List<MatrixStyleUtils.FloatingParticle> particles;
    private transient List<MatrixStyleUtils.MatrixChar> matrixChars;

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

    private JPanel createButtonWithSpacing(JButton button) {
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
    }

    private void newGame() {
        MatrixStyleUtils.animateButton(newGameButton, MatrixStyleUtils.MATRIX_GREEN);
        dispose();

        new SplashScreen().setVisible(true);
    }

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


    private void exitGame() {
        MatrixStyleUtils.animateButton(exitButton, MatrixStyleUtils.DANGER_COLOR);
        org.example.utils.GameExit.confirmAndExit(this);
    }


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

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}