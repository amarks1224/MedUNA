package cr.ac.una.meduna.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class EspecialidadController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private MFXButton btnBuscarPaciente;
    @FXML
    private MFXTextField txfCodigo;
    @FXML
    private MFXTextField txfNombre;
    @FXML
    private MFXTextField txfDescripcion;
    @FXML
    private Spinner<Integer> spDuración;
    @FXML
    private MFXButton btnNuevo;
    @FXML
    private MFXButton btnEliminar;
    @FXML
    private MFXButton btnGuardar;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
     @Override
    public void initialize() {
        // TODO
    }    

    @FXML
    private void onActionBtnBuscarPaciente(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedTxtId(KeyEvent event) {
    }

    @FXML
    private void onActionBtnNuevo(ActionEvent event) {
    }

    @FXML
    private void onActionBtnEliminar(ActionEvent event) {
    }

    @FXML
    private void onActionBtnGuardar(ActionEvent event) {
    }
    
}
