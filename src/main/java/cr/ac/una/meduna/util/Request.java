package cr.ac.una.meduna.util;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.util.Map;

/**
 * Clase para manejar peticiones HTTP REST
 * @author ccarranza
 */
public class Request {

    private Client client;
    private Invocation.Builder builder;
    private WebTarget webTarget;
    private Response response;
    private static final String AUTHENTICATION_SCHEME = "Bearer ";
    private static final String DEFAULT_URL = "http://localhost:8080/MedUNA/ws";

    public Request() {
        this.client = ClientBuilder.newClient();
    }

    public Request(String target) {
        this();
        setTarget(target);
    }

    public Request(String target, String parametros, Map<String, Object> valores) {
        this();
        String baseUrl = obtenerUrlBase();
        this.webTarget = client.target(baseUrl + target).path(parametros).resolveTemplates(valores);
        this.builder = webTarget.request(MediaType.APPLICATION_JSON);
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        builder.headers(headers);
    }

    /**
     * Obtiene la URL base del servidor REST desde AppContext
     * Si no existe, usa la URL por defecto
     */
    private String obtenerUrlBase() {
        String url = null;
        
        try {
            // Intenta obtener desde AppContext con la key "resturl"
            Object urlObj = AppContext.getInstance().get("resturl");
            if (urlObj != null && !urlObj.toString().trim().isEmpty()) {
                url = urlObj.toString().trim();
            }
        } catch (Exception e) {
            System.err.println("⚠ Error obteniendo URL desde AppContext: " + e.getMessage());
        }

        // Si no se encontró URL, usar la por defecto
        if (url == null || url.isEmpty()) {
            url = DEFAULT_URL;
            System.err.println("⚠ ADVERTENCIA: Usando URL por defecto: " + url);
            System.err.println("⚠ Verifica que config/properties.ini exista y contenga:");
            System.err.println("   propiedades.medurl=http://localhost:8080/RestUNA/ws");
        } else {
            System.out.println("✓ URL del servidor REST cargada: " + url);
        }

        return url;
    }

    /**
     * Ingresa el objetivo de la petición
     * @param target Objetivo de la petición
     */
    public void setTarget(String target) {
        String baseUrl = obtenerUrlBase();
        String fullUrl = baseUrl + target;
        
        System.out.println("→ Creando petición a: " + fullUrl);
        
        this.webTarget = client.target(fullUrl);
        this.builder = webTarget.request(MediaType.APPLICATION_JSON);
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        builder.headers(headers);
    }

    public void setHeader(String nombre, Object valor) {
        builder.header(nombre, valor);
    }

    public void setHeader(MultivaluedMap<String, Object> valores) {
        valores.add("Content-Type", "application/json; charset=UTF-8");
        builder.headers(valores);
    }

    public void get() {
        response = builder.get();
    }

    public void getToken() {
        response = builder.get();
    }

    public void post(Object clazz) {
        Entity<?> entity = Entity.entity(clazz, "application/json; charset=UTF-8");
        response = builder.post(entity);
    }

    public void put(Object clazz) {
        Entity<?> entity = Entity.entity(clazz, "application/json; charset=UTF-8");
        response = builder.put(entity);
    }

    public void delete() {
        response = builder.delete();
    }

    public int getStatus() {
        return response.getStatus();
    }

    public Boolean isError() {
        return getStatus() != Response.Status.OK.getStatusCode();
    }

    public String getError() {
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            String mensaje;
            if (response.hasEntity()) {
                if (response.getMediaType().equals(MediaType.TEXT_PLAIN_TYPE)) {
                    mensaje = response.readEntity(String.class);
                } else if (response.getMediaType().getType().equals(MediaType.TEXT_HTML_TYPE.getType())
                        && response.getMediaType().getSubtype()
                                .equals(MediaType.TEXT_HTML_TYPE.getSubtype())) {
                    mensaje = response.readEntity(String.class);
                    mensaje = mensaje.substring(mensaje.indexOf("<b>message</b>") + ("<b>message</b>").length());
                    mensaje = mensaje.substring(0, mensaje.indexOf("</p>"));
                } else if (response.getMediaType().equals(MediaType.APPLICATION_JSON_TYPE)) {
                    mensaje = response.readEntity(String.class);
                } else {
                    mensaje = response.getStatusInfo().getReasonPhrase();
                }
            } else {
                mensaje = response.getStatusInfo().getReasonPhrase();
            }
            return mensaje;
        }
        return null;
    }

    public Object readEntity(Class<?> clazz) {
        return response.readEntity(clazz);
    }

    public Object readEntity(GenericType<?> genericType) {
        return response.readEntity(genericType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Request{");

        if (webTarget != null) {
            sb.append("uri=").append(webTarget.getUri().toString());
        } else {
            sb.append("uri=null");
        }

        if (response != null) {
            sb.append(", status=").append(response.getStatus());
            sb.append(", mediaType=").append(response.getMediaType());
        }

        sb.append('}');
        return sb.toString();
    }
}