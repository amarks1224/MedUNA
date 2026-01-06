package cr.ac.una.meduna.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "MEDICO")
@NamedQueries({
    @NamedQuery(
            name = "Medico.findAll",
            query = "SELECT m FROM MedicoEntity m"
    ),
    @NamedQuery(
            name = "Medico.findByCodigo",
            query = "SELECT m FROM MedicoEntity m WHERE m.idMedico = :id"
    ),
    @NamedQuery(
            name = "Medico.findByNombre",
            query = "SELECT m FROM MedicoEntity m WHERE LOWER(m.nombre) LIKE :nombre"
    )
})
public class MedicoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEDICO_SEQ")
    @SequenceGenerator(
            name = "MEDICO_SEQ",
            sequenceName = "MEDICO_SEQ01",
            allocationSize = 1
    )
    @Column(name = "ID_MEDICO")
    private Long idMedico;

    @Column(name = "NOMBRE_MED", nullable = false)
    private String nombre;

    @Column(name = "APELLIDO_MED", nullable = false)
    private String apellido;

    @Column(name = "NUM_COLEGIADO", nullable = false)
    private Long numColegiado;

    @Column(name = "TEL_MED", nullable = false)
    private String telefono;

    @Column(name = "CORREO_MED", nullable = false, unique = true)
    private String correo;

    @Column(name = "ESTADO_MED", nullable = false)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "ID_ESPECIALIDAD")
    private EspecialidadEntity especialidad;

    public MedicoEntity() {
    }

    public MedicoEntity(MedicoDTO dto) {
        actualizar(dto);
    }

    public void actualizar(MedicoDTO dto) {
        this.nombre = dto.getNombre();
        this.apellido = dto.getApellido();
        this.numColegiado = dto.getNumColegiado();
        this.telefono = dto.getTelefono();
        this.correo = dto.getCorreo();
        this.estado = dto.getEstado();
        this.especialidad = dto.getEspecialidad();
    }

    public Long getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Long idMedico) {
        this.idMedico = idMedico;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Long getNumColegiado() {
        return numColegiado;
    }

    public void setNumColegiado(Long numColegiado) {
        this.numColegiado = numColegiado;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public EspecialidadEntity getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(EspecialidadEntity especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idMedico);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MedicoEntity)) return false;
        MedicoEntity other = (MedicoEntity) obj;
        return Objects.equals(this.idMedico, other.idMedico);
    }

    @Override
    public String toString() {
        return "MedicoEntity{" +
                "idMedico=" + idMedico +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", numColegiado=" + numColegiado +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", estado='" + estado + '\'' +
                ", especialidad=" + (especialidad != null ? especialidad.getNombre() : null) +
                '}';
    }
}
