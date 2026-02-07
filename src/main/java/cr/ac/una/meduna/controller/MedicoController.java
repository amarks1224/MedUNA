package cr.ac.una.meduna.controller;

import cr.ac.una.meduna.model.EspecialidadDTO;
import cr.ac.una.meduna.model.MedicoDTO;
import cr.ac.una.meduna.service.EspecialidadService;
import cr.ac.una.meduna.service.MedicoService;
import cr.ac.una.meduna.util.FlowController;
import cr.ac.una.meduna.util.Formato;
import cr.ac.una.meduna.util.Mensaje;
import cr.ac.una.meduna.util.Respuesta;
import cr.ac.una.meduna.util.Shake;
import cr.ac.una.meduna.util.UIAnimator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
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
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

/**
 * Controlador gestión de médicos
 *
 * @author Angie Marks
 * @author Juan Calderón
 */

public class MedicoController extends Controller implements Initializable {

    @FXML 
    private MFXTextField txfId;
    @FXML 
    private MFXTextField txfNombre;
    @FXML 
    private MFXTextField txfApellido;
    @FXML 
    private MFXTextField txfCorreo;
    @FXML 
    private MFXTextField txfTelefono;
    @FXML 
    private MFXTextField txfNumColegiado;
    @FXML 
    private MFXComboBox<EspecialidadDTO> cmbEspecialidades;
    @FXML 
    private MFXRadioButton rdbActivo;
    @FXML 
    private MFXButton btnNuevo;
    @FXML 
    private MFXButton btnEliminar;
    @FXML 
    private MFXButton btnGuardar;
    @FXML
    private MFXButton btnBuscarMedico;
    @FXML 
    private AnchorPane root;

