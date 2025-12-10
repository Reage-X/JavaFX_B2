package view;

import dao.*;
import model.Comptes;
import model.Joueurs;
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

public class InscriptionView extends Application {

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1a1a2e;");

        // Titre
        Label titre = new Label("📝 INSCRIPTION");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titre.setTextFill(Color.YELLOW);

        // Formulaire
        GridPane form = new GridPane();
        form.setAlignment(Pos.CENTER);
        form.setHgap(10);
        form.setVgap(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");

        // Champs
        Label nomLabel = new Label("Nom complet:");
        nomLabel.setTextFill(Color.WHITE);
        TextField nomField = new TextField();
        nomField.setPromptText("Jean Dupont");

        Label emailLabel = new Label("Email:");
        emailLabel.setTextFill(Color.WHITE);
        TextField emailField = new TextField();
        emailField.setPromptText("email@example.com");

        Label passLabel = new Label("Mot de passe:");
        passLabel.setTextFill(Color.WHITE);
        PasswordField passField = new PasswordField();
        passField.setPromptText("••••••••");

        Label confirmLabel = new Label("Confirmer:");
        confirmLabel.setTextFill(Color.WHITE);
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("••••••••");

        Label pseudoLabel = new Label("Pseudo de jeu:");
        pseudoLabel.setTextFill(Color.WHITE);
        TextField pseudoField = new TextField();
        pseudoField.setPromptText("PacmanPro");

        Label roleLabel = new Label("Rôle:");
        roleLabel.setTextFill(Color.WHITE);
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("USER", "ADMIN");
        roleBox.setValue("USER");

        form.add(nomLabel, 0, 0);
        form.add(nomField, 1, 0);
        form.add(emailLabel, 0, 1);
        form.add(emailField, 1, 1);
        form.add(passLabel, 0, 2);
        form.add(passField, 1, 2);
        form.add(confirmLabel, 0, 3);
        form.add(confirmField, 1, 3);
        form.add(pseudoLabel, 0, 4);
        form.add(pseudoField, 1, 4);
        form.add(roleLabel, 0, 5);
        form.add(roleBox, 1, 5);

        // Boutons
        HBox boutonsBox = new HBox(15);
        boutonsBox.setAlignment(Pos.CENTER);

        Button btnCreer = new Button("Créer le compte");
        btnCreer.setStyle("-fx-background-color: #ffd700; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnCreer.setPrefWidth(150);

        Button btnRetour = new Button("Retour");
        btnRetour.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnRetour.setPrefWidth(150);

        boutonsBox.getChildren().addAll(btnCreer, btnRetour);

        // Message
        Label message = new Label();
        message.setTextFill(Color.RED);
        message.setVisible(false);

        // Actions
        btnCreer.setOnAction(e -> {
            String nom = nomField.getText().trim();
            String email = emailField.getText().trim();
            String pass = passField.getText();
            String confirm = confirmField.getText();
            String pseudo = pseudoField.getText().trim();
            String role = roleBox.getValue();

            // Validations
            if (nom.isEmpty() || email.isEmpty() || pass.isEmpty() || pseudo.isEmpty()) {
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

            if (!email.contains("@")) {
                message.setText("⚠ Email invalide");
                message.setTextFill(Color.RED);
                message.setVisible(true);
                return;
            }

            // Création du compte
            try (Connection conn = Database.getConnection()) {
                ComptesDAO compteDAO = new ComptesDAO(conn);
                JoueursDAO joueurDAO = new JoueursDAO(conn);

                Comptes compte = new Comptes(nom, email, pass, role);

                if (compteDAO.ajouterCompte(compte)) {
                    Joueurs joueur = new Joueurs(pseudo, compte.getId());
                    if (joueurDAO.ajouterJoueur(joueur)) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Succès");
                        alert.setHeaderText("Compte créé avec succès !");
                        alert.setContentText("Vous pouvez maintenant vous connecter.");
                        alert.showAndWait();

                        new LoginView().start(new Stage());
                        stage.close();
                    }
                } else {
                    message.setText("❌ Email déjà utilisé");
                    message.setTextFill(Color.RED);
                    message.setVisible(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("❌ Erreur : " + ex.getMessage());
                message.setTextFill(Color.RED);
                message.setVisible(true);
            }
        });

        btnRetour.setOnAction(e -> {
            new LoginView().start(new Stage());
            stage.close();
        });

        root.getChildren().addAll(titre, form, boutonsBox, message);

        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("Pacman - Inscription");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}