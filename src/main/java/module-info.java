module com.example.demo {
    // Modules JavaFX requis
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // Module SQL pour la connexion à la base de données
    requires java.sql;

    // Exporter tous les packages pour JavaFX
    exports view;
    exports dao;
    exports model;

    // Ouvrir les packages pour la réflexion JavaFX
    opens view to javafx.graphics, javafx.fxml;
    opens dao to javafx.graphics;
    opens model to javafx.graphics, javafx.base;

    // Si vous avez un package principal
    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}