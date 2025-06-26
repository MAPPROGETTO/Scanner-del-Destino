package org.example.utils;

import java.util.List;

public interface InterfacciaInputUtente {
    int chiediSceltaUtente(List<String> opzioni);
    String leggiLinea();
    void attendiInvio();
}
