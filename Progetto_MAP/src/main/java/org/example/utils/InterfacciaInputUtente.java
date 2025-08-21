package org.example.utils;

import java.io.Serializable;
import java.util.List;

public interface InterfacciaInputUtente extends Serializable {
    int chiediSceltaUtente(List<String> opzioni);
    String leggiLinea();
    void attendiInvio();
}
