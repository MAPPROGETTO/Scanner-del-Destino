package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.example.mappa.Mappa;

public class MappaPanel extends JPanel {
    private Mappa mappa;
    private int playerX = 6;
    private int playerY = 6;
    private int currentFloor = 1;
    private boolean stanzaSegretaAperta = false;

    private static final int CELL_SIZE = 30;

    // Sostituiti con i colori di MatrixStyleUtils
    private static final Color WALL_COLOR = MatrixStyleUtils.MATRIX_GREEN;
    private static final Color DOOR_COLOR = MatrixStyleUtils.MATRIX_GREEN_TRANSPARENT;
    private static final Color STAIRS_COLOR = MatrixStyleUtils.MATRIX_GREEN_LIGHT;
    private static final Color PLAYER_COLOR = MatrixStyleUtils.MATRIX_GREEN;
    private static final Color VISITED_COLOR = MatrixStyleUtils.MATRIX_GREEN_DARK;
    private static final Color SECRET_ROOM_COLOR = new Color(0, 150, 40, 80);
    private static final Color ITEM_COLOR = MatrixStyleUtils.MATRIX_GREEN;

    // Mappa delle stanze per entrambi i piani
    private Map<String, StanzaInfo> stanzeMap;

    // Particelle Matrix per effetti visivi
    private List<MatrixStyleUtils.MatrixChar> matrixChars;
    private List<MatrixStyleUtils.FloatingParticle> floatingParticles;
    private Timer effectTimer;

    public MappaPanel() {
        setOpaque(false);
        setBorder(MatrixStyleUtils.createThickMatrixBorder());
        initializeStanze();
        initializeEffects();
        startEffects();
    }

    private void initializeStanze() {
        stanzeMap = new HashMap<>();

        // PRIMO PIANO (12x8 celle)
        stanzeMap.put("salotto_1", new StanzaInfo(4, 4, 5, 3, "Salotto", "PEZZO", ITEM_COLOR));
        stanzeMap.put("cucina_1", new StanzaInfo(5, 1, 4, 2, "Cucina", "PEZZO", ITEM_COLOR));
        stanzeMap.put("bagno_1", new StanzaInfo(10, 3, 2, 3, "Bagno", "PEZZO", ITEM_COLOR));
        stanzeMap.put("segreta_1", new StanzaInfo(1, 1, 3, 2, "Stanza Segreta",
                stanzaSegretaAperta ? "APERTA" : "CHIUSA",
                stanzaSegretaAperta ? SECRET_ROOM_COLOR : WALL_COLOR));
        stanzeMap.put("scale_1", new StanzaInfo(1, 4, 2, 2, "Scale", "SCALE", STAIRS_COLOR));

        // SECONDO PIANO (12x8 celle)
        stanzeMap.put("matrimoniale_2", new StanzaInfo(8, 3, 3, 4, "Camera Matrimoniale", "PEZZO", ITEM_COLOR));
        stanzeMap.put("balcone_2", new StanzaInfo(1, 0, 3, 1, "Balcone", "PERICOLO", MatrixStyleUtils.DANGER_COLOR));
        stanzeMap.put("bambini_2", new StanzaInfo(1, 1, 3, 3, "Camera Bambini", "CHIAVE", ITEM_COLOR));
        stanzeMap.put("corridoio_2", new StanzaInfo(5, 0, 2, 7, "Corridoio", "CHIAVE", ITEM_COLOR));
        stanzeMap.put("stanzaOspiti_2", new StanzaInfo(8, 0, 3, 2, "Stanza Ospiti", "PERICOLO", ITEM_COLOR));
    }

    private void initializeEffects() {
        // Inizializza gli effetti Matrix
        matrixChars = MatrixStyleUtils.createMatrixChars(8, getPreferredSize().width, getPreferredSize().height);
        floatingParticles = MatrixStyleUtils.createFloatingParticles(12, getPreferredSize().width, getPreferredSize().height);
    }

    private void startEffects() {
        effectTimer = new Timer(150, e -> {
            MatrixStyleUtils.updateMatrixChars(matrixChars, getWidth(), getHeight());
            if(getWidth()>0 && getHeight()>0 && isDisplayable()){
                floatingParticles = MatrixStyleUtils.createFloatingParticles(12, getWidth(), getHeight());
                MatrixStyleUtils.updateFloatingParticles(floatingParticles, getWidth(), getHeight());
                repaint();
            }
        });
        effectTimer.start();
    }

    public void aggiornaMappa(Mappa nuovaMappa, int x, int y) {
        this.mappa = nuovaMappa;
        this.playerX = x;
        this.playerY = y;
        repaint();
    }

