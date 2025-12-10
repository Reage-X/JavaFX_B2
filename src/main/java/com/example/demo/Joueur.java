package com.example.demo;
import javafx.scene.image.Image;
import java.util.Iterator;
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

    /**
     * NOUVELLE MÉTHODE : Gère le déplacement du joueur sur la map
     */
    public void updateMouvement(Map map) {
        // Effacer l'ancienne position
        map.getMap()[this.y][this.x] = EMPTY;

        // Calculer la nouvelle position
        int newX = this.x + this.dx;
        int newY = this.y + this.dy;

        // Vérifier les limites de la map
        if (newY <= 0 || newY >= map.getHauteur() - 1 ||
                newX <= 0 || newX >= map.getLongueur() - 1) {
            // Remettre le joueur à sa position actuelle
            map.getMap()[this.y][this.x] = PLAYER;
            return;
        }

        // Vérifier collision avec les murs
        boolean collision = false;
        for (Entity mur : map.murs) {
            if (newX == mur.getX() && newY == mur.getY()) {
                collision = true;
                break;
            }
        }

        if (collision) {
            // Remettre le joueur à sa position actuelle
            map.getMap()[this.y][this.x] = PLAYER;
            return;
        }

        // Déplacer le joueur
        this.x = newX;
        this.y = newY;
        map.getMap()[this.y][this.x] = PLAYER;

        // Vérifier les collisions avec ennemis et points
        coli_joueur(map);
    }

    public void coli_joueur(Map map) {
        // Vérifier collision avec ennemis
        for (Ennemi ennemi : map.ennemies) {
            if (collision(ennemi, this)) {
                this.nb_vie -= 1;
                map.getMap()[this.getY()][this.getX()] = EMPTY;
                this.reset(map);
                return; // Sortir après reset
            }
        }

        // Vérifier collision avec points (orbes)
        Iterator<Entity> iterator = map.points.iterator();
        while (iterator.hasNext()) {
            Entity point = iterator.next();
            if (collision(this, point)) {
                map.getMap()[point.getY()][point.getX()] = EMPTY;
                this.score_jeu += 10;
                iterator.remove();
            }
        }
    }
}