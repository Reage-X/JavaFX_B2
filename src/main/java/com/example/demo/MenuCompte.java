
package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;

public class MenuCompte extends Application {

    private Stage stage;
    private Connection conn;
    private BorderPane mainContainer;
    private Label usernameLabel;

    public MenuCompte(Stage stage, Connection conn) {
        this.stage = stage;
        this.conn = conn;
        start(stage);
    }

    public MenuCompte() {
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        BorderPane root = createContent();

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

    private BorderPane createContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        // Header
        HBox header = creerHeader();
        root.setTop(header);

        // Menu latéral
        VBox menu = creerMenu(stage);
        root.setLeft(menu);

        // Contenu central par défaut
        mainContainer = root;
        VBox centre = creerAccueil();
        root.setCenter(centre);

        return root;
    }

    private HBox creerHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #16213e;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);

        Label titre = new Label("🎮 PACMAN GAME");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#ffd700"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        usernameLabel = new Label("Joueur: " + Main.compte.getUser_name());
        usernameLabel.setTextFill(Color.WHITE);
        usernameLabel.setFont(Font.font("Arial", 14));

        Button btnDeconnexion = new Button("Déconnexion");
        btnDeconnexion.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-weight: bold;");
        btnDeconnexion.setOnAction(e -> new LoginPage(stage, conn));

        header.getChildren().addAll(titre, spacer, usernameLabel, btnDeconnexion);
        return header;
    }

    private VBox creerMenu(Stage stage) {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(20));
        menu.setStyle("-fx-background-color: #0f3460;");
        menu.setPrefWidth(220);

        Button btnAccueil = creerBoutonMenu("🏠 Accueil");
        Button btnJouer = creerBoutonMenu("🎮 Jouer");
        Button btnScores = creerBoutonMenu("🏆 Scores");
        Button btnProfil = creerBoutonMenu("👤 Mon Profil");

        btnAccueil.setOnAction(e -> mainContainer.setCenter(creerAccueil()));
        btnJouer.setOnAction(e -> new ChoixNiveau(stage, conn));
        btnScores.setOnAction(e -> mainContainer.setCenter(new ScoresPage(conn)));
        btnProfil.setOnAction(e -> mainContainer.setCenter(new ProfilePage(stage, conn, this)));

        menu.getChildren().addAll(btnAccueil, btnJouer, btnScores, btnProfil);
        return menu;
    }

    private Button creerBoutonMenu(String texte) {
        Button btn = new Button(texte);
        btn.setPrefWidth(180);
        btn.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 12; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #ffd700; -fx-text-fill: black; " +
                "-fx-font-size: 14px; -fx-padding: 12; -fx-cursor: hand; -fx-font-weight: bold;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 12; -fx-cursor: hand;"));
        return btn;
    }

    private VBox creerAccueil() {
        VBox vbox = new VBox(30);
        vbox.setPadding(new Insets(40));
        vbox.setAlignment(Pos.TOP_CENTER);

        Label titre = new Label("🏠 BIENVENUE");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titre.setTextFill(Color.web("#ffd700"));

        // Statistiques du joueur
        HBox stats = new HBox(20);
        stats.setAlignment(Pos.CENTER);

        VBox statScore = creerCarteStat("🎯 Meilleur Score",
                Main.compte.getScore().isEmpty() ? "0" : String.valueOf(Main.compte.getScore().get(0)), "#3498db");
        VBox statNbScores = creerCarteStat("📊 Parties Jouées", String.valueOf(Main.compte.getScore().size()), "#2ecc71");
        VBox statTotal = creerCarteStat("💯 Score Total",
                String.valueOf(Main.compte.getScore().stream().mapToInt(Integer::intValue).sum()), "#e74c3c");

        stats.getChildren().addAll(statScore, statNbScores, statTotal);

        // Bouton de jeu principal
        Button btnJouerGrand = new Button("▶️ COMMENCER À JOUER");
        btnJouerGrand.setStyle("-fx-background-color: #ffd700; -fx-text-fill: black; " +
                "-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 20 40; -fx-cursor: hand;");
        btnJouerGrand.setPrefSize(400, 80);
        btnJouerGrand.setOnAction(e -> new ChoixNiveau(stage, conn));

        vbox.getChildren().addAll(titre, stats, btnJouerGrand);
        return vbox;
    }

    private VBox creerCarteStat(String titre, String valeur, String couleur) {
        VBox carte = new VBox(10);
        carte.setAlignment(Pos.CENTER);
        carte.setPadding(new Insets(30));
        carte.setStyle("-fx-background-color: " + couleur + "; -fx-background-radius: 10;");
        carte.setPrefSize(200, 150);

        Label lblTitre = new Label(titre);
        lblTitre.setTextFill(Color.WHITE);
        lblTitre.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label lblValeur = new Label(valeur);
        lblValeur.setTextFill(Color.WHITE);
        lblValeur.setFont(Font.font("Arial", FontWeight.BOLD, 48));

        carte.getChildren().addAll(lblTitre, lblValeur);
        return carte;
    }

    public void refreshUsername() {
        usernameLabel.setText("Joueur: " + Main.compte.getUser_name());
    }

    public static void main(String[] args) {
        launch(args);
    }
}