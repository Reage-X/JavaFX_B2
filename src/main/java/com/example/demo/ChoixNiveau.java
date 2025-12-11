package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ChoixNiveau extends Application {

    private Stage stage;
    private Connection conn;

    public ChoixNiveau(Stage stage, Connection conn) {
        this.stage = stage;
        this.conn = conn;
        start(stage);
    }

    public ChoixNiveau() {
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

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

        VBox root = createContent();

        // Si la scène existe déjà, on change juste le root
        if (stage.getScene() != null) {
            stage.getScene().setRoot(root);
        } else {
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Pacman Game");
            stage.setMaximized(true);
            stage.show();
        }
    }

    private VBox createContent() {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label titre = new Label("🎮 CHOIX DU NIVEAU");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titre.setTextFill(Color.web("#ffd700"));

        Label subtitle = new Label("Sélectionnez votre niveau de difficulté");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        subtitle.setTextFill(Color.WHITE);

        Button niveau1Btn = createButton("NIVEAU 1");
        Button niveau2Btn = createButton("NIVEAU 2");
        Button retourBtn = createButton("RETOUR");
        retourBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 20 50; -fx-font-size: 16px;");

        niveau1Btn.setOnAction(e -> lancerJeu(1));
        niveau2Btn.setOnAction(e -> lancerJeu(2));
        retourBtn.setOnAction(e -> new MenuCompte(stage, conn));

        root.getChildren().addAll(titre, subtitle, niveau1Btn, niveau2Btn, retourBtn);

        return root;
    }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        btn.setPrefWidth(400);
        btn.setPrefHeight(80);
        btn.setStyle("-fx-background-color: #ffd700; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 20 50;");
        return btn;
    }

    private void lancerJeu(int niveau) {
        Main.compte.getJoueur().setScore_jeu(0);
        Main.compte.getJoueur().setNb_vie(3);
        new GameScene(stage, niveau, conn);
    }

    public static void main(String[] args) {
        launch(args);
    }
}