package cr.ac.una.meduna.service;

import cr.ac.una.meduna.model.PacienteDTO;
import cr.ac.una.meduna.model.PacienteEntity;
import cr.ac.una.meduna.util.EntityManagerHelper;
import cr.ac.una.meduna.util.Respuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PacienteService {

    private static final Logger logger = Logger.getLogger(PacienteService.class.getName());
    private EntityManager em;

    /* ===================== GET POR ID ===================== */
    public Respuesta getPacienteById(Long idPaciente) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            
            PacienteEntity entity = em.find(PacienteEntity.class, idPaciente);
            
            if (entity == null) {
                return new Respuesta(false, "Paciente no encontrado", "");
            }
            
            PacienteDTO dto = new PacienteDTO(entity);
            return new Respuesta(true, "", "", "Paciente", dto);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo paciente por ID", e);
            return new Respuesta(false, "Error consultando paciente", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /* ===================== GET TODOS ===================== */
    public Respuesta getPacientes() {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            
            Query query = em.createNamedQuery("Paciente.findAll", PacienteEntity.class);
            List<PacienteEntity> entities = query.getResultList();
            
            List<PacienteDTO> pacientesDTO = new ArrayList<>();
            for (PacienteEntity entity : entities) {
                pacientesDTO.add(new PacienteDTO(entity));
            }
            
            return new Respuesta(true, "", "", "Pacientes", pacientesDTO);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo lista de pacientes", e);
            return new Respuesta(false, "Error consultando pacientes", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /* ===================== GET POR NOMBRE ===================== */
    public Respuesta getPacientesByNombre(String nombre) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            
            Query query = em.createQuery(
                "SELECT p FROM PacienteEntity p WHERE LOWER(p.nombre) LIKE LOWER(:nombre)", 
                PacienteEntity.class
            );
            query.setParameter("nombre", "%" + nombre + "%");
            
            List<PacienteEntity> entities = query.getResultList();
            
            List<PacienteDTO> pacientesDTO = new ArrayList<>();
            for (PacienteEntity entity : entities) {
                pacientesDTO.add(new PacienteDTO(entity));
            }
            
            return new Respuesta(true, "", "", "Pacientes", pacientesDTO);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error buscando pacientes por nombre", e);
            return new Respuesta(false, "Error consultando pacientes", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /* ===================== GET POR CÉDULA ===================== */
    public Respuesta getPacienteByCedula(String cedula) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            
            Query query = em.createNamedQuery("Paciente.findByCedula", PacienteEntity.class);
            query.setParameter("cedula", cedula);
            
            List<PacienteEntity> entities = query.getResultList();
            
            if (entities.isEmpty()) {
                return new Respuesta(false, "Paciente no encontrado", "");
            }
            
            PacienteDTO dto = new PacienteDTO(entities.get(0));
            return new Respuesta(true, "", "", "Paciente", dto);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error buscando paciente por cédula", e);
            return new Respuesta(false, "Error consultando paciente", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /* ===================== GET CON FILTROS ===================== */
    public Respuesta getPacientesByFilters(String nombre, String cedula) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            
            StringBuilder jpql = new StringBuilder("SELECT p FROM PacienteEntity p WHERE 1=1");
            
            if (nombre != null && !nombre.trim().isEmpty()) {
                jpql.append(" AND LOWER(p.nombre) LIKE LOWER(:nombre)");
            }
            if (cedula != null && !cedula.trim().isEmpty()) {
                jpql.append(" AND LOWER(p.cedula) LIKE LOWER(:cedula)");
            }
            
            Query query = em.createQuery(jpql.toString(), PacienteEntity.class);
            
            if (nombre != null && !nombre.trim().isEmpty()) {
                query.setParameter("nombre", "%" + nombre + "%");
            }
            if (cedula != null && !cedula.trim().isEmpty()) {
                query.setParameter("cedula", "%" + cedula + "%");
            }
            
            List<PacienteEntity> entities = query.getResultList();
            
            List<PacienteDTO> pacientesDTO = new ArrayList<>();
            for (PacienteEntity entity : entities) {
                pacientesDTO.add(new PacienteDTO(entity));
            }
            
            return new Respuesta(true, "", "", "Pacientes", pacientesDTO);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo pacientes con filtros", e);
            return new Respuesta(false, "Error consultando pacientes", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /* ===================== GUARDAR ===================== */
    public Respuesta guardarPaciente(PacienteDTO pacienteDTO) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            em.getTransaction().begin();
            
            PacienteEntity entity;
            
            if (pacienteDTO.getIdPaciente() != null && pacienteDTO.getIdPaciente() > 0) {
                // Actualizar paciente existente
                entity = em.find(PacienteEntity.class, pacienteDTO.getIdPaciente());
                if (entity == null) {
                    em.getTransaction().rollback();
                    return new Respuesta(false, "Paciente no encontrado", "");
                }
                entity.actualizar(pacienteDTO);
                entity = em.merge(entity);
            } else {
                // Crear nuevo paciente
                entity = new PacienteEntity(pacienteDTO);
                em.persist(entity);
            }
            
            em.getTransaction().commit();
            
            PacienteDTO resultado = new PacienteDTO(entity);
            return new Respuesta(true, "Paciente guardado exitosamente", "", "Paciente", resultado);
            
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error guardando paciente", e);
            return new Respuesta(false, "Error guardando paciente", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /* ===================== ELIMINAR ===================== */
    public Respuesta eliminarPaciente(Long idPaciente) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            em.getTransaction().begin();
            
            PacienteEntity entity = em.find(PacienteEntity.class, idPaciente);
            
            if (entity == null) {
                em.getTransaction().rollback();
                return new Respuesta(false, "Paciente no encontrado", "");
            }
            
            em.remove(entity);
            em.getTransaction().commit();
            
            return new Respuesta(true, "Paciente eliminado exitosamente", "");
            
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error eliminando paciente", e);
            return new Respuesta(false, "Error eliminando paciente", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /* ===================== VERIFICAR CITA ===================== */
    public Respuesta tieneCita(Long idPaciente) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            
            // Ajusta esto según tu entidad de Citas cuando exista
            Query query = em.createQuery(
                "SELECT COUNT(c) FROM CitaEntity c WHERE c.paciente.idPaciente = :idPaciente"
            );
            query.setParameter("idPaciente", idPaciente);
            
            Long count = (Long) query.getSingleResult();
            Boolean tieneCitas = count > 0;
            
            return new Respuesta(true, "", "", "TieneCita", tieneCitas);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error verificando citas (posiblemente la entidad Cita no existe aún)", e);
            // Si la entidad Cita no existe aún, retornar false
            return new Respuesta(true, "", "", "TieneCita", false);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}