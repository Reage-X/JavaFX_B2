package dao;

import model.Joueurs;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) pour la gestion des joueurs
 * Gère toutes les opérations CRUD sur la table 'joueurs'
 */
public class JoueursDAO {
    private Connection connection;

    public JoueursDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Ajouter un nouveau joueur
     * @param joueur Le joueur à ajouter
     * @return true si l'ajout a réussi, false sinon
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean ajouterJoueur(Joueurs joueur) throws SQLException {
        String sql = "INSERT INTO joueurs (pseudo, score_total, compte_id, niveau, vies_restantes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, joueur.getPseudo());
            stmt.setInt(2, joueur.getScoreTotal());
            stmt.setInt(3, joueur.getCompteId());
            stmt.setInt(4, joueur.getNiveau());
            stmt.setInt(5, joueur.getViesRestantes());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    joueur.setId(keys.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Récupérer un joueur par son compte_id
     * @param compteId L'ID du compte associé
     * @return Le joueur correspondant, null si non trouvé
     * @throws SQLException en cas d'erreur SQL
     */
    public Joueurs getJoueurParCompte(int compteId) throws SQLException {
        String sql = "SELECT * FROM joueurs WHERE compte_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, compteId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Joueurs(
                        rs.getInt("id"),
                        rs.getString("pseudo"),
                        rs.getInt("score_total"),
                        rs.getInt("compte_id"),
                        rs.getInt("niveau"),
                        rs.getInt("vies_restantes")
                );
            }
        }
        return null;
    }

    /**
     * Mettre à jour le score total d'un joueur
     * @param joueurId L'ID du joueur
     * @param nouveauScore Le nouveau score total
     * @return true si la mise à jour a réussi, false sinon
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean mettreAJourScore(int joueurId, int nouveauScore) throws SQLException {
        String sql = "UPDATE joueurs SET score_total = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, nouveauScore);
            stmt.setInt(2, joueurId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Récupérer le classement des joueurs (top 10)
     * @return Liste des 10 meilleurs joueurs triés par score
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Joueurs> getClassement() throws SQLException {
        List<Joueurs> classement = new ArrayList<>();
        String sql = "SELECT * FROM joueurs ORDER BY score_total DESC LIMIT 10";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                classement.add(new Joueurs(
                        rs.getInt("id"),
                        rs.getString("pseudo"),
                        rs.getInt("score_total"),
                        rs.getInt("compte_id"),
                        rs.getInt("niveau"),
                        rs.getInt("vies_restantes")
                ));
            }
        }
        return classement;
    }

    /**
     * Modifier les informations d'un joueur
     * @param joueur Le joueur avec les nouvelles informations
     * @return true si la modification a réussi, false sinon
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean modifierJoueur(Joueurs joueur) throws SQLException {
        String sql = "UPDATE joueurs SET pseudo = ?, niveau = ?, vies_restantes = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, joueur.getPseudo());
            stmt.setInt(2, joueur.getNiveau());
            stmt.setInt(3, joueur.getViesRestantes());
            stmt.setInt(4, joueur.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprimer un joueur par son ID
     * @param id L'ID du joueur à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean supprimerJoueur(int id) throws SQLException {
        String sql = "DELETE FROM joueurs WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Lister tous les joueurs
     * @return Liste de tous les joueurs
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Joueurs> listerJoueurs() throws SQLException {
        List<Joueurs> joueurs = new ArrayList<>();
        String sql = "SELECT * FROM joueurs";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                joueurs.add(new Joueurs(
                        rs.getInt("id"),
                        rs.getString("pseudo"),
                        rs.getInt("score_total"),
                        rs.getInt("compte_id"),
                        rs.getInt("niveau"),
                        rs.getInt("vies_restantes")
                ));
            }
        }
        return joueurs;
    }
}