package cr.ac.una.meduna_web.bean;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Named("acercaBean")
@RequestScoped
public class AcercaDeBean implements Serializable {
    
    private List<String> caracteristicas;
    private List<Miembro> equipo;
    
    @PostConstruct
    public void init() {
        caracteristicas = Arrays.asList(
            "Arquitectura Moderna",
            "Seguridad Robusta",
            "Interfaz Táctil",
            "Base de Datos Oracle",
            "Comunicación Digital",
            "Analytics Integrado"
        );
        
        equipo = Arrays.asList(
            new Miembro("Angie Marks", "Desarrolladora Frontend", "images/Angie.jpg"),
            new Miembro("Juan Calderón", "Desarrollador Backend", "images/Juan.jpg")
        );
    }
    
    public List<String> getCaracteristicas() {
        return caracteristicas;
    }
    
    public List<Miembro> getEquipo() {
        return equipo;
    }
    
    public static class Miembro {
        private String nombre;
        private String rol;
        private String foto;
        
        public Miembro(String nombre, String rol, String foto) {
            this.nombre = nombre;
            this.rol = rol;
            this.foto = foto;
        }
        
        public String getNombre() { return nombre; }
        public String getRol() { return rol; }
        public String getFoto() { return foto; }
        public boolean hasFoto() { return foto != null && !foto.isEmpty(); }
        public String getIniciales() {
            String[] partes = nombre.split(" ");
            return partes.length >= 2 
                ? String.valueOf(partes[0].charAt(0)) + String.valueOf(partes[1].charAt(0))
                : String.valueOf(nombre.charAt(0));
        }
    }
}