package com.example.demo;
import javafx.scene.image.Image;
import java.awt.event.*;
import java.util.ArrayList;

public class Joueur extends Entity implements KeyListener{
    private int score_jeu;
    private int nb_vie;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    public Joueur(int startX, int startY, int largeur, int hauteur) {
        super(startX, startY, largeur, hauteur);
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

    public void reset()
    {
        super.reset();
        this.score_jeu = 0;
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            this.setDirection(0);
            this.sprite_frame = pacmanUpImage;
            this.updateDx_Dy();
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            this.setDirection(2);
            this.sprite_frame = pacmanDownImage;
            this.updateDx_Dy();
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            this.setDirection(3);
            this.sprite_frame = pacmanLeftImage;
            this.updateDx_Dy();
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.setDirection(1);
            this.sprite_frame = pacmanRightImage;
            this.updateDx_Dy();
        }
        this.x += this.dx;
        this.y += this.dy;
    }


    public void coli_joueur(ArrayList<Entity> murs, ArrayList<Entity> points, ArrayList<Ennemi> ennemies) {
        //check wall collisions
        for (Entity mur : murs) {
            if (collision(this, mur)) {
                this.x -= this.dx;
                this.y -= this.dy;
                break;
            }
        }

        //check ghost collisions
        for (Ennemi ennemi : ennemies) {
            if (collision(ennemi, this)) {
                this.nb_vie -= 1;
                this.reset();
            }
        }

        //check food collision
        Entity pointSupp = null;
        for (Entity point : points) {
            if (collision(this, point)) {
                pointSupp = point;
                this.score_jeu += 10;
                points.remove(pointSupp);
            }
        }
    }
}