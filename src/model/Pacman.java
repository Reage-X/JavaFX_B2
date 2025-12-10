package model;


public class Pacman {
    private double posX;
    private double posY;
    private Direction direction;
    private Direction prochainDirection; // Pour permettre les virages anticipés
    private double vitesse;
    private boolean invincible;
    private long tempsInvincibilite;
    private int vies;
    private boolean enTrainDeManger; // Ralentit quand il mange des dots

    // VITESSES RÉELLES du Pac-Man original (en pixels par frame à 60 FPS)
    // Dans le jeu original, la grille fait 224x288 pixels
    public static final double VITESSE_NORMALE = 1.5;           // 80% vitesse max
    public static final double VITESSE_BOOST = 1.8;             // 90% avec power pellet
    public static final double VITESSE_RALENTI = 1.35;          // 71% en mangeant
    public static final double VITESSE_DANS_TUNNEL = 0.8;       // 40% dans les tunnels

    // DURÉES (en millisecondes)
    public static final int DUREE_INVINCIBILITE = 6000;         // 6 secondes (diminue par niveau)
    public static final int DUREE_RALENTI_MANGER = 100;         // Léger ralenti quand mange dot

    // CARACTÉRISTIQUES DE JEU
    public static final int VIES_DEPART = 3;
    public static final int TAILLE_PACMAN = 10;                 // Taille en pixels (rayon)

    // Animation de la bouche (ouverture/fermeture)
    private int frameAnimation = 0;
    private final int FRAMES_PAR_CYCLE = 8;                     // Vitesse d'animation bouche

    /**
     * Énumération des directions possibles pour Pacman
     */
    public enum Direction {
        HAUT(0, -1),
        BAS(0, 1),
        GAUCHE(-1, 0),
        DROITE(1, 0),
        IMMOBILE(0, 0);

        public final int dx;
        public final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    /**
     * Constructeur de Pacman
     * @param posX Position X initiale (en pixels)
     * @param posY Position Y initiale (en pixels)
     */
    public Pacman(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
        this.direction = Direction.IMMOBILE;
        this.prochainDirection = Direction.IMMOBILE;
        this.vitesse = VITESSE_NORMALE;
        this.invincible = false;
        this.vies = VIES_DEPART;
        this.enTrainDeManger = false;
    }

    /**
     * Déplacer Pacman selon sa direction actuelle
     * Prend en compte la vitesse réaliste et les ralentissements
     */
    public void deplacer() {
        if (direction == Direction.IMMOBILE) {
            return;
        }

        // Animation de la bouche
        frameAnimation = (frameAnimation + 1) % FRAMES_PAR_CYCLE;

        // Calculer la vitesse effective
        double vitesseEffective = vitesse;
        if (enTrainDeManger) {
            vitesseEffective = VITESSE_RALENTI;
        }

        // Déplacement selon la direction
        posX += direction.dx * vitesseEffective;
        posY += direction.dy * vitesseEffective;
    }

    /**
     * Tenter de changer de direction
     * Dans le vrai Pac-Man, on peut "préparer" un virage avant d'arriver au carrefour
     * @param nouvelleDirection La direction souhaitée
     */
    public void demanderChangementDirection(Direction nouvelleDirection) {
        this.prochainDirection = nouvelleDirection;
    }

    /**
     * Appliquer le changement de direction si possible (à un carrefour)
     * @param peutTourner Indique si Pacman est à un carrefour
     */
    public void appliquerChangementDirection(boolean peutTourner) {
        if (peutTourner && prochainDirection != Direction.IMMOBILE) {
            this.direction = this.prochainDirection;
            this.prochainDirection = Direction.IMMOBILE;
        }
    }

    /**
     * Activer le mode invincible (Power Pellet mangé)
     * Augmente la vitesse et active l'invincibilité temporaire
     */
    public void activerInvincibilite() {
        this.invincible = true;
        this.vitesse = VITESSE_BOOST;
        this.tempsInvincibilite = System.currentTimeMillis();
    }

    /**
     * Vérifier si l'invincibilité est toujours active
     * Désactive l'invincibilité après la durée prévue
     */
    public void verifierInvincibilite() {
        if (invincible && (System.currentTimeMillis() - tempsInvincibilite) > DUREE_INVINCIBILITE) {
            this.invincible = false;
            this.vitesse = VITESSE_NORMALE;
        }
    }

    /**
     * Indiquer que Pacman est en train de manger un dot
     * Provoque un léger ralentissement réaliste
     */
    public void commencerManger() {
        this.enTrainDeManger = true;
    }

    /**
     * Pacman a fini de manger
     */
    public void arreterManger() {
        this.enTrainDeManger = false;
    }

    /**
     * Faire perdre une vie à Pacman
     * @return true si Pacman a encore des vies, false sinon (game over)
     */
    public boolean perdreVie() {
        this.vies--;
        return this.vies > 0;
    }

    /**
     * Gagner une vie bonus (tous les 10 000 points dans le jeu original)
     */
    public void gagnerVie() {
        this.vies++;
    }

    /**
     * Réinitialiser la position de Pacman (après avoir perdu une vie)
     * @param x Nouvelle position X
     * @param y Nouvelle position Y
     */
    public void reinitialiserPosition(double x, double y) {
        this.posX = x;
        this.posY = y;
        this.direction = Direction.IMMOBILE;
        this.prochainDirection = Direction.IMMOBILE;
        this.vitesse = VITESSE_NORMALE;
        this.invincible = false;
        this.enTrainDeManger = false;
    }

    /**
     * Entrer dans un tunnel (ralentissement)
     */
    public void entrerDansTunnel() {
        if (!invincible) {
            this.vitesse = VITESSE_DANS_TUNNEL;
        }
    }

    /**
     * Sortir d'un tunnel (retour vitesse normale)
     */
    public void sortirDuTunnel() {
        this.vitesse = invincible ? VITESSE_BOOST : VITESSE_NORMALE;
    }

    /**
     * Obtenir l'angle d'ouverture de la bouche pour l'animation
     * @return Angle en degrés (0-45)
     */
    public double getAngleOuvertureBouche() {
        // Animation sinusoïdale de la bouche
        return 45 * Math.abs(Math.sin(frameAnimation * Math.PI / FRAMES_PAR_CYCLE));
    }

    // ============= GETTERS ET SETTERS =============

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public Direction getDirection() {
        return direction;
    }

    /**
     * CORRECTION DU BUG : Ne pas modifier prochainDirection ici !
     * Cette méthode doit seulement changer la direction actuelle
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
        // NE PAS TOUCHER prochainDirection ici !
    }

    public Direction getProchainDirection() {
        return prochainDirection;
    }

    public double getVitesse() {
        return vitesse;
    }

    public void setVitesse(double vitesse) {
        this.vitesse = vitesse;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public int getVies() {
        return vies;
    }

    public void setVies(int vies) {
        this.vies = vies;
    }

    public boolean isEnTrainDeManger() {
        return enTrainDeManger;
    }

    public int getFrameAnimation() {
        return frameAnimation;
    }

    @Override
    public String toString() {
        return "Pacman{" +
                "pos=(" + String.format("%.1f", posX) + "," + String.format("%.1f", posY) + ")" +
                ", direction=" + direction +
                ", vies=" + vies +
                ", invincible=" + invincible +
                ", vitesse=" + String.format("%.2f", vitesse) +
                '}';
    }
}