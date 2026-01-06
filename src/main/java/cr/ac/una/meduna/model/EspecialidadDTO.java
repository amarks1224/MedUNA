package cr.ac.una.meduna.model;

public class EspecialidadDTO {

    private String idEspecialidad;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Integer duracion;

    public EspecialidadDTO() {
    }

    public EspecialidadDTO(EspecialidadEntity entity) {
        this.idEspecialidad = entity.getIdEspecialidad().toString();
        this.codigo = entity.getCodigo();
        this.nombre = entity.getNombre();
        this.descripcion = entity.getDescripcion();
        this.duracion = entity.getDuracion();
    }

    public String getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(String idEspecialidad) {
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
}
