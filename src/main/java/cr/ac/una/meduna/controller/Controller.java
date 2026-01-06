package cr.ac.una.meduna.controller;

import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public abstract class Controller {

    private Stage stage;
    private String accion;
    private String nombreVista;
    protected ResourceBundle bundle;

    // ======================
    // Getters / Setters
    // ======================
    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public String getNombreVista() {
        return nombreVista;
    }

    public void setNombreVista(String nombreVista) {
        this.nombreVista = nombreVista;
    }

    // ======================
    // Utilidades
    // ======================
    public void sendTabEvent(KeyEvent event) {
        event.consume();
        KeyEvent keyEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                null,
                null,
                KeyCode.TAB,
                false,
                false,
                false,
                false
        );
        ((Control) event.getSource()).fireEvent(keyEvent);
    }

    // ======================
    // Inicialización manual
    // (usada por FlowController)
    // ======================
    public void initialize() {
        // Se sobreescribe en controladores hijos si se necesita
    }

    // ======================
    // Root (si FlowController lo usa)
    // ======================
    public Node getRoot() {
        return null;
    }

    // ======================
    // Limpieza (opcional)
    // ======================
    public void cleanup() {
        // Para liberar recursos si se requiere
    }
}
