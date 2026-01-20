/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.meduna.controller;

import cr.ac.una.meduna.model.CitaDTO;
import cr.ac.una.meduna.model.MedicoDTO;
import cr.ac.una.meduna.model.PacienteDTO;
import cr.ac.una.meduna.service.CitaService;
import cr.ac.una.meduna.service.HolidayService;
import cr.ac.una.meduna.service.MedicoService;
import cr.ac.una.meduna.service.PacienteService;
import cr.ac.una.meduna.util.Respuesta;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

/**
 * FXML Controller class
 *
 * @author juans
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

    private final CitaService citaService = new CitaService();
    private final MedicoService medicoService = new MedicoService();
    private final PacienteService pacienteService = new PacienteService();

    private final Map<Long, String> medicoNombreMap = new HashMap<>();
    private final Map<Long, String> pacienteNombreMap = new HashMap<>();

    private List<CitaDTO> citasOcupadas = new ArrayList<>();

    private final LocalTime HORA_APERTURA = LocalTime.of(7, 0);
    private final LocalTime HORA_CIERRE = LocalTime.of(18, 0); // cierre real (fin máximo)
    private final int BLOQUE_MINUTOS = 30;

    private final HolidayService holidayService = new HolidayService();
    private final java.util.Map<Integer, java.util.Map<java.time.LocalDate, cr.ac.una.meduna.model.HolidayDTO>> feriadosPorAnio = new java.util.HashMap<>();

    private final ObservableList<CitaDTO> citas = FXCollections.observableArrayList();

    private String formatearEstado(String estado) {
        return switch (estado) {
            case "P" ->
                "P - Programada";
            case "C" ->
                "C - Cancelada";
            case "A" ->
                "A - Atendida";
            case "F" ->
                "F - Finalizada";
            case "S" ->
                "S - Suspendida";
            default ->
                estado;
        };
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        tbCitas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configurarCombos();

        cbMedico.valueProperty().addListener((obs, old, neu) -> actualizarDisponibilidad());
        dpFecha.valueProperty().addListener((obs, old, neu) -> actualizarDisponibilidad());

        // cuando el usuario elige hora inicio, recalculamos posibles horas fin
        cbHoraInicio.valueProperty().addListener((obs, old, neu) -> actualizarHorasFinDisponibles());

        tbCitas.setItems(citas);

        tbCitas.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                cargarCitaEnFormulario(sel);
            }
        });

        cargarFeriadosAsync(java.time.LocalDate.now().getYear());
        aplicarDayCellFactory();

        dpFecha.valueProperty().addListener((obs, old, neu) -> {
            if (neu != null) {
                cargarFeriadosAsync(neu.getYear());
            }
        });

        cargarMedicos();
        cargarPacientes();
        cargarCitas();

        limpiarFormulario();

        // ✅ refrescar combos cuando la ventana vuelve a enfocarse
        Platform.runLater(() -> {
            Window w = root.getScene() != null ? root.getScene().getWindow() : null;
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

    // ---------------------------
    // Configuración UI
    // ---------------------------
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

        // ===== ESTADO =====
        cbEstado.setItems(FXCollections.observableArrayList("P", "A", "F", "S", "C"));
        cbEstado.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatearEstado(item));
            }
        });
        cbEstado.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatearEstado(item));
            }
        });
        cbEstado.getSelectionModel().select("P");

        // Horas base (se actualizan con filtros)
        cbHoraInicio.setItems(FXCollections.observableArrayList(
                generarHoras(HORA_APERTURA, HORA_CIERRE.minusMinutes(BLOQUE_MINUTOS), BLOQUE_MINUTOS)
        ));
        cbHoraFin.getItems().clear();

        // ✅ LISTENER: cada vez que cambie la hora inicio -> hora fin = inicio + 1 hora
        cbHoraInicio.valueProperty().addListener((obs, old, neu) -> {
            if (neu != null) {
                cbHoraFin.setItems(FXCollections.observableArrayList(neu.plusHours(1)));
                cbHoraFin.getSelectionModel().selectFirst();
            } else {
                cbHoraFin.getItems().clear();
            }
        });

        // ===== MÉDICO =====
        cbMedico.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(MedicoDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? ""
                        : (item.getIdMedico() + " - " + item.getNombre() + " " + item.getApellido()));
            }
        });

        cbMedico.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(MedicoDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? ""
                        : (item.getIdMedico() + " - " + item.getNombre() + " " + item.getApellido()));
            }
        });

        // ===== PACIENTE =====
        cbPaciente.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(PacienteDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? ""
                        : (item.getIdPaciente() + " - " + item.getNombre() + " " + item.getApellido()));
            }
        });

        cbPaciente.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(PacienteDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? ""
                        : (item.getIdPaciente() + " - " + item.getNombre() + " " + item.getApellido()));
            }
        });
    }

    private void actualizarHorasDisponibles() {

        cbHoraInicio.getItems().clear();
        cbHoraFin.getItems().clear();

        MedicoDTO medico = cbMedico.getValue();
        LocalDate fecha = dpFecha.getValue();

        if (medico == null || medico.getIdMedico() == null || fecha == null) {
            // si no hay médico o fecha todavía, no mostramos horas
            return;
        }

        // 1) Generar todas las horas posibles de inicio
        List<LocalTime> todas = generarHoras(LocalTime.of(7, 0), LocalTime.of(17, 0), 30);

        // 2) Consultar citas ocupadas del médico en esa fecha
        Respuesta r = citaService.getCitasByMedicoFecha(medico.getIdMedico(), fecha);
        if (!r.getEstado()) {
            mostrarError("Citas", r.getMensaje(), r.getMensajeInterno());
            return;
        }

        @SuppressWarnings("unchecked")
        List<CitaDTO> ocupadas = (List<CitaDTO>) r.getResultado("Citas");
        if (ocupadas == null) {
            ocupadas = List.of();
        }

        // 3) Construir un set de inicios ocupados
        Set<LocalTime> horasOcupadas = new HashSet<>();
        for (CitaDTO c : ocupadas) {
            if (c.getHoraInicio() != null) {
                horasOcupadas.add(c.getHoraInicio());
            }
        }

        // 4) Filtrar solo disponibles:
        //    Disponible si el inicio NO está ocupado y además el fin (inicio+1h) no se pasa del rango.
        List<LocalTime> disponibles = new ArrayList<>();
        for (LocalTime inicio : todas) {
            LocalTime fin = inicio.plusHours(1); // duración 1 hora
            boolean finDentro = !fin.isAfter(LocalTime.of(18, 0)); // ejemplo: última cita inicia 17:00 y termina 18:00
            if (!horasOcupadas.contains(inicio) && finDentro) {
                disponibles.add(inicio);
            }
        }

        cbHoraInicio.setItems(FXCollections.observableArrayList(disponibles));

        // opcional: seleccionar primera disponible
        if (!disponibles.isEmpty()) {
            cbHoraInicio.getSelectionModel().selectFirst();
            cbHoraFin.setItems(FXCollections.observableArrayList(cbHoraInicio.getValue().plusHours(1)));
            cbHoraFin.getSelectionModel().selectFirst();
        }
    }

    private void actualizarDisponibilidad() {

        cbHoraInicio.getItems().clear();
        cbHoraFin.getItems().clear();
        citasOcupadas.clear();

        MedicoDTO medico = cbMedico.getValue();
        LocalDate fecha = dpFecha.getValue();

        if (medico == null || medico.getIdMedico() == null || fecha == null) {
            // sin médico o sin fecha, no filtramos (o podrías dejar vacío)
            cbHoraInicio.setItems(FXCollections.observableArrayList(
                    generarHoras(HORA_APERTURA, HORA_CIERRE.minusMinutes(BLOQUE_MINUTOS), BLOQUE_MINUTOS)
            ));
            return;
        }

        Respuesta r = citaService.getCitasByMedicoFecha(medico.getIdMedico(), fecha);
        if (!r.getEstado()) {
            mostrarError("Citas", r.getMensaje(), r.getMensajeInterno());
            return;
        }

        @SuppressWarnings("unchecked")
        List<CitaDTO> ocupadas = (List<CitaDTO>) r.getResultado("Citas");
        if (ocupadas != null) {
            citasOcupadas = ocupadas;
        }

        // Generar todas las posibles horas inicio
        List<LocalTime> todasInicio = generarHoras(
                HORA_APERTURA,
                HORA_CIERRE.minusMinutes(BLOQUE_MINUTOS),
                BLOQUE_MINUTOS
        );

        // Filtrar: un inicio es válido si existe al menos UN fin posible sin chocar
        List<LocalTime> inicioDisponibles = new ArrayList<>();
        for (LocalTime ini : todasInicio) {
            if (existeAlgunFinValidoParaInicio(ini)) {
                inicioDisponibles.add(ini);
            }
        }

        cbHoraInicio.setItems(FXCollections.observableArrayList(inicioDisponibles));

        if (!inicioDisponibles.isEmpty()) {
            cbHoraInicio.getSelectionModel().selectFirst();
            actualizarHorasFinDisponibles();
        }
    }

    private void actualizarHorasFinDisponibles() {

        cbHoraFin.getItems().clear();

        LocalTime ini = cbHoraInicio.getValue();
        if (ini == null) {
            return;
        }

        // posibles fin: desde ini+30 hasta cierre, en bloques de 30
        List<LocalTime> posiblesFin = generarHoras(
                ini.plusMinutes(BLOQUE_MINUTOS),
                HORA_CIERRE,
                BLOQUE_MINUTOS
        );

        List<LocalTime> finValidos = new ArrayList<>();
        for (LocalTime fin : posiblesFin) {
            if (!hayConflicto(ini, fin)) {
                finValidos.add(fin);
            } else {
                // si ya choca en este fin, normalmente cualquier fin mayor también chocará
                // (porque se alarga el rango), así que podemos cortar para optimizar:
                break;
            }
        }

        cbHoraFin.setItems(FXCollections.observableArrayList(finValidos));
        if (!finValidos.isEmpty()) {
            cbHoraFin.getSelectionModel().selectFirst();
        }
    }

    private boolean existeAlgunFinValidoParaInicio(LocalTime ini) {
        List<LocalTime> posiblesFin = generarHoras(
                ini.plusMinutes(BLOQUE_MINUTOS),
                HORA_CIERRE,
                BLOQUE_MINUTOS
        );

        for (LocalTime fin : posiblesFin) {
            if (!hayConflicto(ini, fin)) {
                return true;
            }
        }
        return false;
    }

    private boolean hayConflicto(LocalTime ini, LocalTime fin) {
        for (CitaDTO c : citasOcupadas) {
            if (c.getHoraInicio() == null || c.getHoraFin() == null) {
                continue;
            }

            LocalTime iniExist = c.getHoraInicio();
            LocalTime finExist = c.getHoraFin();

            // choque si: ini < finExist AND fin > iniExist
            if (ini.isBefore(finExist) && fin.isAfter(iniExist)) {
                return true;
            }
        }
        return false;
    }

    private List<LocalTime> generarHoras(LocalTime desde, LocalTime hasta, int minutosStep) {
        List<LocalTime> lista = new ArrayList<>();
        LocalTime t = desde;
        while (!t.isAfter(hasta)) {
            lista.add(t);
            t = t.plusMinutes(minutosStep);
        }
        return lista;
    }

    private void cargarFeriadosAsync(int year) {

        if (feriadosPorAnio.containsKey(year)) {
            aplicarDayCellFactory();
            return;
        }

        javafx.concurrent.Task<java.util.List<cr.ac.una.meduna.model.HolidayDTO>> task
                = new javafx.concurrent.Task<>() {
            @Override
            protected java.util.List<cr.ac.una.meduna.model.HolidayDTO> call() throws Exception {
                return holidayService.getHolidaysCR(year);
            }
        };

        task.setOnSucceeded(e -> {
            java.util.Map<java.time.LocalDate, cr.ac.una.meduna.model.HolidayDTO> map = new java.util.HashMap<>();
            for (cr.ac.una.meduna.model.HolidayDTO h : task.getValue()) {
                if (h.getDate() != null && !h.getDate().isBlank()) {
                    map.put(java.time.LocalDate.parse(h.getDate()), h);
                }
            }
            feriadosPorAnio.put(year, map);
            aplicarDayCellFactory(); // refresca
        });

        task.setOnFailed(e -> {
            System.err.println("Error feriados: " + task.getException());
            feriadosPorAnio.put(year, java.util.Map.of()); // cache vacío para no spamear
            aplicarDayCellFactory();
        });

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private void aplicarDayCellFactory() {
        dpFecha.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date == null) {
                    setText(null);
                    setDisable(false);
                    setStyle("");
                    setTooltip(null);
                    return;
                }

                var feriado = getFeriado(date);
                if (feriado != null) {
                    setDisable(true); // ✅ evita agendar en feriado
                    setStyle("-fx-background-color: #ffdddd; -fx-text-fill: #b00020; -fx-font-weight: bold;");
                    setTooltip(new javafx.scene.control.Tooltip(feriado.getLocalName()));
                } else {
                    setTooltip(null);
                    setStyle("");
                }
            }
        });
    }

    private cr.ac.una.meduna.model.HolidayDTO getFeriado(java.time.LocalDate date) {
        var map = feriadosPorAnio.get(date.getYear());
        return map != null ? map.get(date) : null;
    }

    // ---------------------------
    // Cargas desde Services
    // ---------------------------
    private void cargarCitas() {
        Respuesta r = citaService.getCitas();
        if (!r.getEstado()) {
            mostrarError("Citas", r.getMensaje(), r.getMensajeInterno());
            return;
        }
        @SuppressWarnings("unchecked")
        List<CitaDTO> lista = (List<CitaDTO>) r.getResultado("Citas");
        citas.setAll(lista != null ? lista : List.of());
    }

    private void cargarMedicos() {
        Respuesta r = medicoService.getMedicos();
        if (!r.getEstado()) {
            mostrarError("Médicos", r.getMensaje(), r.getMensajeInterno());
            return;
        }

        @SuppressWarnings("unchecked")
        List<MedicoDTO> lista = (List<MedicoDTO>) (r.getResultado("Médicos") != null ? r.getResultado("Médicos") : r.getResultado("Médico"));
        if (lista == null) {
            lista = List.of();
        }

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
        if (!r.getEstado()) {
            mostrarError("Pacientes", r.getMensaje(), r.getMensajeInterno());
            return;
        }

        @SuppressWarnings("unchecked")
        List<PacienteDTO> lista = (List<PacienteDTO>) r.getResultado("Pacientes");
        if (lista == null) {
            lista = List.of();
        }

        cbPaciente.setItems(FXCollections.observableArrayList(lista));

        pacienteNombreMap.clear();
        for (PacienteDTO p : lista) {
            if (p != null && p.getIdPaciente() != null) {
                pacienteNombreMap.put(p.getIdPaciente(), p.getNombre() + " " + p.getApellido());
            }
        }

        tbCitas.refresh();
    }

    // ---------------------------
    // Acciones de botones
    // ---------------------------
    @FXML
    private void onNuevo(ActionEvent event) {
        tbCitas.getSelectionModel().clearSelection();
        limpiarFormulario();
    }

    @FXML
    private void onGuardar(ActionEvent event) {
        CitaDTO dto = leerFormulario();

        Respuesta r = citaService.guardarCita(dto);
        if (!r.getEstado()) {
            mostrarError("Guardar cita", r.getMensaje(), r.getMensajeInterno());
            return;
        }

        mostrarInfo("Guardar cita", r.getMensaje());
        cargarCitas();

        CitaDTO guardada = (CitaDTO) r.getResultado("Cita");
        if (guardada != null) {
            seleccionarEnTabla(guardada.getIdCita() != null ? guardada.getIdCita().toString() : null);
            cargarCitaEnFormulario(guardada);
        } else {
            limpiarFormulario();
        }
    }

    @FXML
    private void onCancelarCita(ActionEvent event) {
        String idTxt = txtIdCita.getText();
        if (idTxt == null || idTxt.isBlank()) {
            mostrarError("Cancelar", "Seleccione una cita de la tabla para cancelar.", "");
            return;
        }

        long id = Long.parseLong(idTxt);
        String motivo = txtCancelacion.getText();

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar cancelación");
        conf.setHeaderText("¿Desea cancelar la cita " + id + "?");
        conf.setContentText("Esta acción pondrá el estado en 'C'.");
        if (conf.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        Respuesta r = citaService.cancelarCita(id, motivo);
        if (!r.getEstado()) {
            mostrarError("Cancelar cita", r.getMensaje(), r.getMensajeInterno());
            return;
        }

        mostrarInfo("Cancelar cita", r.getMensaje());
        cargarCitas();

        CitaDTO cancelada = (CitaDTO) r.getResultado("Cita");
        if (cancelada != null) {
            cargarCitaEnFormulario(cancelada);
        } else {
            limpiarFormulario();
        }
    }

    // ---------------------------
    // Helpers Form
    // ---------------------------
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

        seleccionarMedicoPorId(c.getIdMedico());
        seleccionarPacientePorId(c.getIdPaciente());

        dpFecha.setValue(c.getFecha());
        cbHoraInicio.getSelectionModel().select(c.getHoraInicio());
        cbHoraFin.getSelectionModel().select(c.getHoraFin());

        txtMotivo.setText(c.getMotivo());
        cbEstado.getSelectionModel().select(c.getEstado() != null && !c.getEstado().isBlank() ? c.getEstado() : "P");
        txtCancelacion.setText(c.getCancelacion());
    }

    private void seleccionarMedicoPorId(Long idMedico) {
        if (idMedico == null) {
            return;
        }
        for (MedicoDTO m : cbMedico.getItems()) {
            if (m != null && m.getIdMedico() != null && m.getIdMedico().equals(idMedico)) {
                cbMedico.getSelectionModel().select(m);
                return;
            }
        }
    }

    private void seleccionarPacientePorId(Long idPaciente) {
        if (idPaciente == null) {
            return;
        }
        for (PacienteDTO p : cbPaciente.getItems()) {
            if (p != null && p.getIdPaciente() != null && p.getIdPaciente().equals(idPaciente)) {
                cbPaciente.getSelectionModel().select(p);
                return;
            }
        }
    }

    private CitaDTO leerFormulario() {
        CitaDTO dto = new CitaDTO();

        if (txtIdCita.getText() != null && !txtIdCita.getText().isBlank()) {
            dto.setIdCita(Long.valueOf(txtIdCita.getText().trim()));
        }

        MedicoDTO m = cbMedico.getSelectionModel().getSelectedItem();
        if (m != null && m.getIdMedico() != null) {
            dto.setIdMedico(m.getIdMedico());
        }

        PacienteDTO p = cbPaciente.getSelectionModel().getSelectedItem();
        if (p != null && p.getIdPaciente() != null) {
            dto.setIdPaciente(p.getIdPaciente());
        }

        dto.setFecha(dpFecha.getValue());
        dto.setHoraInicio(cbHoraInicio.getSelectionModel().getSelectedItem());
        dto.setHoraFin(cbHoraFin.getSelectionModel().getSelectedItem());
        dto.setMotivo(txtMotivo.getText());

        String estado = cbEstado.getSelectionModel().getSelectedItem();
        dto.setEstado(estado != null ? estado : "P");

        dto.setCancelacion(txtCancelacion.getText());
        return dto;
    }

    private void seleccionarEnTabla(String idCita) {
        if (idCita == null) {
            return;
        }
        for (CitaDTO c : tbCitas.getItems()) {
            if (c != null && idCita.equals(c.idCitaProperty().get())) {
                tbCitas.getSelectionModel().select(c);
                tbCitas.scrollTo(c);
                return;
            }
        }
    }

    // ---------------------------
    // Alerts
    // ---------------------------
    private void mostrarError(String titulo, String mensaje, String interno) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(mensaje);
        if (interno != null && !interno.isBlank()) {
            a.setContentText(interno);
        }
        a.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(mensaje);
        a.showAndWait();
    }
}
