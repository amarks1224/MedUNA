package cr.ac.una.meduna.controller;

import cr.ac.una.meduna.model.PacienteDTO;
import cr.ac.una.meduna.service.PacienteService;
import cr.ac.una.meduna.util.Mensaje;
import cr.ac.una.meduna.util.UIAnimator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import cr.ac.una.meduna.util.BindingUtils;
import cr.ac.una.meduna.util.FlowController;
import cr.ac.una.meduna.util.Formato;
import cr.ac.una.meduna.util.Respuesta;
import cr.ac.una.meduna.util.Shake;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyCode;

/**
 * Clase controller para la gestión de pacientes.
 *
 * @author Angie Marks
 * @author Juan Calderón
 */

public class PacientesController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private MFXTextField txfId;
    @FXML
    private MFXTextField txfCedula;
    @FXML
    private MFXTextField txfNombre;
    @FXML
    private MFXTextField txfApellido;
    @FXML
    private MFXComboBox<String> cmbTipoSangre;
    @FXML
    private MFXTextField txfTelefono;
    @FXML
    private MFXTextField txfCorreo;
    @FXML
    private MFXTextField txfDireccion;
    @FXML
    private MFXDatePicker dpNacimiento;
    @FXML
    private MFXButton btnNuevo;
    @FXML
    private MFXButton btnEliminar;
    @FXML
    private MFXButton btnGuardar;
    @FXML
    private MFXRadioButton rdbMasculino;
    @FXML
    private ToggleGroup tggGenero;
    @FXML
    private MFXRadioButton rdbFemenino;
    
    
    private PacienteDTO pacienteDto;
    private ObjectProperty<PacienteDTO> pacienteProperty = new SimpleObjectProperty<>();
    private List<Node> requeridos = new ArrayList();
    private final Shake shake = new Shake();
    
    private final ObservableList<String> tiposSangre = FXCollections.observableArrayList(
            "A+", "AB+", "B+", "O+", "A-", "AB-", "B-", "O-"
    );
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbTipoSangre.setItems(tiposSangre);
        pacienteDto = new PacienteDTO();
        bindPaciente();
        cargarValoresDefecto();
        indicarRequeridos();
        configurarFormatos();
    } 
    
    @Override
    public void initialize() {
         UIAnimator.slideInLeft(root);
    } 

    private void onActionBtnBuscarPaciente(ActionEvent event) {
        buscarPaciente();
    }


    @FXML
    private void onKeyPressedTxtId(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !txfId.getText().isBlank()) {
            cargarPaciente(Long.valueOf(txfId.getText()));
        }
    }

    @FXML
    private void onActionBtnNuevo(ActionEvent event) {
        if (new Mensaje().showConfirmation("Limpiar Paciente", getStage(), "¿Esta seguro que desea limpiar el registro?")) {
            cargarValoresDefecto();
        }
    }


    @FXML
    private void onActionBtnEliminar(ActionEvent event) {
        eliminarPaciente();
    }


    @FXML
    private void onActionBtnGuardar(ActionEvent event) {
        guardarPaciente();
    }

     private void bindPaciente() {
        try {
            pacienteProperty.addListener((obs, oldVal, newVal) -> {
                if (oldVal != null) {
                    txfId.textProperty().unbind();
                    txfNombre.textProperty().unbindBidirectional(oldVal.nombreProperty());
                    txfApellido.textProperty().unbindBidirectional(oldVal.apellidoProperty());
                    txfCedula.textProperty().unbindBidirectional(oldVal.cedulaProperty());
                    txfCorreo.textProperty().unbindBidirectional(oldVal.correoProperty());
                    txfTelefono.textProperty().unbindBidirectional(oldVal.telefonoProperty());
                    txfDireccion.textProperty().unbindBidirectional(oldVal.direccionProperty());
                    dpNacimiento.valueProperty().unbindBidirectional(oldVal.nacimientoProperty());
                    cmbTipoSangre.valueProperty().unbindBidirectional(oldVal.tipoSangreProperty());
                    BindingUtils.unbindToggleGroupToProperty(tggGenero, oldVal.generoProperty());
                }
                if (newVal != null) {
                    if (newVal.idPacienteProperty().get() != null
                            && !newVal.idPacienteProperty().get().isBlank()) {
                        txfId.textProperty().bind(newVal.idPacienteProperty());
                    }
                    txfNombre.textProperty().bindBidirectional(newVal.nombreProperty());
                    txfApellido.textProperty().bindBidirectional(newVal.apellidoProperty());
                    txfCedula.textProperty().bindBidirectional(newVal.cedulaProperty());
                    txfCorreo.textProperty().bindBidirectional(newVal.correoProperty());
                    txfTelefono.textProperty().bindBidirectional(newVal.telefonoProperty());
                    txfDireccion.textProperty().bindBidirectional(newVal.direccionProperty());
                    dpNacimiento.valueProperty().bindBidirectional(newVal.nacimientoProperty());
                    cmbTipoSangre.valueProperty().bindBidirectional(newVal.tipoSangreProperty());
                    BindingUtils.bindToggleGroupToProperty(tggGenero, newVal.generoProperty());
                }
            });

        } catch (Exception ex) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Error al realizar el bindeo", getStage(),
                    "Ocurrió un error al realizar el bindeo.");
        }
    }

    private void cargarValoresDefecto() {
        pacienteDto = new PacienteDTO();
        pacienteProperty.setValue(pacienteDto);
        txfId.clear();
        txfId.requestFocus();
        dpNacimiento.clear();
        cmbTipoSangre.clearSelection();
    }

    private void indicarRequeridos() {
        requeridos.clear();
        requeridos.addAll(Arrays.asList(
            txfNombre,
            txfApellido,
            txfCedula,
            txfTelefono,
            txfCorreo,
            txfDireccion,
            dpNacimiento,
            cmbTipoSangre
        ));
    }


    public String validarRequeridos() {
        boolean validos = true;
        String invalidos = "";

        for (Node node : requeridos) {

            if (node instanceof MFXTextField txt) {
                if (txt.getText() == null || txt.getText().isBlank()) {
                    invalidos += validos ? txt.getFloatingText() : ", " + txt.getFloatingText();
                    validos = false;
                }

            } else if (node instanceof MFXPasswordField pwd) {
                if (pwd.getText() == null || pwd.getText().isBlank()) {
                    invalidos += validos ? pwd.getFloatingText() : ", " + pwd.getFloatingText();
                    validos = false;
                }

            } else if (node instanceof MFXDatePicker dp) {
                if (dp.getValue() == null) {
                    invalidos += validos ? dp.getFloatingText() : ", " + dp.getFloatingText();
                    validos = false;
                }

            } else if (node instanceof MFXComboBox<?> cmb) {
                if (cmb.getSelectionModel() == null
                        || cmb.getSelectionModel().getSelectedIndex() < 0) {
                    invalidos += validos ? cmb.getFloatingText() : ", " + cmb.getFloatingText();
                    validos = false;
                }
            }
        }

        return validos ? "" : "Campos requeridos o con problemas de formato [" + invalidos + "].";
    }

    private boolean validarRequeridosVisual() {
        boolean valido = true;

        if (txfNombre.getText() == null || txfNombre.getText().isBlank()) {
            shake.error(txfNombre);
            valido = false;
        }

        if (txfApellido.getText() == null || txfApellido.getText().isBlank()) {
            shake.error(txfApellido);
            valido = false;
        }

        if (txfCedula.getText() == null || txfCedula.getText().isBlank()) {
            shake.error(txfCedula);
            valido = false;
        }

        if (txfTelefono.getText() == null || txfTelefono.getText().isBlank()) {
            shake.error(txfTelefono);
            valido = false;
        }

        if (txfCorreo.getText() == null || txfCorreo.getText().isBlank()) {
            shake.error(txfCorreo);
            valido = false;
        }

        if (txfDireccion.getText() == null || txfDireccion.getText().isBlank()) {
            shake.error(txfDireccion);
            valido = false;
        }

        if (dpNacimiento.getValue() == null) {
            shake.error(dpNacimiento);
            valido = false;
        }

        if (cmbTipoSangre.getSelectedItem() == null) {
            shake.error(cmbTipoSangre);
            valido = false;
        }

        if (tggGenero.getSelectedToggle() == null) {
            shake.error(rdbMasculino, rdbFemenino);
            valido = false;
        }

        return valido;
    }

    private void configurarFormatos() {
          txfCedula.delegateTextFormatterProperty()
                  .set(Formato.getInstance().integerFormat(20));

          txfNombre.delegateTextFormatterProperty()
                  .set(Formato.getInstance().letrasFormat(100));

          txfApellido.delegateTextFormatterProperty()
                  .set(Formato.getInstance().letrasFormat(100));

          txfTelefono.delegateTextFormatterProperty()
                  .set(Formato.getInstance().integerFormat(8));

          txfCorreo.delegateTextFormatterProperty()
                  .set(Formato.getInstance().maxLengthFormat(100));

          txfDireccion.delegateTextFormatterProperty()
                  .set(Formato.getInstance().maxLengthFormat(200));
    }

    private void cargarPaciente(Long id) {
        try {
            PacienteService pacienteService = new PacienteService();
            Respuesta respuesta = pacienteService.getPacienteById(id);
            if (respuesta.getEstado()) {
                this.pacienteDto = (PacienteDTO) respuesta.getResultado("Paciente");
                this.pacienteProperty.setValue(this.pacienteDto);
                validarRequeridos();
            } else {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Buscar Paciente", getStage(), respuesta.getMensaje());
            }
        } catch (Exception ex) {
            Logger.getLogger(PacientesController.class.getName()).log(Level.SEVERE, "Error buscando el paciente.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Buscar Paciente", getStage(), "Ocurrió un error buscando el paciente.");
        }
    }

    private void guardarPaciente() {
        try {
            if (!validarRequeridosVisual()) {
                return; 
            }

            PacienteService pacienteService = new PacienteService();
            Respuesta respuesta = pacienteService.guardarPaciente(pacienteDto);

            if (!respuesta.getEstado()) {
                new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Guardar Paciente",
                    getStage(),
                    respuesta.getMensaje()
                );
            } else {
                this.pacienteDto = (PacienteDTO) respuesta.getResultado("Paciente");
                this.pacienteProperty.set(this.pacienteDto);

                new Mensaje().showModal(
                    Alert.AlertType.INFORMATION,
                    "Guardar Paciente",
                    getStage(),
                    "El paciente se guardó correctamente."
                );
            }

        } catch (Exception ex) {
            Logger.getLogger(PacientesController.class.getName())
                    .log(Level.SEVERE, "Error guardando el paciente.", ex);

            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Guardar Paciente",
                getStage(),
                "Ocurrió un error guardando el paciente."
            );
        }
    }
    
    private void eliminarPaciente() {
        try {
            if (pacienteDto == null || pacienteDto.getIdPaciente() == null) {
                shake.error(txfId);
                return;
            }

            boolean confirmar = new Mensaje().showConfirmation(
                    "Eliminar paciente",
                    getStage(),
                    "¿Está seguro que desea eliminar el paciente?"
            );

            if (!confirmar) {
                return;
            }

            PacienteService service = new PacienteService();
            Respuesta respuesta = service.eliminarPaciente(pacienteDto.getIdPaciente());

            if (!respuesta.getEstado()) {
                new Mensaje().showModal(
                        Alert.AlertType.ERROR,
                        "Eliminar paciente",
                        getStage(),
                        respuesta.getMensaje()
                );
            } else {
                new Mensaje().showModal(
                        Alert.AlertType.INFORMATION,
                        "Eliminar paciente",
                        getStage(),
                        "Paciente eliminado correctamente."
                );
                cargarValoresDefecto();
            }

        } catch (Exception ex) {
            Logger.getLogger(PacientesController.class.getName())
                    .log(Level.SEVERE, "Error eliminando el paciente.", ex);

            new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Eliminar paciente",
                    getStage(),
                    "Ocurrió un error eliminando el paciente."
            );
        }
    }
    
    private void buscarPaciente(){
        FlowController.getInstance().goViewInWindowModal("BuscarPacienteView", getStage(), false);
        BuscarPacienteController buscarPacienteController = (BuscarPacienteController) FlowController.getInstance().getController("BuscarPacienteView");

        if (buscarPacienteController != null && buscarPacienteController.getResultado() != null) {
            PacienteDTO pacienteSeleccionado = (PacienteDTO) buscarPacienteController.getResultado();

            if (pacienteSeleccionado.getIdPaciente() != null) {
                cargarPaciente(pacienteSeleccionado.getIdPaciente());
            }
        }
    }
}
