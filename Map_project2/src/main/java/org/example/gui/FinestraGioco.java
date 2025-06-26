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
    private JButton inventarioButton = MatrixStyleUtils.createStyledButton("Inventario", IconFactory.createInventoryIcon(16));
    private JButton mappaButton = MatrixStyleUtils.createStyledButton("Mappa", IconFactory.createMapIcon(16));
    private JButton saltaButton;
    private JButton continuaButton;
    private Scena scena;
    private Lewis lewis;
    private AnimatedPanel backgroundPanel;
    private MappaPanel mappaPanel;
    private GestoreInputGUI inputGest;
    private JScrollPane scrollPane;
    private JPanel bottomCombinedPanel;
    private JPanel topPanel;
    private Inventario inventario;
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

    private void initComponents() {

        backgroundPanel = new AnimatedPanel();
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout(10, 10));

        areaTesto = MatrixStyleUtils.createStyledTextArea();

        scrollPane = MatrixStyleUtils.createTransparentScrollPane(areaTesto);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setOpaque(false);

        inputField = MatrixStyleUtils.createStyledTextField();

        inviaButton = MatrixStyleUtils.createStyledButton("Invia", IconFactory.createSendIcon(16));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(inviaButton, BorderLayout.EAST);

        continuaButton = MatrixStyleUtils.createStyledButton("Continua", IconFactory.createSendIcon(16));
        JPanel continuaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        continuaPanel.setOpaque(false);
        continuaPanel.add(continuaButton);
        continuaButton.setEnabled(true);

        saltaButton = MatrixStyleUtils.createStyledButton("Salta", IconFactory.createSkipIcon(16));
        saltaButton.setEnabled(false);
        continuaPanel.add(saltaButton);
        saltaButton.setVisible(false);

        bottomCombinedPanel = MatrixStyleUtils.bottomCombinedPanel(inputPanel, continuaPanel);
        backgroundPanel.add(bottomCombinedPanel, BorderLayout.SOUTH);

        topPanel = MatrixStyleUtils.createTopPanelWithMatrixBorder(mappaButton, inventarioButton);
        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        inventario = new Inventario();
        inventarioPanel = MatrixStyleUtils.createInventarioPanel(inventario,inventarioVisibile);
        backgroundPanel.add(inventarioPanel, BorderLayout.WEST);


        mappaPanel = new MappaPanel();
        mappaPanel.setVisible(false);
        backgroundPanel.add(mappaPanel, BorderLayout.EAST);
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