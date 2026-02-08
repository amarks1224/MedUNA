package cr.ac.una.meduna.util;

import cr.ac.una.meduna.model.CitaDTO;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class CitaHorarioUtil {

    private CitaHorarioUtil() {}

    public static String formatearEstado(String estado) {
        return switch (estado) {
            case "P" -> "P - Programada";
            case "C" -> "C - Cancelada";
            case "A" -> "A - Atendida";
            case "F" -> "F - Finalizada";
            case "S" -> "S - Suspendida";
            default -> estado;
        };
    }

    public static List<LocalTime> generarHoras(LocalTime desde, LocalTime hasta, int minutosStep) {
        List<LocalTime> lista = new ArrayList<>();
        LocalTime t = desde;
        while (!t.isAfter(hasta)) {
            lista.add(t);
            t = t.plusMinutes(minutosStep);
        }
        return lista;
    }

    public static boolean hayConflicto(List<CitaDTO> citasOcupadas, LocalTime ini, LocalTime fin) {
        for (CitaDTO c : citasOcupadas) {
            if (c.getHoraInicio() == null || c.getHoraFin() == null) continue;

            LocalTime iniExist = c.getHoraInicio();
            LocalTime finExist = c.getHoraFin();

            if (ini.isBefore(finExist) && fin.isAfter(iniExist)) {
                return true;
            }
        }
        return false;
    }

    public static List<LocalTime> calcularInicioDisponibles(
            List<CitaDTO> citasOcupadas,
            LocalTime apertura,
            LocalTime cierre,
            int bloqueMinutos
    ) {
        List<LocalTime> todasInicio = generarHoras(apertura, cierre.minusMinutes(bloqueMinutos), bloqueMinutos);

        List<LocalTime> inicioDisponibles = new ArrayList<>();
        for (LocalTime ini : todasInicio) {
            if (existeAlgunFinValidoParaInicio(citasOcupadas, ini, cierre, bloqueMinutos)) {
                inicioDisponibles.add(ini);
            }
        }
        return inicioDisponibles;
    }

    public static List<LocalTime> calcularFinDisponibles(
            List<CitaDTO> citasOcupadas,
            LocalTime ini,
            LocalTime cierre,
            int bloqueMinutos
    ) {
        if (ini == null) return List.of();

        List<LocalTime> posiblesFin = generarHoras(ini.plusMinutes(bloqueMinutos), cierre, bloqueMinutos);

        List<LocalTime> finValidos = new ArrayList<>();
        for (LocalTime fin : posiblesFin) {
            if (!hayConflicto(citasOcupadas, ini, fin)) {
                finValidos.add(fin);
            } else {
                break;
            }
        }
        return finValidos;
    }

    private static boolean existeAlgunFinValidoParaInicio(
            List<CitaDTO> citasOcupadas,
            LocalTime ini,
            LocalTime cierre,
            int bloqueMinutos
    ) {
        List<LocalTime> posiblesFin = generarHoras(ini.plusMinutes(bloqueMinutos), cierre, bloqueMinutos);
        for (LocalTime fin : posiblesFin) {
            if (!hayConflicto(citasOcupadas, ini, fin)) return true;
        }
        return false;
    }
}
