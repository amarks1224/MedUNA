package cr.ac.una.meduna.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "CITA")
@NamedQueries({
    @NamedQuery(
            name = "Cita.findByMedico",
            query = "SELECT c FROM CitaEntity c WHERE c.medico.idMedico = :idMedico"
    )
})
public class CitaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CITA_SEQ")
    @SequenceGenerator(
            name = "CITA_SEQ",
            sequenceName = "CITA_SEQ01",
            allocationSize = 1
    )
    @Column(name = "ID_CITA")
    private Long idCita;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_MEDICO")
    private MedicoEntity medico;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PACIENTE")
    private PacienteEntity paciente;

    @Column(name = "FECHA_CITA", nullable = false)
    private LocalDate fecha;

    @Column(name = "HORA_INICIO", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "HORA_FIN", nullable = false)
    private LocalTime horaFin;

    @Column(name = "MOTIVO")
    private String motivo;

    @Column(name = "ESTADO_CITA")
    private String estado;

    @Column(name = "OBSERVACIONES")
    private String observaciones;

    @Column(name = "DIAGNOSTICO")
    private String diagnostico;

    @Column(name = "CANCELACION")
    private String cancelacion;

    public CitaEntity() {
    }
    
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getCancelacion() {
        return cancelacion;
    }

    public void setCancelacion(String cancelacion) {
        this.cancelacion = cancelacion;
    }


    public Long getIdCita() {
        return idCita;
    }

    public MedicoEntity getMedico() {
        return medico;
    }

    public void setMedico(MedicoEntity medico) {
        this.medico = medico;
    }

    public PacienteEntity getPaciente() {
        return paciente;
    }

    public void setPaciente(PacienteEntity paciente) {
        this.paciente = paciente;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCita);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CitaEntity)) return false;
        CitaEntity other = (CitaEntity) obj;
        return Objects.equals(this.idCita, other.idCita);
    }
}
