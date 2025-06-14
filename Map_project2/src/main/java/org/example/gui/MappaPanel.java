package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import org.example.mappa.Mappa;

public class MappaPanel extends JPanel {
    private Mappa mappa;
    private int playerX = 6;
    private int playerY = 6;
    private int currentFloor = 1; // 1 = primo piano, 2 = secondo piano
    private boolean stanzaSegretaAperta = false; // per gestire la stanza segreta

    private static final int CELL_SIZE = 30;
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 250); // Nero profondo
    private static final Color WALL_COLOR = new Color(0, 255, 65, 200); // Verde brillante per bordi
    private static final Color DOOR_COLOR = new Color(0, 255, 65, 150); // Verde per porte
    private static final Color STAIRS_COLOR = new Color(0, 200, 50, 100); // Verde scuro per scale
    private static final Color PLAYER_COLOR = new Color(0, 255, 65); // Verde brillante per il giocatore
    private static final Color VISITED_COLOR = new Color(0, 255, 65, 30); // Verde trasparente per aree visitate
    private static final Color BORDER_COLOR = new Color(0, 255, 65, 200);
    private static final Color SECRET_ROOM_COLOR = new Color(0, 150, 40, 80); // Verde scuro per stanza segreta
    private static final Color DANGER_COLOR = new Color(255, 0, 0, 120); // Rosso per pericolo (unico colore non verde)
    private static final Color ITEM_COLOR = new Color(0, 255, 65); // Verde per oggetti

    // Mappa delle stanze per entrambi i piani
    private Map<String, StanzaInfo> stanzeMap;

    public MappaPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        initializeStanze();
    }

    private void initializeStanze() {
        stanzeMap = new HashMap<>();

        // PRIMO PIANO (12x8 celle)
        stanzeMap.put("salotto_1", new StanzaInfo(4, 4, 5, 3, "Salotto", "PEZZO", ITEM_COLOR));
        stanzeMap.put("cucina_1", new StanzaInfo(5, 1, 4, 2, "Cucina", "PEZZO", ITEM_COLOR));
        stanzeMap.put("bagno_1", new StanzaInfo(10, 3, 2, 3, "Bagno", "PEZZO", ITEM_COLOR));
        stanzeMap.put("segreta_1", new StanzaInfo(1, 1, 3, 2, "Stanza Segreta", stanzaSegretaAperta ? "APERTA" : "CHIUSA",
                stanzaSegretaAperta ? SECRET_ROOM_COLOR : WALL_COLOR));
        stanzeMap.put("scale_1", new StanzaInfo(1, 4, 2, 2, "Scale", "SCALE", STAIRS_COLOR));

        // SECONDO PIANO (12x8 celle)
        stanzeMap.put("matrimoniale_2", new StanzaInfo(8, 3, 3, 4, "Camera Matrimoniale", "PEZZO", ITEM_COLOR));
        stanzeMap.put("balcone_2", new StanzaInfo(1, 0, 3, 1, "Balcone", "PERICOLO", DANGER_COLOR));
        stanzeMap.put("bambini_2", new StanzaInfo(1, 1, 3, 3, "Camera Bambini", "CHIAVE", ITEM_COLOR));
        stanzeMap.put("corridoio_2", new StanzaInfo(5, 0, 2, 7, "Corridoio", "CHIAVE", ITEM_COLOR));
        stanzeMap.put("stanzaOspiti_2", new StanzaInfo(8, 0, 3, 2, "Stanza Ospiti", "PERICOLO", ITEM_COLOR));

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
        initializeStanze(); // Ricarica le stanze per il piano corrente
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

        // Sfondo del panel
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

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

    private void drawTitle(Graphics2D g2d) {
        g2d.setFont(new Font("Consolas", Font.BOLD, 14));
        g2d.setColor(new Color(0, 255, 65));

        String title = "CASA ABBANDONATA";
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int x = (getWidth() - titleWidth) / 2;

        g2d.drawString(title, x, 25);

        // Linea sotto il titolo
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(new Color(0, 255, 65, 100));
        g2d.drawLine(10, 30, getWidth() - 10, 30);
    }

    private void drawHouseMap(Graphics2D g2d) {
        int mapWidth = 12; // Larghezza fissa della casa
        int mapHeight = 8; // Altezza fissa della casa

        int totalMapWidth = mapWidth * CELL_SIZE;
        int totalMapHeight = mapHeight * CELL_SIZE;
        int offsetX = (getWidth() - totalMapWidth) / 2;
        int offsetY = 60; // Spazio per il titolo

        // Sfondo della casa completamente nero
        g2d.setColor(Color.BLACK);
        g2d.fillRect(offsetX, offsetY, totalMapWidth, totalMapHeight);

        // Bordo esterno della casa in verde brillante
        g2d.setColor(WALL_COLOR);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(offsetX, offsetY, totalMapWidth, totalMapHeight);

        // Disegna le stanze per il piano corrente
        for (Map.Entry<String, StanzaInfo> entry : stanzeMap.entrySet()) {
            StanzaInfo stanza = entry.getValue();

            // Controlla se la stanza appartiene al piano corrente
            if ((currentFloor == 1 && entry.getKey().endsWith("_1")) ||
                    (currentFloor == 2 && entry.getKey().endsWith("_2"))) {

                drawRoom(g2d, stanza, offsetX, offsetY);
            }
        }

        // Disegna porte tra le stanze
        drawDoors(g2d, offsetX, offsetY);

        // Disegna dettagli speciali
        drawSpecialFeatures(g2d, offsetX, offsetY);
    }

    private void drawRoom(Graphics2D g2d, StanzaInfo stanza, int offsetX, int offsetY) {
        int x = offsetX + stanza.x * CELL_SIZE;
        int y = offsetY + stanza.y * CELL_SIZE;
        int width = stanza.width * CELL_SIZE;
        int height = stanza.height * CELL_SIZE;

        // Interno della stanza sempre nero
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, width, height);

        // Se è visitata, aggiungi overlay verde trasparente
        if (isRoomVisited(stanza)) {
            g2d.setColor(VISITED_COLOR);
            g2d.fillRect(x, y, width, height);
        }

        // Bordi della stanza in verde brillante
        g2d.setColor(WALL_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, width, height);

        // Per la stanza segreta chiusa, riempimento diverso
        if (stanza.contenuto.equals("CHIUSA")) {
            g2d.setColor(new Color(0, 100, 30, 150)); // Verde molto scuro per stanza chiusa
            g2d.fillRect(x + 2, y + 2, width - 4, height - 4);
        }

        // Nome della stanza in verde
        g2d.setColor(new Color(0, 255, 65, 180));
        g2d.setFont(new Font("Arial", Font.BOLD, 9));
        FontMetrics fm = g2d.getFontMetrics();
        String[] lines = stanza.nome.split(" ");

        int textY = y + height/2 - (lines.length * 10) / 2;
        for (String line : lines) {
            int textWidth = fm.stringWidth(line);
            int textX = x + (width - textWidth) / 2;
            g2d.drawString(line, textX, textY);
            textY += 12;
        }

        // Icona dell'oggetto/stato
        drawRoomIcon(g2d, stanza, x, y, width, height);
    }

    private void drawRoomIcon(Graphics2D g2d, StanzaInfo stanza, int x, int y, int width, int height) {
        int iconX = x + width - 15;
        int iconY = y + 5;

        g2d.setFont(new Font("Arial", Font.BOLD, 8));

        switch (stanza.contenuto) {
            case "PEZZO":
                g2d.setColor(ITEM_COLOR);
                g2d.fillOval(iconX, iconY, 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawString("P", iconX + 2, iconY + 8);
                break;
            case "CHIAVE":
                g2d.setColor(ITEM_COLOR);
                g2d.fillRect(iconX, iconY + 3, 8, 4);
                g2d.fillRect(iconX + 8, iconY + 1, 2, 8);
                break;
            case "PERICOLO":
                g2d.setColor(DANGER_COLOR);
                g2d.fillPolygon(new int[]{iconX + 5, iconX, iconX + 10},
                        new int[]{iconY, iconY + 10, iconY + 10}, 3);
                g2d.setColor(Color.WHITE);
                g2d.drawString("!", iconX + 3, iconY + 8);
                break;
            case "CHIUSA":
                g2d.setColor(new Color(255, 0, 0, 200)); // Rosso per chiuso
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(iconX, iconY, iconX + 10, iconY + 10);
                g2d.drawLine(iconX + 10, iconY, iconX, iconY + 10);
                break;
            case "SCALE":
                g2d.setColor(new Color(0, 255, 65, 150));
                g2d.setStroke(new BasicStroke(1));
                for (int i = 0; i < 3; i++) {
                    g2d.drawLine(iconX, iconY + i * 3, iconX + 8, iconY + i * 3);
                }
                break;
        }
    }

    private void drawDoors(Graphics2D g2d, int offsetX, int offsetY) {
        g2d.setColor(DOOR_COLOR);
        g2d.setStroke(new BasicStroke(4));

        if (currentFloor == 1) {
            // Porte primo piano
            // Porta tra salotto e scale
            g2d.drawLine(offsetX + 3 * CELL_SIZE, offsetY + 5 * CELL_SIZE,
                    offsetX + 4 * CELL_SIZE, offsetY + 5 * CELL_SIZE);

            // Porta tra salotto e bagno
            g2d.drawLine(offsetX + 9 * CELL_SIZE, offsetY + 5 * CELL_SIZE,
                    offsetX + 10 * CELL_SIZE, offsetY + 5 * CELL_SIZE);

            // Porta tra salotto e cucina
            g2d.drawLine(offsetX + 6 * CELL_SIZE, offsetY + 4 * CELL_SIZE,
                    offsetX + 6 * CELL_SIZE, offsetY + 3 * CELL_SIZE);

            // Porta tra scale e stanza segreta (solo se aperta)
            if (stanzaSegretaAperta) {
                g2d.drawLine(offsetX + 2 * CELL_SIZE, offsetY + 4 * CELL_SIZE,
                        offsetX + 2 * CELL_SIZE, offsetY + 3 * CELL_SIZE);
            }
        } else if (currentFloor == 2) {
            // Porte secondo piano
            // Porta dal corridoio a camera matrimoniale
            g2d.drawLine(offsetX + 7 * CELL_SIZE, offsetY + 4 * CELL_SIZE,
                    offsetX + 8 * CELL_SIZE, offsetY + 4 * CELL_SIZE);
            // Porta dal corridoio a camera bambini
            g2d.drawLine(offsetX + 4 * CELL_SIZE, offsetY + 3 * CELL_SIZE,
                    offsetX + 5 * CELL_SIZE, offsetY + 3 * CELL_SIZE);
            // Porta dal corridoio a camera ospiti
            g2d.drawLine(offsetX + 7 * CELL_SIZE, offsetY + CELL_SIZE,
                    offsetX + 8 * CELL_SIZE, offsetY + CELL_SIZE);
        }
    }

    private void drawSpecialFeatures(Graphics2D g2d, int offsetX, int offsetY) {
        // Aggiungi dettagli come finestre in verde trasparente
        g2d.setColor(new Color(0, 255, 65, 80));
        g2d.setStroke(new BasicStroke(2));

        if (currentFloor == 1) {
            // Finestre primo piano
            g2d.drawRect(offsetX + 2 * CELL_SIZE, offsetY, CELL_SIZE, 5);
            g2d.drawRect(offsetX + 9 * CELL_SIZE, offsetY, CELL_SIZE, 5);
        } else if (currentFloor == 2) {
            // Finestre secondo piano
            g2d.drawRect(offsetX + 3 * CELL_SIZE, offsetY, CELL_SIZE, 5);
            g2d.drawRect(offsetX + 9 * CELL_SIZE, offsetY + 5 * CELL_SIZE, 5, CELL_SIZE);
        }
    }

    private void drawPlayer(Graphics2D g2d) {
        if (mappa == null) return;

        int totalMapWidth = 12 * CELL_SIZE;
        int offsetX = (getWidth() - totalMapWidth) / 2;
        int offsetY = 60;

        int playerScreenX = offsetX + playerX * CELL_SIZE + CELL_SIZE / 2;
        int playerScreenY = offsetY + playerY * CELL_SIZE + CELL_SIZE / 2;

        // Effetto pulsante per il giocatore
        long time = System.currentTimeMillis();
        float pulse = (float) (0.8 + 0.2 * Math.sin(time * 0.005));

        // Alone luminoso verde
        g2d.setColor(new Color(0, 255, 65, 60));
        g2d.fillOval(playerScreenX - 20, playerScreenY - 20, 40, 40);

        // Alone medio
        g2d.setColor(new Color(0, 255, 65, 100));
        g2d.fillOval(playerScreenX - 15, playerScreenY - 15, 30, 30);

        // Giocatore
        g2d.setColor(PLAYER_COLOR);
        int playerSize = (int) (14 * pulse);
        g2d.fillOval(playerScreenX - playerSize/2, playerScreenY - playerSize/2, playerSize, playerSize);

        // Bordo del giocatore
        g2d.setColor(new Color(0, 255, 65, 255));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(playerScreenX - playerSize/2, playerScreenY - playerSize/2, playerSize, playerSize);
    }

    private void drawFloorInfo(Graphics2D g2d) {
        g2d.setFont(new Font("Consolas", Font.BOLD, 12));
        g2d.setColor(new Color(0, 255, 65));

        String floorText = currentFloor == 1 ? "PRIMO PIANO" : "SECONDO PIANO";

        g2d.drawString(floorText, 15, getHeight() - 45);
    }

    private void drawCoordinates(Graphics2D g2d) {
        g2d.setFont(new Font("Consolas", Font.PLAIN, 11));
        g2d.setColor(new Color(0, 200, 50));

        String coords = String.format("Posizione: (%d, %d)", playerX, playerY);
        FontMetrics fm = g2d.getFontMetrics();
        int coordsWidth = fm.stringWidth(coords);
        int x = (getWidth() - coordsWidth) / 2;

        g2d.drawString(coords, x, getHeight() - 15);
    }

    private void drawNoMapMessage(Graphics2D g2d) {
        g2d.setFont(new Font("Consolas", Font.ITALIC, 12));
        g2d.setColor(new Color(0, 150, 40));

        String message = "Casa abbandonata non disponibile";
        FontMetrics fm = g2d.getFontMetrics();
        int messageWidth = fm.stringWidth(message);
        int x = (getWidth() - messageWidth) / 2;
        int y = getHeight() / 2;

        g2d.drawString(message, x, y);
    }

    private boolean isRoomVisited(StanzaInfo stanza) {
        // Logica per determinare se una stanza è stata visitata
        return playerX >= stanza.x && playerX < stanza.x + stanza.width &&
                playerY >= stanza.y && playerY < stanza.y + stanza.height;
    }

    private boolean isVisited(int x, int y) {
        int dx = Math.abs(x - playerX);
        int dy = Math.abs(y - playerY);
        return dx <= 1 && dy <= 1;
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

    public int getCurrentFloor() {return currentFloor;}

    public void setCurrentFloor(int currentFloor) {this.currentFloor = currentFloor;}
}