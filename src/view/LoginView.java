package view;

import dao.*;
import model.Comptes;
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

public class LoginView extends Application {

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #1a1a2e;");

        // Titre
        Label titre = new Label("🎮 PACMAN GAME");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titre.setTextFill(Color.YELLOW);

        Label sousTitre = new Label("Connexion");
        sousTitre.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        sousTitre.setTextFill(Color.WHITE);

        // Formulaire
        GridPane form = new GridPane();
        form.setAlignment(Pos.CENTER);
        form.setHgap(10);
        form.setVgap(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");

        Label emailLabel = new Label("Email:");
        emailLabel.setTextFill(Color.WHITE);
        TextField emailField = new TextField();
        emailField.setPromptText("email@example.com");
        emailField.setPrefWidth(250);

        Label passLabel = new Label("Mot de passe:");
        passLabel.setTextFill(Color.WHITE);
        PasswordField passField = new PasswordField();
        passField.setPromptText("••••••••");
        passField.setPrefWidth(250);

        form.add(emailLabel, 0, 0);
        form.add(emailField, 1, 0);
        form.add(passLabel, 0, 1);
        form.add(passField, 1, 1);

        // Boutons
        HBox boutonsBox = new HBox(15);
        boutonsBox.setAlignment(Pos.CENTER);

        Button btnConnexion = new Button("Se connecter");
        btnConnexion.setStyle("-fx-background-color: #ffd700; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnConnexion.setPrefWidth(150);

        Button btnInscription = new Button("S'inscrire");
        btnInscription.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnInscription.setPrefWidth(150);

        boutonsBox.getChildren().addAll(btnConnexion, btnInscription);

        // Message d'erreur
        Label messageErreur = new Label();
        messageErreur.setTextFill(Color.RED);
        messageErreur.setVisible(false);

        // Actions des boutons
        btnConnexion.setOnAction(e -> {
            String email = emailField.getText().trim();
            String motDePasse = passField.getText();

            if (email.isEmpty() || motDePasse.isEmpty()) {
                messageErreur.setText("⚠ Veuillez remplir tous les champs");
                messageErreur.setVisible(true);
                return;
            }

            try (Connection conn = Database.getConnection()) {
                ComptesDAO compteDAO = new ComptesDAO(conn);
                Comptes compte = compteDAO.authentifier(email, motDePasse);

                if (compte != null) {
                    messageErreur.setVisible(false);
                    // Redirection vers le tableau de bord
                    if (compte.getRole().equals("ADMIN")) {
                        new DashBoardAdmin().start(new Stage(), compte);
                    } else {
                        new DashBoardUser().start(new Stage(), compte);
                    }
                    stage.close();
                } else {
                    messageErreur.setText("❌ Email ou mot de passe incorrect");
                    messageErreur.setVisible(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                messageErreur.setText("❌ Erreur de connexion : " + ex.getMessage());
                messageErreur.setVisible(true);
            }
        });

        btnInscription.setOnAction(e -> {
            new InscriptionView().start(new Stage());
            stage.close();
        });

        // Ajout des éléments
        root.getChildren().addAll(titre, sousTitre, form, boutonsBox, messageErreur);

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("Pacman - Connexion");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}