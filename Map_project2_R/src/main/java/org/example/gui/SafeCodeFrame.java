package org.example.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SafeCodeFrame extends JPanel {
    // Colori del tuo tema
    public static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 250);
    public static final Color MATRIX_GREEN = new Color(0, 255, 65);
    public static final Color MATRIX_GREEN_TRANSPARENT = new Color(0, 255, 65, 150);
    public static final Color MATRIX_GREEN_LIGHT = new Color(0, 255, 65, 100);
    public static final Color MATRIX_GREEN_DARK = new Color(0, 255, 65, 50);
    public static final Color TEXT_COLOR = new Color(220, 220, 220);

    // Configurazione cassaforte
    private static final String CORRECT_CODE = "1337"; // Cambia questo codice
    private static final int CODE_LENGTH = 4;

    // Stato dell'interfaccia
    private String currentCode = "";
    private boolean isUnlocked = false;
    private boolean isAnimating = false;
    private int animationStep = 0;
    private Timer animationTimer;
    private Timer backgroundTimer;

    // Effetti visivi
    private List<MatrixEffect> matrixEffects;
    private Random random;
    private float scanLinePosition = 0;

    // Font
    private Font digitalFont;
    private Font titleFont;

    public SafeCodeFrame() {
        setPreferredSize(new Dimension(400, 500));
        setBackground(Color.BLACK);
        setFocusable(true);

        random = new Random();
        matrixEffects = new ArrayList<>();

        // Inizializza font
        try {
            digitalFont = new Font("Courier New", Font.BOLD, 24);
            titleFont = new Font("Courier New", Font.BOLD, 18);
        } catch (Exception e) {
            digitalFont = new Font(Font.MONOSPACED, Font.BOLD, 24);
            titleFont = new Font(Font.MONOSPACED, Font.BOLD, 18);
        }

        // Timer per effetti di background
        backgroundTimer = new Timer(50, e -> {
            updateMatrixEffects();
            scanLinePosition += 0.5f;
            if (scanLinePosition > getHeight()) scanLinePosition = 0;
            repaint();
        });
        backgroundTimer.start();

        // Timer per animazioni
        animationTimer = new Timer(100, e -> {
            if (isAnimating) {
                animationStep++;
                if (animationStep > 20) {
                    isAnimating = false;
                    animationStep = 0;
                }
                repaint();
            }
        });

        // Gestione input tastiera
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        // Genera effetti matrix iniziali
        for (int i = 0; i < 15; i++) {
            matrixEffects.add(new MatrixEffect());
        }
    }

    private void handleKeyPress(KeyEvent e) {
        if (isUnlocked || isAnimating) return;

        char keyChar = e.getKeyChar();

        if (Character.isDigit(keyChar) && currentCode.length() < CODE_LENGTH) {
            currentCode += keyChar;
            playInputSound();
            repaint();

            if (currentCode.length() == CODE_LENGTH) {
                checkCode();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && !currentCode.isEmpty()) {
            currentCode = currentCode.substring(0, currentCode.length() - 1);
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER && currentCode.length() == CODE_LENGTH) {
            checkCode();
        }
    }

    private void checkCode() {
        isAnimating = true;
        animationStep = 0;
        animationTimer.start();

        if (currentCode.equals(CORRECT_CODE)) {
            // Codice corretto
            Timer successTimer = new Timer(2000, e -> {
                isUnlocked = true;
                backgroundTimer.stop();
                repaint();
                // Qui puoi chiamare un callback per aprire la stanza segreta
                onCodeUnlocked();
                ((Timer)e.getSource()).stop();
            });
            successTimer.setRepeats(false);
            successTimer.start();
        } else {
            // Codice sbagliato
            Timer errorTimer = new Timer(1500, e -> {
                currentCode = "";
                repaint();
                ((Timer)e.getSource()).stop();
            });
            errorTimer.setRepeats(false);
            errorTimer.start();
        }
    }

    private void playInputSound() {
        // Qui puoi aggiungere un suono di input se necessario
        Toolkit.getDefaultToolkit().beep();
    }

    private void onCodeUnlocked() {
        // Callback quando il codice è corretto
        System.out.println("Accesso alla stanza segreta concesso!");
        // Puoi sostituire questo con la logica per aprire la stanza
    }

    private void updateMatrixEffects() {
        for (MatrixEffect effect : matrixEffects) {
            effect.update();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Abilita antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Effetti matrix di background
        drawMatrixEffects(g2d);

        // Scanline effect
        drawScanLine(g2d);

        if (isUnlocked) {
            drawUnlockedScreen(g2d);
        } else {
            drawSafeInterface(g2d);
        }

        g2d.dispose();
    }

    private void drawMatrixEffects(Graphics2D g2d) {
        for (MatrixEffect effect : matrixEffects) {
            effect.draw(g2d);
        }
    }

    private void drawScanLine(Graphics2D g2d) {
        g2d.setColor(MATRIX_GREEN_DARK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(0, (int)scanLinePosition, getWidth(), (int)scanLinePosition);
    }

    private void drawSafeInterface(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Titolo
        g2d.setFont(titleFont);
        g2d.setColor(MATRIX_GREEN);
        String title = "ACCESSO RISTRETTO";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, centerX - fm.stringWidth(title) / 2, centerY - 120);

        // Sottotitolo
        g2d.setColor(TEXT_COLOR);
        String subtitle = "Inserire codice di sicurezza";
        g2d.drawString(subtitle, centerX - fm.stringWidth(subtitle) / 2, centerY - 90);

        // Display del codice
        drawCodeDisplay(g2d, centerX, centerY - 40);

        // Tastierino numerico virtuale
        drawKeypad(g2d, centerX, centerY + 40);

        // Messaggio di stato
        drawStatusMessage(g2d, centerX, centerY + 180);
    }

    private void drawCodeDisplay(Graphics2D g2d, int centerX, int centerY) {
        int boxWidth = 200;
        int boxHeight = 40;
        int x = centerX - boxWidth / 2;
        int y = centerY - boxHeight / 2;

        // Box principale
        g2d.setColor(MATRIX_GREEN_DARK);
        g2d.fillRect(x, y, boxWidth, boxHeight);
        g2d.setColor(MATRIX_GREEN);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, boxWidth, boxHeight);

        // Codice inserito
        g2d.setFont(digitalFont);
        g2d.setColor(MATRIX_GREEN);
        String displayCode = "";
        for (int i = 0; i < CODE_LENGTH; i++) {
            if (i < currentCode.length()) {
                displayCode += "*";
            } else {
                displayCode += "_";
            }
            if (i < CODE_LENGTH - 1) displayCode += " ";
        }

        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(displayCode, centerX - fm.stringWidth(displayCode) / 2, centerY + 8);

        // Effetto di animazione durante il controllo
        if (isAnimating) {
            Color animColor = currentCode.equals(CORRECT_CODE) ? MATRIX_GREEN : Color.RED;
            int alpha = (int)(Math.sin(animationStep * 0.5) * 127 + 128);
            g2d.setColor(new Color(animColor.getRed(), animColor.getGreen(), animColor.getBlue(), alpha));
            g2d.fillRect(x, y, boxWidth, boxHeight);
        }
    }

    private void drawKeypad(Graphics2D g2d, int centerX, int centerY) {
        g2d.setColor(MATRIX_GREEN_TRANSPARENT);
        g2d.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));

        String[] keys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#"};
        int buttonSize = 30;
        int spacing = 5;
        int startX = centerX - (3 * buttonSize + 2 * spacing) / 2;
        int startY = centerY;

        for (int i = 0; i < keys.length; i++) {
            int row = i / 3;
            int col = i % 3;
            int x = startX + col * (buttonSize + spacing);
            int y = startY + row * (buttonSize + spacing);

            g2d.drawRect(x, y, buttonSize, buttonSize);

            FontMetrics fm = g2d.getFontMetrics();
            String key = keys[i];
            g2d.drawString(key, x + buttonSize/2 - fm.stringWidth(key)/2, y + buttonSize/2 + fm.getAscent()/2);
        }
    }

    private void drawStatusMessage(Graphics2D g2d, int centerX, int centerY) {
        g2d.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        String message;
        Color color;

        if (isAnimating) {
            if (currentCode.equals(CORRECT_CODE)) {
                message = "ACCESSO CONCESSO";
                color = MATRIX_GREEN;
            } else {
                message = "ACCESSO NEGATO";
                color = Color.RED;
            }
        } else {
            message = "Usa i tasti numerici per inserire il codice";
            color = TEXT_COLOR;
        }

        g2d.setColor(color);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(message, centerX - fm.stringWidth(message) / 2, centerY);
    }

    private void drawUnlockedScreen(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Sfondo verde success
        g2d.setColor(MATRIX_GREEN_DARK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Messaggio di successo
        g2d.setFont(titleFont);
        g2d.setColor(MATRIX_GREEN);
        String successMsg = "ACCESSO AUTORIZZATO";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(successMsg, centerX - fm.stringWidth(successMsg) / 2, centerY - 20);

        g2d.setColor(TEXT_COLOR);
        String enterMsg = "Benvenuto nella stanza segreta";
        g2d.drawString(enterMsg, centerX - fm.stringWidth(enterMsg) / 2, centerY + 20);
    }

    // Classe per gli effetti matrix di background
    private class MatrixEffect {
        private int x, y;
        private char character;
        private float alpha;
        private float speed;

        public MatrixEffect() {
            reset();
        }

        private void reset() {
            x = random.nextInt(getWidth());
            y = random.nextInt(getHeight());
            character = (char)('0' + random.nextInt(10));
            alpha = random.nextFloat() * 0.5f + 0.1f;
            speed = random.nextFloat() * 2 + 0.5f;
        }

        public void update() {
            y += speed;
            alpha -= 0.01f;

            if (y > getHeight() || alpha <= 0) {
                reset();
            }
        }

        public void draw(Graphics2D g2d) {
            g2d.setColor(new Color(MATRIX_GREEN.getRed(), MATRIX_GREEN.getGreen(),
                    MATRIX_GREEN.getBlue(), (int)(alpha * 255)));
            g2d.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
            g2d.drawString(String.valueOf(character), x, y);
        }
    }
}