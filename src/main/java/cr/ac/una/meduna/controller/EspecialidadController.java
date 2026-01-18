package cr.ac.una.meduna.controller;

import cr.ac.una.meduna.model.EspecialidadDTO;
import cr.ac.una.meduna.service.EspecialidadService;
import cr.ac.una.meduna.util.FlowController;
import cr.ac.una.meduna.util.Formato;
import cr.ac.una.meduna.util.Mensaje;
import cr.ac.una.meduna.util.Respuesta;
import cr.ac.una.meduna.util.Shake;
import cr.ac.una.meduna.util.UIAnimator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 * Clase controller para la gestión de especialidades.
 *
 * @author Angie Marks
 * @author Juan Calderón
 */

public class EspecialidadController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private MFXTextField txfCodigo;
    @FXML
    private MFXTextField txfNombre;
    @FXML
    private MFXTextField txfDescripcion;
    @FXML
    private MFXTextField txfId;
    @FXML
    private Spinner<Integer> spDuración;
    @FXML
    private MFXButton btnNuevo;
    @FXML
    private MFXButton btnEliminar;
    @FXML
    private MFXButton btnGuardar;
    @FXML
    private MFXButton btnBuscarEspecialidad;
    
    private EspecialidadDTO especialidadDto;
    private ObjectProperty<EspecialidadDTO> especialidadProperty = new SimpleObjectProperty<>();
    private List<Node> requeridos = new ArrayList();
    private final Shake shake = new Shake();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        especialidadDto = new EspecialidadDTO();
        bindEspecialidad();
        cargarValoresDefecto();
        indicarRequeridos();
        configurarFormatos();
    }    
    
     @Override
    public void initialize() {
        UIAnimator.slideInLeft(root);
    }    
    
    @FXML
    private void onActionBtnBuscarPaciente(ActionEvent event) {
        buscarEspecialidad();
    }

    @FXML
    private void onKeyPressedTxtId(KeyEvent event) {
         if (event.getCode() == KeyCode.ENTER && !txfId.getText().isBlank()) {
            cargarEspecialidad(Long.valueOf(txfId.getText()));
        }
    }

    @FXML
    private void onActionBtnNuevo(ActionEvent event) {
         if (new Mensaje().showConfirmation("Limpiar Especialidad", getStage(), "¿Está seguro que desea limpiar el registro?")) {
            cargarValoresDefecto();
        }
    }

    @FXML
    private void onActionBtnEliminar(ActionEvent event) {
        eliminarEspecialidad();
    }

    @FXML
    private void onActionBtnGuardar(ActionEvent event) {
        guardarEspecialidad();
    }
   
    private void bindEspecialidad() {
        try {
            especialidadProperty.addListener((obs, oldVal, newVal) -> {
                if (oldVal != null) {
                    txfId.textProperty().unbind();
                    txfCodigo.textProperty().unbindBidirectional(oldVal.codigoProperty());
                    txfNombre.textProperty().unbindBidirectional(oldVal.nombreProperty());
                    txfDescripcion.textProperty().unbindBidirectional(oldVal.descripcionProperty());
                }

                if (newVal != null) {

                    txfId.textProperty().unbind();
                    if (newVal.getIdEspecialidad() != null) {
                        txfId.setText(newVal.getIdEspecialidad().toString());
                    } else {
                        txfId.clear();
                    }

                    txfCodigo.textProperty().bindBidirectional(newVal.codigoProperty());
                    txfNombre.textProperty().bindBidirectional(newVal.nombreProperty());
                    txfDescripcion.textProperty().bindBidirectional(newVal.descripcionProperty());

                    if (spDuración.getValueFactory() == null) {
                        spDuración.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 240, 30));
                        spDuración.setEditable(true);
                    }

                    spDuración.getValueFactory().setValue(newVal.getDuracion() != null ? newVal.getDuracion() : 30);
                    spDuración.valueProperty().addListener((o, a, n) -> {if 
                            (n != null) newVal.setDuracion(n);});
                }
            });

        } catch (Exception ex) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Error al realizar el bindeo",
                    getStage(), "Ocurrió un error al realizar el bindeo."
            );
        }
    }

    private void cargarValoresDefecto() {
        especialidadDto = new EspecialidadDTO();
        especialidadProperty.setValue(especialidadDto);

        txfId.clear();
        txfCodigo.clear();
        txfNombre.clear();
        txfDescripcion.clear();
        txfId.requestFocus();

        if (spDuración.getValueFactory() == null) {
            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 240, 30); valueFactory.setWrapAround(true);
            spDuración.setValueFactory(valueFactory);
            spDuración.setEditable(true);
        } else {
            spDuración.getValueFactory().setValue(30);
        }
    }

    private void indicarRequeridos() {
        requeridos.clear();
        requeridos.addAll(Arrays.asList(
            txfNombre,
            txfCodigo,
            txfDescripcion,
            spDuración
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

            } else if (node instanceof Spinner<?> spinner) {
                if (spinner.getValueFactory() == null || spinner.getValue() == null) {
                    String nombreSpinner = spinner.getId() != null ? spinner.getId() : "Spinner";
                    invalidos += validos ? nombreSpinner : ", " + nombreSpinner;
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

        if (txfCodigo.getText() == null || txfCodigo.getText().isBlank()) {
            shake.error(txfCodigo);
            valido = false;
        }

        if (txfDescripcion.getText() == null || txfDescripcion.getText().isBlank()) {
            shake.error(txfDescripcion);
            valido = false;
        }
        
        if (spDuración.getValueFactory() == null || spDuración.getValue() == null) {
            shake.error(spDuración);
            valido = false;
        }

        return valido;
    }

    private void configurarFormatos() {
          txfNombre.delegateTextFormatterProperty()
                  .set(Formato.getInstance().letrasFormat(100));
    }

    private void cargarEspecialidad(Long id) {
        try {
            EspecialidadService especialidadService = new EspecialidadService();
            Respuesta respuesta = especialidadService.getEspecialidadById(id);
            if (respuesta.getEstado()) {
                this.especialidadDto = (EspecialidadDTO) respuesta.getResultado("Especialidad");
                this.especialidadProperty.setValue(this.especialidadDto);
                validarRequeridos();
            } else {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Buscar Especialidad", getStage(), respuesta.getMensaje());
            }
        } catch (Exception ex) {
            Logger.getLogger(PacientesController.class.getName()).log(Level.SEVERE, "Error buscando la especialidad.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Buscar Especialidad", getStage(), "Ocurrió un error buscando la especialidad.");
        }
    }

    private void guardarEspecialidad() {
        try {
            if (!validarRequeridosVisual()) {
                return; 
            }

            EspecialidadService especialidadService = new EspecialidadService();
            Respuesta respuesta = especialidadService.guardarEspecialidad(especialidadDto);

            if (!respuesta.getEstado()) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar Especialidad",
                    getStage(), respuesta.getMensaje());
            } else {
                this.especialidadDto = (EspecialidadDTO) respuesta.getResultado("Especialidad");
                this.especialidadProperty.set(this.especialidadDto);

                new Mensaje().showModal(
                    Alert.AlertType.INFORMATION, "Guardar Especialidad",
                    getStage(), "La especialidad se guardó correctamente.");
            }

        } catch (Exception ex) {
            Logger.getLogger(PacientesController.class.getName())
                    .log(Level.SEVERE, "Error guardando la especialidad.", ex);

            new Mensaje().showModal(
                Alert.AlertType.ERROR, "Guardar Especialidad", 
                getStage(), "Ocurrió un error guardando la especialidad.");
        }
    }

    private void eliminarEspecialidad() {
        try {
            if (especialidadDto == null || especialidadDto.getIdEspecialidad() == null) {
                shake.error(txfId);
                return;
            }

            boolean confirmar = new Mensaje().showConfirmation("Eliminar Especialidad",
                    getStage(), "¿Está seguro que desea eliminar la especialidad?"
            );

            if (!confirmar) {
                return;
            }

            EspecialidadService service = new EspecialidadService();
            Respuesta respuesta = service.eliminarEspecialidad(
                    especialidadDto.getIdEspecialidad()
            );

            if (!respuesta.getEstado()) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar especialidad",
                        getStage(), respuesta.getMensaje()
                );
            } else {
                new Mensaje().showModal(
                        Alert.AlertType.INFORMATION, "Eliminar especialidad",
                        getStage(), "Especialidad eliminada correctamente."
                );
                cargarValoresDefecto();
            }

        } catch (Exception ex) {
            Logger.getLogger(EspecialidadController.class.getName())
                    .log(Level.SEVERE, "Error eliminando la especialidad.", ex);

            new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar especialidad",
                    getStage(), "Ocurrió un error eliminando la especialidad."
            );
        }
    }

    private void buscarEspecialidad(){
        FlowController.getInstance().goViewInWindowModal("BuscarEspecialidadView", getStage(), false);
        BuscarEspecialidadController buscarEspecialidadController = (BuscarEspecialidadController) FlowController.getInstance().getController("BuscarEspecialidadView");

        if (buscarEspecialidadController != null && buscarEspecialidadController.getResultado() != null) {
            EspecialidadDTO especialidadSeleccionada = (EspecialidadDTO) buscarEspecialidadController.getResultado();

            if (especialidadSeleccionada.getIdEspecialidad() != null) {
                cargarEspecialidad(especialidadSeleccionada.getIdEspecialidad());
            }
        }
    }
   
}
