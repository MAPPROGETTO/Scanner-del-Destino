package org.example.engine.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import org.example.mappa.Mappa;
import org.example.mappa.Posizione;
import org.example.mappa.Stanza;
import org.example.model.Lewis;
import org.example.model.StatoGioco;
import org.example.story.*;
import org.example.utils.GameStateMapper;
import org.example.utils.GestoreInput;
import org.example.inventario.Inventario;
/**
 * Finestra principale del gioco, con gestione della storia, dialoghi, esplorazione,
 * inventario, mappa e salvataggio/caricamento.
 * * Implementa un'interfaccia in stile "Matrix" con animazioni e design coerente.
 * * Gestisce l'introduzione, il dialogo con il boss finale e l'epilogo.
 * * @version 2.0
 *
 */
public class FinestraGioco extends JFrame implements Serializable {
    private enum ModalitaStoria { NONE, INTRO, DIALOGO, FINALE }
    private ModalitaStoria modalita = ModalitaStoria.NONE;
    private boolean dialogMode = false;
    private JLabel label;
    private Timer timer;
    private JTextArea areaTesto;
    private JTextField inputField;
    private JButton inviaButton;
    private transient JButton inventarioButton;
    private transient JButton mappaButton;
    static {
        // Evita che il Metal L&F generi icone "disabled" partendo da immagini potenzialmente nulle
        UIManager.put("Button.disabledIcon", null);
        UIManager.put("Button.disabledSelectedIcon", null);
    }
    private JButton saltaButton;
    private JButton continuaButton, indietroButton;
    private Scena scena;
    private Mappa mappa;
    private Lewis lewis;
    private AnimatedPanel backgroundPanel;
    private MappaPanel mappaPanel;
    private JScrollPane scrollPane;
    private JPanel bottomCombinedPanel;
    private JPanel topPanel;
    private Inventario inventario;
    private InventarioPanel inventarioPanel;
    private GestoreInput commandService;
    private boolean mappaVisibile = false;
    private boolean inventarioVisibile = false;
    private boolean testoInScrittura = false;
    private String testoCompleto = "";
    private StoryEngine storyEngine;
    private SceneManager sceneManager;
    private transient boolean ripristinoDaSalvataggio = false;
    // --- slot attualmente associato alla sessione (null se partita nuova mai salvata) ---
    private Integer slotCaricatoCorrente = null;
    private boolean newGameInitialized = false;
    // evita ri-avvii/azzeramenti del finale
    private boolean epilogoAvviato = false;
    private int indiceFinale = 0;
    /** * Costruttore per una nuova partita.
     * Inizializza tutti i componenti della GUI e avvia l'introduzione.
     */
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
    /** * Costruttore per caricare una partita esistente.
     * Inizializza la GUI senza avviare l'introduzione.
     * @param lewis Istanza di Lewis già configurata.
     * @param mappa Istanza della mappa di gioco.
     * @param inventario Istanza dell'inventario del giocatore.
     * @param storyEngine Istanza del motore della storia.
     */
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

        initComponents();         // costruisci UI
        setupEventHandlers();     // bind eventi

        // collega il pannello mappa al Lewis già creato
        lewis.setMappaPanel(mappaPanel);

        // NON chiamare initLogic() qui (quello fa partire l'intro!)
        // La GUI la popoleremo con applyLoadedState(...) per i salvataggi
        // e con initLogicNewGame() per una nuova partita (vedi sotto)

