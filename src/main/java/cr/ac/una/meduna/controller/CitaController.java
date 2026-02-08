package cr.ac.una.meduna.controller;

import cr.ac.una.meduna.model.CitaDTO;
import cr.ac.una.meduna.model.MedicoDTO;
import cr.ac.una.meduna.model.PacienteDTO;
import cr.ac.una.meduna.service.CitaService;
import cr.ac.una.meduna.service.HolidayService;
import cr.ac.una.meduna.service.MedicoService;
import cr.ac.una.meduna.service.PacienteService;
import cr.ac.una.meduna.util.CitaHorarioUtil;
import cr.ac.una.meduna.util.FeriadoDatePickerHelper;
import cr.ac.una.meduna.util.Mensaje;
import cr.ac.una.meduna.util.Respuesta;
import cr.ac.una.meduna.util.Shake;
import cr.ac.una.meduna.util.UIAnimator;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Clase controlador para la gestión de citas.
 * @author Angie Marks
 * @author Juan Calderón 
 */

public class CitaController extends Controller implements Initializable {

    @FXML 
    private TableView<CitaDTO> tbCitas;
    @FXML 
    private TableColumn<CitaDTO, String> colId;
    @FXML 
    private TableColumn<CitaDTO, String> colMedico;
    @FXML 
    private TableColumn<CitaDTO, String> colPaciente;
    @FXML 
    private TableColumn<CitaDTO, Object> colFecha;
    @FXML 
    private TableColumn<CitaDTO, Object> colHoraIni;
    @FXML 
    private TableColumn<CitaDTO, Object> colHoraFin;
    @FXML 
    private TableColumn<CitaDTO, String> colEstado;
    @FXML 
    private TextField txtIdCita;
    @FXML 
    private ComboBox<MedicoDTO> cbMedico;
    @FXML 
    private ComboBox<PacienteDTO> cbPaciente;
    @FXML 
    private DatePicker dpFecha;
    @FXML 
    private ComboBox<LocalTime> cbHoraInicio;
    @FXML 
    private ComboBox<LocalTime> cbHoraFin;
    @FXML 
    private TextField txtMotivo;
    @FXML 
    private ComboBox<String> cbEstado;
    @FXML 
    private TextArea txtCancelacion;
    @FXML 
    private AnchorPane root;

    private final Mensaje mensaje = new Mensaje();
    private final Shake shake = new Shake();
    private final CitaService citaService = new CitaService();
    private final MedicoService medicoService = new MedicoService();
    private final PacienteService pacienteService = new PacienteService();
    private final Map<Long, String> medicoNombreMap = new HashMap<>();
    private final Map<Long, String> pacienteNombreMap = new HashMap<>();
    private final ObservableList<CitaDTO> citas = FXCollections.observableArrayList();
    private List<CitaDTO> citasOcupadas = List.of();
    private static final LocalTime HORA_APERTURA = LocalTime.of(7, 0);
    private static final LocalTime HORA_CIERRE   = LocalTime.of(18, 0);
    private static final int BLOQUE_MINUTOS = 30;
    private final FeriadoDatePickerHelper feriadoHelper = new FeriadoDatePickerHelper(new HolidayService());

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UIAnimator.slideInLeft(root);

        tbCitas.setItems(citas);
        tbCitas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        configurarTabla();
        configurarCombos();
        feriadoHelper.attach(dpFecha);

        cbMedico.valueProperty().addListener((o, a, b) -> actualizarDisponibilidad());
        dpFecha.valueProperty().addListener((o, a, b) -> actualizarDisponibilidad());
        cbHoraInicio.valueProperty().addListener((o, a, b) -> actualizarHorasFin());

