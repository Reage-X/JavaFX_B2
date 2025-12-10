package view;

import dao.*;
import model.*;
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
import java.util.List;

/**
 * Dashboard pour les utilisateurs normaux (non-admin)
 * Permet de jouer, voir les statistiques et gérer son profil
 */
public class DashBoardUser extends Application {

    private Comptes compteConnecte;
    private Joueurs joueur;

    @Override
    public void start(Stage stage) {
        throw new UnsupportedOperationException("Utilisez start(Stage, Comptes)");
    }

    public void start(Stage stage, Comptes compte) {
        this.compteConnecte = compte;

        // Charger les informations du joueur
        try (Connection conn = Database.getConnection()) {
            JoueursDAO joueursDAO = new JoueursDAO(conn);
            this.joueur = joueursDAO.getJoueurParCompte(compte.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        // Header
        HBox header = creerHeader();
        root.setTop(header);

        // Menu latéral
        VBox menu = creerMenu(stage);
        root.setLeft(menu);

        // Contenu central par défaut
        VBox centre = creerAccueil();
        root.setCenter(centre);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Pacman - Tableau de bord");
        stage.show();
    }

    private HBox creerHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #16213e;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);

        Label titre = new Label("🎮 PACMAN GAME");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.YELLOW);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label user = new Label("Joueur: " + (joueur != null ? joueur.getPseudo() : compteConnecte.getNom()));
        user.setTextFill(Color.WHITE);
        user.setFont(Font.font("Arial", 14));

        Button btnDeconnexion = new Button("Déconnexion");
        btnDeconnexion.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-weight: bold;");
        btnDeconnexion.setOnAction(e -> {
            new LoginView().start(new Stage());
            ((Stage) btnDeconnexion.getScene().getWindow()).close();
        });

        header.getChildren().addAll(titre, spacer, user, btnDeconnexion);
        return header;
    }

    private VBox creerMenu(Stage stage) {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(20));
        menu.setStyle("-fx-background-color: #0f3460;");
        menu.setPrefWidth(220);

        Button btnAccueil = creerBoutonMenu("🏠 Accueil");
        Button btnJouer = creerBoutonMenu("🎮 Jouer");
        Button btnClassement = creerBoutonMenu("🏆 Classement");
        Button btnProfil = creerBoutonMenu("👤 Mon Profil");

        btnAccueil.setOnAction(e -> {
            BorderPane root = (BorderPane) stage.getScene().getRoot();
            root.setCenter(creerAccueil());
        });

        btnJouer.setOnAction(e -> {
            if (joueur != null) {
                new PacmanGameView().start(new Stage(), compteConnecte, joueur);
                stage.close();
            } else {
                showAlert("Erreur", "Impossible de charger les données du joueur", Alert.AlertType.ERROR);
            }
        });

        btnClassement.setOnAction(e -> {
            BorderPane root = (BorderPane) stage.getScene().getRoot();
            root.setCenter(creerClassement());
        });

        btnProfil.setOnAction(e -> {
            BorderPane root = (BorderPane) stage.getScene().getRoot();
            root.setCenter(creerProfil());
        });

        menu.getChildren().addAll(btnAccueil, btnJouer, btnClassement, btnProfil);
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
        titre.setTextFill(Color.YELLOW);

        if (joueur != null) {
            // Statistiques du joueur
            HBox stats = new HBox(20);
            stats.setAlignment(Pos.CENTER);

            VBox statScore = creerCarteStat("🎯 Score Total", String.valueOf(joueur.getScoreTotal()), "#3498db");
            VBox statNiveau = creerCarteStat("🎮 Niveau", String.valueOf(joueur.getNiveau()), "#2ecc71");
            VBox statVies = creerCarteStat("❤️ Vies", String.valueOf(joueur.getViesRestantes()), "#e74c3c");

            stats.getChildren().addAll(statScore, statNiveau, statVies);

            // Bouton de jeu principal
            Button btnJouerGrand = new Button("▶️ COMMENCER À JOUER");
            btnJouerGrand.setStyle("-fx-background-color: #ffd700; -fx-text-fill: black; " +
                    "-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 20 40; -fx-cursor: hand;");
            btnJouerGrand.setPrefSize(400, 80);
            btnJouerGrand.setOnAction(e -> {
                new PacmanGameView().start(new Stage(), compteConnecte, joueur);
                ((Stage) btnJouerGrand.getScene().getWindow()).close();
            });

            vbox.getChildren().addAll(titre, stats, btnJouerGrand);
        } else {
            Label erreur = new Label("Erreur de chargement du profil joueur");
            erreur.setTextFill(Color.RED);
            vbox.getChildren().addAll(titre, erreur);
        }

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

