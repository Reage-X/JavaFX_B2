package com.example.demo;

public class Joueur extends Compte
{
    private int x;
    private int y;
    private int dx;
    private int dy;
    private int score;
    private int nb_vies;
    private int sprite_frame;
    
    
    public Joueur(int x, int y, int dx, int dy, int score, int nb_vies, int sprite_frame)
    {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.score = score;
        this.nb_vies = nb_vies;
        this.sprite_frame = sprite_frame;
    }
    public Joueur() 
    {
        this.x = 0;
        this.y = 0;
        this.dx = 0;
        this.dy = 0;
        this.score = 0;
        this.nb_vies = 0;
        this.sprite_frame = 0;
    }

    public int getX()                                   {return x;}
    public int getY()                                   {return y;}
    public int getDx()                                  {return dx;}
    public int getDy()                                  {return dy;}
    public int getScore()                               {return score;}
    public int getNb_vies()                             {return nb_vies;}
    public int getSprite_frame()                        {return sprite_frame;}


    public void setX(int x)                             {this.x = x;}
    public void setY(int y)                             {this.y = y;}
    public void setDx(int dx)                           {this.dx = dx;}
    public void setDy(int dy)                           {this.dy = dy;}
    public void setScore(int score)                     {this.score = score;}
    public void setNb_vies(int nb_vies)                 {this.nb_vies = nb_vies;}
    public void setSprite_frame(int sprite_frame)       {this.sprite_frame = sprite_frame;}
}
