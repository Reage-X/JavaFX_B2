package com.example.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.Connection;

public class ScoresPage extends VBox {

    public ScoresPage(Connection conn) {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(40));
        setSpacing(30);

        // Titre
        Label titre = new Label("🏆 MES MEILLEURS SCORES");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titre.setTextFill(Color.web("#ffd700"));

        // Liste des scores
        VBox scoresList = new VBox(15);
        scoresList.setAlignment(Pos.CENTER);
        scoresList.setPadding(new Insets(20));

        if (Main.compte.getScore().isEmpty()) {
            Label noScore = new Label("Aucun score enregistré");
            noScore.setFont(Font.font("Arial", FontWeight.BOLD, 20));
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
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        getChildren().addAll(titre, scrollPane);
    }

    private Label createScoreLabel(int rank, int score) {
        String emoji = switch (rank) {
            case 1 -> "🥇";
            case 2 -> "🥈";
            case 3 -> "🥉";
            default -> "📊";
        };

        Label label = new Label(String.format("%s #%d - %d points", emoji, rank, score));
        label.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        String color = switch (rank) {
            case 1 -> "#ffd700";
            case 2 -> "#c0c0c0";
            case 3 -> "#cd7f32";
            default -> "#ffffff";
        };

        label.setTextFill(Color.web(color));
        label.setStyle(
                "-fx-background-color: #16213e;" +
                        "-fx-padding: 15;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );
        label.setPrefWidth(450);

        return label;
    }
}