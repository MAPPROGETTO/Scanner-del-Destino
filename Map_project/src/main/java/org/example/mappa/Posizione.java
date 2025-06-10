package org.example.mappa;

public class Posizione {
    private int x;
    private int y;

    public Posizione(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void muoviNord() { y--; }
    public void muoviSud()  { y++; }
    public void muoviEst()  { x++; }
    public void muoviOvest(){ x--; }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}
}
