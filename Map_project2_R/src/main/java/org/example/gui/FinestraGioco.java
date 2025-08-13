package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Optional;

import org.example.mappa.Mappa;
import org.example.mappa.Posizione;
import org.example.mappa.Stanza;
import org.example.model.Lewis;
import org.example.model.StatoGioco;
import org.example.story.Scena;
import org.example.story.SceneLoader;
import org.example.story.StoryEngine;
import org.example.story.SceneManager;
import org.example.utils.GameStateMapper;
import org.example.utils.GestoreInputGUI;
import org.example.inventario.Inventario;

public class FinestraGioco extends JFrame implements Serializable {
    private JLabel label;
    private Timer timer;
    private JTextArea areaTesto;
    private JTextField inputField;
    private JButton inviaButton;
    private transient JButton inventarioButton = MatrixStyleUtils.createStyledButton("Inventario", IconFactory.createInventoryIcon(16));
    private transient JButton mappaButton = MatrixStyleUtils.createStyledButton("Mappa", IconFactory.createMapIcon(16));
    private JButton saltaButton;
    private JButton continuaButton, indietroButton;
    private Scena scena;
    private Mappa mappa;
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
    private transient boolean ripristinoDaSalvataggio = false;
    // --- slot attualmente associato alla sessione (null se partita nuova mai salvata) ---
    private Integer slotCaricatoCorrente = null;
    private boolean newGameInitialized = false;

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
    public FinestraGioco(Lewis lewis, Mappa mappa, Inventario inventario, StoryEngine storyEngine) {
        this.lewis = lewis;
        this.storyEngine = storyEngine;
        this.inventario = inventario;
        this.mappa = mappa;
        this.sceneManager = storyEngine.getSceneManager();

        label = new JLabel("");
        add(label);
        setTitle("Scanner del Destino");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inputGest = new GestoreInputGUI();

        initComponents();         // costruisci UI
        setupEventHandlers();     // bind eventi

        // collega il pannello mappa al Lewis già creato
        lewis.setMappaPanel(mappaPanel);

        // NON chiamare initLogic() qui (quello fa partire l'intro!)
        // La GUI la popoleremo con applyLoadedState(...) per i salvataggi
        // e con initLogicNewGame() per una nuova partita (vedi sotto)

        setVisible(true);
    }


    private void initLogic() {
        // Carica tutte le scene in un solo colpo
        if (newGameInitialized) return; // evita doppio avvio
        newGameInitialized = true;

        sceneManager = new SceneManager();
        loader = new SceneLoader(sceneManager);
        loader.caricaTutteLeScene();
        inputField.setEnabled(false);

        this.mappa = new Mappa(8, 12, sceneManager.getScenePerMappa());
        if (this.inventario == null) this.inventario = new Inventario();
        this.inventario.inizializzaNuovaPartita();
        this.lewis = new Lewis(this.mappa, inputGest, this.inventario, sceneManager);
        this.storyEngine = new StoryEngine(sceneManager);

        lewis.setMappaPanel(mappaPanel);

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
                storyEngine.completaSceneIniziali();
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

        if (this.inventario == null) {
            this.inventario = new Inventario(); // UNA SOLA VOLTA
        }
        inventarioPanel = MatrixStyleUtils.createInventarioPanel(inventario,inventarioVisibile);
        backgroundPanel.add(inventarioPanel, BorderLayout.WEST);


        mappaPanel = new MappaPanel();
        mappaPanel.setVisible(false);
        backgroundPanel.add(mappaPanel, BorderLayout.EAST);
    }

    private void avviaModalitaEsplorazione() {
        if (sceneManager == null) {
            System.err.println("[WARN] sceneManager nullo in avviaModalitaEsplorazione");
            return;
        }
        // Carica sceneDefault se non già caricate
        Stanza stanzaCorrente = lewis.getMappa().getStanza(lewis.getPosizione());
        String nomeStanzaCorrente = stanzaCorrente.getNome();

        sceneManager.getSceneDefault(nomeStanzaCorrente.toLowerCase()).ifPresent(scena -> {
            aggiornaAreaTesto(scena.getDescrizione());
        });

        appendOutput("\nPuoi muoverti usando i comandi, esamina bene ogni stanza...");
    }

