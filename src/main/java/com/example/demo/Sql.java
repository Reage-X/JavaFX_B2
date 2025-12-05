package com.example.demo;
import java.util.ArrayList;
import java.sql.*;


public class Sql {
    public static Compte getCompte(String nom, String prenom, Connection conn) throws SQLException {
        String sqlEtu = "SELECT * FROM etudiants WHERE nom = ? AND prenom = ?";
        Compte etu = null;
    }
}

public static void main(String[] args) throws SQLException {
    String url = "jdbc:mysql://localhost:3306/javafx-b2?createDatabaseIfNotExist=true";
    String user = "root";
    String pass = "";
    //Compte compteTest = null
    try (Connection conn = DriverManager.getConnection(url, user, pass)) {
        System.out.println("Connexion à la BDD réussie.");
    }
}
