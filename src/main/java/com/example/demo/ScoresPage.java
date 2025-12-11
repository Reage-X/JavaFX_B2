
package com.example.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.sql.Connection;

public class ScoresPage extends VBox {

    public ScoresPage(Connection conn) {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(40));
        setStyle("-fx-background-color: black;");
        setSpacing(30);

        // Titre
        Text title = new Text("MES MEILLEURS SCORES");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 40));
        title.setFill(Color.YELLOW);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.YELLOW);
        glow.setRadius(20);
        glow.setSpread(0.7);
        title.setEffect(glow);

        // Liste des scores
        VBox scoresList = new VBox(15);
        scoresList.setAlignment(Pos.CENTER);
        scoresList.setPadding(new Insets(20));

        if (Main.compte.getScore().isEmpty()) {
            Label noScore = new Label("Aucun score enregistré");
            noScore.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
            noScore.setTextFill(Color.LIGHTGRAY);
            scoresList.getChildren().add(noScore);
        } else {
            int rank = 1;
            for (Integer score : Main.compte.getScore()) {
                Label scoreLabel = createScoreLabel(rank, score);
                scoresList.getChildren().add(scoreLabel);
                rank++;
            }
        }

        // ScrollPane pour les scores
        ScrollPane scrollPane = new ScrollPane(scoresList);
        scrollPane.setStyle("-fx-background: black; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        getChildren().addAll(title, scrollPane);
    }

    private Label createScoreLabel(int rank, int score) {
        String emoji = switch (rank) {
            case 1 -> "🥇";
            case 2 -> "🥈";
            case 3 -> "🥉";
            default -> "📊";
        };

        Label label = new Label(String.format("%s #%d - %d points", emoji, rank, score));
        label.setFont(Font.font("Courier New", FontWeight.BOLD, 24));
        
        Color color = switch (rank) {
            case 1 -> Color.GOLD;
            case 2 -> Color.SILVER;
            case 3 -> Color.rgb(205, 127, 50); // Bronze
            default -> Color.LIGHTGRAY;
        };
        
        label.setTextFill(color);
        label.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.05);" +
                        "-fx-padding: 15;" +
                        "-fx-border-color: " + toRgbString(color) + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );
        label.setPrefWidth(450);

        return label;
    }

    private String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255)
        );
    }
}
