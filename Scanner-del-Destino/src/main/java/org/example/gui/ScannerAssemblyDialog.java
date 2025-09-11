package org.example.gui;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
/**
 * Dialog per il minigioco di assemblaggio dello Scanner.
 * L'utente deve trascinare i pezzi nell'ordine corretto.
 * Una volta completato, viene notificato tramite il listener.
 * Se annulla/chiude, viene notificato tramite il listener.
 */
public class ScannerAssemblyDialog extends JDialog {
    /**
     * Listener per notificare il completamento o l'annullamento.
     */
    public interface Listener {
        void onCompleted();   // chiamato quando il montaggio è completato
        void onCancelled();   // chiamato se l’utente chiude/annulla
    }

    private final AssemblyPanel panel;
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    /**
     * Costruttore
     * @param parent
     * @param listener
     */
    public ScannerAssemblyDialog(Window parent, Listener listener) {
        super(parent, "Assemblaggio Scanner", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(820, 540);
        setResizable(false);
        setLocationRelativeTo(parent);

        // Intro
        JPanel intro = buildIntroPanel(listener);

        // Gioco
        panel = new AssemblyPanel(listener);

        root.add(intro, "intro");
        root.add(panel, "game");
        setContentPane(root);

        // focus
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) {
                panel.requestFocusInWindow();
            }
        });
    }
    /**
     * Mostra il dialog
     *
     * @param listener
     */
    private JPanel buildIntroPanel(Listener listener) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(18,18,18));

        JTextArea ta = new JTextArea("""
                ═══════════ ASSEMBLAGGIO SCANNER ═══════════

                Obiettivo: monta lo Scanner trascinando i pezzi
                nell'ORDINE corretto:

                1) Batteria
                2) Motore
                3) Elica
                4) Schermo

                Se sbagli l'ordine, il pezzo tornerà al suo posto.
                Buona fortuna.
                """);
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setForeground(new Color(0,255,65));
        ta.setFont(new Font("Consolas", Font.PLAIN, 16));
        ta.setBorder(BorderFactory.createEmptyBorder(24,24,24,24));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btns.setOpaque(false);
        JButton start = new JButton("Inizia");
        JButton annulla = new JButton("Annulla");
        start.addActionListener(e -> ((CardLayout) root.getLayout()).show(root, "game"));
        annulla.addActionListener(e -> {
            if (listener != null) listener.onCancelled();
            dispose();
        });
        btns.add(start);
        btns.add(annulla);

        p.add(ta, BorderLayout.CENTER);
        p.add(btns, BorderLayout.SOUTH);
        return p;
    }

    // ---------- Pannello Gioco ----------

    /**
     * Pannello principale del gioco di assemblaggio.
     * Contiene i pezzi da trascinare e gli slot di destinazione.
     * Gestisce il drag-and-drop e la logica di gioco.
     * Notifica il listener al completamento o annullamento.
     */
    private static class AssemblyPanel extends JPanel {
        private static final Color BG = new Color(10,10,10);
        private static final Color GREEN = new Color(0,255,65);
        private static final Color GREEN_DIM = new Color(0,255,65,120);
        private static final Font  FONT = new Font("Consolas", Font.PLAIN, 14);

        // ordine richiesto
        private final List<String> order = List.of("batteria", "motore", "elica", "schermo");
        private int expectedIndex = 0;

        // palette pezzi (sinistra)
        private final Map<String, Piece> pieces = new LinkedHashMap<>();

        // slot (destra) – 4 posizioni fisse
        private final List<DropSlot> slots = new ArrayList<>();

        private final Listener listener;
        /**
         * Costruttore
         * @param listener
         */
        AssemblyPanel(Listener listener) {
            this.listener = listener;
            setLayout(null);
            setBackground(BG);

            // crea pezzi con posizione iniziale
            int startX = 40, startY = 80, dy = 70;
            pieces.put("batteria", new Piece("batteria", startX, startY));
            pieces.put("motore",   new Piece("motore",   startX, startY+dy));
            pieces.put("elica",    new Piece("elica",    startX, startY+2*dy));
            pieces.put("schermo",  new Piece("schermo",  startX, startY+3*dy));

            pieces.values().forEach(this::add);

            // crea slot
            int baseX = 460, baseY = 90, sdy = 80, w = 260, h = 50;
            for (int i=0; i<4; i++) slots.add(new DropSlot(baseX, baseY + i*sdy, w, h));

            // titolo/sottotitolo
            JLabel title = new JLabel("ASSEMBLA LO SCANNER");
            title.setForeground(GREEN);
            title.setFont(new Font("Consolas", Font.BOLD, 18));
            title.setBounds(24, 16, 400, 24);
            add(title);

            JButton reset = new JButton("Reset");
            reset.setBounds(24, 430, 100, 30);
            reset.addActionListener(e -> resetGame());
            add(reset);

            JButton quit = new JButton("Chiudi");
            quit.setBounds(136, 430, 100, 30);
            quit.addActionListener(e -> {
                if (listener != null) listener.onCancelled();
                SwingUtilities.getWindowAncestor(this).dispose();
            });
            add(quit);

            // bordi invisibili per mouse
            setPreferredSize(new Dimension(820, 540));
        }
        /**
         * Resetta il gioco alla condizione iniziale
         * (pezzi alla posizione iniziale, indice atteso a 0).
         * Viene chiamato anche dal pulsante "Reset".
         * Reimposta anche la grafica.
         */
        private void resetGame() {
            expectedIndex = 0;
            for (Piece p : pieces.values()) p.resetPosition();
            repaint();
        }
        /**
         * Ridisegna il pannello, inclusi i pezzi e gli slot.
         * @param g
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Silhouette del dispositivo
            g2.setColor(new Color(30,30,30));
            g2.fillRoundRect(430, 60, 320, 380, 16, 16);
            g2.setColor(GREEN_DIM);
            g2.drawRoundRect(430, 60, 320, 380, 16, 16);

            // Slot + etichette
            g2.setFont(FONT);
            for (int i=0; i<slots.size(); i++) {
                DropSlot s = slots.get(i);
                boolean isNext = (i == expectedIndex);
                s.draw(g2, isNext);
            }

            // Istruzioni
            g2.setColor(new Color(200,200,200));
            g2.drawString("Trascina i pezzi nell'ordine richiesto. Se sbagli, tornano indietro.", 24, 64);

            g2.dispose();
        }

        // --------- Componenti: Piece + DropSlot ----------

        /**
         * Componente per un pezzo trascinabile.
         * Gestisce il drag-and-drop e il rendering.
         * Ha uno stato "locked" quando è posizionato correttamente.
         * Ha una posizione "origin" per resettarsi.
         */
        private class Piece extends JComponent {
            final String id;
            final int w = 200, h = 40;
            final Point origin;
            boolean locked = false; // true quando posizionato sullo slot corretto

            private int dragOffsetX, dragOffsetY;

            Piece(String id, int x, int y) {
                this.id = id;
                setBounds(x, y, w, h);
                origin = new Point(x, y);

                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setToolTipText(id);

                MouseInputAdapter mia = new MouseInputAdapter() {
                    @Override public void mousePressed(MouseEvent e) {
                        if (locked) return;
                        dragOffsetX = e.getX();
                        dragOffsetY = e.getY();
                        toFront(Piece.this);
                    }
                    @Override public void mouseDragged(MouseEvent e) {
                        if (locked) return;
                        int nx = getX() + e.getX() - dragOffsetX;
                        int ny = getY() + e.getY() - dragOffsetY;
                        setLocation(nx, ny);
                        repaint();
                    }
                    @Override public void mouseReleased(MouseEvent e) {
                        if (locked) return;
                        handleDrop(Piece.this);
                    }
                };
                addMouseListener(mia);
                addMouseMotionListener(mia);
            }

            /**
             * Riporta il pezzo alla posizione iniziale e sblocca lo stato.
             * Viene chiamato dal reset del gioco.
             */
            void resetPosition() {
                locked = false;
                setLocation(origin);
            }

            /**
             * Ridisegna il pezzo con grafica personalizzata.
             * @param g
             */
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // box
                g2.setColor(new Color(25,25,25));
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                g2.setColor(GREEN);
                g2.drawRoundRect(0, 0, w, h, 10, 10);

                // testo
                g2.setFont(FONT);
                g2.setColor(new Color(230,230,230));
                String t = switch (id) {
                    case "batteria" -> "Batteria 🔋";
                    case "motore"   -> "Motore ⚙️";
                    case "elica"    -> "Elica 🌀";
                    case "schermo"  -> "Schermo 🖥️";
                    default -> id;
                };
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(t, 12, (h + fm.getAscent())/2 - 2);

                g2.dispose();
            }
        }

        /**
         * Slot di destinazione per un pezzo.
         * Ha una posizione e dimensione fissa.
         * Può essere "filled" quando un pezzo è posizionato correttamente.
         * Gestisce il rendering, evidenziandosi se è lo slot successivo atteso.
         */
        private static class DropSlot {
            final int x,y,w,h;
            boolean filled = false;
            DropSlot(int x, int y, int w, int h) { this.x=x; this.y=y; this.w=w; this.h=h; }
            Rectangle rect() { return new Rectangle(x,y,w,h); }
            void draw(Graphics2D g2, boolean highlight) {
                g2.setColor(new Color(20,20,20));
                g2.fillRoundRect(x, y, w, h, 10, 10);
                g2.setStroke(new BasicStroke(highlight ? 3f : 1.5f));
                g2.setColor(highlight ? new Color(0,255,65) : new Color(120,120,120));
                g2.drawRoundRect(x, y, w, h, 10, 10);
            }
        }

        /**
         * Porta il componente in primo piano (sopra gli altri).
         * @param c
         */
        private void toFront(Component c) {
            setComponentZOrder(c, 0);
            repaint();
        }

        /**
         * Gestisce il rilascio di un pezzo.
         * Se è il pezzo atteso e sopra lo slot atteso, lo "blocca" lì.
         * Altrimenti, lo riporta alla posizione iniziale.
         * @param p
         */
        private void handleDrop(Piece p) {
            // Deve essere il pezzo atteso e sullo slot atteso
            String need = order.get(expectedIndex);
            DropSlot target = slots.get(expectedIndex);

            if (p.id.equals(need) && p.getBounds().intersects(target.rect())) {
                // Snap al centro slot
                int nx = target.x + (target.w - p.w)/2;
                int ny = target.y + (target.h - p.h)/2;
                p.setLocation(nx, ny);
                p.locked = true;
                target.filled = true;
                expectedIndex++;

                if (expectedIndex == order.size()) {
                    // COMPLETATO!
                    JOptionPane.showMessageDialog(this, "Scanner assemblato con successo!", "Completato", JOptionPane.INFORMATION_MESSAGE);
                    if (listener != null) listener.onCompleted();
                    SwingUtilities.getWindowAncestor(this).dispose();
                }
                repaint();
            } else {
                // Ritorna alla posizione iniziale
                p.setLocation(p.origin);
                repaint();
            }
        }
    }
}
