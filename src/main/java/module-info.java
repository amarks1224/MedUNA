module cr.ac.una.meduna {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.logging;
    requires MaterialFX;

    requires jakarta.persistence;
    requires org.eclipse.persistence.jpa;
    requires org.eclipse.persistence.core;

    requires jakarta.json.bind;
    requires jakarta.json;
    requires jakarta.annotation;
    requires jakarta.ws.rs;
     requires jakarta.inject;

    requires java.sql;
    requires java.naming;
    requires java.base;

    opens cr.ac.una.meduna.controller to javafx.fxml;
    opens cr.ac.una.meduna.util to javafx.fxml;
    opens cr.ac.una.meduna.model to org.eclipse.persistence.core, org.eclipse.persistence.jpa, jakarta.json.bind;

    exports cr.ac.una.meduna;
    exports cr.ac.una.meduna.controller;
    exports cr.ac.una.meduna.model;
    exports cr.ac.una.meduna.service;
    exports cr.ac.una.meduna.util;
}
