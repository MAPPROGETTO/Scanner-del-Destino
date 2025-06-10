package org.example.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GiocoGUI extends JFrame {
    private JTextArea outputArea;
    private JTextField inputField;
    private JButton sendButton;

    public GiocoGUI() {
        setTitle("Scanner del Destino");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centra la finestra

        // Pannello principale
        JPanel backgroundPanel = new JPanel(new BorderLayout());
        MappaPanel mappaLabel = new MappaPanel();


        // Area di output
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(mappaLabel, BorderLayout.EAST);

        // Pannello inferiore: input + bottone
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputField = new JTextField();
        sendButton = new JButton("Invia");

        // Azione bottone
        sendButton.addActionListener((ActionEvent e) -> {
            String comando = inputField.getText();
            if (!comando.isBlank()) {
                processaComando(comando);
                inputField.setText("");
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        backgroundPanel.add(inputPanel, BorderLayout.SOUTH);
        add(backgroundPanel);

        setVisible(true);

        // Messaggio iniziale
        mostraMessaggio("Benvenuto in Scanner del Destino!\nInserisci un comando per iniziare...");
    }

    private void processaComando(String comando) {
        // Per ora stampiamo solo l'eco del comando
        mostraMessaggio("> " + comando + "\n(comando ricevuto, logica da implementare...)");
    }

    private void mostraMessaggio(String messaggio) {
        outputArea.append(messaggio + "\n");
    }
}

