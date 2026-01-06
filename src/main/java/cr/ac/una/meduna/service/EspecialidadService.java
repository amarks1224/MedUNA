package cr.ac.una.meduna.service;

import cr.ac.una.meduna.model.EspecialidadDTO;
import cr.ac.una.meduna.model.EspecialidadEntity;
import cr.ac.una.meduna.observer.EspecialidadListener;
import cr.ac.una.meduna.util.EntityManagerHelper;
import cr.ac.una.meduna.util.Respuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Usuario
 */
public class EspecialidadService {
    
    private static final Logger logger = Logger.getLogger(EspecialidadService.class.getName());
    private EntityManager em;
    private List<EspecialidadListener> listeners = new ArrayList<>();
    
    public Respuesta getEspecialidadById(Long idEspecialidad) {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            EspecialidadEntity entity = em.find(EspecialidadEntity.class, idEspecialidad);

            if (entity == null) {
                return new Respuesta(false, "Especialidad no encontrada", "");
            }

            EspecialidadDTO dto = new EspecialidadDTO(entity);
            return new Respuesta(true, "", "", "Especialidad", dto);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo especialidad por ID", e);
            return new Respuesta(false, "Error consultando especialidad", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    public Respuesta getEspecialidadByCode(String codigo){
          try {
            em = EntityManagerHelper.getInstance().getManager();
            
            EspecialidadEntity entity = em.find(EspecialidadEntity.class, codigo);
            
            if (entity == null) {
                return new Respuesta(false, "Especialidad no encontrado", "");
            }
            
            EspecialidadDTO dto = new EspecialidadDTO(entity);
            return new Respuesta(true, "", "", "Especialidad", dto);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo especialidad por código", e);
            return new Respuesta(false, "Error consultando especialidad", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    public Respuesta getEspecialidades(){
        try {
            em = EntityManagerHelper.getInstance().getManager();
            
            Query query = em.createNamedQuery("Especialidad.findAll", EspecialidadEntity.class);
            List<EspecialidadEntity> entities = query.getResultList();
            
            List<EspecialidadDTO> especialidadDto = new ArrayList<>();
            for (EspecialidadEntity entity : entities) {
                especialidadDto.add(new EspecialidadDTO(entity));
            }
            
            return new Respuesta(true, "", "", "Especialidad", especialidadDto);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo lista de especialidades", e);
            return new Respuesta(false, "Error consultando especialidades", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    public Respuesta getEspecialidadByNombre(String nombre){
       try {
            em = EntityManagerHelper.getInstance().getManager();
            
            Query query = em.createQuery(
                "SELECT e FROM EspecialidadEntity e WHERE LOWER(e.nombre) LIKE LOWER(:nombre)", 
                EspecialidadEntity.class
            );
            query.setParameter("nombre", "%" + nombre + "%");
            
            List<EspecialidadEntity> entities = query.getResultList();
            
            List<EspecialidadDTO> especialidadDto = new ArrayList<>();
            for (EspecialidadEntity entity : entities) {
                especialidadDto.add(new EspecialidadDTO(entity));
            }
            
            return new Respuesta(true, "", "", "Especialidades", especialidadDto);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error buscando especialidades por nombre", e);
            return new Respuesta(false, "Error consultando especialidades", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    public Respuesta getEspecialidadByFilters(String nombre, String codigo) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            
            StringBuilder jpql = new StringBuilder("SELECT e FROM EspecialidadEntity e WHERE 1=1");
            
            if (nombre != null && !nombre.trim().isEmpty()) {
                jpql.append(" AND LOWER(e.nombre) LIKE LOWER(:nombre)");
            }
            if (codigo != null && !codigo.trim().isEmpty()) {
                jpql.append(" AND LOWER(e.codigo) LIKE LOWER(:codigo)");
            }
            
            Query query = em.createQuery(jpql.toString(), EspecialidadEntity.class);
            
            if (nombre != null && !nombre.trim().isEmpty()) {
                query.setParameter("nombre", "%" + nombre + "%");
            }
            if (codigo != null && !codigo.trim().isEmpty()) {
                query.setParameter("codigo", "%" + codigo + "%");
            }
            
            List<EspecialidadEntity> entities = query.getResultList();
            
            List<EspecialidadDTO> especialidadDTO = new ArrayList<>();
            for (EspecialidadEntity entity : entities) {
                especialidadDTO.add(new EspecialidadDTO(entity));
            }
            
            return new Respuesta(true, "", "", "Especialidades", especialidadDTO);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo especialidades con filtros", e);
            return new Respuesta(false, "Error consultando especialidades", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
 
    public Respuesta guardarEspecialidad(EspecialidadDTO especialidadDTO){
         try {
            em = EntityManagerHelper.getInstance().getManager();
            em.getTransaction().begin();
            
            EspecialidadEntity entity;
            
           if (especialidadDTO.getIdEspecialidad() != null) {
                entity = em.find(EspecialidadEntity.class, especialidadDTO.getIdEspecialidad());
                if (entity == null) {
                    em.getTransaction().rollback();
                    return new Respuesta(false, "Especialidad no encontrada", "");
                }
                entity.actualizar(especialidadDTO);
                entity = em.merge(entity);
            } else {
                entity = new EspecialidadEntity(especialidadDTO);
                em.persist(entity);
            }
            
            em.getTransaction().commit();
            
            EspecialidadDTO resultado = new EspecialidadDTO(entity);
            return new Respuesta(true, "Especialidad guardada exitosamente", "", "Especialidad", resultado);
            
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error guardando especialidad", e);
            return new Respuesta(false, "Error guardando especialidad", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
  
    public Respuesta eliminarEspecialidad(Long idEspecialidad) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            em.getTransaction().begin();

            EspecialidadEntity entity = em.find(EspecialidadEntity.class, idEspecialidad);

            if (entity == null) {
                em.getTransaction().rollback();
                notifyNoEliminada("Especialidad no encontrada");
                return new Respuesta(false, "Especialidad no encontrada", "");
            }

            Respuesta tieneMedicos = tieneMedico(idEspecialidad);
            if ((Boolean) tieneMedicos.getResultado("TieneMedico")) {
                em.getTransaction().rollback();
                notifyNoEliminada("No se puede eliminar la especialidad: tiene médicos asociados");
                return new Respuesta(false, "No se puede eliminar la especialidad: tiene médicos asociados", "");
            }

            em.remove(entity);
            em.getTransaction().commit();
            notifyEliminada("Especialidad eliminada exitosamente");
            return new Respuesta(true, "Especialidad eliminada exitosamente", "");

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
            notifyNoEliminada("Error eliminando especialidad");
            return new Respuesta(false, "Error eliminando especialidad", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    public Respuesta tieneMedico(Long idEspecialidad) {
        try {
            Query query = em.createQuery(
                "SELECT COUNT(m) FROM MedicoEntity m WHERE m.especialidad.idEspecialidad = :id"
            );
            query.setParameter("id", idEspecialidad);

            Long count = (Long) query.getSingleResult();
            return new Respuesta(true, "", "", "TieneMedico", count > 0);
        } catch (Exception e) {
            return new Respuesta(true, "", "", "TieneMedico", false);
        }
    }

    
    // Método para registrar un listener
    public void addListener(EspecialidadListener listener) {
        listeners.add(listener);
    }

    // Método para notificar que NO se eliminó
    private void notifyNoEliminada(String mensaje) {
        for (EspecialidadListener listener : listeners) {
            listener.onEspecialidadNoEliminada(mensaje);
        }
    }

    // Método para notificar que se eliminó
    private void notifyEliminada(String mensaje) {
        for (EspecialidadListener listener : listeners) {
            listener.onEspecialidadEliminada(mensaje);
        }
    }


}
