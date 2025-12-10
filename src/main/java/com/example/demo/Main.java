package com.example.demo;

import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.event.KeyListener;

import static com.example.demo.Map.*;
import static com.example.demo.Sql.updateScore;

public class Main implements KeyListener {

    static boolean jeuEnCours = true;
    static Map map = new Map();
    static Compte compte = new Compte("test","test");

    public static void main(String[] args)throws SQLException {
        String url = "jdbc:mysql://localhost:3306/javafx_b2?createDatabaseIfNotExist=true";
        String user = "root";
        String pass = "";

        Connection conn = DriverManager.getConnection(url, user, pass);
        if (conn == null) {
            System.out.println("Impossible de se connecter à la base.");
            return;
        }


        Scanner sc = new Scanner(System.in);

        menuLoginSign(sc, conn);
        menuCompte(sc, conn);
    }

    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            compte.getJoueur().setDirection(0);
            compte.getJoueur().updateDx_Dy();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            compte.getJoueur().setDirection(2);
            compte.getJoueur().updateDx_Dy();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            compte.getJoueur().setDirection(3);
            compte.getJoueur().updateDx_Dy();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            compte.getJoueur().setDirection(1);
            compte.getJoueur().updateDx_Dy();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            jeuEnCours = false;
        }
        compte.getJoueur().changeSprit();
        compte.getJoueur().setX(compte.getJoueur().getX() + compte.getJoueur().getDx());
        compte.getJoueur().setY(compte.getJoueur().getY() + compte.getJoueur().getDy());
        map.getMap()[compte.getJoueur().getY()][compte.getJoueur().getX()] = PLAYER ;
    }
    public void keyReleased(KeyEvent e) {}


    private static void menuLoginSign(Scanner sc, Connection conn) throws SQLException {

        while (true) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Connexion");
            System.out.println("2. Créer un compte");
            System.out.println("3. Quitter");
            System.out.print("Choix : ");
            int choix = sc.nextInt();
            sc.nextLine();
            switch (choix) {
                // -------------------------------------------------
                // 1) CONNEXION
                // -------------------------------------------------
                case 1:
                    System.out.print("Pseudo : ");
                    String pseudo = sc.nextLine();
                    System.out.print("Mot de passe : ");
                    String mdp = sc.nextLine();
                    try {
                        compte = Sql.getCompte(pseudo, mdp, conn);
                        if (compte != null) {
                            System.out.println("Connexion réussie !");
                            menuCompte(sc, conn);
                        } else {
                            System.out.println("Identifiants incorrects.");
                        }
                    } catch (SQLException e) {
                        System.out.println("Erreur SQL : " + e.getMessage());
                    }
                    break;// -------------------------------------------------
                // 2) CREATION DE COMPTE
                // -------------------------------------------------
                case 2:
                    System.out.print("Nouveau pseudo : ");
                    String newPseudo = sc.nextLine();
                    while (!Sql.PseudoExiste(newPseudo, conn)) {
                        System.out.println("Ce pseudo existe déjà !");
                        System.out.println("Pseudo : ");
                        newPseudo = sc.nextLine();
                        break;
                    }
                    System.out.print("Mot de passe : ");
                    String newMDP = sc.nextLine();
                    try {
                        Compte cmp = new Compte(newPseudo, newMDP);
                        cmp.setScore(new ArrayList<>());
                        if (Sql.addCompte(cmp, conn)) {
                            System.out.println("Compte créé !");
                        } else {
                            System.out.println("Échec de la création.");
                        }
                    } catch (SQLException e) {
                        System.out.println("Erreur SQL : " + e.getMessage());
                    }
                    break;// -------------------------------------------------
                // QUITTER
                // -------------------------------------------------
                case 3:
                    System.out.println("Au revoir !");
                    try {
                        sc.close();
                        conn.close();
                    } catch (Exception ignored) {
                    }
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    // =============================================================
    //           MENU APRES CONNEXION
    // =============================================================
    private static void menuCompte(Scanner sc, Connection conn) {
        while (true) {
            System.out.println("\n=== MENU COMPTE (" + compte.getUser_name() + ") ===");
            System.out.println("1. Jouer");
            System.out.println("2. Afficher scores");
            System.out.println("3. Modifier pseudo");
            System.out.println("4. Modifier mot de passe");
            System.out.println("5. Supprimer compte");
            System.out.println("6. Déconnexion");
            System.out.print("Choix : ");

            int choix = sc.nextInt();
            sc.nextLine();

            try {
                switch (choix) {

                    // -------------------------------------------------
                    case 1:
                        start(sc);
                        play(conn);
                        break;

                    // -------------------------------------------------
                    case 2:
                        System.out.println("Scores : " + compte.getScore());
                        break;

                    // -------------------------------------------------
                    case 3:
                        System.out.print("Nouveau pseudo : ");
                        String newPseudo = sc.nextLine();
                        String oldPseudo = compte.getUser_name();

                        compte.setUser_name(newPseudo);

                        if (Sql.updateUserName(compte, oldPseudo, conn)) {
                            System.out.println("Pseudo modifié !");
                        }
                        break;

                    // -------------------------------------------------
                    case 4:
                        System.out.print("Nouveau MDP : ");
                        String newMDP = sc.nextLine();
                        String oldMDP = compte.getMDP();

                        compte.setMDP(newMDP);

                        if (Sql.updateMDP(compte, oldMDP, conn)) {
                            System.out.println("Mot de passe modifié !");
                        }
                        break;

                    // -------------------------------------------------
                    case 5:
                        if (Sql.deleteCompte(compte, conn)) {
                            System.out.println("Compte supprimé !");
                            return;
                        }
                        break;

                    case 6:
                        System.out.println("Déconnexion...");
                        return;

                    default:
                        System.out.println("Choix invalide.");
                }

            } catch (SQLException e) {
                System.out.println("Erreur SQL : " + e.getMessage());
            }
        }
    }

    public static void start(Scanner sc) {;
        System.out.println("Quel niveau voulez-vous jouer ? (1 ou 2)");
        int niveau = sc.nextInt();
        while (niveau != 1 && niveau != 2) {
            System.out.println("Veuillez choisir le niveau 1 ou 2 :");
            niveau = sc.nextInt();
        }

        if (niveau == 1) map = map.niveau1();
        else map = map.niveau2();
    }

    public static void  play(Connection conn) throws SQLException {
        while (jeuEnCours)
        {
            if(compte.getJoueur().getNb_vie() !=0)
            {
                compte.getJoueur().coli_joueur(map);
                for (Ennemi ennemi : map.ennemies) {
                    ennemi.updateMouvement(map);
                    /*System.out.println("direction: " +ennemi.getDirection()+
                            "\n"+"coordonées: "+ennemi.getX()+","+ennemi.getY()+
                            "\n"+"derivé: "+ennemi.getDx()+","+ennemi.getDy()+
                            "\n"+"couleur: "+ennemi.getCouleur());*/
                }
                /*System.out.println("\n\n\n");
                System.out.println("coordonnées: "+compte.getJoueur().getX()+","+compte.getJoueur().getY()+
                        "\n"+"derive: "+compte.getJoueur().getDx()+","+compte.getJoueur().getDy()+
                        "\n"+"direction: "+compte.getJoueur().getDirection());*/
                //compte.getJoueur().setNb_vie(compte.getJoueur().getNb_vie()-1);
                compte.getJoueur().addScore(100);
                map.print();
            }
            else{
                System.out.println("\n\n");
                System.out.println("GameOver !");
                jeuEnCours = false;
                compte.addScore();
                updateScore(compte, conn);
                break;
            }

            if(compte.getJoueur().getScore_jeu() >= map.getMaxPoint())
            {
                System.out.println("\n\n");
                System.out.println("Victoire");
                jeuEnCours = false;
                compte.addScore();
                updateScore(compte, conn);
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
