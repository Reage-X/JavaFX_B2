import com.example.demo.*;
import java.sql.*;
import java.util.Scanner;

public static void main(String[] args) throws SQLException {
    String url = "jdbc:mysql://localhost:3306/javafx-b2?createDatabaseIfNotExist=true";
    String user = "root";
    String pass = "";
    Compte compteTest = null;
    try (Connection conn = DriverManager.getConnection(url, user, pass)) {
        System.out.println("Connexion à la BDD réussie.");
    }


    Scanner scanner = new Scanner(System.in);
    boolean running = true;

    while (running) {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1 - Créer un compte");
        System.out.println("2 - Se connecter");
        System.out.println("3 - Quitter");
        System.out.print("Choix : ");

        int choix = scanner.nextInt();
        scanner.nextLine(); // vider buffer

        switch (choix) {
            case 1 -> creerCompte(scanner);
            case 2 -> connexion(scanner);
            case 3 -> {
                System.out.println("Au revoir !");
                running = false;
            }
            default -> System.out.println("Choix invalide.");
        }
    }
}

// ------------------------------------------------------------
// CREATION D'UN COMPTE
// ------------------------------------------------------------

private static void creerCompte(Scanner sc) {
    System.out.println("\n=== CRÉATION D'UN COMPTE ===");

    System.out.print("Nom d'utilisateur : ");
    String user = sc.nextLine();

    // Vérifie si le nom est déjà pris
    for (Compte c : comptes) {
        if (c.getUser_name().equals(user)) {
            System.out.println("⚠ Ce nom est déjà utilisé !");
            return;
        }
    }

    System.out.print("Mot de passe : ");
    String mdp = sc.nextLine();

    Compte nouveau = new Compte(user, mdp);
    comptes.add(nouveau);

    System.out.println("✔ Compte créé avec succès !");
}

// ------------------------------------------------------------
// CONNEXION À UN COMPTE
// ------------------------------------------------------------

private static void connexion(Scanner sc) {
    System.out.println("\n=== CONNEXION ===");

    System.out.print("Nom d'utilisateur : ");
    String user = sc.nextLine();

    System.out.print("Mot de passe : ");
    String mdp = sc.nextLine();

    // Recherche du compte
    Compte compte = null;
    for (Compte c : comptes) {
        if (c.getUser_name().equals(user) && c.getMDP().equals(mdp)) {
            compte = c;
            break;
        }
    }

    if (compte == null) {
        System.out.println("❌ Identifiants incorrects !");
        return;
    }

    System.out.println("✔ Connexion réussie !");
    menuCompte(sc, compte);
}


// ------------------------------------------------------------
// MENU UNE FOIS CONNECTÉ
// ------------------------------------------------------------

private static void menuCompte(Scanner sc, Compte c) {

    boolean connected = true;

    while (connected) {
        System.out.println("\n=== MENU COMPTE (" + c.getUser_name() + ") ===");
        System.out.println("1 - Voir mes scores");
        System.out.println("2 - Ajouter un score");
        System.out.println("3 - Déconnexion");
        System.out.print("Choix : ");

        int choix = sc.nextInt();
        sc.nextLine(); // vider buffer

        switch (choix) {
            case 1 -> {
                System.out.println("\n--- VOS SCORES ---");
                if (c.getScore() == null || c.getScore().isEmpty()) {
                    System.out.println("Aucun score enregistré.");
                } else {
                    for (int s : c.getScore()) {
                        System.out.println("• " + s);
                    }
                }
            }

            case 2 -> {
                System.out.print("Entrez le score à ajouter : ");
                int s = sc.nextInt();
                c.addScore(s);
                System.out.println("✔ Score ajouté !");
            }

            case 3 -> {
                System.out.println("Déconnexion réussie.");
                connected = false;
            }

            default -> System.out.println("Choix invalide.");
        }
    }
}