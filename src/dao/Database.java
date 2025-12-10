package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de gestion de la connexion à la base de données MySQL
 * Utilise le pattern Singleton pour gérer les connexions
 */
public class Database {

    // Configuration de la connexion - À MODIFIER selon votre environnement
    private static final String URL = "jdbc:mysql://localhost:3306/pacman?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // VIDE par défaut avec XAMPP

    /**
     * Méthode pour obtenir une connexion à la base de données
     * @return Connection active vers la base de données
     * @throws SQLException en cas d'erreur de connexion
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Retourner la connexion
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL non trouvé. Ajoutez mysql-connector-java.jar à votre projet", e);
        }
    }

    /**
     * Méthode pour tester la connexion à la base de données
     */
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✓ ========================================");
                System.out.println("✓ Connexion à la base de données RÉUSSIE !");
                System.out.println("✓ Base: pacman_game");
                System.out.println("✓ Serveur: localhost:3306");
                System.out.println("✓ ========================================");
            }
        } catch (SQLException e) {
            System.err.println("✗ ========================================");
            System.err.println("✗ ERREUR de connexion à la base de données");
            System.err.println("✗ ========================================");
            System.err.println("Message d'erreur: " + e.getMessage());
            System.err.println("\nVÉRIFIEZ QUE :");
            System.err.println("1. XAMPP est démarré");
            System.err.println("2. MySQL est en cours d'exécution (vert dans XAMPP)");
            System.err.println("3. La base 'pacman_game' existe dans phpMyAdmin");
            System.err.println("4. Le driver mysql-connector-java.jar est dans votre projet");
            e.printStackTrace();
        }
    }

    /**
     * Méthode main pour tester la connexion
     */
    public static void main(String[] args) {
        System.out.println("Test de connexion à la base de données...\n");
        testConnection();
    }
}