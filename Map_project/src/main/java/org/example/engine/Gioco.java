package org.example.engine;

import org.example.model.Lewis;
import org.example.story.*;
import org.example.utils.*;
import java.util.List;
import org.example.mappa.*;

public class Gioco {
    private Inventario inventario;
    private List<Scena> scene;
    private List<Missione> missioni;
    private GestoreInput input;

    public Gioco() {
        this.scene = SceneIniziali.creaScene();
        this.missioni = MissioniIniziali.creaMissioni();
        this.input = new GestoreInput();
        this.inventario = new Inventario();
    }

    public void avvia() {
        System.out.println("Benvenuto in 'Scanner del Destino'!");
        System.out.println("Preparati a un viaggio tra passato e futuro...\n");

        mostraMissioni();

        for (int i = 0; i < scene.size(); i++) {
            Scena scena = scene.get(i);

            int scelta = input.chiediSceltaUtente(scena.getOpzioni());
            System.out.println("Hai scelto: " + scena.getOpzioni().get(scelta));

            // LOGICA DI AVANZAMENTO MISSIONI
            if (i == 0) {
                missioni.get(0).avvia(); // Scanner Distrutto
            } else if (i == 1) {
                missioni.get(1).avvia(); // Fiducia nel Viaggiatore
            } else if (i == 2) {
                missioni.get(0).completa(); // Completata
                missioni.get(1).completa(); // Completata
                missioni.get(2).avvia(); // La Vendetta del Compagno
            }

            mostraMissioni(); // mostra lo stato aggiornato
            input.attendiInvio();
        }

        System.out.println("\nModalità esplorazione attivata! Spostati per trovare gli indizi sparsi nella città...\n");

        Mappa mappa = new Mappa(5, 5); // puoi regolare dimensione a piacere
        Lewis lewis = new Lewis(mappa, input, inventario);

        lewis.esplora();

        // Mostra inventario alla fine dell'esplorazione
        inventario.mostraInventario();

        System.out.println("Fine della demo iniziale. Altre avventure ti attendono...");
    }

    private void mostraMissioni() {
        System.out.println("Missioni attive:");
        for (Missione missione : missioni) {
            missione.mostraDettagli();
            System.out.println();
        }
    }
}
