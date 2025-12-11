
package com.example.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
        setSpacing(30);

        // Titre
        Label titre = new Label("👤 MON PROFIL");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titre.setTextFill(Color.web("#ffd700"));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(30));
        grid.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");

        Label lblPseudo = new Label("Pseudo:");
        lblPseudo.setTextFill(Color.WHITE);
        lblPseudo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label valPseudo = new Label(Main.compte.getUser_name());
        valPseudo.setTextFill(Color.LIGHTGRAY);
        valPseudo.setFont(Font.font("Arial", 16));

        Label lblScores = new Label("Scores enregistrés:");
        lblScores.setTextFill(Color.WHITE);
        lblScores.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label valScores = new Label(String.valueOf(Main.compte.getScore().size()));
        valScores.setTextFill(Color.web("#ffd700"));
        valScores.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        grid.add(lblPseudo, 0, 0);
        grid.add(valPseudo, 1, 0);
        grid.add(lblScores, 0, 1);
        grid.add(valScores, 1, 1);

        // Message de feedback
        messageLabel = new Label("");
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(500);

        // Boutons d'action
        Button changePseudoBtn = createActionButton("Modifier Pseudo");
        Button changeMdpBtn = createActionButton("Modifier Mot de Passe");
        Button deleteBtn = createActionButton("Supprimer Compte");
        deleteBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        changePseudoBtn.setOnAction(e -> handleChangePseudo(valPseudo));
        changeMdpBtn.setOnAction(e -> handleChangeMdp());
        deleteBtn.setOnAction(e -> handleDeleteAccount());

        getChildren().addAll(titre, grid, messageLabel, changePseudoBtn, changeMdpBtn, deleteBtn);
    }

    private Button createActionButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btn.setPrefWidth(300);
        return btn;
    }

    private void handleChangePseudo(Label valPseudo) {
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
                        valPseudo.setText(newPseudo.trim());
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
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Compte supprimé");
                    success.setContentText("Votre compte a été supprimé avec succès.");
                    success.showAndWait();
                    new LoginPage(stage, conn);
                }
            } catch (SQLException ex) {
                messageLabel.setText("⚠ Erreur : " + ex.getMessage());
                messageLabel.setTextFill(Color.RED);
            }
        }
    }
}