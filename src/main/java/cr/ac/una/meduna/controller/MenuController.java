package cr.ac.una.meduna.controller;

import cr.ac.una.meduna.util.FlowController;
import cr.ac.una.meduna.util.Mensaje;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controlador menú servicios
 *
 * @author Angie Marks
 * @author Juan Calderón
 */
public class MenuController extends Controller implements Initializable {

    @FXML
    private MFXButton btnCitas;
    @FXML
    private MFXButton btnPacientes;
    @FXML
    private MFXButton btnMedicos;
    @FXML
    private MFXButton btnEspecialidades;
    @FXML
    private MFXButton btnEstadisticas;
    @FXML
    private MFXButton btnSalir;
    @FXML
    private Button btnMenu;
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblTitulo1;
    @FXML
    private Label lblTitulo11;
    
    private ResourceBundle bundle;
    private String ventanaActual;
    private MFXButton botonMenuActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
        this.ventanaActual = "";
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @FXML
    private void onActionBtnCitas(ActionEvent event) {
        openWindowAtCenter(btnCitas, "CitaView");
    }

    @FXML
    private void onActionBtnPacientes(ActionEvent event) {
        openWindowAtCenter(btnPacientes, "PacientesView");
    }

    @FXML
    private void onActionBtnMedicos(ActionEvent event) {
        openWindowAtCenter(btnMedicos, "MedicoView");
    }

    @FXML
    private void onActionBtnEspecialidades(ActionEvent event) {
        openWindowAtCenter(btnEspecialidades, "EspecialidadView");
    }

    @FXML
    private void onActionBtnEstadisticas(ActionEvent event) {
        openWindowAtCenter(btnEstadisticas, "EstadisticaView");
    }

    @FXML
    private void onActionBtnSalir(ActionEvent event) {

        boolean confirmar = new Mensaje().showConfirmation(
                "Salir del sistema",
                getStage(),
                "¿Desea cerrar la aplicación?"
        );

        if (confirmar) {
            System.exit(0);
        }
    }

    private void openWindowAtCenter(MFXButton botonMenu, String nombreVentana) {

        if (nombreVentana.equals(ventanaActual)) {
            return;
        }

        if (botonMenuActual != null) {
            botonMenuActual.setStyle("");
        }

        botonMenuActual = botonMenu;
        botonMenuActual.setStyle("-fx-background-color: -fx-secondary;");

        ventanaActual = nombreVentana;
        FlowController.getInstance().goView(ventanaActual);
    }

}
