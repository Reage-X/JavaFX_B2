package com.example.demo;
import java.util.Scanner;
import java.awt.event.*;

public class Jeux implements KeyListener{
    Map map = new Map();
    Compte compte = new Compte("test","test");
    Scanner scanner;
    boolean jeuEnCours = true;
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            jeuEnCours = false;
        }
    }
    public void keyReleased(KeyEvent e) {}

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Quel niveau voulez-vous jouer ? (1 ou 2)");
        int niveau = scanner.nextInt();
        while (niveau != 1 && niveau != 2) {
            System.out.println("Veuillez choisir le niveau 1 ou 2 :");
            niveau = scanner.nextInt();
        }

        if (niveau == 1) map = map.niveau1();
        else map = map.niveau2();
    }

    public void play() {
        while (jeuEnCours)
        {
            if(compte.getJoueur().getNb_vie() !=0)
            {
                compte.getJoueur().coli_joueur(map.murs, map.points, map.ennemies);
                for (Ennemi ennemi : map.ennemies) { ennemi.updateMouvement(map.murs); }
                map.print();
            }
            else{
                System.out.println("GameOver !");
                jeuEnCours = false;
                compte.addScore();
                break;
            }


            if(compte.getJoueur().getScore_jeu() >= map.getMaxPoint())
            {
                System.out.println("Victoire");
                jeuEnCours = false;
                compte.addScore();
                break;
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
