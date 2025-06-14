package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.example.mappa.Mappa;
import org.example.model.Lewis;
import org.example.story.Scena;
import org.example.story.SceneIniziali;
import org.example.utils.GestoreInputGUI;
import org.example.utils.Inventario;

public class FinestraGioco extends JFrame {
    private JLabel label;
    private Timer timer;
    private JTextArea areaTesto;
    private JTextField inputField;
    private JButton inviaButton;
    private JButton inventarioButton;
    private JButton mappaButton;
    private JButton continuaButton;
    private List<Scena> scene;
    private int indiceScenaCorrente = 0;
    private Scena scena;
    private Lewis lewis;
    private AnimatedPanel backgroundPanel;
    private MappaPanel mappaPanel;
    private int indice = 0;
    private GestoreInputGUI inputGest;
    private boolean mappaVisibile = false;

    public FinestraGioco() {
        label = new JLabel("");
        add(label);
        setTitle("Scanner del Destino");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inputGest = new GestoreInputGUI();

        initComponents();
        initLogic();
        setupEventHandlers();

        setVisible(true);
    }

    private void animaTesto(String testo) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        areaTesto.setText("");
        indice = 0;

        timer = new Timer(30, null);
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (indice < testo.length()) {
                    areaTesto.append(String.valueOf(testo.charAt(indice)));
                    indice++;
                } else {
                    timer.stop();
                    areaTesto.append("\n\n");
                }
            }
        });

        timer.start();
    }

    private void initComponents() {
        Color testo = new Color(220, 220, 220);
        Font fontMonospace = new Font("Consolas", Font.PLAIN, 14);

        backgroundPanel = new AnimatedPanel();
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout(10, 10));

        //Area di testo
        areaTesto = new JTextArea();
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

        JScrollPane scrollPane = new JScrollPane(areaTesto);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        //Panello per l'input
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setOpaque(false);

        inputField = new JTextField();
        inputField.setFont(fontMonospace);
        inputField.setBackground(new Color(40, 40, 40, 200));
        inputField.setForeground(testo);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 65, 100), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        inviaButton = createStyledButton("Invia", IconFactory.createSendIcon(16));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(inviaButton, BorderLayout.EAST);

        continuaButton = createStyledButton("Continua", IconFactory.createSendIcon(16));
        JPanel continuaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        continuaPanel.setOpaque(false);
        continuaPanel.add(continuaButton);

        JPanel bottomCombinedPanel = new JPanel(new BorderLayout());
        bottomCombinedPanel.setOpaque(false);
        bottomCombinedPanel.add(inputPanel, BorderLayout.NORTH);
        bottomCombinedPanel.add(continuaPanel, BorderLayout.SOUTH);
        backgroundPanel.add(bottomCombinedPanel, BorderLayout.SOUTH);

        // Panel superiore per i bottoni
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        inventarioButton = createStyledButton("Inventario", IconFactory.createInventoryIcon(16));
        mappaButton = createStyledButton("Mappa", IconFactory.createMapIcon(16));
        topPanel.add(inventarioButton);
        topPanel.add(mappaButton);
        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        // Crea il panel della mappa (inizialmente invisibile)
        mappaPanel = new MappaPanel();
        mappaPanel.setVisible(false);
        mappaPanel.setPreferredSize(new Dimension(250, 400)); // 1/4 della larghezza, 1/2 dell'altezza
        backgroundPanel.add(mappaPanel, BorderLayout.EAST);
    }

    private JButton createStyledButton(String text, ImageIcon icon) {
        JButton button = new JButton(text, icon);
        button.setFont(new Font("Consolas", Font.PLAIN, 12));
        button.setBackground(new Color(80, 80, 80, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 65, 100), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {//* Aggiungi l'effetto hover */
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

    private void initLogic() {
        Mappa mappa = new Mappa(12, 8);
        Inventario inventario = new Inventario();
        lewis = new Lewis(mappa, inputGest, inventario);
        lewis.setMappaPanel(mappaPanel);
        scene = SceneIniziali.creaScene();

        appendOutput("Benvenuto, Lewis! Inserisci un comando per iniziare.");
        mostraScena(0);
    }

    private void setupEventHandlers() {
        inviaButton.addActionListener(e -> processInput());
        inputField.addActionListener(e -> processInput());

        continuaButton.addActionListener(e -> {
            if (indiceScenaCorrente < scene.size() - 1) {
                indiceScenaCorrente++;
                if (indiceScenaCorrente == 4) {
                    TimePortalSplashScreen.showWithCallback(() -> {
                        appendOutput("Benvenuto nella nuova dimensione temporale!");
                        appendOutput("Il viaggio nel tempo è stato completato con successo.");
                        mostraScena(indiceScenaCorrente);
                    });
                } else {
                    mostraScena(indiceScenaCorrente);
                }
                System.out.println("Scena corrente: " + indiceScenaCorrente);
            } else {
                appendOutput("Hai raggiunto la fine della storia.");
            }
        });

        // Modifica il gestore del bottone mappa per mostrare/nascondere il panel
        mappaButton.addActionListener(e -> {
            mappaVisibile = !mappaVisibile;
            mappaPanel.setVisible(mappaVisibile);

            if (mappaVisibile) {
                mappaButton.setText("Nascondi Mappa");
                // Aggiorna il contenuto della mappa
                mappaPanel.aggiornaMappa(lewis.getMappa(), lewis.getPosizioneX(), lewis.getPosizioneY());
            } else {
                mappaButton.setText("Mappa");
            }

            // Richiama il revalidate e repaint per aggiornare il layout
            backgroundPanel.revalidate();
            backgroundPanel.repaint();
        });

        inventarioButton.addActionListener(e -> appendOutput("Controllo inventario..."));
    }

    private void processInput() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) return;

        inputGest.setUltimaLineaInserita(input);

        try {
            int scelta = Integer.parseInt(input);
            scena = scene.get(indiceScenaCorrente);
            if (scelta < 1) {
                appendOutput("Scelta non valida, riprova.");
            } else {
                indiceScenaCorrente = processaComando(indiceScenaCorrente);
                mostraScena(indiceScenaCorrente);
            }
        } catch (NumberFormatException ex) {
            String risposta = lewis.interpretaComandoDaGUI(input);
            appendOutput(input);
            appendOutput(risposta);

            // Aggiorna la mappa se è visibile
            if (mappaVisibile) {
                mappaPanel.aggiornaMappa(lewis.getMappa(), lewis.getPosizioneX(), lewis.getPosizioneY());
            }
        }

        inputField.setText("");
    }

    private void mostraScena(int indice) {
        if (indice < 0 || indice >= scene.size()) {
            appendOutput("Fine della storia o indice scena non valido.");
            return;
        }
        indiceScenaCorrente = indice;
        scena = scene.get(indice);
        animaTesto(scena.mostra());
    }

    private int processaComando(int indiceScena) {
        return indiceScena + 1;
    }

    private void appendOutput(String testo) {
        areaTesto.append(testo + "\n");
        areaTesto.setCaretPosition(areaTesto.getDocument().getLength());
    }

    @Override
    public void dispose() {
        if (backgroundPanel != null) {
            backgroundPanel.stopAnimation();
        }
        super.dispose();
    }
}
