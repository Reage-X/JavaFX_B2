package com.example.demo;

public class Grille {


    /** Retourne le niveau 1 */
    public static Map niveau1() {
        Map map = baseMap();

        for (int x = 1; x <= 20; x++) map.addWall(x, 1);
        for (int x = 1; x <= 20; x++) map.addWall(x, 15);
        for (int y = 1; y <= 15; y++) map.addWall(1, y);
        for (int y = 1; y <= 15; y++) map.addWall(20, y);

        for (int y = 3; y <= 15; y++) map.addWall(3, y);
        for (int y = 3; y <= 7; y++) map.addWall(5, y);
        for (int x = 4; x <= 7; x++) map.addWall(x, 7);
        for (int y = 8; y <= 11; y++) map.addWall(7, y);
        for (int y = 7; y <= 15; y++) map.addWall(9, y);
        for (int x = 9; x <= 12; x++) map.addWall(x, 4);
        for (int x = 10; x <= 16; x++) map.addWall(x, 7);
        for (int y = 10; y <= 15; y++) map.addWall(11, y);
        for (int y = 8; y <= 12; y++) map.addWall(13, y);
        for (int y = 10; y <= 15; y++) map.addWall(16, y);
        for (int y = 4; y <= 12; y++) map.addWall(18, y);


        map.addWall(14, 7);
        map.addWall(4, 11);
        map.addWall(5, 11);
        map.addWall(7, 4);
        map.addWall(15, 4);

        return map;
    }

    /** Retourne le niveau 2 */
    public static Map niveau2() {
        Map map = baseMap();

        // Structure interne
        for (int y = 3; y <= 9; y++) map.addWall(9, y);
        for (int y = 3; y <= 9; y++) map.addWall(10, y);

        map.addWall(4, 4);
        map.addWall(5, 4);
        map.addWall(14, 4);
        map.addWall(15, 4);

        map.addWall(4, 8);
        map.addWall(5, 8);
        map.addWall(14, 8);
        map.addWall(15, 8);

        return map;
    }

    /** Retourne le niveau 3 */
    public static Map niveau3() {
        Map map = baseMap();

        // Couloir horizontal central
        for (int x = 2; x <= 17; x++) map.addWall(x, 6);

        // Quelques ouvertures dans ce mur
        map.getMap()[6][8] = Map.ORB;
        map.getMap()[6][11] = Map.ORB;

        // Blocs de chaque côté
        map.addWall(4, 4);
        map.addWall(4, 5);
        map.addWall(4, 7);
        map.addWall(4, 8);

        map.addWall(15, 4);
        map.addWall(15, 5);
        map.addWall(15, 7);
        map.addWall(15, 8);

        return map;
    }

    /** Retourne le niveau 4 */
    public static Map niveau4() {
        Map map = baseMap();

        // Labyrinthe symétrique
        for (int x = 3; x <= 16; x++) map.addWall(x, 4);
        for (int x = 3; x <= 16; x++) map.addWall(x, 8);

        map.addWall(8, 6);
        map.addWall(9, 6);
        map.addWall(10, 6);
        map.addWall(11, 6);

        map.addWall(5, 2);
        map.addWall(14, 2);

        map.addWall(5, 10);
        map.addWall(14, 10);

        return map;
    }


    private static Map baseMap() {

        // On garde les dimensions 20 x 13
        Map map = new Map(20, 13);

        int longueur = map.getLongueur();
        int hauteur = map.getHauteur();

        // Murs du haut et du bas
        for (int x = 0; x < longueur; x++) {
            map.addWall(x, 0);
            map.addWall(x, hauteur - 1);
        }


        int milieu = hauteur / 2;

        for (int y = 0; y < hauteur; y++) {
            if (y != milieu) {
                map.addWall(0, y);              // gauche
                map.addWall(longueur - 1, y);   // droite
            }
        }

        return map;
    }
}