    public void cambiaPiano(int piano, int x, int y) {
        this.currentFloor = piano;
        this.playerX = x;
        this.playerY = y;
        initializeStanze();
        repaint();
    }

    public void apriStanzaSegreta() {
        this.stanzaSegretaAperta = true;
        initializeStanze();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Applica sfondo gradiente Matrix
        MatrixStyleUtils.applyAlternativeGradient(g2d, getWidth(), getHeight());

        // Effetti di sfondo Matrix
        drawMatrixBackground(g2d);

        // Titolo
        drawTitle(g2d);

        if (mappa != null) {
            drawHouseMap(g2d);
            drawPlayer(g2d);
            drawCoordinates(g2d);
            drawFloorInfo(g2d);
        } else {
            drawNoMapMessage(g2d);
        }

        g2d.dispose();
    }

    private void drawMatrixBackground(Graphics2D g2d) {
        // Disegna particelle fluttuanti
        MatrixStyleUtils.drawFloatingParticles(g2d, floatingParticles);

        // Disegna caratteri Matrix di sfondo
        Composite oldComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        MatrixStyleUtils.drawMatrixChars(g2d, matrixChars);
        g2d.setComposite(oldComposite);
    }

    private void drawTitle(Graphics2D g2d) {
        g2d.setFont(MatrixStyleUtils.COURIER_BOLD);
        g2d.setColor(MatrixStyleUtils.MATRIX_GREEN);

        String title = "CASA ABBANDONATA";
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int x = (getWidth() - titleWidth) / 2;

        // Effetto glow per il titolo
        g2d.setColor(new Color(0, 255, 65, 100));
        g2d.drawString(title, x + 1, 26);
        g2d.drawString(title, x - 1, 24);

        g2d.setColor(MatrixStyleUtils.MATRIX_GREEN);
        g2d.drawString(title, x, 25);

        // Linea sotto il titolo con effetto Matrix
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(MatrixStyleUtils.MATRIX_GREEN_LIGHT);
        g2d.drawLine(10, 30, getWidth() - 10, 30);

        // Piccoli dettagli decorativi
        g2d.setColor(MatrixStyleUtils.MATRIX_GREEN_DARK);
        g2d.fillRect(8, 28, 4, 4);
        g2d.fillRect(getWidth() - 12, 28, 4, 4);
    }

    private void drawHouseMap(Graphics2D g2d) {
        int mapWidth = 12;
        int mapHeight = 8;
        int totalMapWidth = mapWidth * CELL_SIZE;
        int totalMapHeight = mapHeight * CELL_SIZE;
        int offsetX = (getWidth() - totalMapWidth) / 2;
        int offsetY = 60;

        // Sfondo della casa con effetto Matrix
        g2d.setColor(MatrixStyleUtils.DARK_BACKGROUND);
        g2d.fillRect(offsetX, offsetY, totalMapWidth, totalMapHeight);

        // Bordo esterno con effetto glow
        drawGlowingBorder(g2d, offsetX, offsetY, totalMapWidth, totalMapHeight);

        // Disegna le stanze per il piano corrente
        for (Map.Entry<String, StanzaInfo> entry : stanzeMap.entrySet()) {
            StanzaInfo stanza = entry.getValue();
            if ((currentFloor == 1 && entry.getKey().endsWith("_1")) ||
                    (currentFloor == 2 && entry.getKey().endsWith("_2"))) {
                drawRoom(g2d, stanza, offsetX, offsetY);
            }
        }

        drawDoors(g2d, offsetX, offsetY);
        drawSpecialFeatures(g2d, offsetX, offsetY);
    }

    private void drawGlowingBorder(Graphics2D g2d, int x, int y, int width, int height) {
        // Effetto glow multiplo per il bordo
        for (int i = 5; i > 0; i--) {
            g2d.setColor(new Color(0, 255, 65, 20 + i * 10));
            g2d.setStroke(new BasicStroke(i * 2 + 1));
            g2d.drawRect(x - i, y - i, width + i * 2, height + i * 2);
        }

        // Bordo principale
        g2d.setColor(WALL_COLOR);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(x, y, width, height);
    }

