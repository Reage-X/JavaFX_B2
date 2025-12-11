package com.example.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class ProfilePage extends VBox {

    private Stage stage;
    private Connection conn;
    private MenuCompte menuCompte;
    private Label messageLabel;

    public ProfilePage(Stage stage, Connection conn, MenuCompte menuCompte) {
        this.stage = stage;
        this.conn = conn;
        this.menuCompte = menuCompte;

        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(40));
        setStyle("-fx-background-color: black;");
        setSpacing(30);

        // Titre
        Text title = new Text("GESTION DU PROFIL");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 40));
        title.setFill(Color.rgb(255, 184, 174));

        DropShadow glow = new DropShadow();
        glow.setColor(Color.rgb(255, 184, 174));
        glow.setRadius(20);
        glow.setSpread(0.7);
        title.setEffect(glow);

        // Info utilisateur
        Text userInfo = new Text("Pseudo actuel : " + Main.compte.getUser_name());
        userInfo.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        userInfo.setFill(Color.LIGHTGRAY);

        // Message de feedback
        messageLabel = new Label("");
        messageLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(500);

        // Boutons d'action
        Button changePseudoBtn = createActionButton("MODIFIER PSEUDO", Color.CYAN);
        Button changeMdpBtn = createActionButton("MODIFIER MOT DE PASSE", Color.YELLOW);
        Button deleteBtn = createActionButton("SUPPRIMER COMPTE", Color.RED);

        // Actions
        changePseudoBtn.setOnAction(e -> handleChangePseudo(userInfo));
        changeMdpBtn.setOnAction(e -> handleChangeMdp());
        deleteBtn.setOnAction(e -> handleDeleteAccount());

        getChildren().addAll(title, userInfo, messageLabel, changePseudoBtn, changeMdpBtn, deleteBtn);
    }

    private Button createActionButton(String text, Color color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
        btn.setPrefWidth(400);
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

    private void handleChangePseudo(Text userInfo) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Modifier Pseudo");
        dialog.setHeaderText("Entrez votre nouveau pseudo :");
        dialog.setContentText("Nouveau pseudo:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPseudo -> {
            if (!newPseudo.trim().isEmpty()) {
                try {
                    String oldPseudo = Main.compte.getUser_name();
                    Main.compte.setUser_name(newPseudo.trim());

                    if (Sql.updateUserName(Main.compte, oldPseudo, conn)) {
                        messageLabel.setText("✓ Pseudo modifié avec succès !");
                        messageLabel.setTextFill(Color.LIME);
                        userInfo.setText("Pseudo actuel : " + newPseudo.trim());
                        menuCompte.refreshUsername();
                    } else {
                        messageLabel.setText("⚠ Échec de la modification");
                        messageLabel.setTextFill(Color.RED);
                        Main.compte.setUser_name(oldPseudo);
                    }
                } catch (SQLException ex) {
                    messageLabel.setText("⚠ Erreur : " + ex.getMessage());
                    messageLabel.setTextFill(Color.RED);
                }
            }
        });
    }

    private void handleChangeMdp() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Modifier Mot de Passe");
        dialog.setHeaderText("Entrez votre nouveau mot de passe :");
        dialog.setContentText("Nouveau MDP:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newMdp -> {
            if (!newMdp.trim().isEmpty()) {
                try {
                    String oldMdp = Main.compte.getMDP();
                    Main.compte.setMDP(newMdp.trim());

                    if (Sql.updateMDP(Main.compte, oldMdp, conn)) {
                        messageLabel.setText("✓ Mot de passe modifié !");
                        messageLabel.setTextFill(Color.LIME);
                    } else {
                        messageLabel.setText("⚠ Échec de la modification");
                        messageLabel.setTextFill(Color.RED);
                        Main.compte.setMDP(oldMdp);
                    }
                } catch (SQLException ex) {
                    messageLabel.setText("⚠ Erreur : " + ex.getMessage());
                    messageLabel.setTextFill(Color.RED);
                }
            }
        });
    }

    private void handleDeleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Supprimer Compte");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer votre compte ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (Sql.deleteCompte(Main.compte, conn)) {
                    messageLabel.setText("✓ Compte supprimé");
                    messageLabel.setTextFill(Color.LIME);
                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                            javafx.application.Platform.runLater(() -> new LoginPage(stage, conn));
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                }
            } catch (SQLException ex) {
                messageLabel.setText("⚠ Erreur : " + ex.getMessage());
                messageLabel.setTextFill(Color.RED);
            }
        }
    }

    private String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255)
        );
    }
}
