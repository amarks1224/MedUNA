package cr.ac.una.meduna.controller;

import cr.ac.una.meduna.model.EspecialidadDTO;
import cr.ac.una.meduna.service.EspecialidadService;
import cr.ac.una.meduna.util.Respuesta;
import cr.ac.una.meduna.util.UIAnimator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import cr.ac.una.meduna.util.Mensaje;
import java.util.logging.Logger;
import java.util.logging.Level;
import javafx.scene.control.Alert;


/**
 * Clase controlador de la ventana de buscar especialidades.
 * @author Angie Marks S.
 * @author Juan Calderón S.
 */

public class BuscarEspecialidadController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private MFXTextField txfNombre;
    @FXML
    private MFXTextField txfCodigo;
    @FXML
    private MFXButton btnReestablecer;
    @FXML
    private MFXButton btnBuscar;
    @FXML
    private TableView<EspecialidadDTO> tbvEspecialidades;
    @FXML
    private TableColumn<EspecialidadDTO, String> clNombre;
    @FXML
    private TableColumn<EspecialidadDTO, String> clCodigo;
    @FXML
    private TableColumn<EspecialidadDTO, String> clDescripcion;
    @FXML
    private TableColumn<EspecialidadDTO, Integer> clDuracion;    
    
    private EventHandler<KeyEvent> keyEnter;
    private Object resultado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
        configurarTabla();
        configurarEventos();
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
        buscarEspecialidad();
    }
    
    private void configurarTabla() {
          clNombre.setCellValueFactory(cd -> cd.getValue().nombreProperty());
          clDuracion.setCellValueFactory(cd -> cd.getValue().duracionProperty().asObject());        
          clDescripcion.setCellValueFactory(cd -> cd.getValue().descripcionProperty());

          clNombre.setPrefWidth(200);
          clDuracion.setPrefWidth(200);
          clDescripcion.setPrefWidth(200);
          tbvEspecialidades.setOnMousePressed(this::onMousePressedTbvPacientes);
      }

    private void onMousePressedTbvPacientes(MouseEvent event) {
          if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
              EspecialidadDTO pacienteSeleccionado = (EspecialidadDTO) tbvEspecialidades.getSelectionModel().getSelectedItem();
              if (pacienteSeleccionado != null) {
                  resultado = pacienteSeleccionado;
                  getStage().close();
              }
          }
      }
    
    private void buscarEspecialidad() {
        try {
            tbvEspecialidades.getItems().clear();

            EspecialidadService service = new EspecialidadService();
            String nombre = "%" + txfNombre.getText().trim() + "%";
            String codigo = "%" + txfCodigo.getText().trim() + "%";

            Respuesta respuesta = service.getEspecialidadByFilters(
                    nombre.toUpperCase(),
                    codigo.toUpperCase()
            );

            if (respuesta.getEstado()) {
                ObservableList<EspecialidadDTO> especialidades = FXCollections.observableList(
                        (List<EspecialidadDTO>) respuesta.getResultado("Especialidades")
                );
                tbvEspecialidades.setItems(especialidades);
                tbvEspecialidades.refresh();

                if (especialidades.isEmpty()) {
                    tbvEspecialidades.refresh();
                }
            } else {
                new Mensaje().showModal(
                        Alert.AlertType.ERROR,
                        "Búsqueda de especialidades",
                        getStage(),
                        respuesta.getMensaje()
                );
            }
        } catch (Exception ex) {
            Logger.getLogger(BuscarEspecialidadController.class.getName())
                  .log(Level.SEVERE, "Error buscando la especialidad.", ex);
            new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Búsqueda de especialidades",
                    getStage(),
                    "No se pudo realizar la búsqueda."
            );
        }
    }
    
    private void refresh() {
        txfNombre.clear();
        txfCodigo.clear();
        tbvEspecialidades.getItems().clear();
        txfNombre.requestFocus();
    }

    private void configurarEventos() {
        keyEnter = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnBuscar.fire();
            }
        };

        txfNombre.setOnKeyPressed(keyEnter);
        txfCodigo.setOnKeyPressed(keyEnter);
    }
    
    public Object getResultado() {
        return resultado;
    }

}
