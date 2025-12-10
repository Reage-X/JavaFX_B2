package com.example.demo;

import com.example.demo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Sql {

    // Récupération
    public static Compte getCompte(String pseudo, String mdpDonne, Connection conn) throws SQLException {
        // 1. On ne cherche QUE par pseudo pour la sécurité
        String sqlCmp = "SELECT * FROM comptes WHERE pseudo = ?";
        Compte cmp = null;

        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            psCmp.setString(1, pseudo);

            try (ResultSet rsCmp = psCmp.executeQuery()) {
                if (rsCmp.next()) {
                    // 2. Vérification basique du MDP (En production, utilise un Hash !)
                    String mdpStocke = rsCmp.getString("MDP");
                    if (!mdpStocke.equals(mdpDonne)) {
                        return null; // Mauvais mot de passe
                    }

                    cmp = new Compte(rsCmp.getString("pseudo"), mdpStocke);

                    // 3. Lecture du score stocké en TEXT/JSON (ex: "[10, 200]")
                    String jsonScores = rsCmp.getString("score");
                    if (jsonScores != null && !jsonScores.isEmpty() && !jsonScores.equals("[]")) {
                        // On retire les crochets [ ] et on coupe aux virgules
                        String clean = jsonScores.replace("[", "").replace("]", "");
                        String[] parts = clean.split(",");

                        for (String part : parts) {
                            try {
                                cmp.addScore(Integer.parseInt(part.trim()));
                            } catch (NumberFormatException e) {
                                // Ignorer les erreurs de parsing
                            }
                        }
                    }
                }
            }
        }
        return cmp;
    }

    // Ajout
    public static boolean addCompte(Compte compte, Connection conn) throws SQLException {
        // Note: La colonne 'score' dans la BDD doit être de type TEXT ou JSON
        String sqlCmp = "INSERT INTO comptes (pseudo, MDP, score) VALUES (?, ?, ?)";

        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            psCmp.setString(1, compte.getUser_name());
            psCmp.setString(2, compte.getMDP());

            // 4. Conversion de l'ArrayList en String format "[1, 2, 3]"
            String scoreString = compte.getScore().toString();

            psCmp.setString(3, scoreString); // On envoie une String, pas un Array SQL

            return psCmp.executeUpdate() > 0;
        }
    }

    // Mise à jour
    public static void updateScore(Compte compte, Connection conn) throws SQLException {
        String sqlCmp = "UPDATE comptes SET score = ? WHERE pseudo = ?";

        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            // Conversion ArrayList -> String
            String scoreString = compte.getScore().toString();

            psCmp.setString(1, scoreString);
            psCmp.setString(2, compte.getUser_name());
        }
    }

    public static boolean PseudoExiste(String pseudo, Connection conn) throws SQLException {
        String sqlCmp = "SELECT pseudo FROM comptes WHERE pseudo = ?";
        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            psCmp.setString(1, pseudo);
            try (ResultSet rsCmp = psCmp.executeQuery()) {
                return rsCmp.next();
            }
        }
    }

    public static boolean updateUserName(Compte compte, String oldUserName, Connection conn) throws SQLException {
        if (!oldUserName.equals(compte.getUser_name()) && PseudoExiste(compte.getUser_name(), conn)) {
            throw new SQLException("Username already exists.");
        }

        String query = "UPDATE comptes SET pseudo = ? WHERE pseudo = ? AND MDP = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, compte.getUser_name());
            statement.setString(2, oldUserName);
            statement.setString(3, compte.getMDP());
            return statement.executeUpdate() > 0;
        }
    }

    public static boolean updateMDP(Compte compte, String oldMDP, Connection conn) throws SQLException {
        String sqlCmp = "UPDATE comptes SET MDP = ? WHERE pseudo = ? AND MDP = ?";
        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            psCmp.setString(1, compte.getMDP());
            psCmp.setString(2, compte.getUser_name());
            psCmp.setString(3, oldMDP);
            return psCmp.executeUpdate() > 0;
        }
    }

    public static boolean deleteCompte(Compte compte, Connection conn) throws SQLException {
        String sqlCmp = "DELETE FROM comptes WHERE pseudo = ? AND MDP = ?";
        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            psCmp.setString(1, compte.getUser_name());
            psCmp.setString(2, compte.getMDP());
            return psCmp.executeUpdate() > 0;
        }
    }
}