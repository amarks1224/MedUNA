package cr.ac.una.meduna.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import cr.ac.una.meduna.util.FlowController;
import cr.ac.una.meduna.util.Mensaje;
import cr.ac.una.meduna.util.UIAnimator;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.AnchorPane;

/**
 * Controlador inicio de la aplicación
 *
 * @author Angie M.
 * @author Juan C.
 */
public class InicioController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblSubtitulo;
    @FXML
    private Hyperlink linkAcercaDe;
    @FXML
    private MFXButton btnIngresar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        iniciarVentana();
    }    
    
     @Override
    public void initialize() {
        // TODO
    }    

    @FXML
    private void onActionLinkAcercaDe(ActionEvent event) {
        try {
            String url = "http://localhost:8080/MedUNA_Web/AcercaDe.xhtml";
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            new Mensaje().showModal(Alert.AlertType.ERROR,
                    "Error al abrir la página",
                    getStage(),
                    "No se pudo abrir la página Acerca de.");
            e.printStackTrace();
        }
    }


    @FXML
    private void onActionBtnIngresar(ActionEvent event) {
    }

    
    private void iniciarVentana() {
        UIAnimator.slideInLeft(root);
        btnIngresar.setOnAction(e -> abrirMenuPrincipal());
    }

    private void abrirMenuPrincipal() {
        UIAnimator.slideOutRight(root, () -> {
            FlowController.getInstance().goView("MenuView", "Left", "");
        });
    }

}
