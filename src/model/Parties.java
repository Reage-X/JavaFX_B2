package model;

import java.time.LocalDateTime;

/**
 * Classe modèle représentant une partie de jeu
 * Contient toutes les informations relatives à une partie de Pacman
 */
public class Parties {
    private int id;
    private int joueurId;
    private int scorePartie;
    private int niveauAtteint;
    private String statut; // EN_COURS, TERMINEE, PAUSE
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String etatJeu; // JSON avec position Pacman, fantômes, etc.

    /**
     * Constructeur pour créer une nouvelle partie
     * @param joueurId L'ID du joueur qui démarre la partie
     */
    public Parties(int joueurId) {
        this.joueurId = joueurId;
        this.scorePartie = 0;
        this.niveauAtteint = 1;
        this.statut = "EN_COURS";
        this.dateDebut = LocalDateTime.now();
    }

    /**
     * Constructeur complet pour charger une partie existante
     * @param id L'ID de la partie
     * @param joueurId L'ID du joueur
     * @param scorePartie Le score de la partie
     * @param niveauAtteint Le niveau atteint
     * @param statut Le statut de la partie
     * @param dateDebut La date de début
     * @param dateFin La date de fin (peut être null)
     * @param etatJeu L'état du jeu en JSON
     */
    public Parties(int id, int joueurId, int scorePartie, int niveauAtteint,
                   String statut, LocalDateTime dateDebut, LocalDateTime dateFin, String etatJeu) {
        this.id = id;
        this.joueurId = joueurId;
        this.scorePartie = scorePartie;
        this.niveauAtteint = niveauAtteint;
        this.statut = statut;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.etatJeu = etatJeu;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJoueurId() {
        return joueurId;
    }

    public void setJoueurId(int joueurId) {
        this.joueurId = joueurId;
    }

    public int getScorePartie() {
        return scorePartie;
    }

    public void setScorePartie(int scorePartie) {
        this.scorePartie = scorePartie;
    }

    public int getNiveauAtteint() {
        return niveauAtteint;
    }

    public void setNiveauAtteint(int niveauAtteint) {
        this.niveauAtteint = niveauAtteint;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public String getEtatJeu() {
        return etatJeu;
    }

    public void setEtatJeu(String etatJeu) {
        this.etatJeu = etatJeu;
    }

    @Override
    public String toString() {
        return "Partie{" +
                "id=" + id +
                ", joueurId=" + joueurId +
                ", score=" + scorePartie +
                ", niveau=" + niveauAtteint +
                ", statut='" + statut + '\'' +
                '}';
    }
}