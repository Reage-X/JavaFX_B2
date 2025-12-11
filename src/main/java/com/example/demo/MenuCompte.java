
package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;

public class MenuCompte extends Application {

    private Stage stage;
    private Connection conn;
    private BorderPane mainContainer;
    private Text usernameText;
    private VBox leftMenu;

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

        // Conteneur principal BorderPane
        mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: black;");

        // === LEFT : Menu latéral ===
        leftMenu = createLeftMenu();
        mainContainer.setLeft(leftMenu);

        // === CENTER : Zone de contenu dynamique ===
        // Afficher la page d'accueil par défaut
        loadHomePage();

        // Créer la scène
        Scene scene = new Scene(mainContainer, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Pacman - Menu Compte");
        stage.show();
    }

    private VBox createLeftMenu() {
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.TOP_CENTER);
        menu.setPadding(new Insets(30, 20, 30, 20));
        menu.setStyle("-fx-background-color: #0a0a0a; -fx-border-color: cyan; -fx-border-width: 0 2 0 0;");
        menu.setPrefWidth(280);

        // Titre et nom utilisateur
        Text title = new Text("MENU");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 32));
        title.setFill(Color.CYAN);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.CYAN);
        glow.setRadius(15);
        glow.setSpread(0.7);
        title.setEffect(glow);

        usernameText = new Text(Main.compte.getUser_name());
        usernameText.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        usernameText.setFill(Color.LIGHTGRAY);

        Region spacer1 = new Region();
        spacer1.setPrefHeight(20);

        // Boutons du menu
        Button homeBtn = createMenuButton("🏠 ACCUEIL", Color.CYAN);
        Button playBtn = createMenuButton("🎮 JOUER", Color.LIME);
        Button scoresBtn = createMenuButton("🏆 SCORES", Color.YELLOW);
        Button profileBtn = createMenuButton("👤 PROFIL", Color.rgb(255, 184, 174));
        
        Region spacer2 = new Region();
        VBox.setVgrow(spacer2, Priority.ALWAYS);
        
        Button logoutBtn = createMenuButton("🚪 DÉCONNEXION", Color.RED);

        // Actions
        homeBtn.setOnAction(e -> loadHomePage());
        playBtn.setOnAction(e -> new ChoixNiveau(stage, conn));
        scoresBtn.setOnAction(e -> loadContentPage(new ScoresPage(conn)));
        profileBtn.setOnAction(e -> loadContentPage(new ProfilePage(stage, conn, this)));
        logoutBtn.setOnAction(e -> new LoginPage(stage, conn));

        menu.getChildren().addAll(title, usernameText, spacer1, homeBtn, playBtn, 
                                  scoresBtn, profileBtn, spacer2, logoutBtn);
        return menu;
    }

    private Button createMenuButton(String text, Color color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        btn.setPrefWidth(240);
        btn.setPrefHeight(50);
        btn.setTextFill(color);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(0, 0, 0, 20));
        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: " + toRgbString(color) + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(color);
        shadow.setRadius(10);
        shadow.setSpread(0.4);
        btn.setEffect(shadow);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                    "-fx-background-color: " + toRgbString(color.deriveColor(0, 1, 1, 0.15)) + ";" +
                            "-fx-border-color: " + toRgbString(color) + ";" +
                            "-fx-border-width: 2px;" +
                            "-fx-border-radius: 8px;" +
                            "-fx-background-radius: 8px;"
            );
            shadow.setRadius(18);
            shadow.setSpread(0.6);
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-border-color: " + toRgbString(color) + ";" +
                            "-fx-border-width: 2px;" +
                            "-fx-border-radius: 8px;" +
                            "-fx-background-radius: 8px;"
            );
            shadow.setRadius(10);
            shadow.setSpread(0.4);
        });

        return btn;
    }

    // Méthode pour charger une page dans le centre
    public void loadContentPage(Region content) {
        mainContainer.setCenter(content);
    }

    // Page d'accueil par défaut
    private void loadHomePage() {
        VBox homePage = new VBox(40);
        homePage.setAlignment(Pos.CENTER);
        homePage.setStyle("-fx-background-color: black;");

        Text welcome = new Text("BIENVENUE\n" + Main.compte.getUser_name() + " !");
        welcome.setFont(Font.font("Courier New", FontWeight.BOLD, 48));
        welcome.setFill(Color.CYAN);
        welcome.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.CYAN);
        glow.setRadius(25);
        glow.setSpread(0.7);
        welcome.setEffect(glow);

        Text instruction = new Text("Utilisez le menu pour naviguer");
        instruction.setFont(Font.font("Courier New", FontWeight.NORMAL, 18));
        instruction.setFill(Color.LIGHTGRAY);

        homePage.getChildren().addAll(welcome, instruction);
        mainContainer.setCenter(homePage);
    }

    // Méthode pour rafraîchir le nom d'utilisateur
    public void refreshUsername() {
        usernameText.setText(Main.compte.getUser_name());
    }

    private String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255)
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
