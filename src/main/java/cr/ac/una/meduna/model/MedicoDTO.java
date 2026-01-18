package cr.ac.una.meduna.model;

import jakarta.json.bind.annotation.JsonbTransient;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MedicoDTO {

    private StringProperty idMedico;
    private StringProperty nombre;
    private StringProperty apellido;
    private StringProperty numColegiado;
    private StringProperty telefono;
    private StringProperty correo;
    private StringProperty estado;
    private ObjectProperty<EspecialidadDTO> especialidad;

    public MedicoDTO() {
        this.idMedico = new SimpleStringProperty("");
        this.nombre = new SimpleStringProperty("");
        this.apellido = new SimpleStringProperty("");
        this.numColegiado = new SimpleStringProperty("");
        this.telefono = new SimpleStringProperty("");
        this.correo = new SimpleStringProperty("");
        this.estado = new SimpleStringProperty("A");
        this.especialidad = new SimpleObjectProperty<>();
    }

    public MedicoDTO(MedicoEntity entity) {
        this();

        if (entity.getIdMedico() != null) {
            this.idMedico.set(entity.getIdMedico().toString());
        }

        this.nombre.set(entity.getNombre());
        this.apellido.set(entity.getApellido());
        this.numColegiado.set(entity.getNumColegiado().toString());
        this.telefono.set(entity.getTelefono());
        this.correo.set(entity.getCorreo());
        this.estado.set(entity.getEstado());

        if (entity.getEspecialidadEntity() != null) {
            this.especialidad.set(new EspecialidadDTO(entity.getEspecialidadEntity()));
        }
    }

    public Long getIdMedico() {
        return idMedico.get() == null || idMedico.get().isBlank()
                ? null
                : Long.valueOf(idMedico.get());
    }

    public String getNombre() {
        return nombre.get();
    }

    public String getApellido() {
        return apellido.get();
    }

    public Long getNumColegiado() {
        return numColegiado.get() == null || numColegiado.get().isBlank()
                ? null
                : Long.valueOf(numColegiado.get());
    }

    public String getTelefono() {
        return telefono.get();
    }

    public String getCorreo() {
        return correo.get();
    }

    public String getEstado() {
        return estado.get();
    }

    public EspecialidadDTO getEspecialidad() {
        return especialidad.get();
    }

    public void setIdMedico(Long id) {
        idMedico.set(id != null ? id.toString() : "");
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public void setApellido(String apellido) {
        this.apellido.set(apellido);
    }

    public void setNumColegiado(Long num) {
        this.numColegiado.set(num != null ? num.toString() : "");
    }

    public void setTelefono(String telefono) {
        this.telefono.set(telefono);
    }

    public void setCorreo(String correo) {
        this.correo.set(correo);
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    public void setEspecialidad(EspecialidadDTO especialidad) {
        this.especialidad.set(especialidad);
    }

    @JsonbTransient
    public StringProperty idMedicoProperty() {
        return idMedico;
    }

    @JsonbTransient
    public StringProperty nombreProperty() {
        return nombre;
    }

    @JsonbTransient
    public StringProperty apellidoProperty() {
        return apellido;
    }

    @JsonbTransient
    public StringProperty numColegiadoProperty() {
        return numColegiado;
    }

    @JsonbTransient
    public StringProperty telefonoProperty() {
        return telefono;
    }

    @JsonbTransient
    public StringProperty correoProperty() {
        return correo;
    }

    @JsonbTransient
    public StringProperty estadoProperty() {
        return estado;
    }

    @JsonbTransient
    public ObjectProperty<EspecialidadDTO> especialidadProperty() {
        return especialidad;
    }
    
    
    @Override
    public int hashCode() {
        return Objects.hash(idMedico.get());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MedicoDTO)) return false;
        MedicoDTO other = (MedicoDTO) obj;
        return Objects.equals(this.idMedico.get(), other.idMedico.get());
    }
}
