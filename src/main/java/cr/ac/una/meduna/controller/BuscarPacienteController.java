package cr.ac.una.meduna.controller;

import cr.ac.una.meduna.model.PacienteDTO;
import cr.ac.una.meduna.service.PacienteService;
import cr.ac.una.meduna.util.Formato;
import cr.ac.una.meduna.util.Mensaje;
import cr.ac.una.meduna.util.Respuesta;
import cr.ac.una.meduna.util.UIAnimator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * Clase Controler de la ventana de buscar pacientes.
 * @author Angie Marks
 * @author Juan Calderón 
 */

public class BuscarPacienteController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private MFXTextField txfNombre;
    @FXML
    private MFXTextField txfCedula;
    @FXML
    private MFXButton btnReestablecer;
    @FXML
    private MFXButton btnBuscar;
    @FXML
    private TableView<PacienteDTO> tbvPacientes;
    @FXML
    private TableColumn<PacienteDTO, String> clNombre;
    @FXML
    private TableColumn<PacienteDTO, String> clApellido;
    @FXML
    private TableColumn<PacienteDTO, String> clCedula;
    @FXML
    private TableColumn<PacienteDTO, String> clCorreo;
    
    private EventHandler<KeyEvent> keyEnter;
    private Object resultado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
        configurarTabla();
        configurarEventos();
        configurarFormatos();
    }

    @Override
    public void initialize() {
        super.initialize();
        refresh();
        UIAnimator.fadeIn(root);

        Platform.runLater(() -> {
            txfNombre.requestFocus();
        });
    }

    @FXML
    private void onActionBtnReestablecer(ActionEvent event) {
        refresh();
    }

    @FXML
    private void onActionBtnBuscar(ActionEvent event) {
        buscarPaciente();
    }
    
    public Object getResultado() {
        return resultado;
    }
    
    private void configurarTabla() {
        clNombre.setCellValueFactory(cd -> cd.getValue().nombreProperty());
        clApellido.setCellValueFactory(cd -> cd.getValue().apellidoProperty());
        clCorreo.setCellValueFactory(cd -> cd.getValue().correoProperty());
        clCedula.setCellValueFactory(cd -> cd.getValue().cedulaProperty());

        clNombre.setPrefWidth(200);
        clApellido.setPrefWidth(200);
        clCedula.setPrefWidth(200);
        clCorreo.setPrefWidth(250);
        tbvPacientes.setOnMousePressed(this::onMousePressedTbvPacientes);
    }
    
    private void onMousePressedTbvPacientes(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            PacienteDTO pacienteSeleccionado = (PacienteDTO) tbvPacientes.getSelectionModel().getSelectedItem();
            if (pacienteSeleccionado != null) {
                resultado = pacienteSeleccionado;
                getStage().close();
            }
        }
    }
    
      private void configurarEventos() {
        keyEnter = (KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnBuscar.fire();
            }
        };

        txfNombre.setOnKeyPressed(keyEnter);
        txfCedula.setOnKeyPressed(keyEnter);
    }

    private void buscarPaciente() {
        try {
            tbvPacientes.getItems().clear();

            PacienteService service = new PacienteService();
            String nombre = "%" + txfNombre.getText().trim() + "%";
            String cedula = "%" + txfCedula.getText().trim() + "%";

            Respuesta respuesta = service.getPacientesByFilters(
                    nombre.toUpperCase(),
                    cedula.toUpperCase()
            );

            if (respuesta.getEstado()) {
                ObservableList<PacienteDTO> pacientes = FXCollections.observableList(
                        (List<PacienteDTO>) respuesta.getResultado("Pacientes")
                );
                tbvPacientes.setItems(pacientes);
                tbvPacientes.refresh();

                if (pacientes.isEmpty()) {
                    tbvPacientes.refresh();
                }
            } else {
                new Mensaje().showModal(
                        Alert.AlertType.ERROR,
                        "Búsqueda de usuarios",
                        getStage(),
                        respuesta.getMensaje()
                );
            }
        } catch (Exception ex) {
            Logger.getLogger(BuscarPacienteController.class.getName()).log(Level.SEVERE, "Error buscando el paciente.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, bundle.getString("users.errormsg.noencontrado.title"), getStage(), bundle.getString("users.errormsg.noencontrado.gen"));
        }
    }
    
    private void refresh() {
        txfNombre.clear();
        txfCedula.clear();
        tbvPacientes.getItems().clear();
        txfNombre.requestFocus();
    }
    
    private void configurarFormatos() {
          txfCedula.delegateTextFormatterProperty()
                  .set(Formato.getInstance().integerFormat(20));

          txfNombre.delegateTextFormatterProperty()
                  .set(Formato.getInstance().letrasFormat(100));
    }

}
