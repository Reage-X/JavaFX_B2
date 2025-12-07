package com.example.demo;
import java.awt.event.*;

public class Joueur extends Entity{
    private int score_jeu;
    private int nb_vie;

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

    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}