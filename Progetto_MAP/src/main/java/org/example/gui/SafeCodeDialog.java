package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Dialog modale con tastierino per inserire un codice numerico.
 * Uso:
 *   SafeCodeDialog.mostra(parentWindow, "4913", () -> {/* on unlock *\/});
 */
public final class SafeCodeDialog {

    private SafeCodeDialog() {}

    public static void mostra(Window parent, String correctCode, Runnable onUnlock) {
        JDialog dlg = new JDialog(parent, "Tastierino", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dlg.setSize(420, 520);
        dlg.setResizable(false);
        dlg.setLocationRelativeTo(parent);

        SafeCodePanel panel = new SafeCodePanel(correctCode, () -> {
            if (onUnlock != null) onUnlock.run();
            dlg.dispose();
        });
        dlg.setContentPane(panel);

        // focus
        dlg.addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { panel.requestFocusInWindow(); }
        });

        dlg.setVisible(true);
    }

    // ----------------- UI PANEL (adattato dalla tua classe) -----------------
    private static class SafeCodePanel extends JPanel {
        // Colori stile Matrix
        private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 250);
        private static final Color MATRIX_GREEN = new Color(0, 255, 65);
        private static final Color MATRIX_GREEN_TRANSPARENT = new Color(0, 255, 65, 150);
        private static final Color MATRIX_GREEN_DARK = new Color(0, 255, 65, 50);
        private static final Color TEXT_COLOR = new Color(220, 220, 220);

        private final String CORRECT_CODE;
        private final int CODE_LENGTH;

        private String currentCode = "";
        private boolean isUnlocked = false;
        private boolean isAnimating = false;
        private int animationStep = 0;
        private final Timer animationTimer;
        private final Timer backgroundTimer;

        private final List<MatrixEffect> matrixEffects = new ArrayList<>();
        private final Random random = new Random();
        private float scanLinePosition = 0;

        private Font digitalFont;
        private Font titleFont;

        private final Runnable onUnlockCb;

        SafeCodePanel(String correctCode, Runnable onUnlockCb) {
            this.CORRECT_CODE = correctCode != null ? correctCode : "";
            this.CODE_LENGTH = this.CORRECT_CODE.length() > 0 ? this.CORRECT_CODE.length() : 4;
            this.onUnlockCb = onUnlockCb;

            setPreferredSize(new Dimension(400, 500));
            setBackground(Color.BLACK);
            setFocusable(true);

            // font
            try {
                digitalFont = new Font("Courier New", Font.BOLD, 24);
                titleFont = new Font("Courier New", Font.BOLD, 18);
            } catch (Exception e) {
                digitalFont = new Font(Font.MONOSPACED, Font.BOLD, 24);
                titleFont = new Font(Font.MONOSPACED, Font.BOLD, 18);
            }

            // timers
            backgroundTimer = new Timer(50, e -> {
                updateMatrixEffects();
                scanLinePosition += 0.5f;
                if (scanLinePosition > getHeight()) scanLinePosition = 0;
                repaint();
            });
            backgroundTimer.start();

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

            // tastiera fisica
            addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) { handleKeyPress(e); }
            });

            // clic sul tastierino virtuale (0-9; * = backspace, # = enter)
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    String key = hitKey(e.getPoint());
                    if (key == null) return;
                    if (key.matches("\\d")) {
                        typeDigit(key.charAt(0));
                    } else if ("*".equals(key)) {
                        backspace();
                    } else if ("#".equals(key)) {
                        if (currentCode.length() == CODE_LENGTH) checkCode();
                    }
                }
            });

            // effetti matrix iniziali
            for (int i = 0; i < 15; i++) matrixEffects.add(new MatrixEffect());
        }

        private void handleKeyPress(KeyEvent e) {
            if (isUnlocked || isAnimating) return;
            char ch = e.getKeyChar();
            if (Character.isDigit(ch) && currentCode.length() < CODE_LENGTH) {
                typeDigit(ch);
            } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                backspace();
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER && currentCode.length() == CODE_LENGTH) {
                checkCode();
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                SwingUtilities.getWindowAncestor(this).dispose();
            }
        }

        private void typeDigit(char d) {
            currentCode += d;
            beep();
            repaint();
            if (currentCode.length() == CODE_LENGTH) checkCode();
        }

        private void backspace() {
            if (!currentCode.isEmpty()) {
                currentCode = currentCode.substring(0, currentCode.length() - 1);
                repaint();
            }
        }

        private void checkCode() {
            isAnimating = true;
            animationStep = 0;
            animationTimer.start();

            if (currentCode.equals(CORRECT_CODE)) {
                Timer successTimer = new Timer(700, e -> {
                    isUnlocked = true;
                    backgroundTimer.stop();
                    repaint();
                    ((Timer) e.getSource()).stop();
                    // chiama callback e chiudi
                    if (onUnlockCb != null) onUnlockCb.run();
                });
                successTimer.setRepeats(false);
                successTimer.start();
            } else {
                Timer errorTimer = new Timer(700, e -> {
                    currentCode = "";
                    repaint();
                    ((Timer) e.getSource()).stop();
                });
                errorTimer.setRepeats(false);
                errorTimer.start();
            }
        }

        private void beep() { Toolkit.getDefaultToolkit().beep(); }

        private void updateMatrixEffects() {
            for (MatrixEffect effect : matrixEffects) effect.update(getWidth(), getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2d.setColor(BACKGROUND_COLOR);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            drawMatrixEffects(g2d);
            drawScanLine(g2d);

            if (isUnlocked) drawUnlockedScreen(g2d);
            else drawSafeInterface(g2d);

            g2d.dispose();
        }

        private void drawMatrixEffects(Graphics2D g2d) {
            for (MatrixEffect effect : matrixEffects) effect.draw(g2d);
        }

        private void drawScanLine(Graphics2D g2d) {
            g2d.setColor(MATRIX_GREEN_DARK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(0, (int) scanLinePosition, getWidth(), (int) scanLinePosition);
        }

        private void drawSafeInterface(Graphics2D g2d) {
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            g2d.setFont(titleFont);
            g2d.setColor(MATRIX_GREEN);
            String title = "ACCESSO RISTRETTO";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(title, centerX - fm.stringWidth(title) / 2, centerY - 120);

            g2d.setColor(TEXT_COLOR);
            String subtitle = "Inserire codice di sicurezza";
            g2d.drawString(subtitle, centerX - fm.stringWidth(subtitle) / 2, centerY - 90);

            drawCodeDisplay(g2d, centerX, centerY - 40);
            drawKeypad(g2d, centerX, centerY + 40);
            drawStatusMessage(g2d, centerX, centerY + 180);
        }

        private void drawCodeDisplay(Graphics2D g2d, int centerX, int centerY) {
            int boxWidth = 200, boxHeight = 40;
            int x = centerX - boxWidth / 2;
            int y = centerY - boxHeight / 2;

            g2d.setColor(MATRIX_GREEN_DARK);
            g2d.fillRect(x, y, boxWidth, boxHeight);
            g2d.setColor(MATRIX_GREEN);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(x, y, boxWidth, boxHeight);

            g2d.setFont(digitalFont);
            g2d.setColor(MATRIX_GREEN);
            StringBuilder display = new StringBuilder();
            for (int i = 0; i < CODE_LENGTH; i++) {
                display.append(i < currentCode.length() ? '*' : '_');
                if (i < CODE_LENGTH - 1) display.append(' ');
            }
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(display.toString(), centerX - fm.stringWidth(display.toString()) / 2, centerY + 8);

            if (isAnimating) {
                Color animColor = currentCode.equals(CORRECT_CODE) ? MATRIX_GREEN : Color.RED;
                int alpha = (int) (Math.sin(animationStep * 0.5) * 127 + 128);
                g2d.setColor(new Color(animColor.getRed(), animColor.getGreen(), animColor.getBlue(), alpha));
                g2d.fillRect(x, y, boxWidth, boxHeight);
            }
        }

        // layout tastierino 3x4: 1..9, *,0,# — includiamo click hit-test
        private Rectangle[][] keypadCells;
        private void drawKeypad(Graphics2D g2d, int centerX, int centerY) {
            g2d.setColor(MATRIX_GREEN_TRANSPARENT);
            g2d.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));

            String[] keys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#"};
            int buttonSize = 30, spacing = 5;
            int startX = centerX - (3 * buttonSize + 2 * spacing) / 2;
            int startY = centerY;

            if (keypadCells == null) keypadCells = new Rectangle[4][3];

            FontMetrics fm = g2d.getFontMetrics();
            for (int i = 0; i < keys.length; i++) {
                int row = i / 3, col = i % 3;
                int x = startX + col * (buttonSize + spacing);
                int y = startY + row * (buttonSize + spacing);
                g2d.drawRect(x, y, buttonSize, buttonSize);
                String key = keys[i];
                g2d.drawString(key, x + buttonSize/2 - fm.stringWidth(key)/2, y + buttonSize/2 + fm.getAscent()/2 - 4);
                keypadCells[row][col] = new Rectangle(x, y, buttonSize, buttonSize);
            }
        }

        private String hitKey(Point p) {
            if (keypadCells == null) return null;
            String[] keys = {"1","2","3","4","5","6","7","8","9","*","0","#"};
            int idx = 0;
            for (int r = 0; r < keypadCells.length; r++) {
                for (int c = 0; c < keypadCells[r].length; c++) {
                    if (keypadCells[r][c] != null && keypadCells[r][c].contains(p)) {
                        return keys[idx];
                    }
                    idx++;
                }
            }
            return null;
        }

        private void drawStatusMessage(Graphics2D g2d, int centerX, int centerY) {
            g2d.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            String message;
            Color color;
            if (isAnimating) {
                if (currentCode.equals(CORRECT_CODE)) { message = "ACCESSO CONCESSO"; color = MATRIX_GREEN; }
                else { message = "ACCESSO NEGATO"; color = Color.RED; }
            } else {
                message = "Usa 0-9 (o clic) • * = backspace • # = invio";
                color = TEXT_COLOR;
            }
            g2d.setColor(color);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(message, centerX - fm.stringWidth(message) / 2, centerY);
        }

        private void drawUnlockedScreen(Graphics2D g2d) {
            int centerX = getWidth() / 2, centerY = getHeight() / 2;
            g2d.setColor(MATRIX_GREEN_DARK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setFont(titleFont);
            g2d.setColor(MATRIX_GREEN);
            String success = "ACCESSO AUTORIZZATO";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(success, centerX - fm.stringWidth(success) / 2, centerY - 20);
            g2d.setColor(TEXT_COLOR);
            String enter = "Benvenuto nella stanza segreta";
            g2d.drawString(enter, centerX - fm.stringWidth(enter) / 2, centerY + 20);
        }

        private static class MatrixEffect {
            private int x, y; private char ch; private float alpha; private float speed;
            MatrixEffect() { reset(400, 500); }
            private void reset(int w, int h) {
                x = (int)(Math.random() * w);
                y = (int)(Math.random() * h);
                ch = (char) ('0' + (int)(Math.random() * 10));
                alpha = (float)(Math.random() * 0.5 + 0.1);
                speed = (float)(Math.random() * 2 + 0.5);
            }
            void update(int w, int h) {
                y += speed; alpha -= 0.01f;
                if (y > h || alpha <= 0) reset(w, h);
            }
            void draw(Graphics2D g2d) {
                g2d.setColor(new Color(0, 255, 65, (int)(alpha * 255)));
                g2d.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
                g2d.drawString(String.valueOf(ch), x, y);
            }
        }
    }
}
