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
import org.example.utils.GestoreInput;
import org.example.utils.Inventario;

public class FinestraGioco extends JFrame {
    private JLabel label;
    private Timer timer;
    private JTextArea areaTesto;
    private JTextField inputField;
    private JButton inviaButton;
    private JButton inventarioButton;
    private JButton mappaButton;
    private List<Scena> scene;
    private int indiceScenaCorrente = 0;
    private Scena scena;
    private Lewis lewis;
    private AnimatedPanel backgroundPanel;
    private int indice = 0;

    public FinestraGioco() {
        label = new JLabel("");
        add(label);
        setTitle("Scanner del Destino");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        initLogic();
        setupEventHandlers();

        setVisible(true);
    }

    private void animaTesto(String testo) {
        areaTesto.setText("");
        indice = 0;
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        timer = new Timer(30, new ActionListener() {
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
        // Colori tema
        Color sfondo = new Color(30, 30, 30);
        Color testo = new Color(220, 220, 220);
        Font fontMonospace = new Font("Consolas", Font.PLAIN, 14);

        // Panel principale con sfondo animato
        backgroundPanel = new AnimatedPanel();
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout(10, 10));

        // Area di testo con trasparenza
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

        // Panel di input
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

        // Bottone invia con icona
        inviaButton = createStyledButton("Invia", IconFactory.createSendIcon(16));

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(inviaButton, BorderLayout.EAST);
        backgroundPanel.add(inputPanel, BorderLayout.SOUTH);

        // Panel superiore con bottoni
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);

        inventarioButton = createStyledButton("Inventario", IconFactory.createInventoryIcon(16));
        mappaButton = createStyledButton("Mappa", IconFactory.createMapIcon(16));

        topPanel.add(inventarioButton);
        topPanel.add(mappaButton);
        backgroundPanel.add(topPanel, BorderLayout.NORTH);
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

        // Effetto hover
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

    private void initLogic() {
        Mappa mappa = new Mappa(5, 5);
        GestoreInput inputGest = new GestoreInput();
        Inventario inventario = new Inventario();
        lewis = new Lewis(mappa, inputGest, inventario);
        scene = SceneIniziali.creaScene();

        appendOutput("Benvenuto, Lewis! Inserisci un comando per iniziare.");
        mostraScena(0);
    }

    private void setupEventHandlers() {
        // Bottone INVIA
        inviaButton.addActionListener(e -> processInput());

        // Enter nel campo di input
        inputField.addActionListener(e -> processInput());

        // Bottone MAPPA
        mappaButton.addActionListener(e -> {
            appendOutput(lewis.visualizzaMappa());
        });

        // Bottone INVENTARIO
        inventarioButton.addActionListener(e -> {
            appendOutput("Controllo inventario...");
            // Qui puoi aggiungere la logica per mostrare l'inventario
        });
    }

    private void processInput() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) return;

        try {
            int scelta = Integer.parseInt(input);
            scena = scene.get(indiceScenaCorrente);
            if (scelta < 1 || scelta > scena.getOpzioni().size()) {
                appendOutput("Scelta non valida, riprova.");
            } else {
                indiceScenaCorrente = processaComando(indiceScenaCorrente, scelta);
                mostraScena(indiceScenaCorrente);
            }
        } catch (NumberFormatException ex) {
            String risposta = lewis.interpretaComandoDaGUI(input);
            appendOutput(input);
            appendOutput(risposta);
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
        animaTesto(scena.mostra()); // Anima il testo della scena
    }

    private int processaComando(int indiceScena, int scelta) {
        String comando = inputField.getText().trim();
        inputField.setText("");

        if (!comando.isEmpty()) {
            appendOutput(comando);
            String risposta = lewis.interpretaComandoDaGUI(comando);
            appendOutput(risposta);
        }
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

    // Getter per compatibilità
    public JTextArea getOutputArea() {
        return areaTesto;
    }
}