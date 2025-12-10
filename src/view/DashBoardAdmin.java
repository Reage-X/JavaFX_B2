package view;

import dao.*;
import model.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class DashBoardAdmin extends Application {

    private Comptes compteConnecte;
    private TableView<Comptes> tableComptes;
    private TableView<Joueurs> tableJoueurs;

    @Override
    public void start(Stage stage) {
        throw new UnsupportedOperationException("Utilisez start(Stage, Comptes)");
    }

    public void start(Stage stage, Comptes compte) {
        this.compteConnecte = compte;

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
        stage.setTitle("Pacman - Administration");
        stage.show();
    }

    private HBox creerHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #16213e;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);

        Label titre = new Label("⚙️ ADMINISTRATION PACMAN");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.RED);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label admin = new Label("Admin: " + compteConnecte.getNom());
        admin.setTextFill(Color.WHITE);
        admin.setFont(Font.font("Arial", 14));

        Button btnDeconnexion = new Button("Déconnexion");
        btnDeconnexion.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-weight: bold;");
        btnDeconnexion.setOnAction(e -> {
            new LoginView().start(new Stage());
            ((Stage) btnDeconnexion.getScene().getWindow()).close();
        });

        header.getChildren().addAll(titre, spacer, admin, btnDeconnexion);
        return header;
    }

    private VBox creerMenu(Stage stage) {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(20));
        menu.setStyle("-fx-background-color: #0f3460;");
        menu.setPrefWidth(220);

        Button btnAccueil = creerBoutonMenu("🏠 Accueil");
        Button btnComptes = creerBoutonMenu("👥 Gestion Comptes");
        Button btnJoueurs = creerBoutonMenu("🎮 Gestion Joueurs");
        Button btnStatistiques = creerBoutonMenu("📊 Statistiques");

        btnAccueil.setOnAction(e -> {
            BorderPane root = (BorderPane) stage.getScene().getRoot();
            root.setCenter(creerAccueil());
        });

        btnComptes.setOnAction(e -> {
            BorderPane root = (BorderPane) stage.getScene().getRoot();
            root.setCenter(creerGestionComptes());
        });

        btnJoueurs.setOnAction(e -> {
            BorderPane root = (BorderPane) stage.getScene().getRoot();
            root.setCenter(creerGestionJoueurs());
        });

        btnStatistiques.setOnAction(e -> {
            BorderPane root = (BorderPane) stage.getScene().getRoot();
            root.setCenter(creerStatistiques());
        });

        menu.getChildren().addAll(btnAccueil, btnComptes, btnJoueurs, btnStatistiques);
        return menu;
    }

    private Button creerBoutonMenu(String texte) {
        Button btn = new Button(texte);
        btn.setPrefWidth(180);
        btn.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 12; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 12; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-padding: 12; -fx-cursor: hand;"));
        return btn;
    }

    private VBox creerAccueil() {
        VBox vbox = new VBox(30);
        vbox.setPadding(new Insets(40));
        vbox.setAlignment(Pos.TOP_CENTER);

        Label titre = new Label("🏠 TABLEAU DE BORD");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titre.setTextFill(Color.RED);

        // Statistiques rapides
        HBox stats = new HBox(20);
        stats.setAlignment(Pos.CENTER);

        try (Connection conn = Database.getConnection()) {
            ComptesDAO comptesDAO = new ComptesDAO(conn);
            JoueursDAO joueursDAO = new JoueursDAO(conn);

            int nbComptes = comptesDAO.listerComptes().size();
            int nbJoueurs = joueursDAO.listerJoueurs().size();

            VBox statComptes = creerCarteStat("👥 Comptes", String.valueOf(nbComptes), "#3498db");
            VBox statJoueurs = creerCarteStat("🎮 Joueurs", String.valueOf(nbJoueurs), "#2ecc71");
            VBox statTotal = creerCarteStat("📊 Total", String.valueOf(nbComptes + nbJoueurs), "#e74c3c");

            stats.getChildren().addAll(statComptes, statJoueurs, statTotal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        vbox.getChildren().addAll(titre, stats);
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

    // ========== GESTION COMPTES ==========
    private VBox creerGestionComptes() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));

        Label titre = new Label("👥 GESTION DES COMPTES");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titre.setTextFill(Color.RED);

        // Boutons d'action
        HBox boutons = new HBox(15);
        boutons.setAlignment(Pos.CENTER_LEFT);

        Button btnAjouter = new Button("➕ Ajouter");
        Button btnModifier = new Button("✏️ Modifier");
        Button btnSupprimer = new Button("🗑️ Supprimer");
        Button btnRechercher = new Button("🔍 Rechercher");

        styliserBouton(btnAjouter, "#2ecc71");
        styliserBouton(btnModifier, "#3498db");
        styliserBouton(btnSupprimer, "#e74c3c");
        styliserBouton(btnRechercher, "#f39c12");

        boutons.getChildren().addAll(btnAjouter, btnModifier, btnSupprimer, btnRechercher);

        // Table des comptes
        tableComptes = new TableView<>();
        tableComptes.setStyle("-fx-background-color: #16213e;");

        TableColumn<Comptes, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Comptes, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(150);

        TableColumn<Comptes, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(200);

        TableColumn<Comptes, String> colRole = new TableColumn<>("Rôle");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setPrefWidth(100);

        tableComptes.getColumns().addAll(colId, colNom, colEmail, colRole);

        chargerComptes();

        // Actions des boutons
        btnAjouter.setOnAction(e -> ajouterCompte());
        btnModifier.setOnAction(e -> modifierCompte());
        btnSupprimer.setOnAction(e -> supprimerCompte());
        btnRechercher.setOnAction(e -> rechercherCompte());

        vbox.getChildren().addAll(titre, boutons, tableComptes);
        return vbox;
    }

    private void chargerComptes() {
        try (Connection conn = Database.getConnection()) {
            ComptesDAO dao = new ComptesDAO(conn);
            List<Comptes> comptes = dao.listerComptes();
            tableComptes.getItems().clear();
            tableComptes.getItems().addAll(comptes);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Erreur", "Impossible de charger les comptes", Alert.AlertType.ERROR);
        }
    }

    private void ajouterCompte() {
        Dialog<Comptes> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un compte");
        dialog.setHeaderText("Créer un nouveau compte");

        ButtonType btnValider = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nomField = new TextField();
        TextField emailField = new TextField();
        PasswordField passField = new PasswordField();
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("USER", "ADMIN");
        roleBox.setValue("USER");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Mot de passe:"), 0, 2);
        grid.add(passField, 1, 2);
        grid.add(new Label("Rôle:"), 0, 3);
        grid.add(roleBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == btnValider) {
                return new Comptes(nomField.getText(), emailField.getText(),
                        passField.getText(), roleBox.getValue());
            }
            return null;
        });

        Optional<Comptes> result = dialog.showAndWait();
        result.ifPresent(compte -> {
            try (Connection conn = Database.getConnection()) {
                ComptesDAO dao = new ComptesDAO(conn);
                if (dao.ajouterCompte(compte)) {
                    showAlert("Succès", "Compte créé avec succès", Alert.AlertType.INFORMATION);
                    chargerComptes();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Erreur lors de la création", Alert.AlertType.ERROR);
            }
        });
    }

    private void modifierCompte() {
        Comptes selected = tableComptes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner un compte", Alert.AlertType.WARNING);
            return;
        }

        Dialog<Comptes> dialog = new Dialog<>();
        dialog.setTitle("Modifier le compte");
        dialog.setHeaderText("Modifier: " + selected.getEmail());

        ButtonType btnValider = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nomField = new TextField(selected.getNom());
        TextField emailField = new TextField(selected.getEmail());
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("USER", "ADMIN");
        roleBox.setValue(selected.getRole());

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Rôle:"), 0, 2);
        grid.add(roleBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == btnValider) {
                selected.setNom(nomField.getText());
                selected.setEmail(emailField.getText());
                selected.setRole(roleBox.getValue());
                return selected;
            }
            return null;
        });

        Optional<Comptes> result = dialog.showAndWait();
        result.ifPresent(compte -> {
            try (Connection conn = Database.getConnection()) {
                ComptesDAO dao = new ComptesDAO(conn);
                if (dao.modifierCompte(compte)) {
                    showAlert("Succès", "Compte modifié", Alert.AlertType.INFORMATION);
                    chargerComptes();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Erreur lors de la modification", Alert.AlertType.ERROR);
            }
        });
    }

    private void supprimerCompte() {
        Comptes selected = tableComptes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner un compte", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le compte ?");
        confirmation.setContentText("Email: " + selected.getEmail());

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = Database.getConnection()) {
                ComptesDAO dao = new ComptesDAO(conn);
                if (dao.supprimerCompte(selected.getId())) {
                    showAlert("Succès", "Compte supprimé", Alert.AlertType.INFORMATION);
                    chargerComptes();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Erreur lors de la suppression", Alert.AlertType.ERROR);
            }
        }
    }

    private void rechercherCompte() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rechercher");
        dialog.setHeaderText("Rechercher un compte");
        dialog.setContentText("Email:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(email -> {
            try (Connection conn = Database.getConnection()) {
                ComptesDAO dao = new ComptesDAO(conn);
                Comptes compte = dao.rechercherParEmail(email);
                if (compte != null) {
                    tableComptes.getItems().clear();
                    tableComptes.getItems().add(compte);
                } else {
                    showAlert("Aucun résultat", "Aucun compte trouvé", Alert.AlertType.INFORMATION);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // ========== GESTION JOUEURS ==========
    private VBox creerGestionJoueurs() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));

        Label titre = new Label("🎮 GESTION DES JOUEURS");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titre.setTextFill(Color.RED);

        HBox boutons = new HBox(15);
        boutons.setAlignment(Pos.CENTER_LEFT);

        Button btnModifier = new Button("✏️ Modifier");
        Button btnSupprimer = new Button("🗑️ Supprimer");
        Button btnActualiser = new Button("🔄 Actualiser");

        styliserBouton(btnModifier, "#3498db");
        styliserBouton(btnSupprimer, "#e74c3c");
        styliserBouton(btnActualiser, "#95a5a6");

        boutons.getChildren().addAll(btnModifier, btnSupprimer, btnActualiser);

        tableJoueurs = new TableView<>();
        tableJoueurs.setStyle("-fx-background-color: #16213e;");

        TableColumn<Joueurs, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Joueurs, String> colPseudo = new TableColumn<>("Pseudo");
        colPseudo.setCellValueFactory(new PropertyValueFactory<>("pseudo"));

        TableColumn<Joueurs, Integer> colScore = new TableColumn<>("Score Total");
        colScore.setCellValueFactory(new PropertyValueFactory<>("scoreTotal"));

        TableColumn<Joueurs, Integer> colNiveau = new TableColumn<>("Niveau");
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));

        TableColumn<Joueurs, Integer> colVies = new TableColumn<>("Vies");
        colVies.setCellValueFactory(new PropertyValueFactory<>("viesRestantes"));

        tableJoueurs.getColumns().addAll(colId, colPseudo, colScore, colNiveau, colVies);

        chargerJoueurs();

        btnModifier.setOnAction(e -> modifierJoueur());
        btnSupprimer.setOnAction(e -> supprimerJoueur());
        btnActualiser.setOnAction(e -> chargerJoueurs());

        vbox.getChildren().addAll(titre, boutons, tableJoueurs);
        return vbox;
    }

    private void chargerJoueurs() {
        try (Connection conn = Database.getConnection()) {
            JoueursDAO dao = new JoueursDAO(conn);
            List<Joueurs> joueurs = dao.listerJoueurs();
            tableJoueurs.getItems().clear();
            tableJoueurs.getItems().addAll(joueurs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void modifierJoueur() {
        Joueurs selected = tableJoueurs.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner un joueur", Alert.AlertType.WARNING);
            return;
        }

        Dialog<Joueurs> dialog = new Dialog<>();
        dialog.setTitle("Modifier le joueur");

        ButtonType btnValider = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField pseudoField = new TextField(selected.getPseudo());
        TextField niveauField = new TextField(String.valueOf(selected.getNiveau()));
        TextField viesField = new TextField(String.valueOf(selected.getViesRestantes()));

        grid.add(new Label("Pseudo:"), 0, 0);
        grid.add(pseudoField, 1, 0);
        grid.add(new Label("Niveau:"), 0, 1);
        grid.add(niveauField, 1, 1);
        grid.add(new Label("Vies:"), 0, 2);
        grid.add(viesField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == btnValider) {
                selected.setPseudo(pseudoField.getText());
                selected.setNiveau(Integer.parseInt(niveauField.getText()));
                selected.setViesRestantes(Integer.parseInt(viesField.getText()));
                return selected;
            }
            return null;
        });

        Optional<Joueurs> result = dialog.showAndWait();
        result.ifPresent(joueur -> {
            try (Connection conn = Database.getConnection()) {
                JoueursDAO dao = new JoueursDAO(conn);
                if (dao.modifierJoueur(joueur)) {
                    showAlert("Succès", "Joueur modifié", Alert.AlertType.INFORMATION);
                    chargerJoueurs();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void supprimerJoueur() {
        Joueurs selected = tableJoueurs.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner un joueur", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le joueur ?");
        confirmation.setContentText("Pseudo: " + selected.getPseudo());

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = Database.getConnection()) {
                JoueursDAO dao = new JoueursDAO(conn);
                if (dao.supprimerJoueur(selected.getId())) {
                    showAlert("Succès", "Joueur supprimé", Alert.AlertType.INFORMATION);
                    chargerJoueurs();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private VBox creerStatistiques() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.TOP_CENTER);

        Label titre = new Label("📊 STATISTIQUES");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titre.setTextFill(Color.RED);

        TextArea stats = new TextArea();
        stats.setEditable(false);
        stats.setPrefHeight(400);
        stats.setStyle("-fx-font-family: monospace; -fx-font-size: 14px;");

        StringBuilder sb = new StringBuilder();
        try (Connection conn = Database.getConnection()) {
            ComptesDAO comptesDAO = new ComptesDAO(conn);
            JoueursDAO joueursDAO = new JoueursDAO(conn);

            List<Comptes> comptes = comptesDAO.listerComptes();
            List<Joueurs> joueurs = joueursDAO.listerJoueurs();

            sb.append("=== STATISTIQUES GÉNÉRALES ===\n\n");
            sb.append("Nombre total de comptes: ").append(comptes.size()).append("\n");
            sb.append("Nombre total de joueurs: ").append(joueurs.size()).append("\n\n");

            sb.append("=== TOP 5 JOUEURS ===\n");
            List<Joueurs> top5 = joueursDAO.getClassement();
            for (int i = 0; i < Math.min(5, top5.size()); i++) {
                Joueurs j = top5.get(i);
                sb.append((i+1)).append(". ").append(j.getPseudo())
                        .append(" - Score: ").append(j.getScoreTotal())
                        .append(" - Niveau: ").append(j.getNiveau()).append("\n");
            }

        } catch (Exception ex) {
            sb.append("Erreur lors du chargement des statistiques");
            ex.printStackTrace();
        }

        stats.setText(sb.toString());
        vbox.getChildren().addAll(titre, stats);
        return vbox;
    }

    private void styliserBouton(Button btn, String couleur) {
        btn.setStyle("-fx-background-color: " + couleur + "; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
    }

    private void showAlert(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}