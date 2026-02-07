package cr.ac.una.meduna.controller;

import cr.ac.una.meduna.model.EspecialidadDTO;
import cr.ac.una.meduna.model.EstadisticaDTO;
import cr.ac.una.meduna.model.MedicoDTO;
import cr.ac.una.meduna.service.EspecialidadService;
import cr.ac.una.meduna.service.EstadisticaService;
import cr.ac.una.meduna.service.MedicoService;
import cr.ac.una.meduna.util.Mensaje;
import cr.ac.una.meduna.util.Respuesta;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;

/**
 * Controlador gestión estadísticas
 *
 * @author Angie Marks
 * @author Juan Calderón
 */

public class EstadisticaController extends Controller implements Initializable {

    @FXML
    private DatePicker dpDesde;
    @FXML
    private DatePicker dpHasta;
    @FXML
    private ComboBox<MedicoDTO> cbMedico;
    @FXML
    private ComboBox<EspecialidadDTO> cbEspecialidad;
    @FXML
    private ComboBox<String> cbReporte;

    @FXML
    private TableView<EstadisticaDTO> tbvStats;
    @FXML
    private TableColumn<EstadisticaDTO, String> colCategoria;
    @FXML
    private TableColumn<EstadisticaDTO, Number> colCantidad;

    @FXML
    private StackPane chartContainer;
    @FXML
    private Label lblTituloTabla;
    @FXML
    private Label lblTituloChart;

    private final EstadisticaService estadisticaService = new EstadisticaService();
    private final MedicoService medicoService = new MedicoService();
    private final EspecialidadService especialidadService = new EspecialidadService();

    private final ObservableList<EstadisticaDTO> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        tbvStats.setItems(data);

