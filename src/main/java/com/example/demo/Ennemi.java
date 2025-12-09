package com.example.demo;

import java.util.ArrayList;

public class Ennemi extends Entity {

    public Ennemi(int startX, int startY,int largeur, int hauteur) {
        super(startX, startY, largeur, hauteur);
        this.updateDx_Dy();
    }


    void updateMouvement(ArrayList<Entity> murs) {
        this.x += this.dx;
        this.y += this.dy;
        for (Entity mur : murs) {
            if (collision(this, mur)) {
                this.x -= this.dx;
                this.y -= this.dy;
                this.setDirection(rand.nextInt(4));
                this.updateDx_Dy();
            }
        }
    }
}