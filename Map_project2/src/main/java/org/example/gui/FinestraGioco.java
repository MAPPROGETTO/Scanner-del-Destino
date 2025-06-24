package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import org.example.mappa.Mappa;
import org.example.mappa.Stanza;
import org.example.model.Lewis;
import org.example.story.Scena;
import org.example.story.SceneLoader;
import org.example.story.StoryEngine;
import org.example.story.SceneManager;
import org.example.utils.GestoreInputGUI;
import org.example.inventario.Inventario;

public class FinestraGioco extends JFrame {
    private JLabel label;
    private Timer timer;
    private JTextArea areaTesto;
    private JTextField inputField;
    private JButton inviaButton;
    private JButton inventarioButton;
    private JButton mappaButton;
    private JButton saltaButton;
    private JButton continuaButton;
    private Scena scena;
    private Lewis lewis;
    private AnimatedPanel backgroundPanel;
    private MappaPanel mappaPanel;
    private GestoreInputGUI inputGest;

    private InventarioPanel inventarioPanel;
    private boolean mappaVisibile = false;
    private boolean inventarioVisibile = false;

    private boolean testoInScrittura = false;
    private String testoCompleto = "";

    private StoryEngine storyEngine;
    private SceneManager sceneManager;
    private SceneLoader loader;

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

    private void initLogic() {
        // Carica tutte le scene in un solo colpo
        sceneManager = new SceneManager();
        loader = new SceneLoader(sceneManager);
        loader.caricaTutteLeScene();

        // Inizializza mappa con le scene default già caricate
        Mappa mappa = new Mappa(12, 8, sceneManager.getScenePerMappa());

        Inventario inventario = new Inventario();
        lewis = new Lewis(mappa, inputGest, inventario, sceneManager);
        lewis.setMappaPanel(mappaPanel);

        storyEngine = new StoryEngine(sceneManager);

        mostraProssimaScena();
    }


    private void mostraProssimaScena() {
        Optional<Scena> prossima = storyEngine.prossimaScenaIniziale();

        if (prossima.isPresent()) {
            scena = prossima.get();
            animaTesto(scena.mostra());
            continuaButton.setEnabled(false);
            continuaButton.setVisible(false);
            inputField.setEnabled(false);
            inviaButton.setEnabled(false);
            inviaButton.setVisible(false);
        } else {
            // Fine delle scene iniziali, abilita input
            areaTesto.append("\nHai raggiunto la fine della storia iniziale.\n");
            inputField.setEnabled(true);
            inviaButton.setEnabled(true);
            inviaButton.setVisible(true);
            continuaButton.setEnabled(false);
            continuaButton.setVisible(false);
        }
    }

    private void animaTesto(String testo) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        testoCompleto = testo;
        areaTesto.setText("");
        testoInScrittura = true;
        saltaButton.setEnabled(true);
        saltaButton.setVisible(true);

