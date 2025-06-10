package org.example.gui;

import java.awt.*;

public class Particle {
    double x, y, speed;
    Color color;

    Particle(double x, double y, double speed, Color color) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.color = color;
    }

    void update() {
        y -= speed;
    }
}
