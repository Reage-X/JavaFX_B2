package com.example.demo;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.example.demo.Main.compte;
import static com.example.demo.Main.jeuEnCours;
import static com.example.demo.Sql.updateScore;

public class GameScene {

    private static final int TILE_SIZE = 32;
    private static final Color BG_COLOR = Color.rgb(26, 26, 46); // #1a1a2e

    private Canvas canvas;
    private GraphicsContext gc;
    private com.example.demo.Map gameMap;
    private Stage stage;
    private Connection conn;

    private Text scoreText;
    private Text livesText;
    private VBox endGameOverlay;

    private AnimationTimer gameLoop;
    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 200_000_000; // 200ms

    // Gestion des touches maintenues
    private Set<KeyCode> pressedKeys = new HashSet<>();

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
            // Charger les sprites depuis le dossier resources/image
            sprites.put("pacman_up", new Image(getClass().getResourceAsStream("/image/pacmanUp.png")));
            sprites.put("pacman_down", new Image(getClass().getResourceAsStream("/image/pacmanDown.png")));
            sprites.put("pacman_left", new Image(getClass().getResourceAsStream("/image/pacmanLeft.png")));
            sprites.put("pacman_right", new Image(getClass().getResourceAsStream("/image/pacmanRight.png")));

            sprites.put("ghost_red", new Image(getClass().getResourceAsStream("/image/redGhost.png")));
            sprites.put("ghost_pink", new Image(getClass().getResourceAsStream("/image/pinkGhost.png")));
            sprites.put("ghost_orange", new Image(getClass().getResourceAsStream("/image/orangeGhost.png")));
            sprites.put("ghost_blue", new Image(getClass().getResourceAsStream("/image/orangeGhost.png"))); // Utiliser orange pour blue si pas de cyan

            sprites.put("wall", new Image(getClass().getResourceAsStream("/image/wall.png")));
            sprites.put("orb", new Image(getClass().getResourceAsStream("/image/powerFood.png")));

        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des sprites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        // === TOP : Header avec score et vies ===
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // === CENTER : Canvas de jeu centré ===
        int canvasWidth = gameMap.getLongueur() * TILE_SIZE;
        int canvasHeight = gameMap.getHauteur() * TILE_SIZE;
        canvas = new Canvas(canvasWidth, canvasHeight);
        gc = canvas.getGraphicsContext2D();

        // StackPane pour superposer le canvas et l'overlay de fin de jeu
        StackPane gameArea = new StackPane();
        gameArea.setStyle("-fx-background-color: #1a1a2e;");
        gameArea.setAlignment(Pos.CENTER);
        gameArea.getChildren().add(canvas);

        // Overlay de fin de jeu (caché par défaut)
        endGameOverlay = createEndGameOverlay();
        endGameOverlay.setVisible(false);
        gameArea.getChildren().add(endGameOverlay);

        root.setCenter(gameArea);

        // Créer ou mettre à jour la scène
        if (stage.getScene() != null) {
            stage.getScene().setRoot(root);
            // Gestion des touches pressées et relâchées
            stage.getScene().setOnKeyPressed(event -> {
                pressedKeys.add(event.getCode());
                handleKeyPress(event.getCode());
            });
            stage.getScene().setOnKeyReleased(event -> {
                pressedKeys.remove(event.getCode());
                // Si aucune touche directionnelle n'est pressée, arrêter le joueur
                if (!pressedKeys.contains(KeyCode.UP) && !pressedKeys.contains(KeyCode.Z) &&
                        !pressedKeys.contains(KeyCode.DOWN) && !pressedKeys.contains(KeyCode.S) &&
                        !pressedKeys.contains(KeyCode.LEFT) && !pressedKeys.contains(KeyCode.Q) &&
                        !pressedKeys.contains(KeyCode.RIGHT) && !pressedKeys.contains(KeyCode.D)) {
                    stopPlayer();
                }
            });
        } else {
            Scene scene = new Scene(root, 1200, 800);
            scene.setOnKeyPressed(event -> {
                pressedKeys.add(event.getCode());
                handleKeyPress(event.getCode());
            });
            scene.setOnKeyReleased(event -> {
                pressedKeys.remove(event.getCode());
                // Si aucune touche directionnelle n'est pressée, arrêter le joueur
                if (!pressedKeys.contains(KeyCode.UP) && !pressedKeys.contains(KeyCode.Z) &&
                        !pressedKeys.contains(KeyCode.DOWN) && !pressedKeys.contains(KeyCode.S) &&
                        !pressedKeys.contains(KeyCode.LEFT) && !pressedKeys.contains(KeyCode.Q) &&
                        !pressedKeys.contains(KeyCode.RIGHT) && !pressedKeys.contains(KeyCode.D)) {
                    stopPlayer();
                }
            });
            stage.setScene(scene);
            stage.setTitle("Pacman Game");
            stage.setMaximized(true);
            stage.show();
        }