        final int[] indice = {0};
        timer = new Timer(30, null);
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (indice[0] < testo.length()) {
                    areaTesto.append(String.valueOf(testo.charAt(indice[0])));
                    indice[0]++;
                } else {
                    timer.stop();
                    areaTesto.append("\n\n");
                    testoInScrittura = false;
                    saltaButton.setEnabled(false);
                    saltaButton.setVisible(false);
                    continuaButton.setEnabled(true);
                    continuaButton.setVisible(true);
                }
            }
        });

        timer.start();
    }

    private void setupEventHandlers() {
        inviaButton.addActionListener(e -> processInput());
        inputField.addActionListener(e -> processInput());

        continuaButton.addActionListener(e -> {
            if (testoInScrittura) {
                // Salta animazione
                timer.stop();
                aggiornaAreaTesto(testoCompleto);
                testoInScrittura = false;
                saltaButton.setEnabled(false);
                saltaButton.setVisible(false);
                continuaButton.setEnabled(true);
                continuaButton.setVisible(true);
                return;
            }

            // Prossima scena
            if (storyEngine.haAltreSceneIniziali()) {
                mostraProssimaScena();
            } else {
                continuaButton.setEnabled(false);
                continuaButton.setVisible(false);
                TimePortalSplashScreen.showWithCallback(() -> {
                    areaTesto.append("\nOra è il momento di agire.");
                    avviaModalitaEsplorazione();
                    inputField.setEnabled(true);
                    inviaButton.setEnabled(true);
                    inviaButton.setVisible(true);
                });

            }
        });

        saltaButton.addActionListener(e -> {
            if (testoInScrittura) {
                timer.stop();
                aggiornaAreaTesto(testoCompleto);
                testoInScrittura = false;
                saltaButton.setEnabled(false);
                saltaButton.setVisible(false);
                continuaButton.setEnabled(true);
                continuaButton.setVisible(true);
            }
        });

        mappaButton.addActionListener(e -> {
            mappaVisibile = !mappaVisibile;
            mappaPanel.setVisible(mappaVisibile);

            if (mappaVisibile) {
                mappaButton.setText("Nascondi Mappa");
                mappaPanel.aggiornaMappa(lewis.getMappa(), lewis.getPosizioneX(), lewis.getPosizioneY());
            } else {
                mappaButton.setText("Mappa");
            }

            backgroundPanel.revalidate();
            backgroundPanel.repaint();
        });

        inventarioButton.addActionListener(e -> {
            inventarioVisibile = !inventarioVisibile;
            inventarioPanel.setVisible(inventarioVisibile);

            if (inventarioVisibile) {
                inventarioButton.setText("Nascondi Inventario");
                inventarioPanel.aggiornaInventario();
            } else {
                inventarioButton.setText("Inventario");
            }

            backgroundPanel.revalidate();
            backgroundPanel.repaint();
        });
    }

    private void processInput() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) return;

        inputGest.setUltimaLineaInserita(input);

        try {
            int scelta = Integer.parseInt(input);
            // Qui potresti implementare logica di avanzamento diversa
            // per ora ignoro e lascio gestire a Lewis la risposta testuale
            appendOutput("Scelta non gestita nel refactor StoryEngine.");
        } catch (NumberFormatException ex) {
            String risposta = lewis.interpretaComandoDaGUI(input);
            appendOutput("> " + input);
            appendOutput(risposta);

            if (mappaVisibile) {
                mappaPanel.aggiornaMappa(lewis.getMappa(), lewis.getPosizioneX(), lewis.getPosizioneY());
            }
        }

        inputField.setText("");
    }

    private void appendOutput(String testo) {
        areaTesto.append(testo + "\n");
        areaTesto.setCaretPosition(areaTesto.getDocument().getLength());
    }

    private void aggiornaAreaTesto(String testo) {
        areaTesto.setText(testo + "\n\n");
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

    private void initComponents() {
        Color testo = new Color(220, 220, 220);
        Font fontMonospace = new Font("Consolas", Font.PLAIN, 14);

        backgroundPanel = new AnimatedPanel();
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout(10, 10));

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

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setOpaque(false);

        inputField = MatrixStyleUtils.createStyledTextField();

        inviaButton = createStyledButton("Invia", IconFactory.createSendIcon(16));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(inviaButton, BorderLayout.EAST);

        continuaButton = createStyledButton("Continua", IconFactory.createSendIcon(16));
        JPanel continuaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        continuaPanel.setOpaque(false);
        continuaPanel.add(continuaButton);
        continuaButton.setEnabled(true);

        saltaButton = createStyledButton("Salta", IconFactory.createSkipIcon(16));
        saltaButton.setEnabled(false);
        continuaPanel.add(saltaButton);
        saltaButton.setVisible(false);

        saltaButton.addActionListener(e -> {
            if (testoInScrittura) {
                timer.stop();
                aggiornaAreaTesto(testoCompleto);
                testoInScrittura = false;
                saltaButton.setEnabled(false);
                saltaButton.setVisible(false);
                continuaButton.setEnabled(true);
                continuaButton.setVisible(true);
            }
        });

        JPanel bottomCombinedPanel = new JPanel(new BorderLayout());
        bottomCombinedPanel.setOpaque(false);
        bottomCombinedPanel.add(inputPanel, BorderLayout.NORTH);
        bottomCombinedPanel.add(continuaPanel, BorderLayout.SOUTH);
        backgroundPanel.add(bottomCombinedPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);
        inventarioButton = createStyledButton("Inventario", IconFactory.createInventoryIcon(16));
        mappaButton = createStyledButton("Mappa", IconFactory.createMapIcon(16));
        topPanel.add(inventarioButton);
        topPanel.add(mappaButton);
        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        Inventario inventario = new Inventario();
        inventarioPanel = new InventarioPanel(inventario);
        inventarioPanel.setVisible(inventarioVisibile);
        inventarioPanel.setPreferredSize(new Dimension(250, 400)); // Adjust size as needed
        backgroundPanel.add(inventarioPanel, BorderLayout.WEST);
        inventarioPanel.aggiornaInventario();

        mappaPanel = new MappaPanel();
        mappaPanel.setVisible(false);
        backgroundPanel.add(mappaPanel, BorderLayout.EAST);

        inputField.setEnabled(false);
        inviaButton.setEnabled(false);
        inviaButton.setVisible(false);
    }

    private void avviaModalitaEsplorazione() {
        // Carica sceneDefault se non già caricate
        Stanza stanzaCorrente = lewis.getMappa().getStanza(lewis.getPosizione());
        String nomeStanzaCorrente = stanzaCorrente.getNome();

        sceneManager.getSceneDefault(nomeStanzaCorrente.toLowerCase()).ifPresent(scena -> {
            aggiornaAreaTesto(scena.getDescrizione());
        });


        appendOutput("\nPuoi muoverti usando i comandi, esamina bene ogni stanza...");
    }

}
