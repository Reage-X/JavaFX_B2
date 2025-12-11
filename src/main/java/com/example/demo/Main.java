package com.example.demo;

import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main extends Application {

    // Variables globales statiques
    static boolean jeuEnCours = true;
    static Map map = new Map();
    static Compte compte = new Compte("guest", "guest");
    static Connection conn;

    @Override
    public void start(Stage primaryStage) {
        // Initialiser la connexion à la base de données
        try {
            String url = "jdbc:mysql://localhost:3306/javafx_b2?createDatabaseIfNotExist=true";
            String user = "root";
            String pass = "";

            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("✓ Connexion à la base de données établie");

        } catch (SQLException e) {
            System.err.println("✗ Erreur de connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
        }

        // Configuration de la fenêtre
        primaryStage.setMaximized(true); // Démarrer en plein écran

        // Lancer la page de connexion
        new LoginPage(primaryStage, conn);
    }

    /**
     * Crée la table 'comptes' si elle n'existe pas déjà
     */


    @Override
    public void stop() {
        // Fermer proprement la connexion lors de la fermeture de l'application
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("✓ Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
