package cr.ac.una.meduna.model;

import jakarta.json.bind.annotation.JsonbTransient;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.function.Predicate;

public class EspecialidadDTO {

    private StringProperty idEspecialidad;
    private StringProperty codigo;
    private StringProperty nombre;
    private StringProperty descripcion;
    private IntegerProperty duracion;

    public EspecialidadDTO() {
        this.idEspecialidad = new SimpleStringProperty("");
        this.codigo = new SimpleStringProperty("");
        this.nombre = new SimpleStringProperty("");
        this.descripcion = new SimpleStringProperty("");
        this.duracion = new SimpleIntegerProperty(0);
    }

    public EspecialidadDTO(EspecialidadEntity entity) {
        this();
        if (entity.getIdEspecialidad() != null) {
            this.idEspecialidad.set(entity.getIdEspecialidad().toString());
        }
        this.codigo.set(entity.getCodigo());
        this.nombre.set(entity.getNombre());
        this.descripcion.set(entity.getDescripcion());
        this.duracion.set(entity.getDuracion());
    }

    public Long getIdEspecialidad() {
        if (esIdValido.test(idEspecialidad)) {
            return Long.valueOf(idEspecialidad.get());
        }
        return null;
    }

    public void setIdEspecialidad(Long idEspecialidad) {
        if (idEspecialidad != null) {
            this.idEspecialidad.set(idEspecialidad.toString());
        } else {
            this.idEspecialidad.set("");
        }
    }

    public String getCodigo() {
        return codigo.get();
    }

    public void setCodigo(String codigo) {
        this.codigo.set(codigo);
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public Integer getDuracion() {
        return duracion.get();
    }

    public void setDuracion(Integer duracion) {
        this.duracion.set(duracion);
    }
 
    public EspecialidadEntity getEntidad() {
        EspecialidadEntity entity = new EspecialidadEntity();

        if (getIdEspecialidad() != null) {
            entity.setIdEspecialidad(getIdEspecialidad());
        }

        entity.setCodigo(getCodigo());
        entity.setNombre(getNombre());
        entity.setDescripcion(getDescripcion());
        entity.setDuracion(getDuracion() != null ? getDuracion() : 0);

        return entity;
    }

    @JsonbTransient
    public StringProperty idEspecialidadProperty() {
        return idEspecialidad;
    }

    @JsonbTransient
    public StringProperty codigoProperty() {
        return codigo;
    }

    @JsonbTransient
    public StringProperty nombreProperty() {
        return nombre;
    }

    @JsonbTransient
    public StringProperty descripcionProperty() {
        return descripcion;
    }

    @JsonbTransient
    public IntegerProperty duracionProperty() {
        return duracion;
    }

    Predicate<StringProperty> esIdValido = id -> id.get() != null && !id.get().isBlank();

    @Override
    public String toString() {
        return "EspecialidadDTO{" +
                "idEspecialidad=" + idEspecialidad.get() +
                ", codigo=" + codigo.get() +
                ", nombre=" + nombre.get() +
                ", descripcion=" + descripcion.get() +
                ", duracion=" + duracion.get() +
                '}';
    }

   
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EspecialidadDTO)) return false;
        EspecialidadDTO other = (EspecialidadDTO) obj;
        return Objects.equals(this.getIdEspecialidad(), other.getIdEspecialidad());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdEspecialidad());
    }
    
    

}
