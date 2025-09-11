package org.example.mappa;

import java.io.Serializable;

/**
 * Rappresenta una posizione sulla mappa con coordinate x, y e piano.
 * Implementa Serializable per permettere la serializzazione dell'oggetto.
 */
public class Posizione implements Serializable {
    private int x;
    private int y;
    private static final long serialVersionUID = 1L;

    /**
     * Costruttore per inizializzare la posizione con coordinate x e y.
     * @param x
     * @param y
     */
    public Posizione(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Muove la posizione di 3 unità a nord.
     */
    public void muoviNord() { y=y-3; }

    /**
     * Muove la posizione di 4 unità a sud.
     */
    public void muoviSud()  { y=y+3; }

    /**
     * Muove la posizione di 4 unità a est.
     *
     */
    public void muoviEst()  { x=x+4; }

    /**
     * Muove la posizione di 4 unità a ovest.
     */
    public void muoviOvest(){ x=x-4; }


    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}
}
