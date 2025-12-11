package com.example.demo;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.example.demo.Main.compte;
import static com.example.demo.Main.jeuEnCours;
import static com.example.demo.Sql.updateScore;

public class GameScene {

    private static final int TILE_SIZE = 32;
    private static final Color BG_COLOR = Color.rgb(0, 0, 0);
    private static final Color WALL_COLOR = Color.rgb(33, 33, 222);
    private static final Color ORB_COLOR = Color.rgb(255, 184, 174);

    private Canvas canvas;
    private GraphicsContext gc;
    private com.example.demo.Map gameMap;
    private Stage stage;
    private Connection conn;

    private Text scoreText;
    private Text livesText;
    private Text messageText;

    private AnimationTimer gameLoop;
    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 200_000_000; // 200ms entre chaque update

    // Images des sprites
    private Map<String, Image> sprites = new HashMap<>();

    public GameScene(Stage stage, int niveau, Connection conn) {
        this.stage = stage;
        this.conn = conn;

        // Initialiser la map selon le niveau
        this.gameMap = new com.example.demo.Map();
        if (niveau == 1) {
            this.gameMap = gameMap.niveau1();
        } else {
            this.gameMap = gameMap.niveau2();
        }

        loadSprites();
        initializeUI();
    }

    private void loadSprites() {
        try {
            // Charger les sprites depuis le dossier resources
            sprites.put("pacman_up", new Image(getClass().getResourceAsStream("/images/pacman_up.png")));
            sprites.put("pacman_down", new Image(getClass().getResourceAsStream("/images/pacman_down.png")));
            sprites.put("pacman_left", new Image(getClass().getResourceAsStream("/images/pacman_left.png")));
            sprites.put("pacman_right", new Image(getClass().getResourceAsStream("/images/pacman_right.png")));

            sprites.put("ghost_red", new Image(getClass().getResourceAsStream("/images/ghost_red.png")));
            sprites.put("ghost_blue", new Image(getClass().getResourceAsStream("/images/ghost_blue.png")));
            sprites.put("ghost_pink", new Image(getClass().getResourceAsStream("/images/ghost_pink.png")));
            sprites.put("ghost_yellow", new Image(getClass().getResourceAsStream("/images/ghost_yellow.png")));

        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des sprites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: black;");

        // === TOP : Score et vies ===
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // === CENTER : Canvas de jeu ===
        int canvasWidth = gameMap.getLongueur() * TILE_SIZE;
        int canvasHeight = gameMap.getHauteur() * TILE_SIZE;
        canvas = new Canvas(canvasWidth, canvasHeight);
        gc = canvas.getGraphicsContext2D();

        VBox centerBox = new VBox(canvas);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setStyle("-fx-background-color: black;");
        root.setCenter(centerBox);

        // === BOTTOM : Message ===
        messageText = new Text("");
        messageText.setFont(Font.font("Courier New", FontWeight.BOLD, 24));
        messageText.setFill(Color.YELLOW);

        VBox bottomBox = new VBox(messageText);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-background-color: black; -fx-padding: 10;");
        root.setBottom(bottomBox);

        // Créer la scène
        Scene scene = new Scene(root, canvasWidth + 40, canvasHeight + 120);
        scene.setFill(Color.BLACK);

        // Gestion des touches
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));

        stage.setScene(scene);
        stage.setTitle("PACMAN - Niveau " + (gameMap.getMaxPoint() == 196 ? "1" : "2"));

        // Démarrer le jeu
        startGame();
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(50);
        topBar.setAlignment(Pos.CENTER);
        topBar.setStyle("-fx-background-color: black; -fx-padding: 15;");

        // Score
        scoreText = new Text("SCORE: 0");
        scoreText.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        scoreText.setFill(Color.WHITE);

        // Vies
        livesText = new Text("VIES: ❤ ❤ ❤");
        livesText.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        livesText.setFill(Color.RED);

