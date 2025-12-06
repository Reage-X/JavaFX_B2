import com.example.demo.*;
import java.sql.*;

public static void main(String[] args) throws SQLException {
    String url = "jdbc:mysql://localhost:3306/javafx-b2?createDatabaseIfNotExist=true";
    String user = "root";
    String pass = "";
    Compte compteTest = null;
    try (Connection conn = DriverManager.getConnection(url, user, pass)) {
        System.out.println("Connexion à la BDD réussie.");
    }
}

