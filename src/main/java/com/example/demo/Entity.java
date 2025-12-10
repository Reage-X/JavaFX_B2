package com.example.demo;
import javafx.scene.image.Image;
import java.util.Random;

public class Entity
{
    Random rand = new Random();

    protected int x;
    protected int y;
    protected int startX;
    protected int startY;
    protected int dx;
    protected int dy;
    private int direction;
    protected Image sprite_frame;
    protected int largeur;
    protected int hauteur;

    public Entity(int startX, int startY, int largeur, int hauteur)
    {
        this.x = startX;
        this.y = startY;
        this.dx = 0;
        this.dy = 0;
        this.direction = rand.nextInt(4);
        this.startX = startX;
        this.startY = startY;
        this.sprite_frame = null;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }
    public Entity()
    {
        this.x = 0;
        this.y = 0;
        this.dx = 0;
        this.dy = 0;
        this.direction = rand.nextInt(4);
        this.startX = 0;
        this.startY = 0;
        this.sprite_frame = null;
        this.largeur = 0;
        this.hauteur = 0;
    }


    public int getX()                                   { return x; }
    public int getY()                                   { return y; }
    public int getDx()                                  { return dx; }
    public int getDy()                                  { return dy; }
    public int getDirection()                           { return direction; }
    public int getStartX()                              { return startX; }
    public int getStartY()                              { return startY; }
    public Image getSprite_frame()                      { return sprite_frame; }
    public int getLargeur()                             { return largeur; }
    public int getHauteur()                             { return hauteur; }

    public void setX(int x)                             { this.x = x; }
    public void setY(int y)                             { this.y = y; }
    public void setDx(int dx)                           { this.dx = dx; }
    public void setDy(int dy)                           { this.dy = dy; }
    public void setDirection(int direction)             { this.direction = direction; }
    public void setStartX(int startX)                   { this.startX = startX; }
    public void setStartY(int startY)                   { this.startY = startY; }
    public void setSprite_frame(Image sprite_frame)     { this.sprite_frame = sprite_frame; }
    public void setLargeur(int largeur)                 { this.largeur = largeur; }
    public void setHauteur(int hauteur)                 { this.hauteur = hauteur; }



    void updateDx_Dy() {
        switch (this.direction) {
            case 0: // Haut
                this.dx = 0;
                this.dy = -1/*tailleCase / 4*/;
                break;
            case 2: // Bas
                this.dx = 0;
                this.dy = 1/*tailleCase / 4*/;
                break;
            case 3: // Gauche
                this.dx = -1 /*tailleCase / 4*/;
                this.dy = 0;
                break;
            case 1: // Droit
                this.dx = 1 /*tailleCase / 4*/;
                this.dy = 0;
                break;
        }
    }

    public boolean collision(Entity a, Entity b) {
        return  a.x == b.x && a.y == b.y;

        /*a.x < b.x + b.largeur &&
                a.x + a.largeur > b.x &&
                a.y < b.y + b.hauteur &&
                a.y + a.hauteur > b.y;*/
    }

    public void reset()
    {
        this.x = this.startX;
        this.y = this.startY;
        this.dx = 0;
        this.dy = 0;
        this.sprite_frame = null;
    }
}