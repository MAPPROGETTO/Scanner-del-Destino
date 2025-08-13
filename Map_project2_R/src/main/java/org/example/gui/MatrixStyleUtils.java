package org.example.gui;

import org.example.inventario.Inventario;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe utility per centralizzare tutti gli stili e metodi comuni
 * utilizzati nelle GUI con tema Matrix
 */
public class MatrixStyleUtils{

    // Palette colori Matrix-style
    public static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 250);
    public static final Color MATRIX_GREEN = new Color(0, 255, 65);
    public static final Color MATRIX_GREEN_TRANSPARENT = new Color(0, 255, 65, 150);
    public static final Color MATRIX_GREEN_LIGHT = new Color(0, 255, 65, 100);
    public static final Color MATRIX_GREEN_DARK = new Color(0, 255, 65, 50);
    public static final Color TEXT_COLOR = new Color(220, 220, 220);
    public static final Color DARK_BACKGROUND = new Color(30, 30, 30, 200);
    public static final Color MEDIUM_DARK = new Color(40, 40, 40, 200);
    public static final Color BUTTON_BACKGROUND = new Color(80, 80, 80, 200);
    public static final Color GRADIENT_START = new Color(26, 26, 26);
    public static final Color GRADIENT_END = new Color(45, 45, 45);
    public static final Color DANGER_COLOR = new Color(255, 0, 0, 120);

    // Font comuni
    public static final Font MONOSPACE_FONT = new Font("Consolas", Font.PLAIN, 14);
    public static final Font MONOSPACE_BOLD = new Font("Consolas", Font.BOLD, 16);
    public static final Font TITLE_FONT = new Font("Consolas", Font.BOLD, 32);
    public static final Font COURIER_FONT = new Font("Courier New", Font.PLAIN, 12);
    public static final Font COURIER_BOLD = new Font("Courier New", Font.BOLD, 16);
    public static final Font COURIER_TITLE = new Font("Courier New", Font.BOLD, 36);

    // Caratteri Matrix
    public static final String MATRIX_CHARS = "01アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン";

    /**
     * Crea un pulsante con stile Matrix standard
     */
    public static JButton createStyledButton(String text, ImageIcon icon) {

        JButton button = new JButton(text, icon);
        button.setFont(new Font("Consolas", Font.PLAIN, 12));
        button.setBackground(new Color(80, 80, 80, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 65, 100), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0, 255, 65, 50));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(80, 80, 80, 200));
            }
        });

        return button;
    }

    public static JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = isHovered
                        ? new Color(color.getRed(), color.getGreen(), color.getBlue(), 100)
                        : new Color(color.getRed(), color.getGreen(), color.getBlue(), 50);
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);

                if (isHovered) {
                    g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                    g2d.setStroke(new BasicStroke(4));
                    g2d.drawRoundRect(-1, -1, getWidth() + 2, getHeight() + 2, 12, 12);
                }

                g2d.dispose();
                super.paintComponent(g);
            }

            {
                setForeground(color);
                setFont(COURIER_BOLD);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }
        };
        button.setPreferredSize(new Dimension(300, 80));
        return button;
    }


    public static JPanel bottomCombinedPanel(JPanel inputPanel, JPanel continuaPanel) {
        JPanel bottomCombinedPanel = new JPanel(new BorderLayout());
        bottomCombinedPanel.setOpaque(false);
        bottomCombinedPanel.add(inputPanel, BorderLayout.NORTH);
        bottomCombinedPanel.add(continuaPanel, BorderLayout.SOUTH);
        return bottomCombinedPanel;
    }

    /**
     * Aggiunge effetto hover a un componente
     */
    public static void addHoverEffect(JButton button, Color hoverColor, Color normalColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
    }

    /**
     * Crea un bordo con stile matrix
     */
    public static Border createMatrixBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MATRIX_GREEN_LIGHT, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
    }

    /**
     * Crea un bordo Matrix più spesso
     */
    public static Border createThickMatrixBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 65, 200), 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
    }

    /**
     * Crea una JTextArea con stile Matrix
     */
    public static JTextArea createStyledTextArea() {
        Color testo = new Color(220, 220, 220);
        Font fontMonospace = new Font("Consolas", Font.PLAIN, 14);

        JTextArea areaTesto = new JTextArea();
        areaTesto.setEditable(false);
        areaTesto.setLineWrap(true);
        areaTesto.setWrapStyleWord(true);
        areaTesto.setBackground(new Color(30, 30, 30, 200));
        areaTesto.setForeground(testo);
        areaTesto.setFont(fontMonospace);
        areaTesto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 65, 100), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return areaTesto;
    }

    /**
     * Crea un JTextField con stile Matrix
     */
    public static JTextField createStyledTextField() {
        JTextField inputField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(180, 255, 200, 120));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString("scrivi qui", 18, getHeight() / 2 + getFont().getSize() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        inputField.setFont(MONOSPACE_FONT);
        inputField.setBackground(MEDIUM_DARK);
        inputField.setForeground(TEXT_COLOR);
        inputField.setCaretColor(MATRIX_GREEN);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MATRIX_GREEN_LIGHT, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return inputField;
    }

    /**
     * Crea un JScrollPane trasparente con stile Matrix
     */
    public static JScrollPane createTransparentScrollPane(JComponent component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        return scrollPane;
    }

    /**
     * Applica uno sfondo gradiente Matrix a un Graphics2D
     */
    public static void applyMatrixGradient(Graphics2D g2d, int width, int height) {
        GradientPaint gradient = new GradientPaint(
                0, 0, GRADIENT_START,
                width, height, GRADIENT_END
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
    }

    /**
     * Applica uno sfondo gradiente Matrix alternativo
     */
    public static void applyAlternativeGradient(Graphics2D g2d, int width, int height) {
        GradientPaint gradient = new GradientPaint(
                0, 0, BACKGROUND_COLOR,
                0, height, new Color(0, 50, 20, 250)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
    }

    /**
     * Disegna l'effetto Matrix (caratteri che cadono)
     */
    public static void drawMatrixEffect(Graphics2D g2d, int width, int height) {
        Random random = new Random();
        g2d.setFont(COURIER_FONT);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        for (int i = 0; i < 15; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int alpha = random.nextInt(100) + 50;
            g2d.setColor(new Color(0, 255, 65, alpha));
            char c = MATRIX_CHARS.charAt(random.nextInt(MATRIX_CHARS.length()));
            g2d.drawString(String.valueOf(c), x, y);
        }
    }

    /**
     * Crea una lista di particelle per animazioni
     */
    public static List<FloatingParticle> createFloatingParticles(int count, int width, int height) {
        // Controllo di validità delle dimensioni
        if (width <= 0 || height <= 0) {
            return new ArrayList<>(); // Ritorna lista vuota se dimensioni non valide
        }

        Random random = new Random();
        List<FloatingParticle> particles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            particles.add(new FloatingParticle(
                    random.nextInt(width),
                    random.nextInt(height),
                    random.nextDouble() * 2 + 1,
                    new Color(0, 255, 65, random.nextInt(100) + 50)
            ));
        }
        return particles;
    }

    /**
     * Aggiorna le particelle fluttuanti - VERSIONE CORRETTA
     */
    public static void updateFloatingParticles(List<FloatingParticle> particles, int width, int height) {
        // Controllo di validità delle dimensioni
        if (width <= 0 || height <= 0) {
            return; // Esci early se le dimensioni non sono valide
        }

        Random random = new Random();
        for (FloatingParticle particle : particles) {
            particle.update();

            // Riposiziona particelle che escono dallo schermo
            if (particle.y < 0) {
                particle.y = height;
                particle.x = random.nextInt(width); // Ora width è garantito essere > 0
            }
        }
    }

    /**
     * Disegna le particelle fluttuanti
     */
    public static void drawFloatingParticles(Graphics2D g2d, List<FloatingParticle> particles) {
        for (FloatingParticle particle : particles) {
            g2d.setColor(particle.color);
            g2d.fillOval((int) particle.x, (int) particle.y, 3, 3);

            // Effetto luminoso
            g2d.setColor(new Color(0, 255, 65, 20));
            g2d.fillOval((int) particle.x - 2, (int) particle.y - 2, 7, 7);
        }
    }

    /**
     * Crea una lista di caratteri Matrix per animazioni
     */
    public static List<MatrixChar> createMatrixChars(int count, int width, int height) {
        // Controllo di validità delle dimensioni
        if (width <= 0 || height <= 0) {
            return new ArrayList<>(); // Ritorna lista vuota se dimensioni non valide
        }

        Random random = new Random();
        List<MatrixChar> matrixChars = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            matrixChars.add(new MatrixChar(
                    random.nextInt(width),
                    random.nextInt(height),
                    random.nextDouble() * 0.8 + 0.2
            ));
        }
        return matrixChars;
    }

    /**
     * Aggiorna i caratteri Matrix
     */
    public static void updateMatrixChars(List<MatrixChar> matrixChars, int width, int height) {
        // Controllo di validità delle dimensioni (già presente ma importante)
        if (width <= 0 || height <= 0) return;

        Random random = new Random();
        for (MatrixChar ch : matrixChars) {
            ch.update();
            if (ch.y > height) {
                ch.y = -50;
                ch.x = random.nextInt(width); // Anche qui width è garantito essere > 0
            }
        }
    }

    public static Border createBorder(String titolo) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GREEN, 2),
                titolo,
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Courier", Font.BOLD, 16),
                Color.GREEN
        );
    }

    /**
     * Disegna i caratteri Matrix
     */
    public static void drawMatrixChars(Graphics2D g2d, List<MatrixChar> matrixChars) {
        g2d.setFont(new Font("Consolas", Font.PLAIN, 12));
        for (MatrixChar ch : matrixChars) {
            g2d.setColor(new Color(0, 255, 65, ch.alpha));
            g2d.drawString(ch.character, (int) ch.x, (int) ch.y);
        }
    }

    /**
     * Crea un pannello con bordo Matrix
     */
    public static JPanel createPanelWithMatrixBorder() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Animazione semplice del pulsante
     */
    public static void animateButton(JButton button, Color originalColor) {
        Timer timer = new Timer(100, new java.awt.event.ActionListener() {
            private int count = 0;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                button.setForeground(count % 2 == 0 ? Color.WHITE : originalColor);
                if (++count >= 4) ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

    public static JPanel createTopPanelWithMatrixBorder(JButton mappaButton, JButton inventarioButton) {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        topPanel.add(inventarioButton);
        topPanel.add(mappaButton);
        return topPanel;
    }

    public static InventarioPanel createInventarioPanel(Inventario inventario, boolean inventarioVisibile) {
        InventarioPanel inventarioPanel = new InventarioPanel(inventario);
        inventarioPanel.setVisible(inventarioVisibile);
        inventarioPanel.setPreferredSize(new Dimension(250, 400)); // Adjust size as needed
        inventarioPanel.aggiornaInventario();
        return inventarioPanel;
    }

    // Classe interna per particelle fluttuanti
    public static class FloatingParticle{
        public double x, y, speed;
        public Color color;

        public FloatingParticle(double x, double y, double speed, Color color) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.color = color;
        }

        public void update() {
            y -= speed;
        }
    }

    // Classe interna per caratteri Matrix
    public static class MatrixChar {
        public double x, y, speed;
        public String character;
        public int alpha;

        private final Random random = new Random();

        public MatrixChar(double x, double y, double speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.character = randomChar();
            this.alpha = random.nextInt(100) + 100;
        }

        public void update() {
            y += speed;
            if (random.nextDouble() < 0.05) {
                character = randomChar();
            }
        }

        private String randomChar() {
            return String.valueOf(MATRIX_CHARS.charAt(random.nextInt(MATRIX_CHARS.length())));
        }
    }
}
