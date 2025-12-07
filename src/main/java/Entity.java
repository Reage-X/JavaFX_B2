import java.util.ArrayList;
import java.util.Random;

public class Entity
{
    protected int x;
    protected int y;
    protected int startX;
    protected int startY;
    protected int dx;
    protected int dy;
    protected int sprite_frame;
    protected int largeur;
    protected int hauteur;


    //
    //
    //      DANS LE MAIN DU JEU
    Random rand = new Random();
    ArrayList<Entity> murs;
    ArrayList<Entity> point;
    ArrayList<Entity> ennemies;
    public static int tailleCase = 64;
    //      DANS LE MAIN DU JEU
    //
    //


    public Entity(int startX, int startY, int largeur, int hauteur)
    {
        this.x = 0;
        this.y = 0;
        this.dx = 0;
        this.dy = 0;
        this.startX = startX;
        this.startY = startY;
        this.sprite_frame = 0;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }
    public Entity()
    {
        this.x = 0;
        this.y = 0;
        this.dx = 0;
        this.dy = 0;
        this.startX = 0;
        this.startY = 0;
        this.sprite_frame = 0;
        this.largeur = 0;
        this.hauteur = 0;
    }


    public int getX()                                   { return x; }
    public int getY()                                   { return y; }
    public int getDx()                                  { return dx; }
    public int getDy()                                  { return dy; }
    public int getStartX()                              { return startX; }
    public int getStartY()                              { return startY; }
    public int getSprite_frame()                        { return sprite_frame; }
    public int getLargeur()                             { return largeur; }
    public int getHauteur()                             { return hauteur; }

    public void setX(int x)                             { this.x = x; }
    public void setY(int y)                             { this.y = y; }
    public void setDx(int dx)                           { this.dx = dx; }
    public void setDy(int dy)                           { this.dy = dy; }
    public void setStartX(int startX)                   { this.startX = startX; }
    public void setStartY(int startY)                   { this.startY = startY; }
    public void setSprite_frame(int sprite_frame)       { this.sprite_frame = sprite_frame; }
    public void setLargeur(int largeur)                 { this.largeur = largeur; }
    public void setHauteur(int hauteur)                 { this.hauteur = hauteur; }

    public boolean collision(Entity a, Entity b) {
        return  a.x < b.x + b.largeur &&
                a.x + a.largeur > b.x &&
                a.y < b.y + b.hauteur &&
                a.y + a.hauteur > b.y;
    }

    void reset()
    {
        this.x = this.startX;
        this.y = this.startY;
        this.dx = 0;
        this.dy = 0;
        this.sprite_frame = 0;
    }
}
