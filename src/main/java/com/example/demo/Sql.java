package com.example.demo;
import java.sql.*;


public class Sql {
    public static Compte getCompte(String pseudo, String MDP, Connection conn) throws SQLException {
        String sqlCmp = "SELECT * FROM comptes WHERE pseudo = ? AND MDP = ?";
        Compte cmp = null;

        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            psCmp.setString(1, pseudo);
            psCmp.setString(2, MDP);

            try (ResultSet rsCmp = psCmp.executeQuery()) {
                if (rsCmp.next()) {
                    cmp = new Compte(rsCmp.getString("pseudo"), rsCmp.getString("MDP"));
                    Array sqlScores = rsCmp.getArray("score");
                    if (sqlScores != null) {
                        Object[] scores = (Object[]) sqlScores.getArray();
                        for (Object score : scores) {
                            if (score instanceof Number) {
                                cmp.addScore(((Number) score).intValue());
                            }
                        }
                    }
                } else {
                    return null;
                }
            }
        }
        return cmp;
    }

    public static void addCompte(Compte compte, Connection conn) throws SQLException {
        String sqlCmp = "INSERT INTO comptes (pseudo, MDP, score) VALUES (?, ?, ?)";
        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp, Statement.RETURN_GENERATED_KEYS)){
            psCmp.setString(1, compte.getUser_name());
            psCmp.setString(2, compte.getMDP());
            Object[] scoresArray = compte.getScore() != null ? compte.getScore().toArray() : new Object[0];
            Array sqlScores = conn.createArrayOf("INTEGER", scoresArray);
            psCmp.setArray(3, sqlScores);
            psCmp.executeUpdate();
        }
    }

    public static void updateScore(Compte compte, Connection conn) throws SQLException {
        String sqlCmp = "UPDATE comptes SET score = ? WHERE pseudo = ? AND MDP = ?";
        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            Object[] scoresArray = compte.getScore() != null ? compte.getScore().toArray() : new Object[0];
            Array sqlScores = conn.createArrayOf("INTEGER", scoresArray);
            psCmp.setArray(1, sqlScores);
            psCmp.setString(2, compte.getUser_name());
            psCmp.setString(3, compte.getMDP());
            psCmp.executeUpdate();
        }
    }

    public static void updateUserName(Compte compte, String oldUserName, Connection conn) throws SQLException {
        String sqlCmp = "UPDATE comptes SET pseudo = ? WHERE pseudo = ? AND MDP = ?";
        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            psCmp.setString(1, compte.getUser_name());
            psCmp.setString(2, oldUserName);
            psCmp.setString(3, compte.getMDP());
            psCmp.executeUpdate();
        }
    }

    public static void updateMDP(Compte compte, String oldMDP, Connection conn) throws SQLException {
        String sqlCmp = "UPDATE comptes SET MDP = ? WHERE pseudo = ? AND MDP = ?";
        try (PreparedStatement psCmp = conn.prepareStatement(sqlCmp)) {
            psCmp.setString(1, compte.getMDP());
            psCmp.setString(2, compte.getUser_name());
            psCmp.setString(3, oldMDP);
            psCmp.executeUpdate();
        }
    }
}