    private final ObjectProperty<MedicoDTO> medicoProperty = new SimpleObjectProperty<>();
    private final List<Node> requeridos = new ArrayList<>();
    private MedicoDTO medicoDto;
    private final Shake shake = new Shake();
   

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bindMedico();
        cargarValoresDefecto();
        indicarRequeridos();
        configurarFormatos();
        configurarComboEspecialidades();
    }
    
     @Override
    public void initialize() {
         UIAnimator.slideInLeft(root);
    } 
    
    @FXML
    private void onActionBtnGuardar(ActionEvent event) {
        guardarMedico();
    }

    @FXML
    private void onActionBtnNuevo(ActionEvent event) {
          if (new Mensaje().showConfirmation("Limpiar Médico", getStage(), "¿Esta seguro que desea limpiar el registro?")) {
            cargarValoresDefecto();
        }
    }

    @FXML
    private void onActionBtnEliminar(ActionEvent event) {
        eliminarMedico();
    }

    @FXML
    private void onActionBtnBuscar(ActionEvent event) {
        buscarMedico();
    }

    @FXML
    private void onKeyPressedTxtId(KeyEvent event) {
         if (event.getCode() == KeyCode.ENTER && !txfId.getText().isBlank()) {
            cargarMedico(Long.valueOf(txfId.getText()));
        }
    }

    private void bindMedico() {
        medicoProperty.addListener((obs, oldVal, newVal) -> {

            if (oldVal != null) {
                txfNombre.textProperty().unbindBidirectional(oldVal.nombreProperty());
                txfApellido.textProperty().unbindBidirectional(oldVal.apellidoProperty());
                txfCorreo.textProperty().unbindBidirectional(oldVal.correoProperty());
                txfTelefono.textProperty().unbindBidirectional(oldVal.telefonoProperty());
                txfNumColegiado.textProperty().unbindBidirectional(oldVal.numColegiadoProperty());
                cmbEspecialidades.valueProperty().unbindBidirectional(oldVal.especialidadProperty());
            }

            if (newVal != null) {
                if (newVal.getIdMedico() != null) {
                    txfId.setText(newVal.getIdMedico().toString());
                } else {
                    txfId.clear();
                }

                txfNombre.textProperty().bindBidirectional(newVal.nombreProperty());
                txfApellido.textProperty().bindBidirectional(newVal.apellidoProperty());
                txfCorreo.textProperty().bindBidirectional(newVal.correoProperty());
                txfTelefono.textProperty().bindBidirectional(newVal.telefonoProperty());
                txfNumColegiado.textProperty().bindBidirectional(newVal.numColegiadoProperty());
                cmbEspecialidades.valueProperty().bindBidirectional(newVal.especialidadProperty());

                rdbActivo.setSelected("A".equals(newVal.getEstado()));
                rdbActivo.selectedProperty().addListener((o, old, s) -> newVal.setEstado(s ? "A" : "I"));
            }
        });
    }

    private void cargarValoresDefecto() {
        medicoDto = new MedicoDTO();
        medicoDto.setEstado("A");
        medicoProperty.setValue(medicoDto);
        txfId.clear();
       cmbEspecialidades.setValue(null);
    }

    public String validarRequeridos() {
        boolean validos = true;
        String invalidos = "";

        if (txfNombre.getText() == null || txfNombre.getText().isBlank()) {
            invalidos += validos ? "Nombre" : ", Nombre";
            validos = false;
        }

        if (txfApellido.getText() == null || txfApellido.getText().isBlank()) {
            invalidos += validos ? "Apellido" : ", Apellido";
            validos = false;
        }

        if (txfNumColegiado.getText() == null || txfNumColegiado.getText().isBlank()) {
            invalidos += validos ? "N° Colegiado" : ", N° Colegiado";
            validos = false;
        }

        if (txfTelefono.getText() == null || txfTelefono.getText().isBlank()) {
            invalidos += validos ? "Teléfono" : ", Teléfono";
            validos = false;
        }

        if (txfCorreo.getText() == null || txfCorreo.getText().isBlank()) {
            invalidos += validos ? "Correo" : ", Correo";
            validos = false;
        }

        if (cmbEspecialidades.getValue() == null) {
            invalidos += validos ? "Especialidad" : ", Especialidad";
            validos = false;
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

        if (txfNumColegiado.getText() == null || txfNumColegiado.getText().isBlank()) {
            shake.error(txfNumColegiado);
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

        if (cmbEspecialidades.getValue() == null) {
            shake.error(cmbEspecialidades);
            valido = false;
        }

        return valido;
    }

    private void configurarFormatos() {
        txfNombre.delegateTextFormatterProperty()
                .set(Formato.getInstance().letrasFormat(100));

        txfApellido.delegateTextFormatterProperty()
                .set(Formato.getInstance().letrasFormat(100));

        txfNumColegiado.delegateTextFormatterProperty()
                .set(Formato.getInstance().integerFormat(20));

        txfTelefono.delegateTextFormatterProperty()
                .set(Formato.getInstance().integerFormat(8));

        txfCorreo.delegateTextFormatterProperty()
                .set(Formato.getInstance().maxLengthFormat(100));
    }

    private void indicarRequeridos() {
        requeridos.clear();
        requeridos.addAll(Arrays.asList(
            txfNombre,
            txfApellido,
            txfNumColegiado,
            txfTelefono,
            txfCorreo,
            cmbEspecialidades
        ));
    }

   private void guardarMedico() {
        try {
            if (!validarRequeridosVisual()) {
                return;
            }

            EspecialidadDTO especialidad = cmbEspecialidades.getValue();
            medicoDto.setEspecialidad(especialidad);

            if (especialidad != null) {
                medicoDto.setEspecialidad(especialidad);
            }

            MedicoService service = new MedicoService();
            Respuesta respuesta = service.guardarMedico(medicoDto);

            if (!respuesta.getEstado()) {
                new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Guardar Médico",
                    getStage(),
                    respuesta.getMensaje()
                );
                return;
            }

            medicoDto = (MedicoDTO) respuesta.getResultado("Medico");
            medicoProperty.setValue(medicoDto);

            new Mensaje().showModal(
                Alert.AlertType.INFORMATION,
                "Guardar Médico",
                getStage(),
                "El médico se guardó correctamente."
            );

        } catch (Exception ex) {
            Logger.getLogger(MedicoController.class.getName())
                  .log(Level.SEVERE, "Error guardando el médico.", ex);

            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Guardar Médico",
                getStage(),
                "Ocurrió un error guardando el médico."
            );
        }
    }
   
    private void eliminarMedico() {
        try {
            if (medicoDto == null || medicoDto.getIdMedico() == null) {
                shake.error(txfId);
                return;
            }

            boolean confirmar = new Mensaje().showConfirmation(
                    "Eliminar Médico",
                    getStage(),
                    "¿Está seguro que desea eliminar el médico?"
            );

            if (!confirmar) {
                return;
            }

            MedicoService service = new MedicoService();
            Respuesta respuesta = service.eliminarMedico(medicoDto.getIdMedico());

            if (!respuesta.getEstado()) {
                new Mensaje().showModal(
                        Alert.AlertType.ERROR,
                        "Eliminar médico",
                        getStage(),
                        respuesta.getMensaje()
                );
            } else {
                new Mensaje().showModal(
                        Alert.AlertType.INFORMATION,
                        "Eliminar médico",
                        getStage(),
                        "Médico eliminado correctamente."
                );
                cargarValoresDefecto();
            }

        } catch (Exception ex) {
            Logger.getLogger(PacientesController.class.getName())
                    .log(Level.SEVERE, "Error eliminando el médico.", ex);

            new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Eliminar médico",
                    getStage(),
                    "Ocurrió un error eliminando el médico."
            );
        }
    }

    private void configurarComboEspecialidades() {
        Respuesta resp = new EspecialidadService().getEspecialidades();
        if (resp.getEstado()) {
            List<EspecialidadDTO> lista =
                    (List<EspecialidadDTO>) resp.getResultado("Especialidades");

            cmbEspecialidades.setItems(FXCollections.observableArrayList(lista));

            cmbEspecialidades.setConverter(new StringConverter<>() {
                @Override
                public String toString(EspecialidadDTO e) {
                    return e != null ? e.getNombre() : "";
                }
                @Override
                public EspecialidadDTO fromString(String s) { return null; }
            });
        }
    }

    private void cargarMedico(Long id) {
        try {
            MedicoService medicoService = new MedicoService();
            Respuesta respuesta = medicoService.getMedicoById(id);

            if (respuesta.getEstado()) {

                MedicoDTO medico = (MedicoDTO) respuesta.getResultado("Medico");
                 this.medicoDto = medico;

                medicoProperty.setValue(medico);     

                validarRequeridos();
            } else {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Buscar Médico", getStage(), respuesta.getMensaje());
            }

        } catch (Exception ex) {
            Logger.getLogger(PacientesController.class.getName()).log(Level.SEVERE, "Error buscando el médico.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Buscar Médico", getStage(), "Ocurrió un error buscando el médico.");
        }
    }
    
      private void buscarMedico(){
        FlowController.getInstance().goViewInWindowModal("BuscarMedicoView", getStage(), false);
        BuscarMedicoController buscarMedicoController = (BuscarMedicoController) FlowController.getInstance().getController("BuscarMedicoView");

        if (buscarMedicoController != null && buscarMedicoController.getResultado() != null) {
            MedicoDTO medicoSeleccionado = (MedicoDTO) buscarMedicoController.getResultado();

            if (medicoSeleccionado.getIdMedico() != null) {
                cargarMedico(medicoSeleccionado.getIdMedico());
            }
        }
    }

}
