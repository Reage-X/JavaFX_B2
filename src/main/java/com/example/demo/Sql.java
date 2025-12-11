package com.example.demo;

import com.example.demo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Sql {

    // Récupération
    public static Compte getCompte(String pseudo, String mdpDonne, Connection conn) throws SQLException {
        String sqlCmp = "SELECT * FROM comptes WHERE pseudo = ?";
        Compte cmp = null;

        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            psCmp.setString(1, pseudo);

            try (ResultSet rsCmp = psCmp.executeQuery()) {
                if (rsCmp.next()) {
                    // Vérification du mot de passe hashé
                    String mdpStocke = rsCmp.getString("MDP");
                    if (!PasswordUtil.verifyPassword(mdpDonne, mdpStocke)) {
                        return null;
                    }

                    // On stocke le hash dans l'objet Compte (pas le mot de passe en clair)
                    cmp = new Compte(rsCmp.getString("pseudo"), mdpStocke);

                    // Lecture du score
                    String jsonScores = rsCmp.getString("score");
                    if (jsonScores != null && !jsonScores.isEmpty() && !jsonScores.equals("[]")) {
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
        String sqlCmp = "INSERT INTO comptes (pseudo, MDP, score) VALUES (?, ?, ?)";

        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            psCmp.setString(1, compte.getUser_name());
            
            // Hash le mot de passe avant de le stocker
            String hashedPassword = PasswordUtil.hashPassword(compte.getMDP());
            psCmp.setString(2, hashedPassword);

            String scoreString = compte.getScore().toString();
            psCmp.setString(3, scoreString);

            boolean result = psCmp.executeUpdate() > 0;
            
            // Mettre à jour le compte avec le hash
            if (result) {
                compte.setMDP(hashedPassword);
            }
            
            return result;
        }
    }

    // Mise à jour
    public static void updateScore(Compte compte, Connection conn) throws SQLException {
        String sqlCmp = "UPDATE comptes SET score = ? WHERE pseudo = ?";

        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            String scoreString = compte.getScore().toString();
            psCmp.setString(1, scoreString);
            psCmp.setString(2, compte.getUser_name());
            psCmp.executeUpdate();
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
            statement.setString(3, compte.getMDP()); // Le MDP est déjà hashé dans l'objet
            return statement.executeUpdate() > 0;
        }
    }

    public static boolean updateMDP(Compte compte, String oldMDP, Connection conn) throws SQLException {
        String sqlCmp = "UPDATE comptes SET MDP = ? WHERE pseudo = ? AND MDP = ?";
        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            // Hash le nouveau mot de passe
            String newHashedPassword = PasswordUtil.hashPassword(compte.getMDP());
            psCmp.setString(1, newHashedPassword);
            psCmp.setString(2, compte.getUser_name());
            psCmp.setString(3, oldMDP); // oldMDP est déjà le hash stocké
            
            boolean result = psCmp.executeUpdate() > 0;
            
            // Mettre à jour le compte avec le nouveau hash
            if (result) {
                compte.setMDP(newHashedPassword);
            }
            
            return result;
        }
    }

    public static boolean deleteCompte(Compte compte, Connection conn) throws SQLException {
        String sqlCmp = "DELETE FROM comptes WHERE pseudo = ? AND MDP = ?";
        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            psCmp.setString(1, compte.getUser_name());
            psCmp.setString(2, compte.getMDP()); // Le MDP est déjà hashé
            return psCmp.executeUpdate() > 0;
        }
    }
}