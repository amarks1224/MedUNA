package cr.ac.una.meduna.controller;

import cr.ac.una.meduna.util.FlowController;
import cr.ac.una.meduna.util.UIAnimator;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.TouchEvent;

/**
 * FXML Controller class
 *
 * @author Usuario
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
    
    private ResourceBundle bundle;
    private String ventanaActual;
    private MFXButton botonMenuActual;

    /**
     * Initializes the controller class.
     */
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
    }

    @FXML
    private void onTouchPressedBtnSalones(TouchEvent event) {
    }

    @FXML
    private void onActionBtnPacientes(ActionEvent event) {
        openWindowAtCenter(btnPacientes, "PacientesView");
    }

    @FXML
    private void onTouchPressedBtnPedidos(TouchEvent event) {
    }

    @FXML
    private void onActionBtnMedicos(ActionEvent event) {
    }

    @FXML
    private void onTouchPressedBtnCajas(TouchEvent event) {
    }

    @FXML
    private void onActionBtnEspecialidades(ActionEvent event) {
    }

    @FXML
    private void onTouchPressedBtnFacturacion(TouchEvent event) {
    }

    @FXML
    private void onActionBtnEstadisticas(ActionEvent event) {
    }

    @FXML
    private void onTouchPressedBtnReportes(TouchEvent event) {
    }

    @FXML
    private void onActionBtnSalir(ActionEvent event) {
    }

    @FXML
    private void onTouchPressedBtnCerrarSesion(TouchEvent event) {
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
