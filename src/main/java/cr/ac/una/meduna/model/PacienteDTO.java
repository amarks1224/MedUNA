package cr.ac.una.meduna.model;

import jakarta.json.bind.annotation.JsonbTransient;
import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Predicate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PacienteDTO {

    private StringProperty idPaciente;
    private StringProperty cedula;
    private StringProperty nombre;
    private StringProperty apellido;
    private ObjectProperty<LocalDate> nacimiento;
    private StringProperty genero;
    private StringProperty telefono;
    private StringProperty correo;
    private StringProperty direccion;
    private StringProperty tipoSangre;
    private boolean modificado;

    public PacienteDTO() {
        this.idPaciente  = new SimpleStringProperty("");
        this.cedula      = new SimpleStringProperty("");
        this.nombre      = new SimpleStringProperty("");
        this.apellido    = new SimpleStringProperty("");
        this.nacimiento  = new SimpleObjectProperty<>();
        this.genero      = new SimpleStringProperty("");
        this.telefono    = new SimpleStringProperty("");
        this.correo      = new SimpleStringProperty("");
        this.direccion   = new SimpleStringProperty("");
        this.tipoSangre  = new SimpleStringProperty("");
        this.modificado  = false;
    }

    public PacienteDTO(PacienteEntity entity) {
        this();
        if (entity.getIdPaciente() != null) {
            this.idPaciente.set(entity.getIdPaciente().toString());
        }
        this.cedula.set(entity.getCedula());
        this.nombre.set(entity.getNombre());
        this.apellido.set(entity.getApellido());
        this.nacimiento.set(entity.getNacimiento());
        this.genero.set(entity.getGenero());
        this.telefono.set(entity.getTelefono());
        this.correo.set(entity.getCorreo());
        this.direccion.set(entity.getDireccion());
        this.tipoSangre.set(entity.getTipoSangre());
        this.modificado = false;
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

    public String getCedula() {
        return cedula.get();
    }

    public void setCedula(String cedula) {
        this.cedula.set(cedula);
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getApellido() {
        return apellido.get();
    }

    public void setApellido(String apellido) {
        this.apellido.set(apellido);
    }

    public LocalDate getNacimiento() {
        return nacimiento.get();
    }

    public void setNacimiento(LocalDate nacimiento) {
        this.nacimiento.set(nacimiento);
    }

    public String getGenero() {
        return genero.get();
    }

    public void setGenero(String genero) {
        this.genero.set(genero);
    }

    public String getTelefono() {
        return telefono.get();
    }

    public void setTelefono(String telefono) {
        this.telefono.set(telefono);
    }

    public String getCorreo() {
        return correo.get();
    }

    public void setCorreo(String correo) {
        this.correo.set(correo);
    }

    public String getDireccion() {
        return direccion.get();
    }

    public void setDireccion(String direccion) {
        this.direccion.set(direccion);
    }

    public String getTipoSangre() {
        return tipoSangre.get();
    }

    public void setTipoSangre(String tipoSangre) {
        this.tipoSangre.set(tipoSangre);
    }

    public boolean getModificado() {
        return modificado;
    }

    public void setModificado(boolean modificado) {
        this.modificado = modificado;
    }

    @JsonbTransient
    public StringProperty idPacienteProperty() {
        return idPaciente;
    }

    @JsonbTransient
    public StringProperty cedulaProperty() {
        return cedula;
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
    public ObjectProperty<LocalDate> nacimientoProperty() {
        return nacimiento;
    }

    @JsonbTransient
    public StringProperty generoProperty() {
        return genero;
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
    public StringProperty direccionProperty() {
        return direccion;
    }

    @JsonbTransient
    public StringProperty tipoSangreProperty() {
        return tipoSangre;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPaciente.get());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PacienteDTO)) return false;
        PacienteDTO other = (PacienteDTO) obj;
        return Objects.equals(this.idPaciente.get(), other.idPaciente.get());
    }

    @Override
    public String toString() {
        return "PacienteDTO{" +
                "idPaciente=" + idPaciente.get() +
                ", cedula=" + cedula.get() +
                ", nombre=" + nombre.get() +
                ", apellido=" + apellido.get() +
                ", nacimiento=" + nacimiento.get() +
                ", genero=" + genero.get() +
                ", telefono=" + telefono.get() +
                ", correo=" + correo.get() +
                ", direccion=" + direccion.get() +
                ", tipoSangre=" + tipoSangre.get() +
                ", modificado=" + modificado +
                '}';
    }

    Predicate<StringProperty> esIdValido =
            id -> id.get() != null && !id.get().isBlank();
}
