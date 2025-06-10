package org.example.story;
import java.util.ArrayList;
import java.util.List;

public class MissioniIniziali {

    public static List<Missione> creaMissioni() {
        List<Missione> missioni = new ArrayList<>();

        Missione m1 = new Missione(
                "Scanner Distrutto",
                "Recupera i pezzi dello scanner mnemonico sparsi nel tempo."
        );

        Missione m2 = new Missione(
                "Fiducia nel Viaggiatore",
                "Convinci Lewis a fidarsi di Wilbur per iniziare l'avventura."
        );

        Missione m3 = new Missione(
                "La Vendetta del Compagno",
                "Scopri perché l'Uomo con la Bombetta odia Lewis e chi è davvero."
        );

        missioni.add(m1);
        missioni.add(m2);
        missioni.add(m3);

        return missioni;
    }
}
