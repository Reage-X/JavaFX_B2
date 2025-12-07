package com.example.demo;

public class Grille {


    /** Retourne le niveau 1 */
    public static Map niveau1() {
        Map map = baseMap();
        map.fillWithOrbs();

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
        map.fillWithOrbs();

        // Bordures haut & bas
        for (int x = 1; x <= 20; x++) map.addWall(x, 1);
        for (int x = 1; x <= 20; x++) map.addWall(x, 15);
        for (int y = 1; y <= 15; y++) map.addWall(1, y);
        for (int y = 1; y <= 15; y++) map.addWall(20, y);


        // Colonne 3 : y 4 à 13
        for (int y = 4; y <= 13; y++) map.addWall(3, y);

        // Colonne 4 : y 4 à 8
        for (int y = 4; y <= 8; y++) map.addWall(4, y);

        // Colonne 6 : y 4 à 8
        for (int y = 4; y <= 8; y++) map.addWall(6, y);

        // Colonne 8 : y 7 à 8
        for (int y = 7; y <= 8; y++) map.addWall(8, y);

        // Colonne 10 : y 4 à 14
        for (int y = 4; y <= 14; y++) map.addWall(10, y);

        // Colonne 11 : y 9 à 12
        for (int y = 9; y <= 12; y++) map.addWall(11, y);

        // Colonne 12 : y 4 à 14
        for (int y = 4; y <= 14; y++) map.addWall(12, y);

        // Colonne 14 : y 4 à 14
        for (int y = 4; y <= 14; y++) map.addWall(14, y);

        // Colonne 16 : y 14 à 14
        map.addWall(16, 14);

        // Colonne 17 : y 7 à 14
        for (int y = 7; y <= 14; y++) map.addWall(17, y);

        // Colonne 18 : y 4 à 14
        for (int y = 4; y <= 14; y++) map.addWall(18, y);



        // Ligne 4 : x 4 à 7
        for (int x = 4; x <= 7; x++) map.addWall(x, 4);

        // Ligne 7 : x 4 à 12
        for (int x = 4; x <= 12; x++) map.addWall(x, 7);

        // Ligne 9 : x 10 à 12
        for (int x = 10; x <= 12; x++) map.addWall(x, 9);

        // Ligne 13 : x 10 à 12
        for (int x = 10; x <= 12; x++) map.addWall(x, 13);

        // Ligne 14 : x 2 à 18 (avec trous)
        for (int x = 2; x <= 18; x += 2) map.addWall(x, 14); // murs sur 2,4,6,...



        map.addWall(7, 4);
        map.addWall(15, 4);

        return map;
    }


    /** Retourne le niveau 3 */
    public static Map niveau3() {
        Map map = baseMap();
        map.fillWithOrbs();


        for (int x = 1; x <= 20; x++) map.addWall(x, 1);
        for (int x = 1; x <= 20; x++) map.addWall(x, 15);
        for (int y = 1; y <= 15; y++) map.addWall(1, y);
        for (int y = 1; y <= 15; y++) map.addWall(20, y);



        // Bloc gauche (x 4 à 6, y 3 à 5)
        for (int x = 4; x <= 6; x++) map.addWall(x, 3);
        for (int x = 4; x <= 6; x++) map.addWall(x, 5);
        map.addWall(4, 4);
        map.addWall(6, 4);

        // Bloc droit (x 14 à 16, y 3 à 5)
        for (int x = 14; x <= 16; x++) map.addWall(x, 3);
        for (int x = 14; x <= 16; x++) map.addWall(x, 5);
        map.addWall(14, 4);
        map.addWall(16, 4);




        for (int y = 3; y <= 11; y++) map.addWall(10, y);


        for (int x = 9; x <= 11; x++) map.addWall(x, 6);


        for (int x = 9; x <= 11; x++) map.addWall(x, 9);



        for (int x = 4; x <= 7; x++) map.addWall(x, 13);
        for (int x = 10; x <= 13; x++) map.addWall(x, 13);



        for (int x = 3; x <= 18; x++) map.addWall(x, 7);


        return map;
    }




    private static Map baseMap() {

        // On garde les dimensions 20 x 13
        Map map = new Map(20, 15);

        int longueur = map.getLongueur();
        int hauteur = map.getHauteur();



        return map;
    }
}


