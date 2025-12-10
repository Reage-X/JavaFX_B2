package com.example.demo;
import javafx.scene.image.Image;
import java.awt.event.*;
import static com.example.demo.Map.*;


public class Joueur extends Entity{
    private int score_jeu;
    private int nb_vie;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    public Joueur(int startX, int startY) {
        super(startX, startY, 32, 32);
        this.score_jeu = 0;
        this.nb_vie = 3;
    }
    public Joueur() {
        super(0,0,32,32);
        this.score_jeu = 0;
        this.nb_vie = 3;
    }


    public int getScore_jeu()                   { return score_jeu; }
    public int getNb_vie()                      { return nb_vie; }

    public void setScore_jeu(int score_jeu)     { this.score_jeu = score_jeu; }
    public void setNb_vie(int nb_vie)           { this.nb_vie = nb_vie; }


    public void addScore(int score) {
        this.score_jeu += score;
    }

    public void reset(Map map)
    {
        super.reset();
        map.getMap()[this.startY][this.startX] = PLAYER;
    }

    public void changeSprit() {
        switch (this.getDirection()) {
            case 0:
                this.sprite_frame = pacmanUpImage;
                break;
            case 2:
                this.sprite_frame = pacmanDownImage;
                break;
            case 3:
                this.sprite_frame = pacmanLeftImage;
                break;
            case 1:
                this.sprite_frame = pacmanRightImage;
                break;
        }
    }

    public void coli_joueur(Map map) {
        //check wall collisions
        for (Entity mur : map.murs) {
            if (collision(this, mur)) {
                this.x -= this.dx;
                this.y -= this.dy;
                break;
            }
        }

        //check ghost collisions
        for (Ennemi ennemi : map.ennemies) {
            if (collision(ennemi, this)) {
                this.nb_vie -= 1;
                map.getMap()[this.getY()][this.getX()] = EMPTY;
                this.reset(map);
            }
        }

        //check food collision
        Entity pointSupp = null;
        for (Entity point : map.points) {
            if (collision(this, point)) {
                pointSupp = point;
                map.getMap()[point.getY()][point.getX()] = EMPTY;
                this.score_jeu += 10;
                map.points.remove(pointSupp);
            }
        }
    }
}