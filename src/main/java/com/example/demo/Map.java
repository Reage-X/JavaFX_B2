package com.example.demo;


public class Map {
    private int longueur;
    private int hauteur;
    private char[][] map;

    public static final char WALL = '#';
    public static final char ORB = '.';
    public static final char EMPTY = ' ';

    public Map(int longueur, int hauteur) {
        this.longueur = longueur;
        this.hauteur = hauteur;
        map = new char[hauteur][longueur];
        fillWithOrbs();
    }
    public int getLongueur() {
        return longueur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public char[][] getMap() {
        return map;
    }


    /** Remplit la carte d'orbes par défaut */
    public void fillWithOrbs() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < longueur; x++) {
                map[x][y] = ORB;  // Par défaut : ORBE
            }
        }
    }

    /** Ajoute un mur qui remplace l'orbe */
    public void addWall(int x, int y) {
        map[x][y] = WALL;
    }

    /** Récupère le contenu d'une case */
    public char getCell(int x, int y) {
        return map[x][y];
    }

    /** Dépose une case vide quand le joueur ramasse une orbe */
    public void collectOrbAt(int x, int y) {
        if (map[x][y] == ORB) {
            map[x][y] = EMPTY;  // devient vide
        }
    }

    /** Affichage console avec le joueur */
    public void print(Entity joueur) {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < longueur; x++) {

                if (joueur.getX() == x && joueur.getY() == y) {
                    System.out.print('P');
                } else {
                    System.out.print(map[x][y]);
                }

            }
            System.out.println();
        }
    }
}
