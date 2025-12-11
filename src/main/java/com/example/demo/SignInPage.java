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

        // Conteneur principal
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: black; -fx-padding: 40;");

        // Titre
        Text title = new Text("CRÉER UN COMPTE");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 42));
        title.setFill(Color.rgb(255, 184, 174));

        DropShadow glow = new DropShadow();
        glow.setColor(Color.rgb(255, 184, 174));
        glow.setRadius(20);
        glow.setSpread(0.7);
        title.setEffect(glow);

        // Champs de saisie
        TextField pseudoField = createNeonTextField("Pseudo");
        PasswordField mdpField = createNeonPasswordField("Mot de passe");
        PasswordField confirmMdpField = createNeonPasswordField("Confirmer mot de passe");

        // Message d'erreur/succès
        Label messageLabel = new Label("");
        messageLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 14));

        // Boutons
        Button createBtn = createNeonButton("CRÉER", Color.rgb(0, 255, 0));
        Button backBtn = createNeonButton("RETOUR", Color.LIGHTGRAY);

        // Action Créer
        createBtn.setOnAction(e -> {
            String pseudo = pseudoField.getText().trim();
            String mdp = mdpField.getText();
            String confirmMdp = confirmMdpField.getText();

            // Validations
            if (pseudo.isEmpty() || mdp.isEmpty() || confirmMdp.isEmpty()) {
                messageLabel.setText("⚠ Veuillez remplir tous les champs");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            if (!mdp.equals(confirmMdp)) {
                messageLabel.setText("⚠ Les mots de passe ne correspondent pas");
                messageLabel.setTextFill(Color.RED);
                mdpField.clear();
                confirmMdpField.clear();
                return;
            }

            try {
                // Vérifier si le pseudo existe déjà
                if (Sql.PseudoExiste(pseudo, conn)) {
                    messageLabel.setText("⚠ Ce pseudo existe déjà");
                    messageLabel.setTextFill(Color.RED);
                    pseudoField.clear();
                    return;
                }

                // Créer le compte
                Compte newCompte = new Compte(pseudo, mdp);
                newCompte.setScore(new ArrayList<>());

                if (Sql.addCompte(newCompte, conn)) {
                    messageLabel.setText("✓ Compte créé avec succès !");
                    messageLabel.setTextFill(Color.LIME);

                    // Rediriger vers login après 1.5 secondes
                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                            javafx.application.Platform.runLater(() -> new LoginPage(stage, conn));
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                } else {
                    messageLabel.setText("⚠ Échec de la création du compte");
                    messageLabel.setTextFill(Color.RED);
                }
            } catch (SQLException ex) {
                messageLabel.setText("⚠ Erreur SQL : " + ex.getMessage());
                messageLabel.setTextFill(Color.RED);
            }
        });

        // Action Retour
        backBtn.setOnAction(e -> new LoginPage(stage, conn));

        root.getChildren().addAll(title, pseudoField, mdpField, confirmMdpField, messageLabel, createBtn, backBtn);

        Scene scene = new Scene(root, 600, 650);
        stage.setScene(scene);
        stage.setTitle("Pacman - Créer un Compte");
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
                        "-fx-border-color: rgb(255, 184, 174);" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;" +
                        "-fx-padding: 10;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(255, 184, 174));
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
                        "-fx-border-color: rgb(255, 184, 174);" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;" +
                        "-fx-padding: 10;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(255, 184, 174));
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
