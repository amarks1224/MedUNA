package cr.ac.una.meduna.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ESPECIALIDAD")
@NamedQueries({
    @NamedQuery(
            name = "Especialidad.findAll",
            query = "SELECT e FROM EspecialidadEntity e"
    ),
    @NamedQuery(
            name = "Especialidad.findByCodigo",
            query = "SELECT e FROM EspecialidadEntity e WHERE e.codigo = :codigo"
    )
})
public class EspecialidadEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ESPECIALIDAD_SEQ")
    @SequenceGenerator(
            name = "ESPECIALIDAD_SEQ",
            sequenceName = "ESPECIALIDAD_SEQ01",
            allocationSize = 1
    )
    @Column(name = "ID_ESPECIALIDAD")
    private Long idEspecialidad;

    @Column(name = "CODIGO_ESP", nullable = false, unique = true)
    private String codigo;

    @Column(name = "NOMBRE_ESP", nullable = false)
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "DURACION", nullable = false)
    private Integer duracion;

    public EspecialidadEntity() {
    }

    public EspecialidadEntity(EspecialidadDTO dto) {
        actualizar(dto);
    }

    public void actualizar(EspecialidadDTO dto) {
        this.codigo = dto.getCodigo();
        this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion();
        this.duracion = dto.getDuracion();
    }

    public Long getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(Long idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idEspecialidad);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EspecialidadEntity)) return false;
        EspecialidadEntity other = (EspecialidadEntity) obj;
        return Objects.equals(this.idEspecialidad, other.idEspecialidad);
    }

    @Override
    public String toString() {
        return "EspecialidadEntity{" +
                "idEspecialidad=" + idEspecialidad +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", duracion=" + duracion +
                '}';
    }
}
