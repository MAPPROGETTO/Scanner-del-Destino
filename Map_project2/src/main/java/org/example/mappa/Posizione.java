package org.example.mappa;

public class Posizione {
    private int x;
    private int y;
    private int piano;

    public Posizione(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void muoviNord() { y=y-3; }
    public void muoviSud()  { y=y+3; }
    public void muoviEst()  { x=x+4; }
    public void muoviOvest(){ x=x-4; }

    // Metodi per il secondo piano
    public void muoviNord2() { y=y-3; }
    public void muoviSud2()  { y=y+3; }
    public void muoviEst2()  { x=x+4; }
    public void muoviOvest2(){ x=x-4; }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getPiano() {return piano;}


    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}
}
