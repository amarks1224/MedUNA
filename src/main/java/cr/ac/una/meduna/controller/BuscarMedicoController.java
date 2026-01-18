package cr.ac.una.meduna.controller;

import cr.ac.una.meduna.model.MedicoDTO;
import cr.ac.una.meduna.service.MedicoService;
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
import javafx.beans.property.SimpleStringProperty;
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

public class BuscarMedicoController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private MFXTextField txfNombre;

    @FXML
    private MFXTextField txtCodigo;

    @FXML
    private MFXButton btnReestablecer;

    @FXML
    private MFXButton btnBuscar;

    @FXML
    private TableView<MedicoDTO> tbvMedicos;

    @FXML
    private TableColumn<MedicoDTO, String> clNombre;

    @FXML
    private TableColumn<MedicoDTO, String> clApellido;

    @FXML
    private TableColumn<MedicoDTO, String> clEspecialidad;

    @FXML
    private TableColumn<MedicoDTO, String> clCorreo;

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

        Platform.runLater(() -> txfNombre.requestFocus());
    }

    @FXML
    private void onActionBtnReestablecer(ActionEvent event) {
        refresh();
    }

    @FXML
    private void onActionBtnBuscar(ActionEvent event) {
        buscarMedico();
    }

    public Object getResultado() {
        return resultado;
    }

    private void configurarTabla() {

        clNombre.setCellValueFactory(cd -> cd.getValue().nombreProperty());
        clApellido.setCellValueFactory(cd -> cd.getValue().apellidoProperty());
        clCorreo.setCellValueFactory(cd -> cd.getValue().correoProperty());

        clEspecialidad.setCellValueFactory(cd ->
            new SimpleStringProperty(
                cd.getValue().getEspecialidad() != null
                    ? cd.getValue().getEspecialidad().getNombre()
                    : ""
            )
        );

        clNombre.setPrefWidth(200);
        clApellido.setPrefWidth(200);
        clEspecialidad.setPrefWidth(200);
        clCorreo.setPrefWidth(250);

        tbvMedicos.setOnMousePressed(this::onMousePressedTbvMedicos);
    }

    private void onMousePressedTbvMedicos(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            MedicoDTO medicoSeleccionado =
                    tbvMedicos.getSelectionModel().getSelectedItem();

            if (medicoSeleccionado != null) {
                resultado = medicoSeleccionado;
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
        txtCodigo.setOnKeyPressed(keyEnter);
    }

    private void buscarMedico() {

        try {
            tbvMedicos.getItems().clear();

            MedicoService service = new MedicoService();

            Respuesta respuesta = service.getMedicodByFilters(
                    txfNombre.getText(),
                    txtCodigo.getText()
            );

            if (!respuesta.getEstado()) {
                new Mensaje().showModal(
                        Alert.AlertType.ERROR,
                        "Búsqueda de médicos",
                        getStage(),
                        respuesta.getMensaje()
                );
                return;
            }

            Object objResultado = respuesta.getResultado("Médicos");

            if (objResultado == null) {
                tbvMedicos.setItems(FXCollections.observableArrayList());

                new Mensaje().showModal(
                        Alert.AlertType.INFORMATION,
                        "Búsqueda",
                        getStage(),
                        "No se encontraron médicos."
                );
                return;
            }

            List<MedicoDTO> lista = (List<MedicoDTO>) objResultado;

            ObservableList<MedicoDTO> medicos =
                    FXCollections.observableArrayList(lista);

            tbvMedicos.setItems(medicos);

        } catch (Exception ex) {

            Logger.getLogger(BuscarMedicoController.class.getName())
                    .log(Level.SEVERE, "Error buscando médico", ex);

            new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Error",
                    getStage(),
                    "Ocurrió un error inesperado."
            );
        }
    }

    private void refresh() {
        txfNombre.clear();
        txtCodigo.clear();
        tbvMedicos.getItems().clear();
        txfNombre.requestFocus();
    }

    private void configurarFormatos() {
        txfNombre.delegateTextFormatterProperty()
                .set(Formato.getInstance().letrasFormat(100));
    }
}