    public static FinestraGioco createNewGameWindow() {
        // Il costruttore senza argomenti chiama già initLogic() e avvia l’intro.
        return new FinestraGioco();
    }

    // FinestraGioco.java
    public void returnToMenu() {
        try {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
        } catch (Exception ignore) {}

        SwingUtilities.invokeLater(() -> new GameMenuScreen().setVisible(true));
        dispose();
    }


    public static FinestraGioco createLoadedGameWindow(StatoGioco s, int slotCaricato) {
        SceneManager sceneManager = new SceneManager();
        SceneLoader loader = new SceneLoader(sceneManager);
        loader.caricaTutteLeScene();

        Mappa mappa = new Mappa(8, 12, sceneManager.getScenePerMappa());
        Inventario inventario = new Inventario();
        GestoreInputGUI inputGUI = new GestoreInputGUI();
        Lewis lewis = new Lewis(mappa, inputGUI, inventario, sceneManager);
        StoryEngine storyEngine = new StoryEngine(sceneManager);

        // finestra “iniettata” (che NON fa partire l’intro)
        FinestraGioco gioco = new FinestraGioco(lewis, mappa, inventario, storyEngine);
        gioco.setSlotCaricatoCorrente(slotCaricato); // <-- qui agganciamo lo slot attivo
        // 3) Applica lo stato salvato (posizione, piano, inventario, flag intro), senza intro
        gioco.applyLoadedState(s);

        return gioco;
    }


    private void aggiornaPulsanti() {
        boolean mostraIndietro = storyEngine.puoiTornareIndietro() && continuaButton.isVisible() && !saltaButton.isVisible();
        indietroButton.setEnabled(mostraIndietro);
        indietroButton.setVisible(mostraIndietro);
    }

    public void setRipristinoDaSalvataggio(boolean v) { this.ripristinoDaSalvataggio = v; }
    public boolean isRipristinoDaSalvataggio() { return ripristinoDaSalvataggio; }
    public Integer getSlotCaricatoCorrente() { return slotCaricatoCorrente; }
    public void setSlotCaricatoCorrente(Integer slot) { this.slotCaricatoCorrente = slot; }

    // in FinestraGioco
    public void applyLoadedState(StatoGioco s) {
        setRipristinoDaSalvataggio(true);

        // Applica i dati
        GameStateMapper.applyTo(s, lewis, inventario, mappa, storyEngine);

        // Riallinea la Stanza Segreta se hai già tutti i pezzi
        if (inventario.tuttiOggettiRaccolti()) {
            Posizione posSegreta = new Posizione(2, 2);
            Posizione posAdiacente = new Posizione(6, 2);
            Stanza stanzaSegreta = mappa.getStanza(posSegreta);
            Stanza stanzaAdiacente = mappa.getStanza(posAdiacente);
            if (stanzaSegreta != null && stanzaAdiacente != null) {
                stanzaSegreta.setEst(true);
                stanzaAdiacente.setOvest(true);
                if (mappaPanel != null) mappaPanel.controllaStanzaSegretaAperta(inventario);
            }
        }

        // Nascondi completamente i controlli della storia quando carichi una partita
        if (continuaButton != null) {
            continuaButton.setVisible(false);
            continuaButton.setEnabled(false);
        }
        if (indietroButton != null) {
            indietroButton.setVisible(false);
            indietroButton.setEnabled(false);
        }
        if (saltaButton != null) {
            saltaButton.setVisible(false);
            saltaButton.setEnabled(false);
        }

        // Aggiorna UI per modalità esplorazione
        areaTesto.setText("");
        inputField.setEnabled(true);
        inviaButton.setEnabled(true);
        inviaButton.setVisible(true);

        if (mappaPanel != null) {
            mappaPanel.setVisible(mappaVisibile); // rispetta lo stato del toggle
            mappaPanel.aggiornaMappa(lewis.getMappa(), lewis.getPosizioneX(), lewis.getPosizioneY());
        }
        if (inventarioPanel != null && inventarioVisibile) {
            inventarioPanel.aggiornaInventario();
        }

        // Mostra la descrizione della stanza corrente con il tuo flusso esplorazione
        avviaModalitaEsplorazione();

        setRipristinoDaSalvataggio(false);
    }

}