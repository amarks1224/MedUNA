package cr.ac.una.meduna.model;

import jakarta.json.bind.annotation.JsonbTransient;
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
    private EspecialidadEntity especialidad;

    public MedicoDTO() {
        this.idMedico = new SimpleStringProperty("");
        this.nombre = new SimpleStringProperty("");
        this.apellido = new SimpleStringProperty("");
        this.numColegiado = new SimpleStringProperty("");
        this.telefono = new SimpleStringProperty("");
        this.correo = new SimpleStringProperty("");
        this.estado = new SimpleStringProperty("A");
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
        this.especialidad = entity.getEspecialidad();
    }

    public Long getIdMedico() {
        return idMedico.get() == null || idMedico.get().isBlank() ? null : Long.valueOf(idMedico.get());
    }

    public String getNombre() {
        return nombre.get();
    }

    public String getApellido() {
        return apellido.get();
    }

    public Long getNumColegiado() {
        return Long.valueOf(numColegiado.get());
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

    public EspecialidadEntity getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(EspecialidadEntity especialidad) {
        this.especialidad = especialidad;
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

}
