package org.example.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.border.Border;

public class GameMenuScreen extends JFrame implements KeyListener {

    // Palette colori Matrix-style
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 250);
    private static final Color DOOR_COLOR = new Color(0, 255, 65, 150);
    private static final Color PLAYER_COLOR = new Color(0, 255, 65);
    private static final Color BORDER_COLOR = new Color(0, 255, 65, 200);
    private static final Color DANGER_COLOR = new Color(255, 0, 0, 120);

    // Componenti UI
    private JButton newGameButton;
    private JButton loadGameButton;
    private JButton exitButton;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private AnimatedPanel mainPanel;

    public GameMenuScreen() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();

        // Timer per animare l'effetto Matrix
        Timer matrixTimer = new Timer(500, e ->
                mainPanel.repaint());
        matrixTimer.start();
    }

    private void initializeComponents() {
        // Pannello di sfondo animato


        // Titolo principale
        titleLabel = new JLabel("GAME MENU", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Courier New", Font.BOLD, 36));
        titleLabel.setForeground(PLAYER_COLOR);

        // Sottotitolo
        subtitleLabel = new JLabel("Seleziona un'opzione per continuare", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Courier New", Font.PLAIN, 14));
        subtitleLabel.setForeground(DOOR_COLOR);

        // Pulsanti del menu
        newGameButton = createStyledButton("NUOVA PARTITA", PLAYER_COLOR);
        loadGameButton = createStyledButton("CARICA PARTITA", DOOR_COLOR);
        exitButton = createStyledButton("ESCI", DANGER_COLOR);

        // Panel principale
        mainPanel = new AnimatedPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                // Sfondo gradiente
                GradientPaint gradient = new GradientPaint(
                        0, 0, BACKGROUND_COLOR,
                        0, getHeight(), new Color(0, 50, 20, 250)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Effetto Matrix - caratteri che cadono
                drawMatrixEffect(g2d);

                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Sfondo del pulsante
                Color bgColor = isHovered ?
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 100) :
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 50);

                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Bordo
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);

                // Effetto glow se hover
                if (isHovered) {
                    g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                    g2d.setStroke(new BasicStroke(4));
                    g2d.drawRoundRect(-1, -1, getWidth()+2, getHeight()+2, 12, 12);
                }

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Courier New", Font.BOLD, 16));
        button.setForeground(color);
        button.setPreferredSize(new Dimension(250, 50));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effetti hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                ((JButton)e.getSource()).putClientProperty("isHovered", true);
                button.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                ((JButton)e.getSource()).putClientProperty("isHovered", false);
                button.repaint();
            }
        });

        return button;
    }

    private void setupLayout() {
        // Panel del titolo
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(50, 10, 30, 10));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Panel dei pulsanti
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

        // Panel con bordo Matrix
        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.setBackground(Color.BLACK);
        borderPanel.setBorder(createMatrixBorder());
        borderPanel.add(mainPanel, BorderLayout.CENTER);

        add(borderPanel);
    }

    private JPanel createButtonWithSpacing(JButton button) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.add(button);
        return panel;
    }

    private Border createMatrixBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
    }

    private void drawMatrixEffect(Graphics2D g2d) {
        g2d.setFont(new Font("Courier New", Font.PLAIN, 12));
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Caratteri Matrix che cadono (effetto statico per semplicità)
        String matrixChars = "01アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン";

        for (int i = 0; i < 15; i++) {
            int x = (int) (Math.random() * getWidth());
            int y = (int) (Math.random() * getHeight());

            // Colore sfumato per effetto profondità
            int alpha = (int) (Math.random() * 100) + 50;
            g2d.setColor(new Color(0, 255, 65, alpha));

            char c = matrixChars.charAt((int) (Math.random() * matrixChars.length()));
            g2d.drawString(String.valueOf(c), x, y);
        }
    }

    private void setupEventHandlers() {
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newGame();
            }
        });

        loadGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadGame();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitGame();
            }
        });
    }

    private void setupFrame() {
        setTitle("Game Menu");
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
        animateButton(newGameButton);// Animazione del pulsante
        JOptionPane.showMessageDialog(this,
                "Iniziando nuova partita...",
                "Nuova Partita",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new SplashScreen().setVisible(true); // Apri la schermata di caricamento
    }

    private void loadGame() {
        animateButton(loadGameButton);

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
        animateButton(exitButton);

        int choice = JOptionPane.showConfirmDialog(this,
                "Sei sicuro di voler uscire?",
                "Conferma Uscita",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void animateButton(JButton button) {
        // Animazione semplice del pulsante
        Timer timer = new Timer(100, new ActionListener() {
            private int count = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (count % 2 == 0) {
                    button.setForeground(Color.WHITE);
                } else {
                    button.setForeground(button == exitButton ? DANGER_COLOR :
                            button == loadGameButton ? DOOR_COLOR : PLAYER_COLOR);
                }
                count++;
                if (count >= 4) {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
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