        colCategoria.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCategoria()));
        colCantidad.setCellValueFactory(cd -> new SimpleLongProperty(cd.getValue().getCantidad()));

        dpHasta.setValue(LocalDate.now());
        dpDesde.setValue(LocalDate.now().minusMonths(1));

        cargarCombos();
        cargarComboReportes();

        cbReporte.getSelectionModel().selectFirst();
        onActionAplicar();
    }

    private void cargarCombos() {
        Respuesta rM = medicoService.getMedicos();
        if (rM.getEstado()) {
            @SuppressWarnings("unchecked")
            List<MedicoDTO> medicos = (List<MedicoDTO>) rM.getResultado("Médico"); 
            if (medicos != null) {
                cbMedico.setItems(FXCollections.observableArrayList(medicos));
                cbMedico.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(MedicoDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? "" : item.getNombre() + " " + item.getApellido());
                    }
                });
                cbMedico.setButtonCell(cbMedico.getCellFactory().call(null));
            }
        }

        Respuesta rE = especialidadService.getEspecialidades();
        if (rE.getEstado()) {
            @SuppressWarnings("unchecked")
            List<EspecialidadDTO> espec = (List<EspecialidadDTO>) rE.getResultado("Especialidades");
            if (espec != null) {
                cbEspecialidad.setItems(FXCollections.observableArrayList(espec));
                cbEspecialidad.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(EspecialidadDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? "" : item.getNombre());
                    }
                });
                cbEspecialidad.setButtonCell(cbEspecialidad.getCellFactory().call(null));
            }
        }
    }

    private void cargarComboReportes() {
        cbReporte.setItems(FXCollections.observableArrayList(
                "Citas por estado (Pie)",
                "Citas atendidas por médico (Barras)",
                "Citas por especialidad (Barras)",
                "Distribución de citas por mes (Barras)",
                "Pacientes más frecuentes TOP 10 (Barras)"
        ));
    }

    @FXML
    public void onActionAplicar() {

        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();

        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            new Mensaje().showModal(Alert.AlertType.WARNING, "Estadísticas", null, "Rango de fechas inválido.");
            return;
        }

        Long idMedico = cbMedico.getValue() != null ? cbMedico.getValue().getIdMedico() : null;
        Long idEspecialidad = cbEspecialidad.getValue() != null ? cbEspecialidad.getValue().getIdEspecialidad() : null;

        String reporte = cbReporte.getValue();
        if (reporte == null) {
            return;
        }

        Respuesta r;

        switch (reporte) {
            case "Citas por estado (Pie)" -> {
                lblTituloTabla.setText("Citas por estado");
                lblTituloChart.setText("Distribución por estado");
                r = estadisticaService.getCitasPorEstado(desde, hasta, idMedico, idEspecialidad);
                if (r.getEstado()) {
                    setData(r);
                    mostrarPieChart();
                } else {
                    showError(r);
                }
            }
            case "Citas atendidas por médico (Barras)" -> {
                lblTituloTabla.setText("Citas atendidas por médico");
                lblTituloChart.setText("Atendidas por médico");
                r = estadisticaService.getCitasAtendidasPorMedico(desde, hasta, idEspecialidad, List.of("A", "F"));
                if (r.getEstado()) {
                    setData(r);
                    mostrarBarChart("Médico", "Citas");
                } else {
                    showError(r);
                }
            }
            case "Citas por especialidad (Barras)" -> {
                lblTituloTabla.setText("Citas por especialidad");
                lblTituloChart.setText("Citas por especialidad");
                r = estadisticaService.getCitasPorEspecialidad(desde, hasta, idMedico);
                if (r.getEstado()) {
                    setData(r);
                    mostrarBarChart("Especialidad", "Citas");
                } else {
                    showError(r);
                }
            }
            case "Distribución de citas por mes (Barras)" -> {
                lblTituloTabla.setText("Distribución por mes");
                lblTituloChart.setText("Citas por mes");
                r = estadisticaService.getCitasPorMes(desde, hasta, idMedico, idEspecialidad);
                if (r.getEstado()) {
                    setData(r);
                    mostrarBarChart("Mes", "Citas");
                } else {
                    showError(r);
                }
            }
            case "Pacientes más frecuentes TOP 10 (Barras)" -> {
                lblTituloTabla.setText("Pacientes más frecuentes");
                lblTituloChart.setText("Top 10 pacientes");
                r = estadisticaService.getPacientesMasFrecuentes(desde, hasta, idMedico, idEspecialidad, 10);
                if (r.getEstado()) {
                    setData(r);
                    mostrarBarChart("Paciente", "Citas");
                } else {
                    showError(r);
                }
            }
            default -> {
            }
        }
    }

    private void setData(Respuesta r) {
        @SuppressWarnings("unchecked")
        List<EstadisticaDTO> lista = (List<EstadisticaDTO>) r.getResultado("Estadisticas");
        data.setAll(lista != null ? lista : List.of());
    }

    private void showError(Respuesta r) {
        new Mensaje().showModal(Alert.AlertType.ERROR, "Estadísticas", null, r.getMensaje());
    }

    private void mostrarPieChart() {
        chartContainer.getChildren().clear();

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (EstadisticaDTO e : data) {
            pieData.add(new PieChart.Data(formatearEstadoSiAplica(e.getCategoria()), e.getCantidad()));
        }

        PieChart pie = new PieChart(pieData);
        pie.setLegendVisible(true);
        pie.setLabelsVisible(true);

        chartContainer.getChildren().add(pie);
    }

    private void mostrarBarChart(String xLabel, String yLabel) {
        chartContainer.getChildren().clear();

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);

        BarChart<String, Number> bar = new BarChart<>(xAxis, yAxis);
        bar.setLegendVisible(false);

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (EstadisticaDTO e : data) {
            serie.getData().add(new XYChart.Data<>(e.getCategoria(), e.getCantidad()));
        }

        bar.getData().add(serie);
        chartContainer.getChildren().add(bar);
    }

    private String formatearEstadoSiAplica(String estado) {
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

}
