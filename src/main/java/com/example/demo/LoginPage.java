
package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

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

        // Conteneur principal
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: black; -fx-padding: 40;");

        // Titre
        Text title = new Text("CONNEXION");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 48));
        title.setFill(Color.CYAN);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.CYAN);
        glow.setRadius(20);
        glow.setSpread(0.7);
        title.setEffect(glow);

        // Champs de saisie
        TextField pseudoField = createNeonTextField("Pseudo");
        PasswordField mdpField = createNeonPasswordField("Mot de passe");

        // Message d'erreur
        Label errorLabel = new Label("");
        errorLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        errorLabel.setTextFill(Color.RED);

        // Boutons
        Button loginBtn = createNeonButton("SE CONNECTER", Color.rgb(0, 255, 0));
        Button signInBtn = createNeonButton("CRÉER UN COMPTE", Color.rgb(255, 184, 174));
        Button quitBtn = createNeonButton("QUITTER", Color.LIGHTGRAY);

        // Action Login
        loginBtn.setOnAction(e -> {
            String pseudo = pseudoField.getText().trim();
            String mdp = mdpField.getText();

            if (pseudo.isEmpty() || mdp.isEmpty()) {
                errorLabel.setText("⚠ Veuillez remplir tous les champs");
                return;
            }

            try {
                Main.compte = Sql.getCompte(pseudo, mdp, conn);
                if (Main.compte != null) {
                    // Connexion réussie → aller au menu compte
                    new MenuCompte(stage, conn);
                } else {
                    errorLabel.setText("⚠ Identifiants incorrects");
                    mdpField.clear();
                }
            } catch (SQLException ex) {
                errorLabel.setText("⚠ Erreur SQL : " + ex.getMessage());
            }
        });

        // Action Sign In
        signInBtn.setOnAction(e -> new SignInPage(stage, conn));

        // Action Quitter
        quitBtn.setOnAction(e -> {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println("Erreur lors de la fermeture : " + ex.getMessage());
            }
            stage.close();
        });

        root.getChildren().addAll(title, pseudoField, mdpField, errorLabel, loginBtn, signInBtn, quitBtn);

        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("Pacman - Connexion");
        stage.show();
    }

    private TextField createNeonTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setFont(Font.font("Courier New", FontWeight.NORMAL, 18));
        field.setPrefWidth(350);
        field.setPrefHeight(50);
        field.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: gray;" +
                        "-fx-border-color: cyan;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;" +
                        "-fx-padding: 10;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.CYAN);
        shadow.setRadius(10);
        shadow.setSpread(0.3);
        field.setEffect(shadow);

        return field;
    }

    private PasswordField createNeonPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setFont(Font.font("Courier New", FontWeight.NORMAL, 18));
        field.setPrefWidth(350);
        field.setPrefHeight(50);
        field.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: gray;" +
                        "-fx-border-color: cyan;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;" +
                        "-fx-padding: 10;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.CYAN);
        shadow.setRadius(10);
        shadow.setSpread(0.3);
        field.setEffect(shadow);

        return field;
    }

    private Button createNeonButton(String text, Color color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        btn.setPrefWidth(350);
        btn.setPrefHeight(55);
        btn.setTextFill(color);
        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: " + toRgbString(color) + ";" +
                        "-fx-border-width: 3px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-radius: 10px;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(color);
        shadow.setRadius(15);
        shadow.setSpread(0.5);
        btn.setEffect(shadow);

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
