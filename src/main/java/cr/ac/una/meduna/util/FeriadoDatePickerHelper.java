package cr.ac.una.meduna.util;

import cr.ac.una.meduna.model.HolidayDTO;
import cr.ac.una.meduna.service.HolidayService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.concurrent.Task;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;

public class FeriadoDatePickerHelper {

    private final HolidayService holidayService;
    private final Map<Integer, Map<LocalDate, HolidayDTO>> feriadosPorAnio = new HashMap<>();

    public FeriadoDatePickerHelper(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    public void attach(DatePicker dpFecha) {
        if (dpFecha == null) return;

        cargarFeriadosAsync(LocalDate.now().getYear(), dpFecha);
        aplicarDayCellFactory(dpFecha);

        dpFecha.valueProperty().addListener((obs, old, neu) -> {
            if (neu != null) {
                cargarFeriadosAsync(neu.getYear(), dpFecha);
            }
        });
    }

    private void cargarFeriadosAsync(int year, DatePicker dpFecha) {
        if (feriadosPorAnio.containsKey(year)) {
            aplicarDayCellFactory(dpFecha);
            return;
        }

        Task<List<HolidayDTO>> task = new Task<>() {
            @Override
            protected List<HolidayDTO> call() throws Exception {
                return holidayService.getHolidaysCR(year);
            }
        };

        task.setOnSucceeded(e -> {
            Map<LocalDate, HolidayDTO> map = new HashMap<>();
            for (HolidayDTO h : task.getValue()) {
                if (h.getDate() != null && !h.getDate().isBlank()) {
                    map.put(LocalDate.parse(h.getDate()), h);
                }
            }
            feriadosPorAnio.put(year, map);
            aplicarDayCellFactory(dpFecha);
        });

        task.setOnFailed(e -> {
            feriadosPorAnio.put(year, Map.of());
            aplicarDayCellFactory(dpFecha);
        });

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private void aplicarDayCellFactory(DatePicker dpFecha) {
        dpFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date == null) {
                    setDisable(false);
                    setStyle("");
                    setTooltip(null);
                    return;
                }

                HolidayDTO feriado = getFeriado(date);
                if (feriado != null) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffdddd; -fx-text-fill: #b00020; -fx-font-weight: bold;");
                    setTooltip(new Tooltip(feriado.getLocalName()));
                } else {
                    setStyle("");
                    setTooltip(null);
                }
            }
        });
    }

    private HolidayDTO getFeriado(LocalDate date) {
        Map<LocalDate, HolidayDTO> map = feriadosPorAnio.get(date.getYear());
        return map != null ? map.get(date) : null;
    }

    public Map<Integer, Map<LocalDate, HolidayDTO>> getFeriadosPorAnio() {
        return feriadosPorAnio;
    }
}