    private void drawRoom(Graphics2D g2d, StanzaInfo stanza, int offsetX, int offsetY) {
        int x = offsetX + stanza.x * CELL_SIZE;
        int y = offsetY + stanza.y * CELL_SIZE;
        int width = stanza.width * CELL_SIZE;
        int height = stanza.height * CELL_SIZE;

        // Interno della stanza con gradiente scuro
        GradientPaint roomGradient = new GradientPaint(
                x, y, MatrixStyleUtils.DARK_BACKGROUND,
                x + width, y + height, MatrixStyleUtils.MEDIUM_DARK
        );
        g2d.setPaint(roomGradient);
        g2d.fillRect(x, y, width, height);

        // Se è visitata, aggiungi overlay verde trasparente
        if (isRoomVisited(stanza)) {
            g2d.setColor(VISITED_COLOR);
            g2d.fillRect(x, y, width, height);

            // Effetto pulsante per stanza visitata
            long time = System.currentTimeMillis();
            float pulse = (float) (0.3 + 0.2 * Math.sin(time * 0.003));
            g2d.setColor(new Color(0, 255, 65, (int)(pulse * 100)));
            g2d.drawRect(x + 1, y + 1, width - 2, height - 2);
        }

        // Bordi della stanza con effetto Matrix
        g2d.setColor(WALL_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, width, height);

        // Per la stanza segreta chiusa
        if (stanza.contenuto.equals("CHIUSA")) {
            g2d.setColor(new Color(0, 100, 30, 150));
            g2d.fillRect(x + 2, y + 2, width - 4, height - 4);

            // Effetto "accesso negato"
            g2d.setColor(new Color(255, 0, 0, 200));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(x + 5, y + 5, x + width - 5, y + height - 5);
            g2d.drawLine(x + width - 5, y + 5, x + 5, y + height - 5);
        }

        // Nome della stanza con font Matrix
        g2d.setColor(MatrixStyleUtils.MATRIX_GREEN_TRANSPARENT);
        g2d.setFont(MatrixStyleUtils.COURIER_FONT);
        FontMetrics fm = g2d.getFontMetrics();
        String[] lines = stanza.nome.split(" ");

        int textY = y + height/2 - (lines.length * 10) / 2;
        for (String line : lines) {
            int textWidth = fm.stringWidth(line);
            int textX = x + (width - textWidth) / 2;

            // Effetto ombra per il testo
            g2d.setColor(Color.BLACK);
            g2d.drawString(line, textX + 1, textY + 1);
            g2d.setColor(MatrixStyleUtils.MATRIX_GREEN_TRANSPARENT);
            g2d.drawString(line, textX, textY);

            textY += 12;
        }

        drawRoomIcon(g2d, stanza, x, y, width, height);
    }

