package com.example.demo;

import static com.example.demo.Map.*;

public class Ennemi extends Entity {
    private char couleur;


    public void setCouleur(char couleur) { this.couleur = couleur; }

    public Ennemi(int startX, int startY,int largeur, int hauteur) {
        super(startX, startY, largeur, hauteur);
        this.updateDx_Dy();
    }


    void updateMouvement(Map map) {
        map.getMap()[this.y][this.x] = EMPTY;
        this.x += this.dx;
        this.y += this.dy;
        for (Entity mur : map.murs) {
            if (collision(this, mur)) {
                this.x -= this.dx;
                this.y -= this.dy;
                this.setDirection(rand.nextInt(4));
                this.updateDx_Dy();
            }
        }
        map.getMap()[this.y][this.x] = couleur;
    }
}