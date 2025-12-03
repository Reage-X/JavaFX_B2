package com.example.demo;

public class Joueur extends Compte
{
    private int x;
    private int y;
    private int dx;
    private int dy;
    private int score_jeu;
    private int nb_vies;
    private int sprite_frame;
    
    
    public Joueur(int x, int y, int dx, int dy, int score_jeu, int nb_vies, int sprite_frame)
    {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.score_jeu = score_jeu;
        this.nb_vies = nb_vies;
        this.sprite_frame = sprite_frame;
    }
    public Joueur() 
    {
        this.x = 0;
        this.y = 0;
        this.dx = 0;
        this.dy = 0;
        this.score_jeu = 0;
        this.nb_vies = 0;
        this.sprite_frame = 0;
    }

    public int getX()                                   { return x; }
    public int getY()                                   { return y; }
    public int getDx()                                  { return dx; }
    public int getDy()                                  { return dy; }
    public int getScore_jeu()                           { return score_jeu; }
    public int getNb_vies()                             { return nb_vies; }
    public int getSprite_frame()                        { return sprite_frame; }


    public void setX(int x)                             { this.x = x; }
    public void setY(int y)                             { this.y = y; }
    public void setDx(int dx)                           { this.dx = dx; }
    public void setDy(int dy)                           { this.dy = dy; }
    public void setScore(int score_jeu)                 { this.score_jeu = score_jeu; }
    public void setNb_vies(int nb_vies)                 { this.nb_vies = nb_vies; }
    public void setSprite_frame(int sprite_frame)       { this.sprite_frame = sprite_frame; }
}
