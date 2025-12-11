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
import java.util.ArrayList;

public class SignInPage extends Application {

    private Stage stage;
    private Connection conn;

    public SignInPage(Stage stage, Connection conn) {
        this.stage = stage;
        this.conn = conn;
        start(stage);
    }

    public SignInPage() {
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
        Label titre = new Label("📝 INSCRIPTION");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titre.setTextFill(Color.web("#ffd700"));

        // Formulaire
        GridPane form = new GridPane();
        form.setAlignment(Pos.CENTER);
        form.setHgap(15);
        form.setVgap(20);
        form.setPadding(new Insets(40));
        form.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");

        // Champs
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

        Label confirmLabel = new Label("Confirmer:");
        confirmLabel.setTextFill(Color.WHITE);
        confirmLabel.setFont(Font.font("Arial", 16));
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("••••••••");
        confirmField.setPrefWidth(350);
        confirmField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        form.add(pseudoLabel, 0, 0);
        form.add(pseudoField, 1, 0);
        form.add(passLabel, 0, 1);
        form.add(passField, 1, 1);
        form.add(confirmLabel, 0, 2);
        form.add(confirmField, 1, 2);

        // Boutons
        HBox boutonsBox = new HBox(20);
        boutonsBox.setAlignment(Pos.CENTER);

        Button btnCreer = new Button("Créer le compte");
        btnCreer.setStyle("-fx-background-color: #ffd700; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 12 30; -fx-font-size: 14px;");
        btnCreer.setPrefWidth(200);

        Button btnRetour = new Button("Retour");
        btnRetour.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 30; -fx-font-size: 14px;");
        btnRetour.setPrefWidth(200);

        boutonsBox.getChildren().addAll(btnCreer, btnRetour);

        // Message
        Label message = new Label();
        message.setTextFill(Color.RED);
        message.setFont(Font.font("Arial", 14));
        message.setVisible(false);

        // Logique d'inscription (à partager entre le bouton et la touche Entrée)
        Runnable createAction = () -> {
            String pseudo = pseudoField.getText().trim();
            String pass = passField.getText();
            String confirm = confirmField.getText();

            // Validations
            if (pseudo.isEmpty() || pass.isEmpty()) {
                message.setText("⚠ Tous les champs sont obligatoires");
                message.setTextFill(Color.RED);
                message.setVisible(true);
                return;
            }

            if (!pass.equals(confirm)) {
                message.setText("⚠ Les mots de passe ne correspondent pas");
                message.setTextFill(Color.RED);
                message.setVisible(true);
                return;
            }

            if (pass.length() < 6) {
                message.setText("⚠ Le mot de passe doit contenir au moins 6 caractères");
                message.setTextFill(Color.RED);
                message.setVisible(true);
                return;
            }

            // Création du compte
            try {
                if (Sql.PseudoExiste(pseudo, conn)) {
                    message.setText("❌ Pseudo déjà utilisé");
                    message.setTextFill(Color.RED);
                    message.setVisible(true);
                    return;
                }

                Compte compte = new Compte(pseudo, pass);
                compte.setScore(new ArrayList<>());

                if (Sql.addCompte(compte, conn)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Succès");
                    alert.setHeaderText("Compte créé avec succès !");
                    alert.setContentText("Vous pouvez maintenant vous connecter.");
                    alert.showAndWait();

                    new LoginPage(stage, conn);
                } else {
                    message.setText("❌ Erreur lors de la création");
                    message.setTextFill(Color.RED);
                    message.setVisible(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("❌ Erreur : " + ex.getMessage());
                message.setTextFill(Color.RED);
                message.setVisible(true);
            }
        };

        // Action du bouton créer
        btnCreer.setOnAction(e -> createAction.run());

        // Touche Entrée sur les champs de texte
        pseudoField.setOnAction(e -> createAction.run());
        passField.setOnAction(e -> createAction.run());
        confirmField.setOnAction(e -> createAction.run());

        btnRetour.setOnAction(e -> new LoginPage(stage, conn));

        root.getChildren().addAll(titre, form, boutonsBox, message);

        return root;
    }

    public static void main(String[] args) {
        launch(args);
    }
}