    private VBox creerClassement() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.TOP_CENTER);

        Label titre = new Label("🏆 CLASSEMENT DES MEILLEURS JOUEURS");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titre.setTextFill(Color.YELLOW);

        TextArea classement = new TextArea();
        classement.setEditable(false);
        classement.setPrefHeight(500);
        classement.setStyle("-fx-font-family: monospace; -fx-font-size: 16px; " +
                "-fx-background-color: #16213e; -fx-text-fill: white;");

        StringBuilder sb = new StringBuilder();
        try (Connection conn = Database.getConnection()) {
            JoueursDAO joueursDAO = new JoueursDAO(conn);
            List<Joueurs> top10 = joueursDAO.getClassement();

            sb.append("╔═══════════════════════════════════════════════════╗\n");
            sb.append("║          TOP 10 - MEILLEURS SCORES                ║\n");
            sb.append("╠═══════════════════════════════════════════════════╣\n");

            for (int i = 0; i < top10.size(); i++) {
                Joueurs j = top10.get(i);
                String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "  ";
                sb.append(String.format("║ %s %2d. %-20s %10d pts  ║\n",
                        medal, (i + 1), j.getPseudo(), j.getScoreTotal()));
            }

            sb.append("╚═══════════════════════════════════════════════════╝\n");

            // Position du joueur actuel
            if (joueur != null) {
                sb.append("\n");
                sb.append("╔═══════════════════════════════════════════════════╗\n");
                sb.append("║              VOTRE POSITION                       ║\n");
                sb.append("╠═══════════════════════════════════════════════════╣\n");
                sb.append(String.format("║ Pseudo: %-40s ║\n", joueur.getPseudo()));
                sb.append(String.format("║ Score Total: %-35d ║\n", joueur.getScoreTotal()));
                sb.append(String.format("║ Niveau: %-40d ║\n", joueur.getNiveau()));
                sb.append("╚═══════════════════════════════════════════════════╝\n");
            }

        } catch (Exception ex) {
            sb.append("Erreur lors du chargement du classement");
            ex.printStackTrace();
        }

        classement.setText(sb.toString());
        vbox.getChildren().addAll(titre, classement);
        return vbox;
    }

    private VBox creerProfil() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.TOP_CENTER);

        Label titre = new Label("👤 MON PROFIL");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titre.setTextFill(Color.YELLOW);

        if (joueur != null) {
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(20);
            grid.setVgap(15);
            grid.setPadding(new Insets(30));
            grid.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");

            // Informations du compte
            Label lblNom = new Label("Nom:");
            lblNom.setTextFill(Color.WHITE);
            lblNom.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            Label valNom = new Label(compteConnecte.getNom());
            valNom.setTextFill(Color.LIGHTGRAY);
            valNom.setFont(Font.font("Arial", 16));

            Label lblEmail = new Label("Email:");
            lblEmail.setTextFill(Color.WHITE);
            lblEmail.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            Label valEmail = new Label(compteConnecte.getEmail());
            valEmail.setTextFill(Color.LIGHTGRAY);
            valEmail.setFont(Font.font("Arial", 16));

            Label lblPseudo = new Label("Pseudo:");
            lblPseudo.setTextFill(Color.WHITE);
            lblPseudo.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            Label valPseudo = new Label(joueur.getPseudo());
            valPseudo.setTextFill(Color.LIGHTGRAY);
            valPseudo.setFont(Font.font("Arial", 16));

            Label lblScore = new Label("Score Total:");
            lblScore.setTextFill(Color.WHITE);
            lblScore.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            Label valScore = new Label(String.valueOf(joueur.getScoreTotal()));
            valScore.setTextFill(Color.YELLOW);
            valScore.setFont(Font.font("Arial", FontWeight.BOLD, 16));

            Label lblNiveau = new Label("Niveau:");
            lblNiveau.setTextFill(Color.WHITE);
            lblNiveau.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            Label valNiveau = new Label(String.valueOf(joueur.getNiveau()));
            valNiveau.setTextFill(Color.CYAN);
            valNiveau.setFont(Font.font("Arial", FontWeight.BOLD, 16));

            Label lblVies = new Label("Vies Restantes:");
            lblVies.setTextFill(Color.WHITE);
            lblVies.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            Label valVies = new Label(String.valueOf(joueur.getViesRestantes()));
            valVies.setTextFill(Color.RED);
            valVies.setFont(Font.font("Arial", FontWeight.BOLD, 16));

            grid.add(lblNom, 0, 0);
            grid.add(valNom, 1, 0);
            grid.add(lblEmail, 0, 1);
            grid.add(valEmail, 1, 1);
            grid.add(lblPseudo, 0, 2);
            grid.add(valPseudo, 1, 2);
            grid.add(lblScore, 0, 3);
            grid.add(valScore, 1, 3);
            grid.add(lblNiveau, 0, 4);
            grid.add(valNiveau, 1, 4);
            grid.add(lblVies, 0, 5);
            grid.add(valVies, 1, 5);

            vbox.getChildren().addAll(titre, grid);
        } else {
            Label erreur = new Label("Impossible de charger les informations du profil");
            erreur.setTextFill(Color.RED);
            vbox.getChildren().addAll(titre, erreur);
        }

        return vbox;
    }

    private void showAlert(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}