    private void drawRoomIcon(Graphics2D g2d, StanzaInfo stanza, int x, int y, int width, int height) {
        int iconX = x + width - 15;
        int iconY = y + 5;

        g2d.setFont(MatrixStyleUtils.COURIER_FONT);

        switch (stanza.contenuto) {
            case "PEZZO":
                // Icona pezzo con effetto glow
                g2d.setColor(new Color(0, 255, 65, 100));
                g2d.fillOval(iconX - 1, iconY - 1, 12, 12);
                g2d.setColor(ITEM_COLOR);
                g2d.fillOval(iconX, iconY, 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawString("P", iconX + 2, iconY + 8);
                break;

            case "CHIAVE":
                // Icona chiave con effetto Matrix
                g2d.setColor(new Color(0, 255, 65, 100));
                g2d.fillRect(iconX - 1, iconY + 2, 10, 6);
                g2d.setColor(ITEM_COLOR);
                g2d.fillRect(iconX, iconY + 3, 8, 4);
                g2d.fillRect(iconX + 8, iconY + 1, 2, 8);
                break;

            case "PERICOLO":
                // Triangolo di pericolo con pulsazione
                long time = System.currentTimeMillis();
                float pulse = (float) (0.7 + 0.3 * Math.sin(time * 0.01));
                g2d.setColor(new Color(255, 0, 0, (int)(pulse * 255)));
                g2d.fillPolygon(new int[]{iconX + 5, iconX, iconX + 10},
                        new int[]{iconY, iconY + 10, iconY + 10}, 3);
                g2d.setColor(Color.WHITE);
                g2d.drawString("!", iconX + 3, iconY + 8);
                break;

            case "CHIUSA":
                // X rossa con effetto glow
                g2d.setColor(new Color(255, 0, 0, 100));
                g2d.setStroke(new BasicStroke(4));
                g2d.drawLine(iconX - 1, iconY - 1, iconX + 11, iconY + 11);
                g2d.drawLine(iconX + 11, iconY - 1, iconX - 1, iconY + 11);

                g2d.setColor(new Color(255, 0, 0, 200));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(iconX, iconY, iconX + 10, iconY + 10);
                g2d.drawLine(iconX + 10, iconY, iconX, iconY + 10);
                break;

            case "SCALE":
                // Scale con effetto Matrix
                g2d.setColor(MatrixStyleUtils.MATRIX_GREEN_TRANSPARENT);
                g2d.setStroke(new BasicStroke(1));
                for (int i = 0; i < 3; i++) {
                    g2d.drawLine(iconX, iconY + i * 3, iconX + 8, iconY + i * 3);
                }
                // Effetto glow per le scale
                g2d.setColor(new Color(0, 255, 65, 80));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(iconX - 1, iconY + 3, iconX + 9, iconY + 3);
                break;
        }
    }

    private void drawDoors(Graphics2D g2d, int offsetX, int offsetY) {
        g2d.setColor(DOOR_COLOR);
        g2d.setStroke(new BasicStroke(4));

        if (currentFloor == 1) {
            // Porte primo piano con effetto glow
            drawGlowingDoor(g2d, offsetX + 3 * CELL_SIZE, offsetY + 5 * CELL_SIZE,
                    offsetX + 4 * CELL_SIZE, offsetY + 5 * CELL_SIZE);
            drawGlowingDoor(g2d, offsetX + 9 * CELL_SIZE, offsetY + 5 * CELL_SIZE,
                    offsetX + 10 * CELL_SIZE, offsetY + 5 * CELL_SIZE);
            drawGlowingDoor(g2d, offsetX + 6 * CELL_SIZE, offsetY + 4 * CELL_SIZE,
                    offsetX + 6 * CELL_SIZE, offsetY + 3 * CELL_SIZE);

            if (stanzaSegretaAperta) {
                drawGlowingDoor(g2d, offsetX + 2 * CELL_SIZE, offsetY + 4 * CELL_SIZE,
                        offsetX + 2 * CELL_SIZE, offsetY + 3 * CELL_SIZE);
            }
        } else if (currentFloor == 2) {
            // Porte secondo piano
            drawGlowingDoor(g2d, offsetX + 7 * CELL_SIZE, offsetY + 4 * CELL_SIZE,
                    offsetX + 8 * CELL_SIZE, offsetY + 4 * CELL_SIZE);
            drawGlowingDoor(g2d, offsetX + 4 * CELL_SIZE, offsetY + 3 * CELL_SIZE,
                    offsetX + 5 * CELL_SIZE, offsetY + 3 * CELL_SIZE);
            drawGlowingDoor(g2d, offsetX + 7 * CELL_SIZE, offsetY + CELL_SIZE,
                    offsetX + 8 * CELL_SIZE, offsetY + CELL_SIZE);
        }
    }

    private void drawGlowingDoor(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        // Effetto glow per le porte
        g2d.setColor(new Color(0, 255, 65, 80));
        g2d.setStroke(new BasicStroke(6));
        g2d.drawLine(x1, y1, x2, y2);

        g2d.setColor(DOOR_COLOR);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(x1, y1, x2, y2);
    }

    private void drawSpecialFeatures(Graphics2D g2d, int offsetX, int offsetY) {
        g2d.setColor(MatrixStyleUtils.MATRIX_GREEN_LIGHT);
        g2d.setStroke(new BasicStroke(2));

        if (currentFloor == 1) {
            // Finestre primo piano con effetto Matrix
            drawMatrixWindow(g2d, offsetX + 2 * CELL_SIZE, offsetY, CELL_SIZE, 5);
            drawMatrixWindow(g2d, offsetX + 9 * CELL_SIZE, offsetY, CELL_SIZE, 5);
        } else if (currentFloor == 2) {
            // Finestre secondo piano
            drawMatrixWindow(g2d, offsetX + 3 * CELL_SIZE, offsetY, CELL_SIZE, 5);
            drawMatrixWindow(g2d, offsetX + 9 * CELL_SIZE, offsetY + 5 * CELL_SIZE, 5, CELL_SIZE);
        }
    }

    private void drawMatrixWindow(Graphics2D g2d, int x, int y, int width, int height) {
        // Finestra con effetto digitale
        g2d.setColor(new Color(0, 255, 65, 100));
        g2d.fillRect(x, y, width, height);

        g2d.setColor(MatrixStyleUtils.MATRIX_GREEN_LIGHT);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, width, height);

        // Piccoli dettagli digitali
        g2d.setColor(new Color(0, 255, 65, 150));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(x + width/2, y, x + width/2, y + height);
        g2d.drawLine(x, y + height/2, x + width, y + height/2);
    }

