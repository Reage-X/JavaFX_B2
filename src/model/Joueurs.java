package model;

/**
 * Classe modèle représentant un joueur
 * Contient toutes les informations relatives à un joueur du jeu Pacman
 */
public class Joueurs {
    private int id;
    private String pseudo;
    private int scoreTotal;
    private int compteId;
    private int niveau;
    private int viesRestantes;

    /**
     * Constructeur pour créer un nouveau joueur
     * @param pseudo Le pseudo du joueur
     * @param compteId L'ID du compte associé
     */
    public Joueurs(String pseudo, int compteId) {
        this.pseudo = pseudo;
        this.compteId = compteId;
        this.scoreTotal = 0;
        this.niveau = 1;
        this.viesRestantes = 3;
    }

    /**
     * Constructeur complet pour charger un joueur existant
     * @param id L'ID du joueur
     * @param pseudo Le pseudo du joueur
     * @param scoreTotal Le score total accumulé
     * @param compteId L'ID du compte associé
     * @param niveau Le niveau actuel
     * @param viesRestantes Le nombre de vies restantes
     */
    public Joueurs(int id, String pseudo, int scoreTotal, int compteId, int niveau, int viesRestantes) {
        this.id = id;
        this.pseudo = pseudo;
        this.scoreTotal = scoreTotal;
        this.compteId = compteId;
        this.niveau = niveau;
        this.viesRestantes = viesRestantes;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getScoreTotal() {
        return scoreTotal;
    }

    public void setScoreTotal(int scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    public int getCompteId() {
        return compteId;
    }

    public void setCompteId(int compteId) {
        this.compteId = compteId;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    public int getViesRestantes() {
        return viesRestantes;
    }

    public void setViesRestantes(int viesRestantes) {
        this.viesRestantes = viesRestantes;
    }

    @Override
    public String toString() {
        return "Joueur{" +
                "id=" + id +
                ", pseudo='" + pseudo + '\'' +
                ", scoreTotal=" + scoreTotal +
                ", niveau=" + niveau +
                ", vies=" + viesRestantes +
                '}';
    }
}