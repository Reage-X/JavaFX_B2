package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;

public class LoginPage extends Application {

    private Stage stage;
    private Connection conn;

    public LoginPage(Stage stage, Connection conn) {
        this.stage = stage;
        this.conn = conn;
        start(stage);
    }

    public LoginPage() {
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

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
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #1a1a2e;");

        // Titre
        Label titre = new Label("🎮 PACMAN GAME");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titre.setTextFill(Color.web("#ffd700"));

        Label sousTitre = new Label("Connexion");
        sousTitre.setFont(Font.font("Arial", FontWeight.NORMAL, 22));
        sousTitre.setTextFill(Color.WHITE);

        // Formulaire
        GridPane form = new GridPane();
        form.setAlignment(Pos.CENTER);
        form.setHgap(15);
        form.setVgap(20);
        form.setPadding(new Insets(40));
        form.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");

        Label pseudoLabel = new Label("Pseudo:");
        pseudoLabel.setTextFill(Color.WHITE);
        pseudoLabel.setFont(Font.font("Arial", 16));
        TextField pseudoField = new TextField();
        pseudoField.setPromptText("Votre pseudo");
        pseudoField.setPrefWidth(350);
        pseudoField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        Label passLabel = new Label("Mot de passe:");
        passLabel.setTextFill(Color.WHITE);
        passLabel.setFont(Font.font("Arial", 16));
        PasswordField passField = new PasswordField();
        passField.setPromptText("••••••••");
        passField.setPrefWidth(350);
        passField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        form.add(pseudoLabel, 0, 0);
        form.add(pseudoField, 1, 0);
        form.add(passLabel, 0, 1);
        form.add(passField, 1, 1);

        // Boutons
        HBox boutonsBox = new HBox(20);
        boutonsBox.setAlignment(Pos.CENTER);

        Button btnConnexion = new Button("Se connecter");
        btnConnexion.setStyle("-fx-background-color: #ffd700; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 12 30; -fx-font-size: 14px;");
        btnConnexion.setPrefWidth(200);

        Button btnInscription = new Button("S'inscrire");
        btnInscription.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 30; -fx-font-size: 14px;");
        btnInscription.setPrefWidth(200);

        boutonsBox.getChildren().addAll(btnConnexion, btnInscription);

        // Message d'erreur
        Label messageErreur = new Label();
        messageErreur.setTextFill(Color.RED);
        messageErreur.setFont(Font.font("Arial", 14));
        messageErreur.setVisible(false);

        // Logique de connexion (à partager entre le bouton et la touche Entrée)
        Runnable loginAction = () -> {
            String pseudo = pseudoField.getText().trim();
            String motDePasse = passField.getText();

            if (pseudo.isEmpty() || motDePasse.isEmpty()) {
                messageErreur.setText("⚠ Veuillez remplir tous les champs");
                messageErreur.setVisible(true);
                return;
            }

            try {
                Main.compte = Sql.getCompte(pseudo, motDePasse, conn);

                if (Main.compte != null) {
                    messageErreur.setVisible(false);
                    new MenuCompte(stage, conn);
                } else {
                    messageErreur.setText("❌ Pseudo ou mot de passe incorrect");
                    messageErreur.setVisible(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                messageErreur.setText("❌ Erreur de connexion : " + ex.getMessage());
                messageErreur.setVisible(true);
            }
        };

        // Action du bouton connexion
        btnConnexion.setOnAction(e -> loginAction.run());

        // Touche Entrée sur les champs de texte
        pseudoField.setOnAction(e -> loginAction.run());
        passField.setOnAction(e -> loginAction.run());

        btnInscription.setOnAction(e -> new SignInPage(stage, conn));

        // Ajout des éléments
        root.getChildren().addAll(titre, sousTitre, form, boutonsBox, messageErreur);

        return root;
    }

    public static void main(String[] args) {
        launch(args);
    }
}