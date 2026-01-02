module cr.ac.una.meduna {

    /* JavaFX */
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    /* Logging */
    requires java.logging;

    /* MaterialFX */
    requires MaterialFX;
    requires java.base;

    /* FXML access */
    opens cr.ac.una.meduna.controller to javafx.fxml;
    opens cr.ac.una.meduna.util to javafx.fxml;

    /* Exports */
    exports cr.ac.una.meduna;
    exports cr.ac.una.meduna.util;
}
