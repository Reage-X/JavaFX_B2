package com.example.demo;

public class Ennemi extends Entity {
    private int direction; // 0:Haut, 1:Droit, 2:Bas, 3:Gauche

    public Ennemi(int startX, int startY,int largeur, int hauteur) {
        super(startX, startY, largeur, hauteur);
        this.direction = rand.nextInt(4);
        updateDx_Dy();
    }


    public void setDirection(int direction)         { this.direction = direction; }
    public int getDirection()                       { return direction; }


    void updateDx_Dy() {
        switch (this.direction) {
            case 0: // Haut
                this.dx = 0;
                this.dy = -tailleCase / 4;
                break;
            case 2: // Bas
                this.dx = 0;
                this.dy = tailleCase / 4;
                break;
            case 3: // Gauche
                this.dx = -tailleCase / 4;
                this.dy = 0;
                break;
            case 1: // Droit
                this.dx = tailleCase / 4;
                this.dy = 0;
                break;
        }
    }


    void updateMouvement(int direction) {
        int prevDirection = this.direction;
        this.direction = direction;
        updateDx_Dy();
        this.x += this.dx;
        this.y += this.dy;
        for (Entity mur : murs) {
            if (collision(this, mur)) {
                this.x -= this.dx;
                this.y -= this.dy;
                this.direction = prevDirection;
                updateDx_Dy();
            }
        }
    }
}

