package cr.ac.una.meduna.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "PACIENTE")
@NamedQueries({
    @NamedQuery(
            name = "Paciente.findAll",
            query = "SELECT p FROM PacienteEntity p"
    ),
    @NamedQuery(
            name = "Paciente.findById",
            query = "SELECT p FROM PacienteEntity p WHERE p.idPaciente = :id"
    ),
    @NamedQuery(
            name = "Paciente.findByCedula",
            query = "SELECT p FROM PacienteEntity p WHERE p.cedula = :cedula"
    )
})
public class PacienteEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PACIENTE_SEQ")
    @SequenceGenerator(
            name = "PACIENTE_SEQ",
            sequenceName = "PACIENTE_SEQ01",
            allocationSize = 1
    )
    @Column(name = "ID_PACIENTE")
    private Long idPaciente;

    @Column(name = "CEDULA_PAC", nullable = false, unique = true)
    private String cedula;

    @Column(name = "NOMBRE_PAC", nullable = false)
    private String nombre;

    @Column(name = "APELLIDO_PAC", nullable = false)
    private String apellido;

    @Column(name = "NACIMIENTO", nullable = false)
    private LocalDate nacimiento;

    @Column(name = "GENERO", nullable = false)
    private String genero;

    @Column(name = "TEL_PAC", nullable = false)
    private String telefono;

    @Column(name = "CORREO_PAC", nullable = false, unique = true)
    private String correo;

    @Column(name = "DIRECCION_PAC")
    private String direccion;

    @Column(name = "SANGRE_PAC", nullable = false)
    private String tipoSangre;

    public PacienteEntity() {
    }

    public PacienteEntity(PacienteDTO dto) {
        actualizar(dto);
    }

    public void actualizar(PacienteDTO dto) {
        this.cedula = dto.getCedula();
        this.nombre = dto.getNombre();
        this.apellido = dto.getApellido();
        this.nacimiento = dto.getNacimiento();
        this.genero = dto.getGenero();
        this.telefono = dto.getTelefono();
        this.correo = dto.getCorreo();
        this.direccion = dto.getDireccion();
        this.tipoSangre = dto.getTipoSangre();
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
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

    public LocalDate getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(LocalDate nacimiento) {
        this.nacimiento = nacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipoSangre() {
        return tipoSangre;
    }

    public void setTipoSangre(String tipoSangre) {
        this.tipoSangre = tipoSangre;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idPaciente);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PacienteEntity)) return false;
        PacienteEntity other = (PacienteEntity) obj;
        return Objects.equals(this.idPaciente, other.idPaciente);
    }

    @Override
    public String toString() {
        return "PacienteEntity{" +
                "idPaciente=" + idPaciente +
                ", cedula='" + cedula + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", nacimiento=" + nacimiento +
                ", genero='" + genero + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", direccion='" + direccion + '\'' +
                ", tipoSangre='" + tipoSangre + '\'' +
                '}';
    }
}