        setVisible(true);
    }
    /** * Inizializza la logica di gioco per una nuova partita.
     * Carica tutte le scene, imposta la mappa, l'inventario e Lewis.
     * Avvia l'introduzione della storia.
     */
    private void initLogic() {
        // Carica tutte le scene in un solo colpo
        if (newGameInitialized) return; // evita doppio avvio
        newGameInitialized = true;

        sceneManager = new SceneManager();
        // Carica le scene iniziali dal DB (intro_1, intro_2, …) e mettile nel manager
        List<String> intro = ScenesDb.inizialiSequenza();
        if (!intro.isEmpty()) {
            sceneManager.aggiungiSceneIniziali(intro.toArray(new String[0]));
        }

        inputField.setEnabled(false);

        this.mappa = new Mappa(8, 12, sceneManager.getScenePerMappa());
        if (this.inventario == null) this.inventario = new Inventario();
        this.inventario.inizializzaNuovaPartita();
        this.lewis = new Lewis(this.mappa, this.inventario, sceneManager);
        this.storyEngine = new StoryEngine(sceneManager);
        modalita = ModalitaStoria.INTRO;   // siamo nell’introduzione
        inputField.setEnabled(false);
        inviaButton.setEnabled(false);
        continuaButton.setVisible(true);
        saltaButton.setVisible(true);
        indietroButton.setVisible(false);

        lewis.setMappaPanel(mappaPanel);
        // --- Command Service: crea e collega a Lewis
        if (commandService == null) {
            commandService = new org.example.utils.GestoreInput(
                    lewis,
                    mappa,
                    inventario,
                    mappaPanel,
                    sceneManager,
                    storyEngine
            );
        }
        // assicura che Lewis deleghi al service (utile anche dopo reload UI)
        lewis.setCommandService(commandService);



        mostraProssimaScena();
    }

    /** * Mostra la scena iniziale successiva.
     * Se non ci sono più scene iniziali, abilita l'ingresso del giocatore.
     */
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

    /** * Mostra la scena finale successiva.
     * Se non ci sono più scene finali, termina l'epilogo.
     */
    private void mostraScenaInizialePrecedente() {
        Optional<Scena> precedente = storyEngine.precedenteScenaIniziale();

        if (precedente.isPresent()) {
            scena = precedente.get();
            animaTesto(scena.mostra());
        }
    }
    /** * Mostra la scena finale successiva.
     * Se non ci sono più scene finali, termina l'epilogo.
     */
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
    /** * Mostra la scena finale successiva.
     * Se non ci sono più scene finali, termina l'epilogo.
     */
    private void mostraBattutaBossCorrente() {
        storyEngine.prossimaBattutaBoss().ifPresentOrElse(b -> {
            animaTesto(b.getDescrizione());
            aggiornaPulsanti();
        }, () -> {
            // FINE DIALOGO → spegni il dialogo e avvia EPILOGO
            dialogMode = false; // <— QUESTA È LA CHIAVE!
            avviaEpilogoTestuale();
        });
    }

    private void avviaEpilogoTestuale() {
        if (epilogoAvviato) {
            System.out.println("[FINALE] avviaEpilogoTestuale() ignorato: già avviato");
            return;
        }
        epilogoAvviato = true;
        indiceFinale = 0; // reset SOLO qui

        modalita = ModalitaStoria.FINALE;

        inputField.setEnabled(false);
        inviaButton.setEnabled(false);

        continuaButton.setVisible(true);
        continuaButton.setEnabled(true);

        saltaButton.setVisible(true);
        saltaButton.setEnabled(true);

        indietroButton.setVisible(false);
        indietroButton.setEnabled(false);

        // niente reset su StoryEngine qui, usiamo indiceFinaleUI locale
        mostraScenaFinaleCorrente();
    }
    /** * Mostra la scena finale successiva.
     * Se non ci sono più scene finali, termina l'epilogo.
     */
    private void mostraBattutaBossPrecedente() {
        storyEngine.precedenteBattutaBoss().ifPresent(b -> {
            animaTesto(b.getDescrizione());
            aggiornaPulsanti();
        });
    }
    /** * Mostra la scena finale successiva.
     * Se non ci sono più scene finali, termina l'epilogo.
     */
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
                "— SCANNER DEL DESTINO —",
                "",
                "Ideato da: Gabriele Specchio, Marius Pascal Velondaza, Domenico Marsico",
                "Storia: Gabriele Specchio",
                "Programmazione: Gabriele Specchio, Marius Pascal Velondaza, Domenico Marsico",
                "Grafica: Gabriele Specchio, Marius Pascal Velondaza, Domenico Marsico",
                "",
                "Special Thanks: la pazienza infinita dei nostri PC",
                "e il caffè (molto caffè)...",
                "",
                "Consulenza tecnica: Fantasmi della Casa Abbandonata™",
                "Supervisione narrativa: L’Uomo con la Bombetta",
                "Beta testing: Wilbur (quando non dormiva)",
                "",
                "ATTENZIONE: nessun divano è stato maltrattato",
                "durante la realizzazione di questo gioco.",
                "",
                "Grazie per aver giocato!",
                "Ora... sei sicuro di aver trovato *tutti* i segreti? 😉"
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
    /** * Anima la scrittura del testo nella areaTesto.
     * Permette di saltare l'animazione e mostrare tutto il testo immediatamente.
     * @param testo Il testo da animare.
     */
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
    /** * Configura i gestori degli eventi per i vari pulsanti e campi di input.
     * Gestisce l'invio dei comandi, la navigazione della storia, la visualizzazione della mappa e dell'inventario.
     */
    private void setupEventHandlers() {
        inviaButton.addActionListener(e -> processInput());
        inputField.addActionListener(e -> processInput());

        continuaButton.addActionListener(e -> {
            // Se il testo è ancora in scrittura: completa subito
            if (testoInScrittura) {
                if (timer != null && timer.isRunning()) timer.stop();
                aggiornaAreaTesto(testoCompleto);
                testoInScrittura = false;
                continuaButton.setEnabled(true);
                continuaButton.setVisible(true);
            }

            // Dopo aver completato (o se già completo) → avanza sempre
            avanzaStoriaCorrente();
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
            // PATCH: usa modalita == FINALE al posto di finaleMode
            if (modalita == ModalitaStoria.FINALE) {
                mostraScenaFinalePrecedente();        // torna indietro nell’EPILOGO
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

    /** * Mostra un dialogo per l'inserimento del codice segreto.
     * Utilizzato per sbloccare porte o meccanismi speciali nel gioco.
     */
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
    /**
     * Mostra un dialogo per l'inserimento del codice segreto.
     * @param testo
     */
    public void appendOutput(String testo) {
        areaTesto.append(testo + "\n");
        areaTesto.setCaretPosition(areaTesto.getDocument().getLength());
    }
    /**
     * Aggiorna l'area di testo con il testo fornito.
     * @param testo
     */
    private void aggiornaAreaTesto(String testo) {
        areaTesto.setText(testo + "\n\n");
    }

    private static void sanitizeButton(JButton b) {
        if (b == null) return;
        Icon ic = b.getIcon();
        // Se l'icona è null o è una ImageIcon con image == null, rimuovila
        if (ic == null || (ic instanceof ImageIcon && ((ImageIcon) ic).getImage() == null)) {
            b.setIcon(null);
        }
        // In ogni caso, non chiedere al L&F di generare la versione disabled
        b.setDisabledIcon(null);
        b.setDisabledSelectedIcon(null);
    }

    private static Icon iconOrNull(Icon ic) {
        if (ic == null) return null;
        if (ic instanceof ImageIcon && ((ImageIcon) ic).getImage() == null) return null;
        return ic;
    }
    /**
     * Inizializza i componenti grafici della finestra di gioco.
     * Configura il layout, i pannelli, i bottoni e le aree di testo.
     * Imposta lo stile "Matrix" per l'interfaccia utente.
     * Utilizza la classe MatrixStyleUtils per creare componenti stilizzati.
     */
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

        // --- CREA QUI I BOTTONI CON LE ICONE, POI SANIFICA ---
        inviaButton = MatrixStyleUtils.createStyledButton("Invia",
                (ImageIcon) iconOrNull(IconFactory.createSendIcon(16)));

        continuaButton = MatrixStyleUtils.createStyledButton("Continua",
                (ImageIcon) iconOrNull(IconFactory.createSendIcon(16)));

        indietroButton = MatrixStyleUtils.createStyledButton("Indietro",
                (ImageIcon) iconOrNull(IconFactory.createSendIcon(16)));
        indietroButton.setEnabled(false);
        indietroButton.setVisible(false);

        saltaButton = MatrixStyleUtils.createStyledButton("Salta",
                (ImageIcon) iconOrNull(IconFactory.createSkipIcon(16)));
        saltaButton.setEnabled(false);
        saltaButton.setVisible(false);

        // --- QUI CREIAMO ANCHE I DUE BOTTONI PRIMA INIZIALIZZATI A CAMPO ---
        mappaButton = MatrixStyleUtils.createStyledButton("Mappa",
                (ImageIcon) iconOrNull(IconFactory.createMapIcon(16)));

        inventarioButton = MatrixStyleUtils.createStyledButton("Inventario",
                (ImageIcon) iconOrNull(IconFactory.createInventoryIcon(16)));

        // Sanifica TUTTI i bottoni che useremo
        sanitizeButton(inviaButton);
        sanitizeButton(continuaButton);
        sanitizeButton(indietroButton);
        sanitizeButton(saltaButton);
        sanitizeButton(mappaButton);
        sanitizeButton(inventarioButton);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(inviaButton, BorderLayout.EAST);

        JPanel continuaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        continuaPanel.setOpaque(false);
        continuaPanel.add(continuaButton);
        continuaButton.setEnabled(true);
        continuaPanel.add(indietroButton);
        continuaPanel.add(saltaButton);

        bottomCombinedPanel = MatrixStyleUtils.bottomCombinedPanel(inputPanel, continuaPanel);
        backgroundPanel.add(bottomCombinedPanel, BorderLayout.SOUTH);

        topPanel = MatrixStyleUtils.createTopPanelWithMatrixBorder(mappaButton, inventarioButton);
        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        if (this.inventario == null) {
            this.inventario = new Inventario(); // UNA SOLA VOLTA
        }
        inventarioPanel = MatrixStyleUtils.createInventarioPanel(inventario, inventarioVisibile);
        backgroundPanel.add(inventarioPanel, BorderLayout.WEST);

        mappaPanel = new MappaPanel();
        mappaPanel.setVisible(false);
        backgroundPanel.add(mappaPanel, BorderLayout.EAST);
    }

    // Avanzamento centralizzato: UN SOLO PUNTO DI VERITÀ
    // Metodo centralizzato per avanzare la storia in base alla modalità corrente
    private void avanzaStoriaCorrente() {
        // Se per qualsiasi motivo entriamo nel FINALE ma dialogMode è ancora true, spegnilo.
        if (modalita == ModalitaStoria.FINALE && dialogMode) dialogMode = false;

        if (dialogMode) { mostraBattutaBossCorrente(); return; }
        if (modalita == ModalitaStoria.FINALE) { mostraScenaFinaleCorrente(); return; }
        if (modalita == ModalitaStoria.INTRO) {
            if (storyEngine.haAltreSceneIniziali()) mostraProssimaScena();
            else {
                continuaButton.setVisible(false);
                indietroButton.setVisible(false);
                inviaButton.setEnabled(false);
                storyEngine.completaSceneIniziali();
                TimePortalSplashScreen.showWithCallback(() -> {
                    // 1) Stanza di start: prova prima "Salotto", poi "Soggiorno" come alias
                    String[] candidati = { "Salotto", "Soggiorno" };
                    Stanza start = null;
                    for (String nome : candidati) {
                        start = lewis.getMappa().getStanzaByNome(nome);
                        if (start != null) {
                            // 2) Imposta anche la POSIZIONE (serve per i comandi di movimento)
                            Posizione p = lewis.getMappa().posizioneDellaStanza(nome);
                            if (p != null) lewis.setPosizione(p.getX(), p.getY());
                            break;
                        }
                    }

                    // 3) Mostra la scena della stanza corrente usando il tuo entraInStanza
                    if (start != null) {
                        String testo = commandService.entraNellaStanza(start);
                        // ATTENZIONE: qui è meglio sovrascrivere il prologo, non appendere.
                        aggiornaAreaTesto(testo);
                    } else {
                        aggiornaAreaTesto("> ERRORE: stanza iniziale non trovata (prova a verificare il nome in Mappa)");
                    }

                    // 4) Prompt comandi + abilita input
                    appendOutput("\nPuoi muoverti usando i comandi, esamina bene ogni stanza...");
                    inputField.setEnabled(true);
                    inviaButton.setEnabled(true);
                    inviaButton.setVisible(true);
                });

            }
        }
    }
    /**
     * Avvia la modalità di esplorazione dopo l'introduzione.
     * Carica la descrizione della stanza corrente e abilita i comandi di movimento.
     * Aggiorna l'area di testo con le informazioni della stanza.
     * Utilizza il SceneManager per ottenere la descrizione della scena corrente.
     */
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
    /**
     * Torna al menu principale del gioco.
     * Chiude la finestra di gioco corrente e apre una nuova istanza del menu principale.
     * Ferma eventuali timer attivi prima di chiudere la finestra.
     */
    public void returnToMenu() {
        try {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
        } catch (Exception ignore) {}

        SwingUtilities.invokeLater(() -> new GameMenuScreen().setVisible(true));
        dispose();
    }

    /**
     * Crea una finestra di gioco caricando uno stato salvato.
     * 1) Crea la GUI senza avviare l’intro
     * 2) Crea mappa, inventario, Lewis, StoryEngine
     * 3) Applica lo stato salvato (posizione, piano, inventario, flag intro), senza intro
     * @param s
     * @param slotCaricato
     * @return L'istanza della finestra di gioco con lo stato caricato.
     */
    public static FinestraGioco createLoadedGameWindow(StatoGioco s, int slotCaricato) {
        SceneManager sceneManager = new SceneManager();

        Mappa mappa = new Mappa(8, 12, sceneManager.getScenePerMappa());
        Inventario inventario = new Inventario();
        Lewis lewis = new Lewis(mappa, inventario, sceneManager);
        StoryEngine storyEngine = new StoryEngine(sceneManager);

        // finestra “iniettata” (che NON fa partire l’intro)
        FinestraGioco gioco = new FinestraGioco(lewis, mappa, inventario, storyEngine);
        gioco.setSlotCaricatoCorrente(slotCaricato); // <-- qui agganciamo lo slot attivo
        // 3) Applica lo stato salvato (posizione, piano, inventario, flag intro), senza intro
        gioco.applyLoadedState(s);

        return gioco;
    }
    /**
     * Verifica se è possibile tornare indietro nella storia corrente.
     * Considera la modalità attuale (dialogo, finale, o normale).
     * @return true se è possibile tornare indietro, false altrimenti.
     */
    private boolean puoTornareIndietroCorrente() {
        if (dialogMode) {
            return storyEngine.puoiTornareIndietroDialogo();
        } else if (modalita == ModalitaStoria.FINALE) { // PATCH
            return storyEngine.puoiTornareIndietroFinale();
        } else {
            return storyEngine.puoiTornareIndietro();
        }
    }
    /**
     * Aggiorna la visibilità e lo stato dei pulsanti in base alla modalità corrente.
     * Mostra o nasconde i pulsanti "Indietro", "Continua" e "Salta" in base al contesto.
     */
    private void aggiornaPulsanti() {
        boolean mostraIndietro =
                puoTornareIndietroCorrente()
                        && continuaButton.isVisible()
                        && !saltaButton.isVisible()
                        && !testoInScrittura;

        // Se sei in modalità finale, mostra comunque il pulsante se puoi tornare indietro
        if (modalita == ModalitaStoria.FINALE && storyEngine.puoiTornareIndietroFinale()) {
            mostraIndietro = true;
        }

        indietroButton.setEnabled(mostraIndietro);
        indietroButton.setVisible(mostraIndietro);
    }
    /**
     * Imposta o verifica se il gioco è in fase di ripristino da un salvataggio.
     * Questo flag può essere utilizzato per evitare determinate azioni durante il caricamento.
     * @param v
     */
    public void setRipristinoDaSalvataggio(boolean v) { this.ripristinoDaSalvataggio = v; }
    /**
     * Imposta e ottiene lo slot di salvataggio attualmente caricato.
     * Utilizzato per tenere traccia dello slot di salvataggio in uso.
     *
     */
    public Integer getSlotCaricatoCorrente() { return slotCaricatoCorrente; }
    /**
     * Imposta lo slot di salvataggio attualmente caricato.
     * @param slot
     */
    public void setSlotCaricatoCorrente(Integer slot) { this.slotCaricatoCorrente = slot; }

    /**
     * Applica lo stato di gioco caricato da un salvataggio.
     * Aggiorna la GUI e lo stato interno con i dati forniti.
     * @param s
     */
    public void applyLoadedState(StatoGioco s) {
        setRipristinoDaSalvataggio(true);

        // Applica i dati
        GameStateMapper.applyTo(s, lewis, inventario, mappa, storyEngine);
        // Dopo il mapping, ri-aggancia il service (dopo un load può servire)
        if (commandService == null) {
            commandService = new org.example.utils.GestoreInput(
                    lewis,
                    mappa,
                    inventario,
                    mappaPanel,
                    sceneManager,
                    storyEngine
            );
        }
        lewis.setCommandService(commandService);

        // --- NUOVO (alla fine di applyLoadedState, prima di nascondere i pulsanti storia) ---
        if (mappaPanel != null) {
            mappaPanel.setVisitedRooms(lewis.getStanzeVisitate());
            mappaPanel.setCompletedRooms(lewis.getStanzeCompletate());
        }

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
    /**
     * Mostra un dialogo per l'inserimento del codice segreto.
     * Utilizzato per sbloccare porte o meccanismi speciali nel gioco.
     * Quando il codice è corretto, marca i flag appropriati e apre la stanza segreta.
     * Se l'utente aveva tentato di muoversi verso la porta, ripete il movimento.
     *
     */
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
                String risposta = lewis.muoviEsploratore(dir);
                appendOutput(risposta);
            } else {
                appendOutput("> Codice corretto. La porta della stanza segreta si apre.");
            }

            if (mappaPanel != null) {
                mappaPanel.aggiornaMappa(mappa, lewis.getPosizioneX(), lewis.getPosizioneY());
            }
        });
    }
    /**
     * Mostra un dialogo per l'assemblaggio dello scanner.
     * Verifica che il giocatore sia nella stanza corretta e che abbia tutti i pezzi.
     * Se l'assemblaggio viene completato, marca lo scanner come assemblato e avvia il dialogo con il boss finale.
     * Al termine del dialogo, mostra automaticamente le scene finali.
     */
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

                // PRECARICA le scene finali dal DB (finale_1..N)
                // 1) Precarica finali dal DB (finale_1, finale_2, …)
                List<String> fin = ScenesDb.finaliSequenza();
                System.out.println("[DEBUG] finali caricate dal DB: " + fin.size());
                if (!fin.isEmpty()) {
                    sceneManager.aggiungiSceneFinali(fin.toArray(new String[0]));
                }

                mostraDialogoBoss();
            }
            @Override public void onCancelled() {
                appendOutput("> Montaggio annullato.");
            }
        });

        dlg.setVisible(true);
    }
    /**
     * Mostra la scena finale corrente.
     * Se non ci sono più scene finali, termina l'epilogo e ripristina l'input di gioco.
     * Utilizza lo StoryEngine per ottenere la scena finale successiva.
     * Aggiorna l'area di testo con la descrizione della scena e i pulsanti di navigazione.
     * Al termine delle scene finali, ripristina l'input di gioco.
     */
    private void mostraScenaFinaleCorrente() {
        List<Scena> fin = sceneManager.getSceneFinale();
        if (fin == null || fin.isEmpty()) {
            System.out.println("[FINALE][UI] finale vuoto!");
            areaTesto.append("\nFine dell’epilogo.\n");
            modalita = ModalitaStoria.NONE;
            continuaButton.setVisible(false);
            saltaButton.setVisible(false);
            indietroButton.setVisible(false);
            terminaDialogoEMostraTitoliDiCoda();
            return;
        }

        if (indiceFinale < fin.size()) {
            Scena s = fin.get(indiceFinale);
            System.out.println("[FINALE][UI] Mostro indice " + indiceFinale + "/" + (fin.size()-1));
            indiceFinale++; // AVANZA QUI — UNA VOLTA SOLA
            animaTesto(s.getDescrizione());
            aggiornaPulsanti();
            return;
        }

        // Fine epilogo
        areaTesto.append("\nFine dell’epilogo.\n");
        modalita = ModalitaStoria.NONE;
        continuaButton.setVisible(false);
        saltaButton.setVisible(false);
        indietroButton.setVisible(false);
        terminaDialogoEMostraTitoliDiCoda();
    }
    /**
     * Mostra la scena finale precedente.
     * Se non ci sono scene precedenti, non fa nulla.
     * Utilizza lo StoryEngine per ottenere la scena finale precedente.
     */
    private void mostraScenaFinalePrecedente() {
        List<Scena> fin = sceneManager.getSceneFinale();
        if (fin == null || fin.isEmpty()) return;
        if (indiceFinale > 1) {
            indiceFinale -= 2; // torna indietro di 1
            Scena s = fin.get(indiceFinale);
            indiceFinale++;
            animaTesto(s.getDescrizione());
            aggiornaPulsanti();
        }
    }

}
