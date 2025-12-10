package dao;

import model.Comptes;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) pour la gestion des comptes utilisateurs
 * Gère toutes les opérations CRUD sur la table 'comptes'
 */
public class ComptesDAO {
    private Connection connection;

    public ComptesDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Ajouter un nouveau compte avec mot de passe hashé
     * @param compte Le compte à ajouter
     * @return true si l'ajout a réussi, false sinon
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean ajouterCompte(Comptes compte) throws SQLException {
        String sql = "INSERT INTO comptes (nom, email, mot_de_passe, role, date_creation) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, compte.getNom());
            stmt.setString(2, compte.getEmail());
            stmt.setString(3, Comptes.hashPassword(compte.getMotDePasse()));
            stmt.setString(4, compte.getRole());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    compte.setId(keys.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Authentifier un utilisateur avec email et mot de passe
     * @param email L'email de l'utilisateur
     * @param motDePasse Le mot de passe (sera hashé)
     * @return Le compte si authentification réussie, null sinon
     * @throws SQLException en cas d'erreur SQL
     */
    public Comptes authentifier(String email, String motDePasse) throws SQLException {
        String sql = "SELECT * FROM comptes WHERE email = ? AND mot_de_passe = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, Comptes.hashPassword(motDePasse));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Comptes(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("role"),
                        rs.getTimestamp("date_creation").toLocalDateTime()
                );
            }
        }
        return null;
    }

    /**
     * Modifier un compte existant
     * @param compte Le compte avec les nouvelles informations
     * @return true si la modification a réussi, false sinon
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean modifierCompte(Comptes compte) throws SQLException {
        String sql = "UPDATE comptes SET nom = ?, email = ?, role = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, compte.getNom());
            stmt.setString(2, compte.getEmail());
            stmt.setString(3, compte.getRole());
            stmt.setInt(4, compte.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprimer un compte par son ID
     * @param id L'ID du compte à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean supprimerCompte(int id) throws SQLException {
        String sql = "DELETE FROM comptes WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Lister tous les comptes
     * @return Liste de tous les comptes
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Comptes> listerComptes() throws SQLException {
        List<Comptes> comptes = new ArrayList<>();
        String sql = "SELECT * FROM comptes";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                comptes.add(new Comptes(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("role"),
                        rs.getTimestamp("date_creation").toLocalDateTime()
                ));
            }
        }
        return comptes;
    }

    /**
     * Rechercher un compte par email (recherche partielle)
     * @param email L'email ou partie d'email à rechercher
     * @return Le premier compte trouvé, null si aucun
     * @throws SQLException en cas d'erreur SQL
     */
    public Comptes rechercherParEmail(String email) throws SQLException {
        String sql = "SELECT * FROM comptes WHERE email LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + email + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Comptes(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("role"),
                        rs.getTimestamp("date_creation").toLocalDateTime()
                );
            }
        }
        return null;
    }
}