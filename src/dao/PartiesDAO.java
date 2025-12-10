package dao;

import model.Parties;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) pour la gestion des parties
 * Gère toutes les opérations CRUD sur la table 'parties'
 */
public class PartiesDAO {
    private Connection connection;

    public PartiesDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Créer une nouvelle partie
     * @param partie La partie à créer
     * @return true si la création a réussi, false sinon
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean creerPartie(Parties partie) throws SQLException {
        String sql = "INSERT INTO parties (joueur_id, score_partie, niveau_atteint, statut, date_debut) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, partie.getJoueurId());
            stmt.setInt(2, partie.getScorePartie());
            stmt.setInt(3, partie.getNiveauAtteint());
            stmt.setString(4, partie.getStatut());
            stmt.setTimestamp(5, Timestamp.valueOf(partie.getDateDebut()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    partie.setId(keys.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sauvegarder l'état d'une partie en cours
     * @param partie La partie à sauvegarder
     * @return true si la sauvegarde a réussi, false sinon
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean sauvegarderPartie(Parties partie) throws SQLException {
        String sql = "UPDATE parties SET score_partie = ?, niveau_atteint = ?, statut = ?, etat_jeu = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, partie.getScorePartie());
            stmt.setInt(2, partie.getNiveauAtteint());
            stmt.setString(3, partie.getStatut());
            stmt.setString(4, partie.getEtatJeu());
            stmt.setInt(5, partie.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Récupérer la partie en cours d'un joueur
     * @param joueurId L'ID du joueur
     * @return La partie en cours, null si aucune
     * @throws SQLException en cas d'erreur SQL
     */
    public Parties getPartieEnCours(int joueurId) throws SQLException {
        String sql = "SELECT * FROM parties WHERE joueur_id = ? AND statut = 'EN_COURS' ORDER BY date_debut DESC LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, joueurId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Parties(
                        rs.getInt("id"),
                        rs.getInt("joueur_id"),
                        rs.getInt("score_partie"),
                        rs.getInt("niveau_atteint"),
                        rs.getString("statut"),
                        rs.getTimestamp("date_debut").toLocalDateTime(),
                        rs.getTimestamp("date_fin") != null ? rs.getTimestamp("date_fin").toLocalDateTime() : null,
                        rs.getString("etat_jeu")
                );
            }
        }
        return null;
    }

    /**
     * Terminer une partie et enregistrer le score final
     * @param partieId L'ID de la partie
     * @param scoreFinal Le score final de la partie
     * @return true si la mise à jour a réussi, false sinon
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean terminerPartie(int partieId, int scoreFinal) throws SQLException {
        String sql = "UPDATE parties SET statut = 'TERMINEE', score_partie = ?, date_fin = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, scoreFinal);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, partieId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Récupérer l'historique des parties d'un joueur
     * @param joueurId L'ID du joueur
     * @return Liste de toutes les parties du joueur, triées par date
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Parties> getHistoriqueParties(int joueurId) throws SQLException {
        List<Parties> historique = new ArrayList<>();
        String sql = "SELECT * FROM parties WHERE joueur_id = ? ORDER BY date_debut DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, joueurId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                historique.add(new Parties(
                        rs.getInt("id"),
                        rs.getInt("joueur_id"),
                        rs.getInt("score_partie"),
                        rs.getInt("niveau_atteint"),
                        rs.getString("statut"),
                        rs.getTimestamp("date_debut").toLocalDateTime(),
                        rs.getTimestamp("date_fin") != null ? rs.getTimestamp("date_fin").toLocalDateTime() : null,
                        rs.getString("etat_jeu")
                ));
            }
        }
        return historique;
    }
}