package cr.ac.una.meduna.model;

import jakarta.json.bind.annotation.JsonbTransient;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.function.Predicate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CitaDTO {

    private StringProperty idCita;
    private StringProperty idMedico;
    private StringProperty idPaciente;
    private ObjectProperty<LocalDate> fecha;
    private ObjectProperty<LocalTime> horaInicio;
    private ObjectProperty<LocalTime> horaFin;
    private StringProperty motivo;
    private StringProperty estado;
    private StringProperty observaciones;
    private StringProperty diagnostico;
    private StringProperty cancelacion;
    private boolean modificado;

    public CitaDTO() {
        this.idCita        = new SimpleStringProperty("");
        this.idMedico      = new SimpleStringProperty("");
        this.idPaciente    = new SimpleStringProperty("");
        this.fecha         = new SimpleObjectProperty<>();
        this.horaInicio    = new SimpleObjectProperty<>();
        this.horaFin       = new SimpleObjectProperty<>();
        this.motivo        = new SimpleStringProperty("");
        this.estado        = new SimpleStringProperty("");
        this.observaciones = new SimpleStringProperty("");
        this.diagnostico   = new SimpleStringProperty("");
        this.cancelacion   = new SimpleStringProperty("");
        this.modificado    = false;
    }

    public CitaDTO(CitaEntity entity) {
        this();

        if (entity.getIdCita() != null) {
            this.idCita.set(entity.getIdCita().toString());
        }

        if (entity.getMedico() != null) {
            this.idMedico.set(entity.getMedico().getIdMedico().toString());
        }

        if (entity.getPaciente() != null) {
            this.idPaciente.set(entity.getPaciente().getIdPaciente().toString());
        }

        this.fecha.set(entity.getFecha());
        this.horaInicio.set(entity.getHoraInicio());
        this.horaFin.set(entity.getHoraFin());
        this.motivo.set(entity.getMotivo());
        this.estado.set(entity.getEstado());
        this.observaciones.set(entity.getObservaciones());
        this.diagnostico.set(entity.getDiagnostico());
        this.cancelacion.set(entity.getCancelacion());
        this.modificado = false;
    }

    public Long getIdCita() {
        if (esIdValido.test(idCita)) {
            return Long.valueOf(idCita.get());
        }
        return null;
    }

    public void setIdCita(Long idCita) {
        this.idCita.set(idCita.toString());
    }

    public Long getIdMedico() {
        if (esIdValido.test(idMedico)) {
            return Long.valueOf(idMedico.get());
        }
        return null;
    }

    public void setIdMedico(Long idMedico) {
        this.idMedico.set(idMedico.toString());
    }

    public Long getIdPaciente() {
        if (esIdValido.test(idPaciente)) {
            return Long.valueOf(idPaciente.get());
        }
        return null;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente.set(idPaciente.toString());
    }

    public LocalDate getFecha() {
        return fecha.get();
    }

    public void setFecha(LocalDate fecha) {
        this.fecha.set(fecha);
    }

    public LocalTime getHoraInicio() {
        return horaInicio.get();
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio.set(horaInicio);
    }

    public LocalTime getHoraFin() {
        return horaFin.get();
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin.set(horaFin);
    }

    public String getMotivo() {
        return motivo.get();
    }

    public void setMotivo(String motivo) {
        this.motivo.set(motivo);
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    public String getObservaciones() {
        return observaciones.get();
    }

    public void setObservaciones(String observaciones) {
        this.observaciones.set(observaciones);
    }

    public String getDiagnostico() {
        return diagnostico.get();
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico.set(diagnostico);
    }

    public String getCancelacion() {
        return cancelacion.get();
    }

    public void setCancelacion(String cancelacion) {
        this.cancelacion.set(cancelacion);
    }

    public boolean getModificado() {
        return modificado;
    }

    public void setModificado(boolean modificado) {
        this.modificado = modificado;
    }

    @JsonbTransient
    public StringProperty idCitaProperty() {
        return idCita;
    }

    @JsonbTransient
    public StringProperty idMedicoProperty() {
        return idMedico;
    }

    @JsonbTransient
    public StringProperty idPacienteProperty() {
        return idPaciente;
    }

    @JsonbTransient
    public ObjectProperty<LocalDate> fechaProperty() {
        return fecha;
    }

    @JsonbTransient
    public ObjectProperty<LocalTime> horaInicioProperty() {
        return horaInicio;
    }

    @JsonbTransient
    public ObjectProperty<LocalTime> horaFinProperty() {
        return horaFin;
    }

    @JsonbTransient
    public StringProperty motivoProperty() {
        return motivo;
    }

    @JsonbTransient
    public StringProperty estadoProperty() {
        return estado;
    }

    @JsonbTransient
    public StringProperty observacionesProperty() {
        return observaciones;
    }

    @JsonbTransient
    public StringProperty diagnosticoProperty() {
        return diagnostico;
    }

    @JsonbTransient
    public StringProperty cancelacionProperty() {
        return cancelacion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCita.get());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CitaDTO)) return false;
        CitaDTO other = (CitaDTO) obj;
        return Objects.equals(this.idCita.get(), other.idCita.get());
    }

    @Override
    public String toString() {
        return "CitaDTO{" +
                "idCita=" + idCita.get() +
                ", idMedico=" + idMedico.get() +
                ", idPaciente=" + idPaciente.get() +
                ", fecha=" + fecha.get() +
                ", horaInicio=" + horaInicio.get() +
                ", horaFin=" + horaFin.get() +
                ", motivo=" + motivo.get() +
                ", estado=" + estado.get() +
                ", observaciones=" + observaciones.get() +
                ", diagnostico=" + diagnostico.get() +
                ", cancelacion=" + cancelacion.get() +
                ", modificado=" + modificado +
                '}';
    }

    Predicate<StringProperty> esIdValido =
            id -> id.get() != null && !id.get().isBlank();
}