        tbCitas.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
            if (sel != null) cargarCitaEnFormulario(sel);
        });

        cargarDatosIniciales();
        refrescoPorFoco();
    }

    private Window window() {
        return (root != null && root.getScene() != null) ? root.getScene().getWindow() : null;
    }

    private void errorDesdeRespuesta(String titulo, Respuesta r) {
        String txt = r.getMensaje();
        if (r.getMensajeInterno() != null && !r.getMensajeInterno().isBlank()) {
            txt += "\n\nDetalle:\n" + r.getMensajeInterno();
        }
        mensaje.showModal(Alert.AlertType.ERROR, titulo, window(), txt);
    }

    private static <T> void setCellFactory(ComboBox<T> cb, Function<T, String> textFn) {
        cb.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : textFn.apply(item));
            }
        });
        cb.setButtonCell(cb.getCellFactory().call(null));
    }

    private static <T> void selectById(ComboBox<T> cb, Predicate<T> match) {
        for (T item : cb.getItems()) {
            if (item != null && match.test(item)) {
                cb.getSelectionModel().select(item);
                return;
            }
        }
    }

    private void req(List<Node> invalidos, boolean cond, Node node) {
        if (!cond) invalidos.add(node);
    }

    private boolean validate(List<Node> invalidos) {
        if (invalidos.isEmpty()) return true;
        invalidos.forEach(shake::error);
        return false;
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCita"));

        colMedico.setCellValueFactory(cell -> {
            CitaDTO c = cell.getValue();
            Long id = c != null ? c.getIdMedico() : null;
            String nombre = (id != null) ? medicoNombreMap.getOrDefault(id, String.valueOf(id)) : "";
            return new SimpleStringProperty(nombre);
        });

        colPaciente.setCellValueFactory(cell -> {
            CitaDTO c = cell.getValue();
            Long id = c != null ? c.getIdPaciente() : null;
            String nombre = (id != null) ? pacienteNombreMap.getOrDefault(id, String.valueOf(id)) : "";
            return new SimpleStringProperty(nombre);
        });

        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHoraIni.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));
        colHoraFin.setCellValueFactory(new PropertyValueFactory<>("horaFin"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void configurarCombos() {
        cbEstado.setItems(FXCollections.observableArrayList("P", "A", "F", "S", "C"));
        setCellFactory(cbEstado, CitaHorarioUtil::formatearEstado);
        cbEstado.getSelectionModel().select("P");

        setCellFactory(cbMedico, m -> m.getIdMedico() + " - " + m.getNombre() + " " + m.getApellido());
        setCellFactory(cbPaciente, p -> p.getIdPaciente() + " - " + p.getNombre() + " " + p.getApellido());

        cbHoraInicio.setItems(FXCollections.observableArrayList(
                CitaHorarioUtil.generarHoras(HORA_APERTURA, HORA_CIERRE.minusMinutes(BLOQUE_MINUTOS), BLOQUE_MINUTOS)
        ));
        cbHoraFin.getItems().clear();
    }

    private void cargarDatosIniciales() {
        cargarMedicos();
        cargarPacientes();
        cargarCitas();
        limpiarFormulario();
    }

    private void refrescoPorFoco() {
        Platform.runLater(() -> {
            Window w = window();
            if (w != null) {
                w.focusedProperty().addListener((o, was, isNow) -> {
                    if (isNow) {
                        cargarMedicos();
                        cargarPacientes();
                    }
                });
            }
        });
    }

    private void cargarCitas() {
        Respuesta r = citaService.getCitas();
        if (!r.getEstado()) { errorDesdeRespuesta("Citas", r); return; }

        @SuppressWarnings("unchecked")
        List<CitaDTO> lista = (List<CitaDTO>) r.getResultado("Citas");
        citas.setAll(lista != null ? lista : List.of());
    }

    private void cargarMedicos() {
        Respuesta r = medicoService.getMedicos();
        if (!r.getEstado()) { errorDesdeRespuesta("Médicos", r); return; }

        @SuppressWarnings("unchecked")
        List<MedicoDTO> lista = (List<MedicoDTO>) (r.getResultado("Médicos") != null ? r.getResultado("Médicos") : r.getResultado("Médico"));
        if (lista == null) lista = List.of();

        cbMedico.setItems(FXCollections.observableArrayList(lista));
        medicoNombreMap.clear();
        for (MedicoDTO m : lista) {
            if (m != null && m.getIdMedico() != null) {
                medicoNombreMap.put(m.getIdMedico(), m.getNombre() + " " + m.getApellido());
            }
        }
        tbCitas.refresh();
    }

    private void cargarPacientes() {
        Respuesta r = pacienteService.getPacientes();
        if (!r.getEstado()) { errorDesdeRespuesta("Pacientes", r); return; }

        @SuppressWarnings("unchecked")
        List<PacienteDTO> lista = (List<PacienteDTO>) r.getResultado("Pacientes");
        if (lista == null) lista = List.of();

        cbPaciente.setItems(FXCollections.observableArrayList(lista));
        pacienteNombreMap.clear();
        for (PacienteDTO p : lista) {
            if (p != null && p.getIdPaciente() != null) {
                pacienteNombreMap.put(p.getIdPaciente(), p.getNombre() + " " + p.getApellido());
            }
        }
        tbCitas.refresh();
    }

    private void actualizarDisponibilidad() {
        cbHoraInicio.getItems().clear();
        cbHoraFin.getItems().clear();
        citasOcupadas = List.of();

        MedicoDTO medico = cbMedico.getValue();
        LocalDate fecha = dpFecha.getValue();

        if (medico == null || medico.getIdMedico() == null || fecha == null) {
            cbHoraInicio.setItems(FXCollections.observableArrayList(
                    CitaHorarioUtil.generarHoras(HORA_APERTURA, HORA_CIERRE.minusMinutes(BLOQUE_MINUTOS), BLOQUE_MINUTOS)
            ));
            return;
        }

        Respuesta r = citaService.getCitasByMedicoFecha(medico.getIdMedico(), fecha);
        if (!r.getEstado()) { errorDesdeRespuesta("Citas", r); return; }

        @SuppressWarnings("unchecked")
        List<CitaDTO> ocupadas = (List<CitaDTO>) r.getResultado("Citas");
        citasOcupadas = (ocupadas != null) ? ocupadas : List.of();

        List<LocalTime> inicioDisp = CitaHorarioUtil.calcularInicioDisponibles(
                citasOcupadas, HORA_APERTURA, HORA_CIERRE, BLOQUE_MINUTOS
        );

        cbHoraInicio.setItems(FXCollections.observableArrayList(inicioDisp));
        if (!inicioDisp.isEmpty()) {
            cbHoraInicio.getSelectionModel().selectFirst();
            actualizarHorasFin();
        }
    }

    private void actualizarHorasFin() {
        cbHoraFin.getItems().clear();
        LocalTime ini = cbHoraInicio.getValue();

        List<LocalTime> finDisp = CitaHorarioUtil.calcularFinDisponibles(
                citasOcupadas, ini, HORA_CIERRE, BLOQUE_MINUTOS
        );

        cbHoraFin.setItems(FXCollections.observableArrayList(finDisp));
        if (!finDisp.isEmpty()) cbHoraFin.getSelectionModel().selectFirst();
    }

    @FXML
    private void onNuevo(ActionEvent event) {
        tbCitas.getSelectionModel().clearSelection();
        limpiarFormulario();
    }

    @FXML
    private void onGuardar(ActionEvent event) {
        if (!validarGuardar()) return;

        CitaDTO dto = leerFormulario();
        Respuesta r = citaService.guardarCita(dto);

        if (!r.getEstado()) { errorDesdeRespuesta("Guardar cita", r); return; }

        mensaje.showModal(Alert.AlertType.INFORMATION, "Guardar cita", window(), r.getMensaje());
        cargarCitas();

        CitaDTO guardada = (CitaDTO) r.getResultado("Cita");
        if (guardada != null) {
            seleccionarEnTabla(guardada.getIdCita() != null ? guardada.getIdCita().toString() : null);
            cargarCitaEnFormulario(guardada);
        } else limpiarFormulario();
    }

    @FXML
    private void onCancelarCita(ActionEvent event) {
        if (!validarCancelar()) {
            mensaje.showModal(Alert.AlertType.ERROR, "Cancelar", window(),
                    "Seleccione una cita de la tabla para cancelar.");
            return;
        }

        long id = Long.parseLong(txtIdCita.getText().trim());
        String motivo = txtCancelacion.getText();

        Boolean ok = mensaje.showConfirmation(
                "Confirmar cancelación",
                window(),
                "¿Desea cancelar la cita " + id + "?\n\nEsta acción pondrá el estado en 'C'."
        );
        if (!ok) return;

        Respuesta r = citaService.cancelarCita(id, motivo);
        if (!r.getEstado()) { errorDesdeRespuesta("Cancelar cita", r); return; }

        mensaje.showModal(Alert.AlertType.INFORMATION, "Cancelar cita", window(), r.getMensaje());
        cargarCitas();

        CitaDTO cancelada = (CitaDTO) r.getResultado("Cita");
        if (cancelada != null) cargarCitaEnFormulario(cancelada);
        else limpiarFormulario();
    }

    @FXML
    private void onActionBtnCalendario() {
        if (!validarCalendario()) {
            mensaje.showModal(Alert.AlertType.WARNING, "Calendario", window(), "Seleccione un médico.");
            return;
        }

        MedicoDTO medico = cbMedico.getValue();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/meduna/view/CitaCalendario.fxml"));
            Parent r = loader.load();

            CitaCalendarioController ctrl = loader.getController();
            ctrl.inicializarConMedico(medico, feriadoHelper.getFeriadosPorAnio());

            Stage stage = new Stage();
            stage.setTitle("Agenda del médico");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(r));
            stage.showAndWait();

            cargarCitas();
        } catch (Exception ex) {
            ex.printStackTrace();
            mensaje.showModal(Alert.AlertType.ERROR, "Calendario", window(),
                    "Error abriendo calendario: " + ex.getMessage());
        }
    }

    private boolean validarGuardar() {
        List<Node> inv = new ArrayList<>();
        req(inv, cbMedico.getValue() != null && cbMedico.getValue().getIdMedico() != null, cbMedico);
        req(inv, cbPaciente.getValue() != null && cbPaciente.getValue().getIdPaciente() != null, cbPaciente);
        req(inv, dpFecha.getValue() != null, dpFecha);
        req(inv, cbHoraInicio.getValue() != null, cbHoraInicio);
        req(inv, cbHoraFin.getValue() != null, cbHoraFin);
        req(inv, txtMotivo.getText() != null && !txtMotivo.getText().isBlank(), txtMotivo);
        req(inv, cbEstado.getValue() != null && !cbEstado.getValue().isBlank(), cbEstado);
        return validate(inv);
    }

    private boolean validarCancelar() {
        List<Node> inv = new ArrayList<>();
        req(inv, txtIdCita.getText() != null && !txtIdCita.getText().isBlank(), txtIdCita);
        return validate(inv);
    }

    private boolean validarCalendario() {
        List<Node> inv = new ArrayList<>();
        req(inv, cbMedico.getValue() != null && cbMedico.getValue().getIdMedico() != null, cbMedico);
        return validate(inv);
    }

    private void limpiarFormulario() {
        txtIdCita.setText("");
        cbMedico.getSelectionModel().clearSelection();
        cbPaciente.getSelectionModel().clearSelection();
        dpFecha.setValue(null);
        cbHoraInicio.getSelectionModel().clearSelection();
        cbHoraFin.getSelectionModel().clearSelection();
        txtMotivo.setText("");
        cbEstado.getSelectionModel().select("P");
        txtCancelacion.setText("");
    }

    private void cargarCitaEnFormulario(CitaDTO c) {
        txtIdCita.setText(c.idCitaProperty().get());

        selectById(cbMedico, m -> m.getIdMedico() != null && m.getIdMedico().equals(c.getIdMedico()));
        selectById(cbPaciente, p -> p.getIdPaciente() != null && p.getIdPaciente().equals(c.getIdPaciente()));

        dpFecha.setValue(c.getFecha());
        cbHoraInicio.getSelectionModel().select(c.getHoraInicio());
        cbHoraFin.getSelectionModel().select(c.getHoraFin());

        txtMotivo.setText(c.getMotivo());
        cbEstado.getSelectionModel().select(c.getEstado() != null && !c.getEstado().isBlank() ? c.getEstado() : "P");
        txtCancelacion.setText(c.getCancelacion());
    }

    private CitaDTO leerFormulario() {
        CitaDTO dto = new CitaDTO();

        if (txtIdCita.getText() != null && !txtIdCita.getText().isBlank()) {
            dto.setIdCita(Long.valueOf(txtIdCita.getText().trim()));
        }

        MedicoDTO m = cbMedico.getValue();
        if (m != null && m.getIdMedico() != null) dto.setIdMedico(m.getIdMedico());

        PacienteDTO p = cbPaciente.getValue();
        if (p != null && p.getIdPaciente() != null) dto.setIdPaciente(p.getIdPaciente());

        dto.setFecha(dpFecha.getValue());
        dto.setHoraInicio(cbHoraInicio.getValue());
        dto.setHoraFin(cbHoraFin.getValue());
        dto.setMotivo(txtMotivo.getText());

        String estado = cbEstado.getValue();
        dto.setEstado(estado != null ? estado : "P");

        dto.setCancelacion(txtCancelacion.getText());
        return dto;
    }

    private void seleccionarEnTabla(String idCita) {
        if (idCita == null) return;
        for (CitaDTO c : tbCitas.getItems()) {
            if (c != null && idCita.equals(c.idCitaProperty().get())) {
                tbCitas.getSelectionModel().select(c);
                tbCitas.scrollTo(c);
                return;
            }
        }
    }
}
