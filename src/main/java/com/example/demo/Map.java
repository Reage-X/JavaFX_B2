package com.example.demo;


public class Map {
    private int longueur;
    private int hauteur;
    private char[][] map;

    public static final char WALL = '#';
    public static final char ORB = '.';
    public static final char EMPTY = ' ';

    public int getLongueur() {
        return longueur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public char[][] getMap() {
        return map;
    }

    public Map(int longueur, int hauteur) {
        this.longueur = longueur;
        this.hauteur = hauteur;
        map = new char[longueur][hauteur];
        fillWithOrbs();
    }

    /** Remplit la carte d'orbes par défaut */
    private void fillWithOrbs() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < longueur; x++) {
                map[y][x] = ORB;
            }
        }
    }

    /** Ajoute un mur qui remplace l'orbe */
    public void addWall(int x, int y) {
        map[y][x] = WALL;
    }

    /** Récupère le contenu d'une case */
    public char getCell(int x, int y) {
        return map[y][x];
    }

    /** Dépose une case vide quand le joueur ramasse une orbe */
    public void collectOrbAt(int x, int y) {
        if (map[y][x] == ORB) {
            map[y][x] = EMPTY;  // devient vide
        }
    }

    /** Affichage console avec le joueur */
    public void print(Joueur joueur) {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < longueur; x++) {

                if (joueur.getX() == x && joueur.getY() == y) {
                    System.out.print('P');
                } else {
                    System.out.print(map[y][x]);
                }

            }
            System.out.println();
        }
    }
}

