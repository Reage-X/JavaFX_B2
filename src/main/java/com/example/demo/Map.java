package com.example.demo;


public class Map {
    private int longueur;
    private int hauteur;
    private char[][] map;

    public Map(int longueur, int hauteur) {
        this.longueur = longueur;
        this.hauteur = hauteur;
        map = new char[hauteur][longueur];
        fillWithEmpty();
    }

    private void fillWithEmpty() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < longueur; x++) {
                map[y][x] = '.';
            }
        }
    }

    public void addWall(int x, int y) {
        map[y][x] = '#';
    }

    public char getCell(int x, int y) {
        return map[y][x];
    }

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