        topBar.getChildren().addAll(scoreText, livesText);
        return topBar;
    }

    private void handleKeyPress(KeyCode code) {
        if (!jeuEnCours) return;

        Joueur joueur = compte.getJoueur();

        switch (code) {
            case UP, Z:
                joueur.setDirection(0);
                break;
            case DOWN, S:
                joueur.setDirection(2);
                break;
            case LEFT, Q:
                joueur.setDirection(3);
                break;
            case RIGHT, D:
                joueur.setDirection(1);
                break;
            case ESCAPE:
                pauseGame();
                return;
            default:
                return;
        }

        joueur.updateDx_Dy();
    }

    private void startGame() {
        Main.jeuEnCours = true;

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= UPDATE_INTERVAL) {
                    update();
                    render();
                    lastUpdate = now;
                }
            }
        };

        gameLoop.start();
    }

    private void update() {
        if (!jeuEnCours) return;

        Joueur joueur = compte.getJoueur();

        // Vérifier si le joueur a encore des vies
        if (joueur.getNb_vie() <= 0) {
            endGame(false);
            return;
        }

        // Déplacer le joueur
        joueur.updateMouvement(gameMap);

        // Déplacer les ennemis
        for (Ennemi ennemi : gameMap.ennemies) {
            ennemi.updateMouvement(gameMap);
        }

        // Vérifier victoire
        if (joueur.getScore_jeu() >= gameMap.getMaxPoint()) {
            endGame(true);
            return;
        }

        // Mettre à jour l'affichage des stats
        updateStats();
    }

    private void render() {
        // Fond noir
        gc.setFill(BG_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Dessiner la grille
        for (int y = 0; y < gameMap.getHauteur(); y++) {
            for (int x = 0; x < gameMap.getLongueur(); x++) {
                char cell = gameMap.getCharAt(x, y);

                switch (cell) {
                    case com.example.demo.Map.WALL:
                        drawWall(x, y);
                        break;
                    case com.example.demo.Map.ORB:
                        drawOrb(x, y);
                        break;
                    case com.example.demo.Map.PLAYER:
                        drawPlayer(x, y);
                        break;
                    case com.example.demo.Map.GHOST_Red:
                        drawGhost(x, y, "red");
                        break;
                    case com.example.demo.Map.GHOST_Blue:
                        drawGhost(x, y, "blue");
                        break;
                    case com.example.demo.Map.GHOST_Pink:
                        drawGhost(x, y, "pink");
                        break;
                    case com.example.demo.Map.GHOST_Yellow:
                        drawGhost(x, y, "yellow");
                        break;
                }
            }
        }
    }

    private void drawWall(int gridX, int gridY) {
        gc.setFill(WALL_COLOR);
        gc.fillRoundRect(
                gridX * TILE_SIZE + 2,
                gridY * TILE_SIZE + 2,
                TILE_SIZE - 4,
                TILE_SIZE - 4,
                5, 5
        );

        // Bordure plus claire
        gc.setStroke(Color.rgb(66, 66, 255));
        gc.setLineWidth(1);
        gc.strokeRoundRect(
                gridX * TILE_SIZE + 2,
                gridY * TILE_SIZE + 2,
                TILE_SIZE - 4,
                TILE_SIZE - 4,
                5, 5
        );
    }

    private void drawOrb(int gridX, int gridY) {
        gc.setFill(ORB_COLOR);
        double centerX = gridX * TILE_SIZE + TILE_SIZE / 2.0;
        double centerY = gridY * TILE_SIZE + TILE_SIZE / 2.0;
        gc.fillOval(centerX - 3, centerY - 3, 6, 6);
    }

    private void drawPlayer(int gridX, int gridY) {
        Joueur joueur = compte.getJoueur();
        String spriteKey = switch (joueur.getDirection()) {
            case 0 -> "pacman_up";
            case 1 -> "pacman_right";
            case 2 -> "pacman_down";
            case 3 -> "pacman_left";
            default -> "pacman_right";
        };

        Image sprite = sprites.get(spriteKey);
        if (sprite != null) {
            gc.drawImage(sprite, gridX * TILE_SIZE, gridY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        } else {
            // Fallback : cercle jaune
            gc.setFill(Color.YELLOW);
            gc.fillOval(
                    gridX * TILE_SIZE + 2,
                    gridY * TILE_SIZE + 2,
                    TILE_SIZE - 4,
                    TILE_SIZE - 4
            );
        }
    }

    private void drawGhost(int gridX, int gridY, String color) {
        Image sprite = sprites.get("ghost_" + color);
        if (sprite != null) {
            gc.drawImage(sprite, gridX * TILE_SIZE, gridY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        } else {
            // Fallback : carré coloré
            Color ghostColor = switch (color) {
                case "red" -> Color.RED;
                case "blue" -> Color.CYAN;
                case "pink" -> Color.PINK;
                case "yellow" -> Color.YELLOW;
                default -> Color.WHITE;
            };
            gc.setFill(ghostColor);
            gc.fillRect(
                    gridX * TILE_SIZE + 4,
                    gridY * TILE_SIZE + 4,
                    TILE_SIZE - 8,
                    TILE_SIZE - 8
            );
        }
    }

    private void updateStats() {
        Joueur joueur = compte.getJoueur();

        // Score
        scoreText.setText("SCORE: " + joueur.getScore_jeu());

        // Vies
        String hearts = "❤ ".repeat(Math.max(0, joueur.getNb_vie()));
        livesText.setText("VIES: " + hearts.trim());
    }

    private void pauseGame() {
        jeuEnCours = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }

        messageText.setText("PAUSE - Appuyez sur ESPACE pour reprendre");

        stage.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                messageText.setText("");
                jeuEnCours = true;
                gameLoop.start();
                stage.getScene().setOnKeyPressed(e -> handleKeyPress(e.getCode()));
            } else if (event.getCode() == KeyCode.ESCAPE) {
                returnToMenu();
            }
        });
    }

    private void endGame(boolean victory) {
        jeuEnCours = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Sauvegarder le score
        compte.addScore();
        try {
            updateScore(compte, conn);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la sauvegarde du score: " + e.getMessage());
        }

        // Afficher message
        if (victory) {
            messageText.setText("🎉 VICTOIRE ! Score: " + compte.getScore(0) + " - ESPACE pour rejouer");
            messageText.setFill(Color.LIME);
        } else {
            messageText.setText("💀 GAME OVER ! Score: " + compte.getScore(0) + " - ESPACE pour rejouer");
            messageText.setFill(Color.RED);
        }

        // Gestion des touches fin de jeu
        stage.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                // Rejouer
                new ChoixNiveau(stage, conn);
            } else if (event.getCode() == KeyCode.ESCAPE) {
                returnToMenu();
            }
        });
    }

    private void returnToMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        // Retour au MenuCompte
        new MenuCompte(stage, conn);
    }
}