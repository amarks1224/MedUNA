package cr.ac.una.meduna.service;

import cr.ac.una.meduna.model.EspecialidadEntity;
import cr.ac.una.meduna.model.MedicoDTO;
import cr.ac.una.meduna.model.MedicoEntity;
import cr.ac.una.meduna.util.EntityManagerHelper;
import cr.ac.una.meduna.util.Respuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase servicio para médicos.
 * @author Angie Marks S.
 * @author Juan Calderón S.
 */
public class MedicoService {

    private static final Logger logger = Logger.getLogger(MedicoService.class.getName());
    private EntityManager em;

    public Respuesta getMedicoById(Long idMedico) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            MedicoEntity entity = em.find(MedicoEntity.class, idMedico);

            if (entity == null) {
                return new Respuesta(false, "Médico no encontrado", "");
            }

            MedicoDTO dto = new MedicoDTO(entity);
            Respuesta respuesta = new Respuesta(true, "", "");
            respuesta.setResultado("Medico", dto);
            return respuesta;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo médico por ID", e);
            return new Respuesta(false, "Error consultando médico", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Respuesta getMedicos() {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            Query query = em.createNamedQuery("Medico.findAll", MedicoEntity.class);
            List<MedicoEntity> entities = query.getResultList();

            List<MedicoDTO> medicoDto = new ArrayList<>();
            for (MedicoEntity entity : entities) {
                medicoDto.add(new MedicoDTO(entity));
            }

            return new Respuesta(true, "", "", "Médico", medicoDto);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo lista de médicos", e);
            return new Respuesta(false, "Error consultando médicos", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Respuesta getMedicoByNombre(String nombre) {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            Query query = em.createQuery(
                "SELECT m FROM MedicoEntity m WHERE LOWER(m.nombre) LIKE LOWER(:nombre)",
                MedicoEntity.class
            );
            query.setParameter("nombre", "%" + nombre + "%");

            List<MedicoEntity> entities = query.getResultList();

            List<MedicoDTO> medicoDto = new ArrayList<>();
            for (MedicoEntity entity : entities) {
                medicoDto.add(new MedicoDTO(entity));
            }

            return new Respuesta(true, "", "", "Médicos", medicoDto);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error buscando médicos por nombre", e);
            return new Respuesta(false, "Error consultando médicos", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Respuesta getMedicodByFilters(String nombre, String apellido) {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            StringBuilder jpql = new StringBuilder("SELECT m FROM MedicoEntity m WHERE 1=1");

            if (nombre != null && !nombre.trim().isEmpty()) {
                jpql.append(" AND LOWER(m.nombre) LIKE LOWER(:nombre)");
            }
            if (apellido != null && !apellido.trim().isEmpty()) {
                jpql.append(" AND LOWER(m.apellido) LIKE LOWER(:apellido)");
            }

            Query query = em.createQuery(jpql.toString(), MedicoEntity.class);

            if (nombre != null && !nombre.trim().isEmpty()) {
                query.setParameter("nombre", "%" + nombre + "%");
            }
            if (apellido != null && !apellido.trim().isEmpty()) {
                query.setParameter("apellido", "%" + apellido + "%");
            }

            List<MedicoEntity> entities = query.getResultList();

            List<MedicoDTO> medicoDTO = new ArrayList<>();
            for (MedicoEntity entity : entities) {
                medicoDTO.add(new MedicoDTO(entity));
            }

            return new Respuesta(true, "", "", "Médicos", medicoDTO);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo médicos con filtros", e);
            return new Respuesta(false, "Error consultando médicos", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
  
    public Respuesta guardarMedico(MedicoDTO medicoDTO) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            em.getTransaction().begin();

            MedicoEntity entity;

            if (medicoDTO.getIdMedico() != null && medicoDTO.getIdMedico() > 0) {
                entity = em.find(MedicoEntity.class, medicoDTO.getIdMedico());

                if (entity == null) {
                    em.getTransaction().rollback();
                    return new Respuesta(false, "Médico no encontrado", "");
                }

                entity.actualizar(medicoDTO);

            } else {
                entity = new MedicoEntity(medicoDTO);
            }

            if (medicoDTO.getEspecialidad() != null &&
                medicoDTO.getEspecialidad().getIdEspecialidad() != null) {

                EspecialidadEntity esp = em.find(
                        EspecialidadEntity.class,
                        medicoDTO.getEspecialidad().getIdEspecialidad()
                );

                entity.setEspecialidadEntity(esp);
            }

            if (entity.getIdMedico() != null) {
                entity = em.merge(entity);
            } else {
                em.persist(entity);
            }

            em.getTransaction().commit();

            return new Respuesta(true, "Médico guardado exitosamente", "", "Medico", new MedicoDTO(entity)
            );

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error guardando médico", e);
            return new Respuesta(false, "Error guardando médico", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Respuesta eliminarMedico(Long idMedico) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            em.getTransaction().begin();

            MedicoEntity entity = em.find(MedicoEntity.class, idMedico);

            if (entity == null) {
                em.getTransaction().rollback();
                return new Respuesta(false, "Médico no encontrado", "");
            }

            Respuesta tieneCitas = tieneCita(idMedico);
            if ((Boolean) tieneCitas.getResultado("TieneCita")) {
                em.getTransaction().rollback();
                return new Respuesta(false, "No se puede eliminar el médico: tiene citas asociadas", "");
            }

            em.remove(entity);
            em.getTransaction().commit();
            return new Respuesta(true, "Médico eliminado exitosamente", "");

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return new Respuesta(false, "Error eliminando médico", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Respuesta tieneCita(Long idMedico) {
        try {
            Long count = em.createQuery(
                "SELECT COUNT(c) FROM CitaEntity c WHERE c.medico.idMedico = :idMedico",
                Long.class
            )
            .setParameter("idMedico", idMedico)
            .getSingleResult();

            boolean tieneCitas = count != null && count > 0;
            return new Respuesta(true, "", "", "TieneCita", tieneCitas);

        } catch (Exception e) {
            return new Respuesta(true, "", "", "TieneCita", false);
        }
    }
}
