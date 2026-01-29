package cr.ac.una.meduna.controller;

import cr.ac.una.meduna.model.CitaDTO;
import cr.ac.una.meduna.model.HolidayDTO;
import cr.ac.una.meduna.model.MedicoDTO;
import cr.ac.una.meduna.model.PacienteDTO;
import cr.ac.una.meduna.service.CitaService;
import cr.ac.una.meduna.service.PacienteService;
import cr.ac.una.meduna.util.Mensaje;
import cr.ac.una.meduna.util.Respuesta;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class CitaCalendarioController implements Initializable {

    @FXML
    private Label lblMes;
    @FXML
    private Label lblFechaSeleccionada;
    @FXML
    private GridPane gridCalendario;

    @FXML
    private TableView<CitaDTO> tbvCitasDia;
    @FXML
    private TableColumn<CitaDTO, String> colPaciente;
    @FXML
    private TableColumn<CitaDTO, String> colHoraIni;
    @FXML
    private TableColumn<CitaDTO, String> colHoraFin;
    @FXML
    private TableColumn<CitaDTO, Void> colAccion;

    private final CitaService citaService = new CitaService();
    private final PacienteService pacienteService = new PacienteService();

    private MedicoDTO medicoSeleccionado;

    private YearMonth mesActual = YearMonth.now();
    private LocalDate fechaSeleccionada = LocalDate.now();

    private Map<LocalDate, List<CitaDTO>> citasPorDia = new HashMap<>();
    private Map<Long, String> pacienteNombrePorId = new HashMap<>();

    private Map<Integer, Map<LocalDate, HolidayDTO>> feriadosPorAnio = new HashMap<>();

    private final ObservableList<CitaDTO> citasDelDia = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        tbvCitasDia.setItems(citasDelDia);

        colHoraIni.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getHoraInicio() != null ? cd.getValue().getHoraInicio().toString() : ""
        ));
        colHoraFin.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getHoraFin() != null ? cd.getValue().getHoraFin().toString() : ""
        ));

        colPaciente.setCellValueFactory(cd -> {
            Long idPac = cd.getValue().getIdPaciente();
            String nombre = (idPac != null) ? pacienteNombrePorId.getOrDefault(idPac, String.valueOf(idPac)) : "";
            return new SimpleStringProperty(nombre);
        });

        configurarColumnaAccion();

    }

    public void inicializarConMedico(MedicoDTO medico,
            Map<Integer, Map<LocalDate, HolidayDTO>> feriadosCacheOpcional) {

        this.medicoSeleccionado = medico;
        if (feriadosCacheOpcional != null) {
            this.feriadosPorAnio = feriadosCacheOpcional;
        }

        this.mesActual = YearMonth.now();
        this.fechaSeleccionada = LocalDate.now();

        cargarPacientesCache();          
        cargarCitasDelMedicoMes();       
        renderCalendario();              
        seleccionarDia(fechaSeleccionada);
    }

    private void cargarPacientesCache() {
        Respuesta r = pacienteService.getPacientes();
        if (!r.getEstado()) {
            return;
        }

        @SuppressWarnings("unchecked")
        List<PacienteDTO> pacientes = (List<PacienteDTO>) r.getResultado("Pacientes");
        if (pacientes == null) {
            return;
        }

        pacienteNombrePorId = pacientes.stream().collect(Collectors.toMap(
                PacienteDTO::getIdPaciente,
                p -> p.getNombre() + " " + p.getApellido(),
                (a, b) -> a
        ));
    }

    private void cargarCitasDelMedicoMes() {
        if (medicoSeleccionado == null || medicoSeleccionado.getIdMedico() == null) {
            return;
        }

        Respuesta r = citaService.getCitasByMedico(medicoSeleccionado.getIdMedico());
        if (!r.getEstado()) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Calendario", null, r.getMensaje());
            return;
        }

        @SuppressWarnings("unchecked")
        List<CitaDTO> todas = (List<CitaDTO>) r.getResultado("Citas");
        if (todas == null) {
            todas = List.of();
        }

        List<CitaDTO> delMes = todas.stream()
                .filter(c -> c.getFecha() != null)
                .filter(c -> YearMonth.from(c.getFecha()).equals(mesActual))
                .collect(Collectors.toList());

        citasPorDia = delMes.stream().collect(Collectors.groupingBy(CitaDTO::getFecha));
    }

    private void renderCalendario() {
        gridCalendario.getChildren().clear();

        String mesNombre = mesActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "CR")).toUpperCase();
        lblMes.setText(mesNombre + " " + mesActual.getYear());

        LocalDate first = mesActual.atDay(1);

        int offset = first.getDayOfWeek().getValue() % 7;

        int daysInMonth = mesActual.lengthOfMonth();

        int cellIndex = 0;

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {

                int dayNumber = cellIndex - offset + 1;

                Button btn = new Button();
                btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                btn.setPrefHeight(70);
                btn.setStyle("-fx-background-radius: 8; -fx-background-color: white;");

                if (dayNumber < 1 || dayNumber > daysInMonth) {
                    btn.setDisable(true);
                    btn.setText("");
                    btn.setStyle("-fx-background-color: transparent;");
                } else {
                    LocalDate date = mesActual.atDay(dayNumber);
                    btn.setText(String.valueOf(dayNumber));

                    if (esFeriado(date)) {
                        btn.setDisable(true);
                        btn.setStyle("-fx-background-radius: 8; -fx-background-color: #ffdddd; -fx-text-fill: #b00020; -fx-font-weight: bold;");
                        btn.setTooltip(new Tooltip("Feriado"));
                    }

                    int cant = citasPorDia.getOrDefault(date, List.of()).size();
                    if (cant > 0) {
                        btn.setStyle(btn.getStyle() + "; -fx-border-color: #3399ff; -fx-border-width: 2;");
                        btn.setTooltip(new Tooltip("Citas: " + cant));
                    }

                    if (date.equals(fechaSeleccionada)) {
                        btn.setStyle(btn.getStyle() + "; -fx-background-color: #d6ecff;");
                    }

                    btn.setOnAction(e -> seleccionarDia(date));
                }

                gridCalendario.add(btn, col, row);
                cellIndex++;
            }
        }
    }

    private void seleccionarDia(LocalDate date) {
        this.fechaSeleccionada = date;
        lblFechaSeleccionada.setText(date.toString());

        citasDelDia.setAll(citasPorDia.getOrDefault(date, List.of()));

        renderCalendario();
    }

    private boolean esFeriado(LocalDate date) {
        Map<LocalDate, HolidayDTO> map = feriadosPorAnio.get(date.getYear());
        return map != null && map.containsKey(date);
    }

    private void configurarColumnaAccion() {
        colAccion.setCellFactory(tc -> new TableCell<>() {

            private final MenuButton menu = new MenuButton("Acciones");

            private final MenuItem miProgramar = new MenuItem("P - Programada");
            private final MenuItem miAtender = new MenuItem("A - Atendida");
            private final MenuItem miFinalizar = new MenuItem("F - Finalizada");
            private final MenuItem miSuspender = new MenuItem("S - Suspendida");
            private final MenuItem miCancelar = new MenuItem("C - Cancelada");

            {
                menu.getItems().addAll(miProgramar, miAtender, miFinalizar, miSuspender, new SeparatorMenuItem(), miCancelar);

                miProgramar.setOnAction(e -> cambiarEstado(getCitaFila(), "P"));
                miAtender.setOnAction(e -> cambiarEstado(getCitaFila(), "A"));
                miFinalizar.setOnAction(e -> cambiarEstado(getCitaFila(), "F"));
                miSuspender.setOnAction(e -> cambiarEstado(getCitaFila(), "S"));
                miCancelar.setOnAction(e -> cancelarConMotivo(getCitaFila()));
            }

            private CitaDTO getCitaFila() {
                return getTableView().getItems().get(getIndex());
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : menu);
            }
        });
    }

    private void cambiarEstado(CitaDTO cita, String estado) {
        if (cita == null || cita.getIdCita() == null) {
            return;
        }

        Respuesta r = citaService.cambiarEstadoCita(cita.getIdCita(), estado, null);
        if (!r.getEstado()) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Agenda", null, r.getMensaje());
            return;
        }

        new Mensaje().showModal(Alert.AlertType.INFORMATION, "Agenda", null, "Cita actualizada con éxito.");

        refrescarDespuesDeCambio();
    }

    private void cancelarConMotivo(CitaDTO cita) {
        if (cita == null || cita.getIdCita() == null) {
            return;
        }

        TextInputDialog d = new TextInputDialog();
        d.setTitle("Cancelar cita");
        d.setHeaderText("Ingrese el motivo de cancelación");
        d.setContentText("Motivo:");

        Optional<String> motivoOpt = d.showAndWait();
        if (motivoOpt.isEmpty()) {
            return;
        }

        String motivo = motivoOpt.get().trim();
        if (motivo.isBlank()) {
            new Mensaje().showModal(Alert.AlertType.WARNING, "Agenda", null, "Debe indicar un motivo.");
            return;
        }

        Respuesta r = citaService.cambiarEstadoCita(cita.getIdCita(), "C", motivo);
        if (!r.getEstado()) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Agenda", null, r.getMensaje());
            return;
        }

        new Mensaje().showModal(Alert.AlertType.INFORMATION, "Agenda", null, "Cita cancelada con éxito.");

        refrescarDespuesDeCambio();
    }

    private void refrescarDespuesDeCambio() {
        cargarCitasDelMedicoMes();
        renderCalendario();
        seleccionarDia(fechaSeleccionada);
    }

    private void cancelarCitaDesdeCalendario(CitaDTO cita) {
        if (cita == null || cita.getIdCita() == null) {
            return;
        }

        TextInputDialog d = new TextInputDialog();
        d.setTitle("Cancelar cita");
        d.setHeaderText("Motivo de cancelación");
        d.setContentText("Motivo:");
        Optional<String> motivoOpt = d.showAndWait();
        if (motivoOpt.isEmpty()) {
            return;
        }

        String motivo = motivoOpt.get().trim();

        Respuesta r = citaService.cancelarCita(cita.getIdCita(), motivo);
        if (!r.getEstado()) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Cancelar", null, r.getMensaje());
            return;
        }

        cargarCitasDelMedicoMes();
        renderCalendario();
        seleccionarDia(fechaSeleccionada);
    }

    @FXML
    private void onActionPrevMes() {
        mesActual = mesActual.minusMonths(1);
        cargarCitasDelMedicoMes();
        renderCalendario();
        seleccionarDia(mesActual.atDay(1));
    }

    @FXML
    private void onActionNextMes() {
        mesActual = mesActual.plusMonths(1);
        cargarCitasDelMedicoMes();
        renderCalendario();
        seleccionarDia(mesActual.atDay(1));
    }

    @FXML
    private void onActionCerrar() {
        lblMes.getScene().getWindow().hide();
    }
}
