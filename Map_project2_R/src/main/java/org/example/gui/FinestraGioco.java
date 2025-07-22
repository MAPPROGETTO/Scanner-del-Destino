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
    private JButton continuaButton, indietroButton;
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
        inputField.setEnabled(false);

        // Inizializza mappa con le scene default già caricate
        Mappa mappa = new Mappa(8, 12, sceneManager.getScenePerMappa());

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

        } else {
            // Fine delle scene iniziali, abilita input
            areaTesto.append("\nHai raggiunto la fine della storia iniziale.\n");
            inputField.setEnabled(true);
            inviaButton.setEnabled(true);
            inviaButton.setVisible(true);
        }
    }

    // Da aggiungere per il bottone "Indietro"
    private void mostraScenaInizialePrecedente() {
        Optional<Scena> precedente = storyEngine.precedenteScenaIniziale();

        if (precedente.isPresent()) {
            scena = precedente.get();
            animaTesto(scena.mostra());
        }
    }

    private void animaTesto(String testo) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        System.out.println("animaTesto chiamato");
        testoCompleto = testo;
        areaTesto.setText("");
        testoInScrittura = true;
        saltaButton.setEnabled(true);
        saltaButton.setVisible(true);
        continuaButton.setEnabled(false);
        continuaButton.setVisible(false);
        indietroButton.setEnabled(false);
        indietroButton.setVisible(false);

        final int[] indice = {0};
        timer = new Timer(30, null);
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (indice[0] < testo.length()) {
                    areaTesto.append(String.valueOf(testo.charAt(indice[0])));
                    indice[0]++;
                } else {
                    try {
                        timer.stop();
                        System.out.println("puoiTornareIndietro: " + storyEngine.puoiTornareIndietro());
                        areaTesto.append("\n\n");
                        testoInScrittura = false;
                        continuaButton.setVisible(true);
                        continuaButton.setEnabled(true);
                        saltaButton.setVisible(false);
                        aggiornaPulsanti();
                        System.out.println("continuaButton visibile: " + continuaButton.isVisible());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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
                timer.stop();
                aggiornaAreaTesto(testoCompleto);
                testoInScrittura = false;
                // Non gestire qui saltaButton/indietroButton
                continuaButton.setEnabled(true);
                continuaButton.setVisible(true);
                return;
            }
            if (storyEngine.haAltreSceneIniziali()) {
                mostraProssimaScena();
            } else {
                continuaButton.setVisible(false);
                indietroButton.setVisible(false);
                inviaButton.setEnabled(false);
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
                // Non gestire qui indietroButton
                saltaButton.setEnabled(false);
                saltaButton.setVisible(false);
                continuaButton.setEnabled(true);
                continuaButton.setVisible(true);
                aggiornaPulsanti();
            }
        });

        indietroButton.addActionListener(e -> {
            if (testoInScrittura) {
                timer.stop();
                aggiornaAreaTesto(testoCompleto);
                testoInScrittura = false;
                return;
            }
            mostraScenaInizialePrecedente();
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
        inputField.setText("");
        String risposta;

        if (input.toLowerCase().startsWith("usa ")) {
            String oggetto = input.substring(4).trim();
            risposta = lewis.usaOggetto(oggetto);
        } else if (input.matches("(?i)(nord|sud|est|ovest|n|s|e|o)")) {
            risposta = lewis.muoviEsploratore(input.toLowerCase());
        } else {
            risposta = lewis.interpretaComandoDaGUI(input);
        }

        appendOutput("> " + input);
        appendOutput(risposta);

        if (mappaVisibile) {
            mappaPanel.aggiornaMappa(lewis.getMappa(), lewis.getPosizioneX(), lewis.getPosizioneY());
        }
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

        indietroButton = MatrixStyleUtils.createStyledButton("Indietro", IconFactory.createSendIcon(16));
        indietroButton.setEnabled(false);
        continuaPanel.add(indietroButton);
        indietroButton.setVisible(false);

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
    private void aggiornaPulsanti() {
        boolean mostraIndietro = storyEngine.puoiTornareIndietro() && continuaButton.isVisible() && !saltaButton.isVisible();
        indietroButton.setEnabled(mostraIndietro);
        indietroButton.setVisible(mostraIndietro);
    }

}