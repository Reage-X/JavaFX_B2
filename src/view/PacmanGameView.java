package view;

import dao.*;
import model.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;

public class PacmanGameView extends Application {

    private Comptes compte;
    private Joueurs joueur;
    private Parties partieActuelle;
    private Pacman pacman;
    private PacmanMap map;

    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    private int score = 0;
    private int niveau = 1;
    private boolean jeuEnPause = false;
    private boolean jeuTermine = false;

    private Label lblScore;
    private Label lblNiveau;
    private Label lblVies;

    @Override
    public void start(Stage stage) {
        throw new UnsupportedOperationException("Utilisez start(Stage, Comptes, Joueurs)");
    }

    public void start(Stage stage, Comptes compte, Joueurs joueur) {
        this.compte = compte;
        this.joueur = joueur;

        // Créer ou reprendre une partie
        try (Connection conn = Database.getConnection()) {
            PartiesDAO partiesDAO = new PartiesDAO(conn);
            partieActuelle = partiesDAO.getPartieEnCours(joueur.getId());

            if (partieActuelle == null) {
                // Nouvelle partie
                partieActuelle = new Parties(joueur.getId());
                partiesDAO.creerPartie(partieActuelle);
            } else {
                // Reprendre la partie
                score = partieActuelle.getScorePartie();
                niveau = partieActuelle.getNiveauAtteint();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Initialiser la map selon le niveau
        map = new PacmanMap(niveau);

        // Trouver une position de départ valide (pas sur un mur)
        double startX = trouverPositionDepart();
        double startY = PacmanMap.grilleVersPixels(23);

        // Vérifier et ajuster si la position est sur un mur
        int gridX = PacmanMap.pixelsVersGrille(startX);
        int gridY = PacmanMap.pixelsVersGrille(startY);

        // Chercher une case vide proche pour démarrer
        while (map.estMur(gridX, gridY)) {
            gridY++;
            startY = PacmanMap.grilleVersPixels(gridY);
            if (gridY >= PacmanMap.HAUTEUR - 1) {
                gridY = 1;
                startY = PacmanMap.grilleVersPixels(gridY);
                break;
            }
        }

        pacman = new Pacman(startX, startY);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #000000;");

        // Header avec infos de jeu
        HBox header = creerHeader();
        root.setTop(header);

        // Canvas de jeu - Adapter la taille à la map
        int canvasWidth = PacmanMap.LARGEUR * PacmanMap.TAILLE_CASE;
        int canvasHeight = PacmanMap.HAUTEUR * PacmanMap.TAILLE_CASE;
        canvas = new Canvas(canvasWidth, canvasHeight);
        gc = canvas.getGraphicsContext2D();

        // Centrer le canvas
        StackPane centerPane = new StackPane(canvas);
        centerPane.setStyle("-fx-background-color: #000000;");
        root.setCenter(centerPane);

        // Panneau de contrôle
        VBox controles = creerControles(stage);
        root.setRight(controles);

        // Gestion des touches
        Scene scene = new Scene(root, canvasWidth + 200, canvasHeight + 60);
        scene.setOnKeyPressed(e -> gererTouches(e.getCode()));

        // Démarrer le jeu
        demarrerJeu();

        stage.setScene(scene);
        stage.setTitle("Pacman - Niveau " + niveau);
        stage.show();

        canvas.requestFocus();
    }

    /**
     * Trouve une position de départ valide pour Pacman (centre horizontal)
     */
    private double trouverPositionDepart() {
        // Chercher le centre horizontal d'une ligne vide
        int centreX = PacmanMap.LARGEUR / 2;
        return PacmanMap.grilleVersPixels(centreX);
    }

    private HBox creerHeader() {
        HBox header = new HBox(30);
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #1a1a2e;");

        lblScore = new Label("Score: 0");
        lblScore.setTextFill(Color.YELLOW);
        lblScore.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        lblNiveau = new Label("Niveau: " + niveau);
        lblNiveau.setTextFill(Color.CYAN);
        lblNiveau.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        lblVies = new Label("Vies: " + pacman.getVies());
        lblVies.setTextFill(Color.RED);
        lblVies.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label lblJoueur = new Label("Joueur: " + joueur.getPseudo());
        lblJoueur.setTextFill(Color.WHITE);
        lblJoueur.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        header.getChildren().addAll(lblScore, lblNiveau, lblVies, lblJoueur);
        return header;
    }

    private VBox creerControles(Stage stage) {
        VBox controles = new VBox(15);
        controles.setPadding(new Insets(20));
        controles.setStyle("-fx-background-color: #16213e;");
        controles.setPrefWidth(180);
        controles.setAlignment(Pos.TOP_CENTER);

        Label titre = new Label("CONTRÔLES");
        titre.setTextFill(Color.YELLOW);
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label infos = new Label(
                "↑ ↓ ← → : Déplacer\n" +
                        "ESPACE : Pause\n" +
                        "ESC : Menu"
        );
        infos.setTextFill(Color.WHITE);
        infos.setFont(Font.font("Monospaced", 12));

        Button btnPause = new Button("⏸ Pause");
        btnPause.setPrefWidth(140);
        btnPause.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        btnPause.setOnAction(e -> {
            jeuEnPause = !jeuEnPause;
            btnPause.setText(jeuEnPause ? "▶ Reprendre" : "⏸ Pause");
            sauvegarderPartie();
        });

        Button btnSauvegarder = new Button("💾 Sauvegarder");
        btnSauvegarder.setPrefWidth(140);
        btnSauvegarder.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        btnSauvegarder.setOnAction(e -> {
            sauvegarderPartie();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Partie sauvegardée !");
            alert.showAndWait();
        });

        Button btnQuitter = new Button("🚪 Quitter");
        btnQuitter.setPrefWidth(140);
        btnQuitter.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnQuitter.setOnAction(e -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Quitter");
            confirmation.setHeaderText("Voulez-vous sauvegarder avant de quitter ?");

            ButtonType btnSave = new ButtonType("Sauvegarder et quitter");
            ButtonType btnNoSave = new ButtonType("Quitter sans sauvegarder");
            ButtonType btnCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

            confirmation.getButtonTypes().setAll(btnSave, btnNoSave, btnCancel);

            confirmation.showAndWait().ifPresent(response -> {
                if (response == btnSave) {
                    sauvegarderPartie();
                    retourDashboard(stage);
                } else if (response == btnNoSave) {
                    retourDashboard(stage);
                }
            });
        });

        Separator sep1 = new Separator();
        Separator sep2 = new Separator();

        Label stats = new Label("STATISTIQUES");
        stats.setTextFill(Color.CYAN);
        stats.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label powerups = new Label("Power Pellets:\n" +
                "• Invincibilité 6s\n" +
                "• Vitesse +10%\n" +
                "• Manger fantômes");
        powerups.setTextFill(Color.LIGHTGRAY);
        powerups.setFont(Font.font("Monospaced", 10));

        controles.getChildren().addAll(
                titre, infos, sep1,
                btnPause, btnSauvegarder, btnQuitter,
                sep2, stats, powerups
        );

        return controles;
    }

    private void demarrerJeu() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!jeuEnPause && !jeuTermine) {
                    update();
                    render();
                }
            }
        };
        gameLoop.start();
    }

    private void update() {
        // Sauvegarder la position actuelle avant déplacement
        double anciennePosX = pacman.getPosX();
        double anciennePosY = pacman.getPosY();

        // Déplacer Pacman
        pacman.deplacer();

        // Vérifier les collisions avec les murs
        if (collisionAvecMur(pacman.getPosX(), pacman.getPosY())) {
            // Annuler le déplacement
            pacman.setPosX(anciennePosX);
            pacman.setPosY(anciennePosY);
            // NE PAS mettre la direction à IMMOBILE !
            // Pacman garde sa direction actuelle pour pouvoir réessayer
        }

        // Vérifier l'invincibilité
        pacman.verifierInvincibilite();

        // Gérer la collecte des dots et power pellets
        int gridX = PacmanMap.pixelsVersGrille(pacman.getPosX());
        int gridY = PacmanMap.pixelsVersGrille(pacman.getPosY());

        int cellule = map.getCase(gridX, gridY);
        if (cellule == PacmanMap.DOT) {
            score += 10;
            map.setCase(gridX, gridY, PacmanMap.VIDE);
        } else if (cellule == PacmanMap.POWER_PELLET) {
            score += 50;
            map.setCase(gridX, gridY, PacmanMap.VIDE);
            pacman.activerInvincibilite();
        }

        // Mettre à jour les labels
        lblScore.setText("Score: " + score);
        lblNiveau.setText("Niveau: " + niveau);
        lblVies.setText("Vies: " + pacman.getVies());

        // Vérifier si le jeu est terminé
        if (pacman.getVies() <= 0) {
            jeuTermine = true;
            terminerPartie();
        }
    }

    /**
     * Vérifie si Pacman entre en collision avec un mur
     * Utilise une hitbox circulaire autour de Pacman
     */
    private boolean collisionAvecMur(double posX, double posY) {
        // Rayon de collision de Pacman (légèrement plus petit que la taille visuelle)
        double rayonCollision = Pacman.TAILLE_PACMAN - 2;

        // Points à vérifier autour de Pacman (8 points cardinaux + centre)
        double[][] pointsCollision = {
                {posX, posY},                                    // Centre
                {posX - rayonCollision, posY},                   // Gauche
                {posX + rayonCollision, posY},                   // Droite
                {posX, posY - rayonCollision},                   // Haut
                {posX, posY + rayonCollision},                   // Bas
                {posX - rayonCollision * 0.7, posY - rayonCollision * 0.7}, // Haut-gauche
                {posX + rayonCollision * 0.7, posY - rayonCollision * 0.7}, // Haut-droite
                {posX - rayonCollision * 0.7, posY + rayonCollision * 0.7}, // Bas-gauche
                {posX + rayonCollision * 0.7, posY + rayonCollision * 0.7}  // Bas-droite
        };

        // Vérifier chaque point
        for (double[] point : pointsCollision) {
            int gridX = PacmanMap.pixelsVersGrille(point[0]);
            int gridY = PacmanMap.pixelsVersGrille(point[1]);

            // Vérifier si le point est dans un mur
            if (map.estMur(gridX, gridY)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Vérifie si une position future serait en collision (pour anticipation)
     */
    private boolean collisionDansDirection(Pacman.Direction direction) {
        if (direction == Pacman.Direction.IMMOBILE) {
            return false;
        }

        double testX = pacman.getPosX();
        double testY = pacman.getPosY();
        double distance = pacman.getVitesse() * 2; // Tester 2 frames à l'avance (réduit de 3 à 2)

        switch (direction) {
            case HAUT:
                testY -= distance;
                break;
            case BAS:
                testY += distance;
                break;
            case GAUCHE:
                testX -= distance;
                break;
            case DROITE:
                testX += distance;
                break;
        }

        return collisionAvecMur(testX, testY);
    }

    /**
     * Aligne automatiquement Pacman sur la grille lors d'un changement de direction
     * C'est LA solution au problème du "pixel perfect" dans Pac-Man
     */
    private void alignerSurGrille(Pacman.Direction nouvelleDirection) {
        double posX = pacman.getPosX();
        double posY = pacman.getPosY();

        // Calculer la position de la case actuelle (centre)
        int gridX = PacmanMap.pixelsVersGrille(posX);
        int gridY = PacmanMap.pixelsVersGrille(posY);

        double centreX = PacmanMap.grilleVersPixels(gridX);
        double centreY = PacmanMap.grilleVersPixels(gridY);

        // Tolérance d'alignement (en pixels)
        final double TOLERANCE = 8;

        // Aligner selon l'axe perpendiculaire au mouvement
        switch (nouvelleDirection) {
            case HAUT:
            case BAS:
                // Pour un mouvement vertical, aligner horizontalement
                if (Math.abs(posX - centreX) < TOLERANCE) {
                    pacman.setPosX(centreX);
                }
                break;

            case GAUCHE:
            case DROITE:
                // Pour un mouvement horizontal, aligner verticalement
                if (Math.abs(posY - centreY) < TOLERANCE) {
                    pacman.setPosY(centreY);
                }
                break;
        }
    }

    private void render() {
        // Effacer l'écran
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Dessiner la grille
        dessinerMap();

        // Dessiner Pacman
        dessinerPacman();

        // Si invincible, afficher un effet visuel
        if (pacman.isInvincible()) {
            gc.setFill(Color.rgb(255, 255, 0, 0.3));
            gc.fillOval(pacman.getPosX() - 25, pacman.getPosY() - 25, 70, 70);
        }

        // Si en pause
        if (jeuEnPause) {
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.YELLOW);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 48));
            gc.fillText("PAUSE", canvas.getWidth()/2 - 70, canvas.getHeight()/2);
        }

        // Si jeu terminé
        if (jeuTermine) {
            gc.setFill(Color.rgb(0, 0, 0, 0.8));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.RED);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 48));
            gc.fillText("GAME OVER", canvas.getWidth()/2 - 140, canvas.getHeight()/2 - 20);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
            gc.fillText("Score final: " + score, canvas.getWidth()/2 - 80, canvas.getHeight()/2 + 30);
        }
    }

    /**
     * Dessiner la map complète (murs, dots, power pellets)
     */
    private void dessinerMap() {
        int[][] grille = map.getGrille();

        for (int y = 0; y < PacmanMap.HAUTEUR; y++) {
            for (int x = 0; x < PacmanMap.LARGEUR; x++) {
                int cellule = grille[y][x];
                double pixelX = x * PacmanMap.TAILLE_CASE;
                double pixelY = y * PacmanMap.TAILLE_CASE;

                switch (cellule) {
                    case PacmanMap.MUR:
                        dessinerMur(pixelX, pixelY);
                        break;

                    case PacmanMap.DOT:
                        dessinerDot(pixelX, pixelY);
                        break;

                    case PacmanMap.POWER_PELLET:
                        dessinerPowerPellet(pixelX, pixelY);
                        break;

                    case PacmanMap.TUNNEL:
                        dessinerTunnel(pixelX, pixelY);
                        break;

                    case PacmanMap.MAISON_FANTOMES:
                        dessinerMaisonFantomes(pixelX, pixelY);
                        break;

                    case PacmanMap.VIDE:
                        // Rien à dessiner (chemin vide)
                        break;
                }
            }
        }
    }

    /**
     * Dessiner un mur (bloc bleu style Pac-Man classique)
     */
    private void dessinerMur(double x, double y) {
        // Mur bleu classique avec bordure
        gc.setFill(Color.rgb(33, 33, 222)); // Bleu Pac-Man
        gc.fillRect(x + 1, y + 1, PacmanMap.TAILLE_CASE - 2, PacmanMap.TAILLE_CASE - 2);

        // Bordure plus claire
        gc.setStroke(Color.rgb(66, 66, 255));
        gc.setLineWidth(1);
        gc.strokeRect(x + 1, y + 1, PacmanMap.TAILLE_CASE - 2, PacmanMap.TAILLE_CASE - 2);
    }

    /**
     * Dessiner un dot (petit point blanc)
     */
    private void dessinerDot(double x, double y) {
        gc.setFill(Color.rgb(255, 184, 174)); // Couleur beige/rose clair
        double centreX = x + PacmanMap.TAILLE_CASE / 2.0;
        double centreY = y + PacmanMap.TAILLE_CASE / 2.0;
        gc.fillOval(centreX - 2, centreY - 2, 4, 4);
    }

    /**
     * Dessiner un power pellet (gros point clignotant)
     */
    private void dessinerPowerPellet(double x, double y) {
        // Animation clignotante
        long time = System.currentTimeMillis();
        double alpha = 0.5 + 0.5 * Math.sin(time / 200.0);

        gc.setFill(Color.rgb(255, 184, 174, alpha));
        double centreX = x + PacmanMap.TAILLE_CASE / 2.0;
        double centreY = y + PacmanMap.TAILLE_CASE / 2.0;
        gc.fillOval(centreX - 5, centreY - 5, 10, 10);
    }

    /**
     * Dessiner un tunnel (zone sombre)
     */
    private void dessinerTunnel(double x, double y) {
        gc.setFill(Color.rgb(20, 20, 20));
        gc.fillRect(x, y, PacmanMap.TAILLE_CASE, PacmanMap.TAILLE_CASE);
    }

    /**
     * Dessiner la maison des fantômes (zone rose/rouge)
     */
    private void dessinerMaisonFantomes(double x, double y) {
        gc.setFill(Color.rgb(255, 0, 255, 0.3)); // Rose transparent
        gc.fillRect(x, y, PacmanMap.TAILLE_CASE, PacmanMap.TAILLE_CASE);

        // Grille
        gc.setStroke(Color.rgb(255, 100, 255));
        gc.setLineWidth(1);
        gc.strokeRect(x, y, PacmanMap.TAILLE_CASE, PacmanMap.TAILLE_CASE);
    }

    private void dessinerPacman() {
        // Couleur jaune pour Pacman
        if (pacman.isInvincible()) {
            gc.setFill(Color.ORANGE); // Orange quand invincible
        } else {
            gc.setFill(Color.YELLOW);
        }

        // Dessiner le corps (cercle)
        double x = pacman.getPosX();
        double y = pacman.getPosY();
        double taille = Pacman.TAILLE_PACMAN;

        // Animation de la bouche selon la direction
        double startAngle = 45;
        double angleArc = 270;

        switch (pacman.getDirection()) {
            case DROITE:
                startAngle = 45;
                break;
            case GAUCHE:
                startAngle = 225;
                break;
            case HAUT:
                startAngle = 135;
                break;
            case BAS:
                startAngle = 315;
                break;
        }

        // Dessiner Pacman avec une bouche ouverte
        gc.fillArc(x - taille, y - taille, taille * 2, taille * 2,
                startAngle, angleArc, javafx.scene.shape.ArcType.ROUND);

        // Ajouter un œil
        gc.setFill(Color.BLACK);
        double eyeX = x + (pacman.getDirection() == Pacman.Direction.GAUCHE ? -6 : 6);
        double eyeY = y - 6;
        gc.fillOval(eyeX - 2, eyeY - 2, 4, 4);
    }

    private void gererTouches(KeyCode code) {
        Pacman.Direction nouvelleDirection = null;

        switch (code) {
            case UP:
                nouvelleDirection = Pacman.Direction.HAUT;
                break;
            case DOWN:
                nouvelleDirection = Pacman.Direction.BAS;
                break;
            case LEFT:
                nouvelleDirection = Pacman.Direction.GAUCHE;
                break;
            case RIGHT:
                nouvelleDirection = Pacman.Direction.DROITE;
                break;
            case SPACE:
                jeuEnPause = !jeuEnPause;
                return;
            case ESCAPE:
                jeuEnPause = true;
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Menu");
                alert.setHeaderText("Que voulez-vous faire ?");

                ButtonType btnReprendre = new ButtonType("Reprendre");
                ButtonType btnSauver = new ButtonType("Sauvegarder");
                ButtonType btnQuitter = new ButtonType("Quitter");

                alert.getButtonTypes().setAll(btnReprendre, btnSauver, btnQuitter);

                alert.showAndWait().ifPresent(response -> {
                    if (response == btnReprendre) {
                        jeuEnPause = false;
                    } else if (response == btnSauver) {
                        sauvegarderPartie();
                        jeuEnPause = false;
                    } else if (response == btnQuitter) {
                        sauvegarderPartie();
                        retourDashboard((Stage) canvas.getScene().getWindow());
                    }
                });
                return;
            default:
                return;
        }

        // Si une nouvelle direction est demandée
        if (nouvelleDirection != null) {
            // Aligner Pacman sur la grille pour éviter le problème du "pixel perfect"
            alignerSurGrille(nouvelleDirection);

            // Vérifier si la nouvelle direction est possible
            if (!collisionDansDirection(nouvelleDirection)) {
                pacman.setDirection(nouvelleDirection);
            }
        }
    }

    private void sauvegarderPartie() {
        try (Connection conn = Database.getConnection()) {
            PartiesDAO partiesDAO = new PartiesDAO(conn);

            // Sauvegarder l'état actuel
            partieActuelle.setScorePartie(score);
            partieActuelle.setNiveauAtteint(niveau);
            partieActuelle.setStatut("PAUSE");

            // Créer un JSON avec l'état du jeu
            String etatJeu = String.format(
                    "{\"pacmanX\":%.2f,\"pacmanY\":%.2f,\"direction\":\"%s\",\"vies\":%d,\"score\":%d,\"niveau\":%d}",
                    pacman.getPosX(), pacman.getPosY(), pacman.getDirection(),
                    pacman.getVies(), score, niveau
            );
            partieActuelle.setEtatJeu(etatJeu);

            partiesDAO.sauvegarderPartie(partieActuelle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void terminerPartie() {
        gameLoop.stop();

        try (Connection conn = Database.getConnection()) {
            PartiesDAO partiesDAO = new PartiesDAO(conn);
            JoueursDAO joueursDAO = new JoueursDAO(conn);

            // Terminer la partie
            partiesDAO.terminerPartie(partieActuelle.getId(), score);

            // Mettre à jour le score total du joueur
            int nouveauScoreTotal = joueur.getScoreTotal() + score;
            joueursDAO.mettreAJourScore(joueur.getId(), nouveauScoreTotal);

            // Afficher un résumé
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Partie terminée");
            alert.setHeaderText("GAME OVER");
            alert.setContentText(
                    "Score final: " + score + "\n" +
                            "Niveau atteint: " + niveau + "\n" +
                            "Score total: " + nouveauScoreTotal
            );
            alert.showAndWait();

            retourDashboard((Stage) canvas.getScene().getWindow());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void retourDashboard(Stage stage) {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        if (compte.getRole().equals("ADMIN")) {
            new DashBoardAdmin().start(new Stage(), compte);
        } else {
            new DashBoardUser().start(new Stage(), compte);
        }
        stage.close();
    }

    public static void main(String[] args) {
        launch();
    }
}