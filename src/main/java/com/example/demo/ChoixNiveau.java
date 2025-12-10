package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ChoixNiveau extends Application {

    private Stage stage;
    private Connection conn;

    // Constructeur avec paramètres (pour appel depuis d'autres classes)
    public ChoixNiveau(Stage stage, Connection conn) {
        this.stage = stage;
        this.conn = conn;
        start(stage);
    }

    // Constructeur par défaut (requis par Application)
    public ChoixNiveau() {
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        // Si conn est null, on établit la connexion
        if (this.conn == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/javafx_b2?createDatabaseIfNotExist=true";
                String user = "root";
                String pass = "";
                this.conn = DriverManager.getConnection(url, user, pass);
            } catch (SQLException e) {
                System.out.println("Erreur de connexion à la base de données: " + e.getMessage());
            }
        }

        // Conteneur principal
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: black;");

        // Titre principal
        Text title = new Text("CHOIX DU NIVEAU");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 42));
        title.setFill(Color.CYAN);

        // Effet d'ombre pour le titre
        DropShadow glow = new DropShadow();
        glow.setColor(Color.CYAN);
        glow.setRadius(20);
        glow.setSpread(0.7);
        title.setEffect(glow);

        // Sous-titre
        Text subtitle = new Text("Sélectionnez votre niveau de difficulté");
        subtitle.setFont(Font.font("Courier New", FontWeight.NORMAL, 16));
        subtitle.setFill(Color.LIGHTGRAY);

        // Boutons de niveau
        Button niveau1Btn = createNeonButton("NIVEAU 1", Color.rgb(255, 184, 174));
        Button niveau2Btn = createNeonButton("NIVEAU 2", Color.rgb(255, 100, 100));
        Button retourBtn = createNeonButton("RETOUR", Color.LIGHTGRAY);

        // Actions des boutons
        niveau1Btn.setOnAction(e -> lancerJeu(1));
        niveau2Btn.setOnAction(e -> lancerJeu(2));
        retourBtn.setOnAction(e -> retourMenu());

        // Ajouter les éléments
        root.getChildren().addAll(title, subtitle, niveau1Btn, niveau2Btn, retourBtn);

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("Pacman - Choix du Niveau");
        stage.show();
    }

    /**
     * Crée un bouton avec effet néon
     */
    private Button createNeonButton(String text, Color color) {
        Button btn = new Button(text);

        // Style de base
        btn.setFont(Font.font("Courier New", FontWeight.BOLD, 24));
        btn.setPrefWidth(300);
        btn.setPrefHeight(60);
        btn.setTextFill(color);
        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: " + toRgbString(color) + ";" +
                        "-fx-border-width: 3px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-radius: 10px;"
        );

        // Effet d'ombre/glow
        DropShadow shadow = new DropShadow();
        shadow.setColor(color);
        shadow.setRadius(15);
        shadow.setSpread(0.5);
        btn.setEffect(shadow);

        // Effet hover
        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                    "-fx-background-color: " + toRgbString(color.deriveColor(0, 1, 1, 0.2)) + ";" +
                            "-fx-border-color: " + toRgbString(color) + ";" +
                            "-fx-border-width: 3px;" +
                            "-fx-border-radius: 10px;" +
                            "-fx-background-radius: 10px;"
            );
            shadow.setRadius(25);
            shadow.setSpread(0.7);
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-border-color: " + toRgbString(color) + ";" +
                            "-fx-border-width: 3px;" +
                            "-fx-border-radius: 10px;" +
                            "-fx-background-radius: 10px;"
            );
            shadow.setRadius(15);
            shadow.setSpread(0.5);
        });

        return btn;
    }

    /**
     * Convertit une couleur en chaîne RGB pour CSS
     */
    private String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255)
        );
    }

    /**
     * Lance le jeu avec le niveau sélectionné
     */
    private void lancerJeu(int niveau) {
        // Réinitialiser le joueur
        Main.compte.getJoueur().setScore_jeu(0);
        Main.compte.getJoueur().setNb_vie(3);

        // Lancer la scène de jeu
        new GameScene(stage, niveau, conn);
    }

    /**
     * Retourne au menu principal
     */
    private void retourMenu() {
        // TODO: Implémenter le retour au menu MenuCompte
        // Exemple : new MenuCompte(stage, conn);
        System.out.println("Retour au menu principal");

        // Pour l'instant, on peut juste afficher un message ou fermer
        // stage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}