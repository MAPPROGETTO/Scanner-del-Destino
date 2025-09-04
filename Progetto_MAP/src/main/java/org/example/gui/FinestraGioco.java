package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.example.utils.GestoreInput;

import org.example.inventario.Inventario;

public class FinestraGioco extends JFrame implements Serializable {
    private enum ModalitaStoria { NONE, INTRO, DIALOGO, FINALE }
    private ModalitaStoria modalita = ModalitaStoria.NONE;
    private boolean dialogMode = false;
    private boolean finaleMode = false;
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
    private GestoreInput gestoreInput;
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
    private int indiceFinale = 0;

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
        modalita = ModalitaStoria.INTRO;   // siamo nell’introduzione
        inputField.setEnabled(false);
        inviaButton.setEnabled(false);
        continuaButton.setVisible(true);
        saltaButton.setVisible(true);
        indietroButton.setVisible(false);


        lewis.setMappaPanel(mappaPanel);
        gestoreInput = new GestoreInput(lewis);

        mostraProssimaScena();
    }


    private void mostraProssimaScena() {
        Optional<Scena> prossima = storyEngine.prossimaScenaIniziale();

        if (prossima.isPresent()) {
            scena = prossima.get();
            animaTesto(scena.mostra());

        } else {
            // Fine delle scene iniziali, abilita ingresso
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

    public void mostraDialogoBoss() {
        dialogMode = true;

        inputField.setEnabled(false);
        inviaButton.setEnabled(false);

        continuaButton.setVisible(true);
        continuaButton.setEnabled(true);

        saltaButton.setVisible(true);
        saltaButton.setEnabled(true);

        indietroButton.setVisible(false);
        indietroButton.setEnabled(false);

        storyEngine.resetDialogoFinale();
        mostraBattutaBossCorrente();
    }

    private void mostraBattutaBossCorrente() {
        storyEngine.prossimaBattutaBoss().ifPresentOrElse(b -> {
            animaTesto(b.getDescrizione());
            aggiornaPulsanti();
        }, () -> {
            // FINE DIALOGO: parte subito la schermata dei TITOLI DI CODA
            terminaDialogoEMostraTitoliDiCoda();
        });
    }

    private void mostraBattutaBossPrecedente() {
        storyEngine.precedenteBattutaBoss().ifPresent(b -> {
            animaTesto(b.getDescrizione());
            aggiornaPulsanti();
        });
    }

    private void terminaDialogoEMostraTitoliDiCoda() {
        dialogMode = false;

        // Nascondi i controlli testuali
        continuaButton.setVisible(false);
        saltaButton.setVisible(false);
        indietroButton.setVisible(false);
        mappaButton.setVisible(false);
        inventarioButton.setVisible(false);
        inviaButton.setVisible(false);
        inputField.setVisible(false);

        // Mostra i titoli di coda (grafici) e al termine torna al menu
        // Quando termina il dialogo finale
        TitoliDiCodaPanel credits = new TitoliDiCodaPanel(Arrays.asList(
                "Scanner del Destino",
                "",
                "Ideato da: Gabriele Specchio",
                "Storia: Gabriele Specchio",
                "Programmazione: Gabriele Specchio",
                "Grafica: ...",
                "",
                "Grazie per aver giocato!"
        ));

        // Mostra overlay nella finestra
        getContentPane().removeAll();
        getContentPane().add(credits, BorderLayout.CENTER);
        revalidate();
        repaint();

        // Avvia animazione, poi chiudi/riapri menu
        credits.startAnimation(() -> {
            JOptionPane.showMessageDialog(this,
                    "Fine del gioco. Alla prossima avventura!");
            dispose(); // chiude la finestra corrente
            System.exit(0); // termina il programma
        });
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
                        System.out.println("puoiTornareIndietro: " + puoTornareIndietroCorrente());
                        areaTesto.append("\n\n");
                        testoInScrittura = false;

                        // Stato post-scrittura = come dopo "Salta"
                        continuaButton.setVisible(true);
                        continuaButton.setEnabled(true);

                        saltaButton.setVisible(false);
                        saltaButton.setEnabled(false);

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
                if (timer != null && timer.isRunning()) timer.stop();
                aggiornaAreaTesto(testoCompleto);
                testoInScrittura = false;
                continuaButton.setEnabled(true);
                continuaButton.setVisible(true);
                return;
            }

            if (dialogMode) {
                mostraBattutaBossCorrente();          // avanza il DIALOGO
                return;
            }
            if (finaleMode) {
                mostraScenaFinaleCorrente();          // se tieni anche un epilogo testuale
                return;
            }

            // === flusso SCENE INIZIALI (TUO CODICE) ===
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

        // SALTA
        saltaButton.addActionListener(e -> {
            if (testoInScrittura) {
                if (timer != null && timer.isRunning()) timer.stop();
                aggiornaAreaTesto(testoCompleto);
                testoInScrittura = false;

                // Stato post-skip: identico alla fine naturale della digitazione
                saltaButton.setEnabled(false);
                saltaButton.setVisible(false);

                continuaButton.setEnabled(true);
                continuaButton.setVisible(true);

                // mostra/nascondi “Indietro” in base alla modalità e agli indici
                aggiornaPulsanti();
            }
        });

        indietroButton.addActionListener(e -> {
            if (testoInScrittura) {
                if (timer != null && timer.isRunning()) timer.stop();
                aggiornaAreaTesto(testoCompleto);
                testoInScrittura = false;
                return;
            }

            if (dialogMode) {
                mostraBattutaBossPrecedente();        // torna indietro nel DIALOGO
                return;
            }
            if (finaleMode) {
                mostraScenaFinalePrecedente();        // torna indietro nell’EPILOGO (se lo usi)
                return;
            }

            // === flusso SCENE INIZIALI (TUO CODICE) ===
            mostraScenaInizialePrecedente();
        });


        // MAPPA
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

        // INVENTARIO
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
        gestoreInput = new GestoreInput(this.lewis);
        if (input.isEmpty()) return;
        inputField.setText("");
        String risposta;

        if (input.toLowerCase().startsWith("usa ")) {
            String oggetto = input.substring(4).trim();
            risposta = lewis.usaOggetto(oggetto);
        } else if (input.matches("(?i)(nord|sud|est|ovest|n|s|e|o)")) {
            risposta = gestoreInput.muoviEsploratore(input.toLowerCase());
        } else {
            risposta = gestoreInput.interpretaComandoDaGUI(input);
        }

        if (input.toLowerCase().startsWith("inserisci")) {
            String resto = input.substring("inserisci".length()).trim();
            if (!resto.matches("\\d{4}")) {
                appendOutput("> " + input);
                appendOutput("> Apro il tastierino numerico...");
                showSecretCodeDialog();
                inputField.setText("");
                return;
            }
        }

        appendOutput("> " + input);
        appendOutput(risposta);

        if (mappaVisibile) {
            mappaPanel.aggiornaMappa(lewis.getMappa(), lewis.getPosizioneX(), lewis.getPosizioneY());
        }
        // Se la porta segreta ha richiesto codice, apri il dialog
        if (risposta != null && risposta.toLowerCase().contains("tastierino numerico")) {
            showSecretCodeDialog();
        }
    }

    public void appendOutput(String testo) {
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

    private boolean puoTornareIndietroCorrente() {
        if (dialogMode) {
            return storyEngine.puoiTornareIndietroDialogo();   // vedi nota sotto
        } else if (finaleMode) {
            return storyEngine.puoiTornareIndietroFinale();
        } else {
            return storyEngine.puoiTornareIndietro();
        }
    }

    private void aggiornaPulsanti() {
        boolean mostraIndietro =
                puoTornareIndietroCorrente()
                        && continuaButton.isVisible()
                        && !saltaButton.isVisible()
                        && !testoInScrittura;

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

    public void terminaAssemblaggioScanner() {
        lewis.setScannerAssemblato(true);
        appendOutput("> Lo Scanner del Destino prende vita! ⚡");
        mostraDialogoBoss();   // 1) parte il dialogo col boss
        // Al termine del dialogo, mostraSceneFinali() viene chiamato automaticamente
    }

    public void showSecretCodeDialog() {
        final String code = lewis.getCodicePortaSegreta();
        SafeCodeDialog.mostra(this, code, () -> {
            // on unlock: marca i flag e apri la stanza segreta
            lewis.setCodiceSegretoLetto(true);
            lewis.setCodiceSegretoSbloccato(true);

            Posizione posSegreta = new Posizione(2, 2);
            Stanza stanzaSegreta = mappa.getStanza(posSegreta);
            if (stanzaSegreta != null) {
                stanzaSegreta.setChiusa(false);
            }

            // Se l’utente aveva provato a muoversi verso la porta, ripeti il movimento
            String dir = lewis.getUltimaDirezioneTentata();
            if (dir != null) {
                String risposta = gestoreInput.muoviEsploratore(dir);
                appendOutput(risposta);
            } else {
                appendOutput("> Codice corretto. La porta della stanza segreta si apre.");
            }

            if (mappaPanel != null) {
                mappaPanel.aggiornaMappa(mappa, lewis.getPosizioneX(), lewis.getPosizioneY());
            }
        });
    }

    public void showScannerAssembly() {
        // facoltativo: verifica stanza corrente
        Stanza curr = lewis.getMappa().getStanza(lewis.getPosizione());
        if (curr == null || !"Stanza Segreta".equalsIgnoreCase(curr.getNome())) {
            appendOutput("> Devi essere nella Stanza Segreta per montare lo scanner.");
            return;
        }

        // Se già montato, evita di ripetere
        if (lewis.isScannerAssemblato()) {
            appendOutput("> Lo scanner è già assemblato.");
            return;
        }

        // opzionale: richiedi che il giocatore abbia tutti i pezzi
        if (!inventario.tuttiOggettiRaccolti()) {
            appendOutput("> Ti mancano dei pezzi per assemblare lo scanner.");
            return;
        }

        ScannerAssemblyDialog dlg = new ScannerAssemblyDialog(this, new ScannerAssemblyDialog.Listener() {
            @Override public void onCompleted() {
                // marca stato + eventuale logica gioco
                lewis.setScannerAssemblato(true);

                // (opzionale) rimuovi i 4 pezzi dall'inventario:
                inventario.rimuovi("batteria");
                inventario.rimuovi("motore");
                inventario.rimuovi("elica");
                inventario.rimuovi("schermo");
                if (inventarioPanel != null && inventarioVisibile) {
                    inventarioPanel.aggiornaInventario();
                }

                appendOutput("> Lo Scanner del Destino prende vita! ⚡");
                mostraDialogoBoss();   // prima dialogo...
                // quando finisce, mostraSceneFinali() viene chiamato automaticamente
            }
            @Override public void onCancelled() {
                appendOutput("> Montaggio annullato.");
            }
        });

        dlg.setVisible(true);
    }

    // src/main/java/org/example/gui/FinestraGioco.java
    public void mostraSceneFinali() {
        // entriamo nella modalità finale: blocco input gioco e mostro i pulsanti storia
        finaleMode = true;

        inputField.setEnabled(false);
        inviaButton.setEnabled(false);

        continuaButton.setVisible(true);
        continuaButton.setEnabled(true);

        saltaButton.setVisible(true);
        saltaButton.setEnabled(true);

        indietroButton.setVisible(false);
        indietroButton.setEnabled(false);

        storyEngine.resetFinale();
        mostraScenaFinaleCorrente();
    }

    private void mostraScenaFinaleCorrente() {
        Optional<Scena> scenaOpt = storyEngine.prossimaScenaFinale();
        if (scenaOpt.isPresent()) {
            Scena scena = scenaOpt.get();
            animaTesto(scena.getDescrizione());
            aggiornaPulsanti();
        } else {
            // Non ci sono più scene finali → chiudi la modalità
            areaTesto.append("\nFine dell’epilogo.\n");

            finaleMode = false;

            continuaButton.setVisible(false);
            saltaButton.setVisible(false);
            indietroButton.setVisible(false);

            inputField.setEnabled(true);
            inviaButton.setEnabled(true);
            inviaButton.setVisible(true);
        }
    }


    private void mostraScenaFinalePrecedente() {
        storyEngine.precedenteScenaFinale().ifPresent(scena -> {
            animaTesto(scena.getDescrizione());
            aggiornaPulsanti();
        });
    }


    private void aggiornaPulsantiFinali() {
        continuaButton.setVisible(true);
        continuaButton.setEnabled(true);
        inviaButton.setEnabled(false);
        inputField.setEnabled(false);
        saltaButton.setVisible(true);
        saltaButton.setEnabled(true);
        indietroButton.setVisible(storyEngine.puoiTornareIndietro());
        indietroButton.setEnabled(storyEngine.puoiTornareIndietro());

        for (ActionListener al : continuaButton.getActionListeners()) continuaButton.removeActionListener(al);
        continuaButton.addActionListener(e -> {
            if (testoInScrittura) {
                if (timer != null && timer.isRunning()) timer.stop();
                aggiornaAreaTesto(testoCompleto);
                testoInScrittura = false;
                // Non gestire qui saltaButton/indietroButton
                continuaButton.setEnabled(true);
                continuaButton.setVisible(true);
                return;
            }

            if (!finaleMode) {
                // Flusso SCENE INIZIALI (tuo codice invariato)
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
            } else {
                // Flusso SCENE FINALI (uguale alle iniziali)
                mostraScenaFinaleCorrente();
            }
        });


        for (ActionListener al : saltaButton.getActionListeners()) saltaButton.removeActionListener(al);
        saltaButton.addActionListener(e -> {
            if (testoInScrittura) {
                if (timer != null && timer.isRunning()) timer.stop();
                aggiornaAreaTesto(testoCompleto);
                testoInScrittura = false;

                // PRIMA nascondi, POI disabilita (evita il repaint in stato disabled con icona nulla)
                saltaButton.setVisible(false);
                saltaButton.setEnabled(false);

                continuaButton.setEnabled(true);
                continuaButton.setVisible(true);

                aggiornaPulsanti();
            }
        });

        for (ActionListener al : indietroButton.getActionListeners()) indietroButton.removeActionListener(al);
        indietroButton.addActionListener(e -> {
            if (testoInScrittura) {
                if (timer != null && timer.isRunning()) timer.stop();
                aggiornaAreaTesto(testoCompleto);
                testoInScrittura = false;
                return;
            }

            if (!finaleMode) {
                // indietro sulle scene iniziali (tuo codice invariato)
                mostraScenaInizialePrecedente();
            } else {
                // indietro sulle scene finali (uguale alle iniziali)
                mostraScenaFinalePrecedente();
            }
        });

    }


}