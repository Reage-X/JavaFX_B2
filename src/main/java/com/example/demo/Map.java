package com.example.demo;

public class Map {
    private int longueur;
    private int hauteur;
    private char[][] map;

    public static final char WALL = '#';
    public static final char ORB = '.';
    public static final char EMPTY = ' ';
    public static final char PLAYER = 'o';
    public static final char GHOST_Red = 'R';
    public static final char GHOST_Blue = 'B';
    public static final char GHOST_Pink = 'P';
    public static final char GHOST_Yellow = 'Y';

    public Map(int longueur, int hauteur) {
        this.longueur = longueur;
        this.hauteur = hauteur;
        map = new char[hauteur][longueur];
    }

    public Map() {}

    public int getLongueur() { return longueur; }
    public int getHauteur() { return hauteur; }
    public char[][] getMap() { return map; }


    /** Remplit la carte d'orbes uniquement dans les cases EMPTY ou non initialisées */
    public void fillWithOrbs() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < longueur; x++) {
                if (map[y][x] == EMPTY || map[y][x] == '\0') {
                    map[y][x] = ORB;
                }
            }
        }
    }

    /** Ajoute un mur */
    public void addWall(int x, int y) {
        map[y][x] = WALL;   // ✔ correction x/y
    }

    public void addPlayer(int x, int y) {
        map[y][x] = PLAYER;   // ✔ correction x/y
    }

    public void addGhost_Red(int x, int y) {
        map[y][x] = GHOST_Red;   // ✔ correction x/y
    }
    public void addGhost_Blue(int x, int y) {
        map[y][x] = GHOST_Blue;
    }
    public void addGhost_Pink(int x, int y) {
        map[y][x] = GHOST_Pink;
    }
    public void addGhost_Yellow(int x, int y) {
        map[y][x] = GHOST_Yellow;
    }


    /** Lecture d'une case */
    public char getCell(int x, int y) {
        return map[y][x];   // ✔ correction x/y
    }

    /** Suppression d'une orbe */
    public void suppOrbAt(int x, int y) {
        if (map[y][x] == ORB) {
            map[y][x] = EMPTY;  // ✔ correction x/y
        }
    }

    public void print() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < longueur; x++) {
                System.out.print(map[y][x]);
            }
            System.out.println();
        }
    }





    public Map niveau1() {

        // Carte 19x21 → index max = [18][20]
        Map m = new Map(19, 21);

        // Remplir toute la map d'orbes par défaut
        m.fillWithOrbs();

        // ---------------------------------------------------------
        // 1. BORDURES EXTÉRIEURES
        // ---------------------------------------------------------

        // Ligne du haut (y=0) et du bas (y=20)
        for (int x = 0; x < 19; x++) {
            m.addWall(x, 0);
            m.addWall(x, 20);
        }

        // Colonnes gauche (x=0) et droite (x=18)
        // SAUF le tunnel (y=9)
        for (int y = 0; y < 21; y++) {
            if (y != 9) {
                m.addWall(0, y);
                m.addWall(18, y);
            }
        }

        // ---------------------------------------------------------
        // 2. LIGNE 3 (y = 2)
        // ---------------------------------------------------------
        for (int x = 2; x <= 3; x++) m.addWall(x, 2);
        for (int x = 5; x <= 7; x++) m.addWall(x, 2);
        for (int x = 11; x <= 13; x++) m.addWall(x, 2);
        for (int x = 15; x <= 16; x++) m.addWall(x, 2);
        m.addWall(9, 2); // pilier central

        // ---------------------------------------------------------
        // 3. LIGNE 5 (y = 4)
        // ---------------------------------------------------------
        for (int x = 2; x <= 3; x++) m.addWall(x, 4);
        for (int x = 15; x <= 16; x++) m.addWall(x, 4);

        m.addWall(5, 4);
        m.addWall(5, 5);

        m.addWall(13, 4);
        m.addWall(13, 5);

        for (int x = 7; x <= 11; x++) m.addWall(x, 4);
        m.addWall(9, 5);

        // ---------------------------------------------------------
        // 4. LIGNES TRANSVERSALES (y = 6 et 12)
        // ---------------------------------------------------------
        int[] yLines = {6, 12};
        for (int y : yLines) {
            for (int x = 0; x <= 3; x++) m.addWall(x, y);
            for (int x = 5; x <= 8; x++) m.addWall(x, y);
            for (int x = 10; x <= 13; x++) m.addWall(x, y);
            for (int x = 15; x <= 18; x++) m.addWall(x, y);
        }

        // ---------------------------------------------------------
        // 5. ZONE CENTRALE / MAISON DES FANTÔMES
        // ---------------------------------------------------------
        for (int y = 7; y <= 11; y++) {
            m.addWall(5, y);
            m.addWall(13, y);
        }

        // Murs extérieurs du tunnel (lignes 8 et 10)
        for (int x = 0; x <= 3; x++) {
            m.addWall(x, 8);
            m.addWall(x, 10);
        }
        for (int x = 15; x <= 18; x++) {
            m.addWall(x, 8);
            m.addWall(x, 10);
        }

        // Boîte des fantômes (x=7 → 11, y=8 → 10)
        m.addWall(7, 8);
        m.addWall(8, 8);
        m.addWall(10, 8);
        m.addWall(11, 8);

        m.addWall(7, 9);
        m.addWall(11, 9);

        for (int x = 7; x <= 11; x++) m.addWall(x, 10);

        // ---------------------------------------------------------
        // 6. LIGNE 15 (y = 14)
        // ---------------------------------------------------------
        for (int x = 2; x <= 3; x++) m.addWall(x, 14);
        for (int x = 5; x <= 7; x++) m.addWall(x, 14);
        for (int x = 11; x <= 13; x++) m.addWall(x, 14);
        for (int x = 15; x <= 16; x++) m.addWall(x, 14);
        m.addWall(9, 14);

        // ---------------------------------------------------------
        // 7. BAS COMPLEXE (y = 16)
        // ---------------------------------------------------------
        for (int x = 0; x <= 1; x++) m.addWall(x, 16);
        for (int x = 17; x <= 18; x++) m.addWall(x, 16);
        m.addWall(3, 16);
        m.addWall(15, 16);

        m.addWall(5, 16);
        m.addWall(5, 17);
        m.addWall(13, 16);
        m.addWall(13, 17);

        for (int x = 7; x <= 11; x++) m.addWall(x, 16);
        m.addWall(9, 17);

        // ---------------------------------------------------------
        // 8. DERNIÈRE LIGNE INTERNE (y = 18)
        // ---------------------------------------------------------
        for (int x = 2; x <= 7; x++) m.addWall(x, 18);
        for (int x = 11; x <= 16; x++) m.addWall(x, 18);
        m.addWall(9, 18);

        return m;
    }
    public Map niveau2() {

        Map map = new Map(19, 21);
        map.fillWithOrbs();

        // --- LIGNE 1 et LIGNE 21 : ###################
        for (int x = 1; x <= 19; x++) {
            map.addWall(x, 1);
            map.addWall(x, 21);
        }

        // --- LIGNE 2 et LIGNE 20 : #.................#
        for (int y : new int[]{2, 20}) {
            map.addWall(1, y);
            map.addWall(19, y);
        }

        // --- LIGNE 3 et LIGNE 19 : #.######.#.######.#
        int[] blocsL3_L19_left  = {3,4,5,6,7,8};
        int[] blocsL3_L19_right = {11,12,13,14,15,16};
        for (int y : new int[]{3, 19}) {
            map.addWall(1, y);
            map.addWall(19, y);
            for (int x : blocsL3_L19_left)  map.addWall(x, y);
            for (int x : blocsL3_L19_right) map.addWall(x, y);
            map.addWall(10, y);
        }

        // --- LIGNE 4 et LIGNE 18 : #.#......#......#.#
        for (int y : new int[]{4, 18}) {
            map.addWall(1, y);
            map.addWall(3, y);
            map.addWall(10, y);
            map.addWall(17, y);
            map.addWall(19, y);
        }

        // --- LIGNE 5 et LIGNE 17 : #.#..#...#...#..#.#
        for (int y : new int[]{5, 17}) {
            map.addWall(1, y);
            map.addWall(3, y);
            map.addWall(6, y);
            map.addWall(10, y);
            map.addWall(14, y);
            map.addWall(17, y);
            map.addWall(19, y);
        }

        // --- LIGNE 6 et LIGNE 16 : #....#.#.#.#.#....#
        for (int y : new int[]{6, 16}) {
            map.addWall(1, y);
            map.addWall(6, y);
            map.addWall(8, y);
            map.addWall(10, y);
            map.addWall(12, y);
            map.addWall(14, y);
            map.addWall(19, y);
        }

        // --- LIGNE 7 et LIGNE 15 : #......#...#......#
        for (int y : new int[]{7, 15}) {
            map.addWall(1, y);
            map.addWall(8, y);
            map.addWall(12, y);
            map.addWall(19, y);
        }

        // --- LIGNE 8 et LIGNE 14 : ########...########
        for (int y : new int[]{8, 14}) {
            for (int x = 1; x <= 8; x++) map.addWall(x, y);
            for (int x = 12; x <= 19; x++) map.addWall(x, y);
        }

        // --- LIGNE 9 et LIGNE 13 : #.##...........##.#
        for (int y : new int[]{9, 13}) {
            map.addWall(1, y);
            map.addWall(3, y);
            map.addWall(4, y);
            map.addWall(16, y);
            map.addWall(17, y);
            map.addWall(19, y);
        }

        // --- LIGNE 10 et LIGNE 12 : #....###...###....#
        for (int y : new int[]{10, 12}) {
            map.addWall(1, y);
            map.addWall(6, y);
            map.addWall(7, y);
            map.addWall(8, y);
            map.addWall(12, y);
            map.addWall(13, y);
            map.addWall(14, y);
            map.addWall(19, y);
        }

        // --- LIGNE 11 : ###.##.......##.###
        // Bloc gauche
        for (int x = 1; x <= 3; x++) map.addWall(x, 11);
        // Petit bloc
        map.addWall(5, 11);
        map.addWall(6, 11);
        // Bloc droit
        for (int x = 14; x <= 16; x++) map.addWall(x, 11);
        for (int x = 1; x <= 3; x++) map.addWall(x, 11);

        // Encadrement final
        map.addWall(19, 11);

        return map;
    }

}