    private void drawPlayer(Graphics2D g2d) {
        if (mappa == null) return;

        int totalMapWidth = 12 * CELL_SIZE;
        int offsetX = (getWidth() - totalMapWidth) / 2;
        int offsetY = 60;

        int playerScreenX = offsetX + playerX * CELL_SIZE + CELL_SIZE / 2;
        int playerScreenY = offsetY + playerY * CELL_SIZE + CELL_SIZE / 2;

        // Effetto pulsante più elaborato
        long time = System.currentTimeMillis();
        float pulse = (float) (0.8 + 0.2 * Math.sin(time * 0.005));
        float rotation = (float) (time * 0.002);

        // Aloni multipli con rotazione
        AffineTransform oldTransform = g2d.getTransform();
        g2d.translate(playerScreenX, playerScreenY);
        g2d.rotate(rotation);

        // Alone esterno rotante
        g2d.setColor(new Color(0, 255, 65, 30));
        g2d.fillOval(-25, -25, 50, 50);

        g2d.rotate(-rotation * 2); // Rotazione opposta per effetto dinamico
        g2d.setColor(new Color(0, 255, 65, 60));
        g2d.fillOval(-20, -20, 40, 40);

        g2d.setTransform(oldTransform);

        // Alone medio fisso
        g2d.setColor(new Color(0, 255, 65, 100));
        g2d.fillOval(playerScreenX - 15, playerScreenY - 15, 30, 30);

        // Giocatore principale
        g2d.setColor(PLAYER_COLOR);
        int playerSize = (int) (14 * pulse);
        g2d.fillOval(playerScreenX - playerSize/2, playerScreenY - playerSize/2, playerSize, playerSize);

        // Bordo del giocatore con effetto speciale
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(playerScreenX - playerSize/2, playerScreenY - playerSize/2, playerSize, playerSize);

        // Piccolo punto centrale
        g2d.setColor(Color.WHITE);
        g2d.fillOval(playerScreenX - 2, playerScreenY - 2, 4, 4);
    }

    private void drawFloorInfo(Graphics2D g2d) {
        g2d.setFont(MatrixStyleUtils.COURIER_BOLD);
        g2d.setColor(MatrixStyleUtils.MATRIX_GREEN);

        String floorText = currentFloor == 1 ? "PRIMO PIANO" : "SECONDO PIANO";

        // Effetto glow per testo piano
        g2d.setColor(new Color(0, 255, 65, 100));
        g2d.drawString(floorText, 16, getHeight() - 44);
        g2d.setColor(MatrixStyleUtils.MATRIX_GREEN);
        g2d.drawString(floorText, 15, getHeight() - 45);
    }

    private void drawCoordinates(Graphics2D g2d) {
        g2d.setFont(MatrixStyleUtils.COURIER_FONT);
        g2d.setColor(new Color(0, 200, 50));

        String coords = String.format("Posizione: (%d, %d)", playerX, playerY);
        FontMetrics fm = g2d.getFontMetrics();
        int coordsWidth = fm.stringWidth(coords);
        int x = (getWidth() - coordsWidth) / 2;

        // Sfondo per le coordinate
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(x - 5, getHeight() - 25, coordsWidth + 10, 15, 5, 5);

        g2d.setColor(new Color(0, 200, 50));
        g2d.drawString(coords, x, getHeight() - 15);
    }

    private void drawNoMapMessage(Graphics2D g2d) {
        g2d.setFont(MatrixStyleUtils.COURIER_FONT);
        g2d.setColor(new Color(0, 150, 40));

        String message = "Casa abbandonata non disponibile";
        FontMetrics fm = g2d.getFontMetrics();
        int messageWidth = fm.stringWidth(message);
        int x = (getWidth() - messageWidth) / 2;
        int y = getHeight() / 2;

        // Sfondo per il messaggio
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(x - 10, y - 20, messageWidth + 20, 30, 10, 10);

        g2d.setColor(new Color(0, 150, 40));
        g2d.drawString(message, x, y);
    }

    private boolean isRoomVisited(StanzaInfo stanza) {
        return playerX >= stanza.x && playerX < stanza.x + stanza.width &&
                playerY >= stanza.y && playerY < stanza.y + stanza.height;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 450);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    // Cleanup degli effetti
    public void cleanup() {
        if (effectTimer != null) {
            effectTimer.stop();
        }
    }

    // Classe interna per rappresentare le informazioni di una stanza
    private static class StanzaInfo {
        int x, y, width, height;
        String nome;
        String contenuto;
        Color specialColor;

        public StanzaInfo(int x, int y, int width, int height, String nome, String contenuto, Color specialColor) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.nome = nome;
            this.contenuto = contenuto;
            this.specialColor = specialColor;
        }
    }

    // Getters e setters
    public int getCurrentFloor() { return currentFloor; }
    public void setCurrentFloor(int currentFloor) { this.currentFloor = currentFloor; }
}