        // Démarrer le jeu
        startGame();
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(50);
        topBar.setAlignment(Pos.CENTER);
        topBar.setStyle("-fx-background-color: #16213e; -fx-padding: 20;");

        // Score
        scoreText = new Text("SCORE: 0");
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreText.setFill(Color.web("#ffd700"));

        // Vies
        livesText = new Text("VIES: ❤ ❤ ❤");
        livesText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        livesText.setFill(Color.RED);

        topBar.getChildren().addAll(scoreText, livesText);
        return topBar;
    }

    private VBox createEndGameOverlay() {
        VBox overlay = new VBox(30);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(50));
        overlay.setStyle("-fx-background-color: rgba(26, 26, 46, 0.95); -fx-border-color: #ffd700; -fx-border-width: 5; -fx-border-radius: 15; -fx-background-radius: 15;");
        overlay.setPrefSize(600, 400);
        return overlay;
    }

    private void handleKeyPress(KeyCode code) {
        if (!jeuEnCours) {
            // Si le jeu est terminé, Entrée revient au menu
            if (code == KeyCode.ENTER) {
                returnToMenu();
            }
            return;
        }

        Joueur joueur = compte.getJoueur();

        switch (code) {
            case UP, Z:
                joueur.setDirection(0);
                joueur.updateDx_Dy();
                break;
            case DOWN, S:
                joueur.setDirection(2);
                joueur.updateDx_Dy();
                break;
            case LEFT, Q:
                joueur.setDirection(3);
                joueur.updateDx_Dy();
                break;
            case RIGHT, D:
                joueur.setDirection(1);
                joueur.updateDx_Dy();
                break;
            case ESCAPE:
                pauseGame();
                return;
        }
    }

    private void stopPlayer() {
        if (jeuEnCours) {
            Joueur joueur = compte.getJoueur();
            joueur.setDx(0);
            joueur.setDy(0);
        }
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

        // Déplacer le joueur seulement s'il a une direction active
        if (joueur.getDx() != 0 || joueur.getDy() != 0) {
            joueur.updateMouvement(gameMap);
        }

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
        // Fond
        gc.setFill(BG_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Dessiner la grille
        for (int y = 0; y < gameMap.getHauteur(); y++) {
            for (int x = 0; x < gameMap.getLongueur(); x++) {
                char cell = gameMap.getCharAt(x, y);

                switch (cell) {
                    case com.example.demo.Map.WALL:
                        drawSprite(x, y, "wall");
                        break;
                    case com.example.demo.Map.ORB:
                        drawOrbSmall(x, y);
                        break;
                    case com.example.demo.Map.PLAYER:
                        drawPlayer(x, y);
                        break;
                    case com.example.demo.Map.GHOST_Red:
                        drawSprite(x, y, "ghost_red");
                        break;
                    case com.example.demo.Map.GHOST_Blue:
                        drawSprite(x, y, "ghost_blue");
                        break;
                    case com.example.demo.Map.GHOST_Pink:
                        drawSprite(x, y, "ghost_pink");
                        break;
                    case com.example.demo.Map.GHOST_Yellow:
                        drawSprite(x, y, "ghost_orange");
                        break;
                }
            }
        }
    }

    private void drawSprite(int gridX, int gridY, String spriteName) {
        Image sprite = sprites.get(spriteName);
        if (sprite != null) {
            gc.drawImage(sprite, gridX * TILE_SIZE, gridY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        } else {
            // Fallback si l'image n'est pas chargée
            gc.setFill(Color.GRAY);
            gc.fillRect(gridX * TILE_SIZE, gridY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    private void drawOrbSmall(int gridX, int gridY) {
        Image sprite = sprites.get("orb");
        if (sprite != null) {
            // Réduire la taille par 4 (32 / 4 = 8)
            int smallSize = TILE_SIZE / 4;
            int offset = (TILE_SIZE - smallSize) / 2; // Centrer
            gc.drawImage(sprite,
                    gridX * TILE_SIZE + offset,
                    gridY * TILE_SIZE + offset,
                    smallSize,
                    smallSize);
        } else {
            // Fallback : petit cercle
            gc.setFill(Color.web("#ffd700"));
            int smallSize = TILE_SIZE / 4;
            int offset = (TILE_SIZE - smallSize) / 2;
            gc.fillOval(
                    gridX * TILE_SIZE + offset,
                    gridY * TILE_SIZE + offset,
                    smallSize,
                    smallSize);
        }
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

        drawSprite(gridX, gridY, spriteKey);
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

        // Arrêter le joueur
        stopPlayer();

        // Afficher overlay de pause
        Text pauseText = new Text("PAUSE");
        pauseText.setFont(Font.font("Arial", FontWeight.BOLD, 64));
        pauseText.setFill(Color.web("#ffd700"));

        Text instruction = new Text("Appuyez sur ESPACE pour reprendre\nESC pour revenir au menu");
        instruction.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        instruction.setFill(Color.WHITE);
        instruction.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        endGameOverlay.getChildren().clear();
        endGameOverlay.getChildren().addAll(pauseText, instruction);
        endGameOverlay.setVisible(true);

        stage.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                endGameOverlay.setVisible(false);
                jeuEnCours = true;
                pressedKeys.clear(); // Réinitialiser les touches
                gameLoop.start();
                // Remettre les handlers normaux
                stage.getScene().setOnKeyPressed(e -> {
                    pressedKeys.add(e.getCode());
                    handleKeyPress(e.getCode());
                });
                stage.getScene().setOnKeyReleased(e -> {
                    pressedKeys.remove(e.getCode());
                    if (!pressedKeys.contains(KeyCode.UP) && !pressedKeys.contains(KeyCode.Z) &&
                            !pressedKeys.contains(KeyCode.DOWN) && !pressedKeys.contains(KeyCode.S) &&
                            !pressedKeys.contains(KeyCode.LEFT) && !pressedKeys.contains(KeyCode.Q) &&
                            !pressedKeys.contains(KeyCode.RIGHT) && !pressedKeys.contains(KeyCode.D)) {
                        stopPlayer();
                    }
                });
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

        // Arrêter le joueur
        stopPlayer();

        // Sauvegarder le score
        compte.addScore();
        try {
            updateScore(compte, conn);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la sauvegarde du score: " + e.getMessage());
        }

        // Afficher l'overlay de fin de jeu
        endGameOverlay.getChildren().clear();

        if (victory) {
            // VICTORY
            Text victoryText = new Text("🎉 VICTORY 🎉");
            victoryText.setFont(Font.font("Arial", FontWeight.BOLD, 64));
            victoryText.setFill(Color.LIME);

            Text scoreResult = new Text("Score Final: " + compte.getScore(0) + " points");
            scoreResult.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            scoreResult.setFill(Color.web("#ffd700"));

            Text instruction = new Text("Appuyez sur ENTRÉE ou ESPACE pour revenir au menu");
            instruction.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
            instruction.setFill(Color.WHITE);

            endGameOverlay.getChildren().addAll(victoryText, scoreResult, instruction);
        } else {
            // GAME OVER
            Text gameOverText = new Text("💀 GAME OVER 💀");
            gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 64));
            gameOverText.setFill(Color.RED);

            Text scoreResult = new Text("Score Final: " + compte.getScore(0) + " points");
            scoreResult.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            scoreResult.setFill(Color.web("#ffd700"));

            Text instruction = new Text("Appuyez sur ENTRÉE ou ESPACE pour revenir au menu");
            instruction.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
            instruction.setFill(Color.WHITE);

            endGameOverlay.getChildren().addAll(gameOverText, scoreResult, instruction);
        }

        endGameOverlay.setVisible(true);

        // Gestion des touches Entrée et Espace pour revenir au menu
        stage.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                returnToMenu();
            }
        });
    }

    private void returnToMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        Main.jeuEnCours = true; // Réinitialiser pour la prochaine partie
        pressedKeys.clear(); // Nettoyer les touches
        new MenuCompte(stage, conn);
    }
}