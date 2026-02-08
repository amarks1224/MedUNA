package cr.ac.una.meduna.service;

import cr.ac.una.meduna.model.CitaDTO;
import cr.ac.una.meduna.model.CitaEntity;
import cr.ac.una.meduna.model.MedicoEntity;
import cr.ac.una.meduna.model.PacienteEntity;
import cr.ac.una.meduna.util.EntityManagerHelper;
import cr.ac.una.meduna.util.Respuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio de citas
 *
 * @author Angie Marks
 * @author Juan Calderón
 */
public class CitaService {

    private static final Logger logger = Logger.getLogger(CitaService.class.getName());
    private EntityManager em;

    public Respuesta getCitaById(Long idCita) {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            CitaEntity entity = em.find(CitaEntity.class, idCita);
            if (entity == null) {
                return new Respuesta(false, "Cita no encontrado", "");
            }

            return new Respuesta(true, "", "", "Cita", new CitaDTO(entity));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo cita por su ID", e);
            return new Respuesta(false, "Error consultando cita", e.getMessage());
        } finally {
            cerrarEntityManager();
        }
    }

    public Respuesta getCitas() {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            TypedQuery<CitaEntity> q = em.createQuery(
                    "SELECT c FROM CitaEntity c ORDER BY c.fecha DESC, c.horaInicio DESC",
                    CitaEntity.class
            );

            return new Respuesta(true, "", "", "Citas", toDTOList(q.getResultList()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo lista de citas", e);
            return new Respuesta(false, "Error consultando citas", e.getMessage());
        } finally {
            cerrarEntityManager();
        }
    }

    public Respuesta getCitasByMedico(Long idMedico) {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            TypedQuery<CitaEntity> q = em.createNamedQuery("Cita.findByMedico", CitaEntity.class);
            q.setParameter("idMedico", idMedico);

            return new Respuesta(true, "", "", "Citas", toDTOList(q.getResultList()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo citas por médico", e);
            return new Respuesta(false, "Error consultando citas del médico", e.getMessage());
        } finally {
            cerrarEntityManager();
        }
    }

    public Respuesta getCitasByPaciente(Long idPaciente) {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            TypedQuery<CitaEntity> q = em.createQuery(
                    "SELECT c FROM CitaEntity c WHERE c.paciente.idPaciente = :idPaciente ORDER BY c.fecha DESC, c.horaInicio DESC",
                    CitaEntity.class
            );
            q.setParameter("idPaciente", idPaciente);

            return new Respuesta(true, "", "", "Citas", toDTOList(q.getResultList()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo citas por paciente", e);
            return new Respuesta(false, "Error consultando citas del paciente", e.getMessage());
        } finally {
            cerrarEntityManager();
        }
    }

    public Respuesta getCitasByMedicoFecha(Long idMedico, LocalDate fecha) {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            Query q = em.createQuery(
                    "SELECT c FROM CitaEntity c WHERE c.medico.idMedico = :idMedico AND c.fecha = :fecha",
                    CitaEntity.class
            );
            q.setParameter("idMedico", idMedico);
            q.setParameter("fecha", fecha);

            @SuppressWarnings("unchecked")
            List<CitaEntity> entities = q.getResultList();

            return new Respuesta(true, "", "", "Citas", toDTOList(entities));
        } catch (Exception e) {
            return new Respuesta(false, "Error consultando citas", e.getMessage());
        } finally {
            cerrarEntityManager();
        }
    }


    public Respuesta guardarCita(CitaDTO citaDTO) {
        try {
            Respuesta validacion = validarDatosBasicos(citaDTO);
            if (!validacion.getEstado()) return validacion;

            em = EntityManagerHelper.getInstance().getManager();
            em.getTransaction().begin();

            MedicoEntity medico = obtenerMedico(citaDTO.getIdMedico());
            if (medico == null) {
                rollbackSeguro();
                return new Respuesta(false, "El médico no existe", "");
            }

            PacienteEntity paciente = obtenerPaciente(citaDTO.getIdPaciente());
            if (paciente == null) {
                rollbackSeguro();
                return new Respuesta(false, "El paciente no existe", "");
            }

            if (hayTraslape(medico.getIdMedico(), citaDTO)) {
                rollbackSeguro();
                return new Respuesta(false, "El médico ya tiene una cita en ese horario", "");
            }

            CitaEntity entity = obtenerOCrearCitaEntity(citaDTO);
            if (entity == null) {
                rollbackSeguro();
                return new Respuesta(false, "Cita no encontrada para editar", "");
            }

            aplicarDatos(entity, citaDTO, medico, paciente);

            entity = guardar(entity);

            em.getTransaction().commit();
            return new Respuesta(true, "Cita guardada exitosamente", "", "Cita", new CitaDTO(entity));

        } catch (Exception e) {
            rollbackSeguro();
            logger.log(Level.SEVERE, "Error guardando cita", e);
            return new Respuesta(false, "Error guardando cita", e.getMessage());
        } finally {
            cerrarEntityManager();
        }
    }

    public Respuesta cancelarCita(Long idCita, String motivoCancelacion) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            em.getTransaction().begin();

            CitaEntity entity = em.find(CitaEntity.class, idCita);
            if (entity == null) {
                rollbackSeguro();
                return new Respuesta(false, "Cita no encontrada", "");
            }

            entity.setEstado("C");
            entity.setCancelacion(motivoCancelacion != null ? motivoCancelacion : "");

            entity = em.merge(entity);

            em.getTransaction().commit();
            return new Respuesta(true, "Cita cancelada exitosamente", "", "Cita", new CitaDTO(entity));

        } catch (Exception e) {
            rollbackSeguro();
            logger.log(Level.SEVERE, "Error cancelando cita", e);
            return new Respuesta(false, "Error cancelando cita", e.getMessage());
        } finally {
            cerrarEntityManager();
        }
    }

    public Respuesta cambiarEstadoCita(Long idCita, String nuevoEstado, String motivoCancelacion) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            em.getTransaction().begin();

            CitaEntity entity = em.find(CitaEntity.class, idCita);
            if (entity == null) {
                rollbackSeguro();
                return new Respuesta(false, "Cita no encontrada", "");
            }

            entity.setEstado(nuevoEstado);

            if ("C".equals(nuevoEstado)) {
                entity.setCancelacion(motivoCancelacion);
            } else {
                entity.setCancelacion(null);
            }

            entity = em.merge(entity);
            em.getTransaction().commit();

            return new Respuesta(true, "Estado actualizado con éxito", "", "Cita", new CitaDTO(entity));

        } catch (Exception e) {
            rollbackSeguro();
            logger.log(Level.SEVERE, "Error cambiando estado de cita", e);
            return new Respuesta(false, "Error cambiando estado de cita", e.getMessage());
        } finally {
            cerrarEntityManager();
        }
    }


    private MedicoEntity obtenerMedico(Long idMedico) {
        return (idMedico != null) ? em.find(MedicoEntity.class, idMedico) : null;
    }

    private PacienteEntity obtenerPaciente(Long idPaciente) {
        return (idPaciente != null) ? em.find(PacienteEntity.class, idPaciente) : null;
    }

    private boolean hayTraslape(Long idMedico, CitaDTO dto) {
        return existeTraslapeHorario(
                idMedico,
                dto.getFecha(),
                dto.getHoraInicio(),
                dto.getHoraFin(),
                dto.getIdCita()
        );
    }

    private CitaEntity obtenerOCrearCitaEntity(CitaDTO dto) {
        if (dto.getIdCita() != null && dto.getIdCita() > 0) {
            return em.find(CitaEntity.class, dto.getIdCita()); 
        }

        if (dto.getEstado() == null || dto.getEstado().isBlank()) {
            dto.setEstado("P");
        }
        return new CitaEntity();
    }

    private void aplicarDatos(CitaEntity entity, CitaDTO dto, MedicoEntity medico, PacienteEntity paciente) {
        entity.setMedico(medico);
        entity.setPaciente(paciente);
        entity.setFecha(dto.getFecha());
        entity.setHoraInicio(dto.getHoraInicio());
        entity.setHoraFin(dto.getHoraFin());
        entity.setMotivo(dto.getMotivo());
        entity.setEstado(dto.getEstado());
        entity.setObservaciones(dto.getObservaciones());
        entity.setDiagnostico(dto.getDiagnostico());
        entity.setCancelacion(dto.getCancelacion());
    }

    private CitaEntity guardar(CitaEntity entity) {
        if (entity.getIdCita() != null) {
            return em.merge(entity);
        }
        em.persist(entity);
        return entity;
    }

    private Respuesta validarDatosBasicos(CitaDTO dto) {
        if (dto == null) return new Respuesta(false, "Datos de cita vacíos", "");
        if (dto.getIdMedico() == null) return new Respuesta(false, "Debe seleccionar un médico", "");
        if (dto.getIdPaciente() == null) return new Respuesta(false, "Debe seleccionar un paciente", "");
        if (dto.getFecha() == null) return new Respuesta(false, "Debe seleccionar una fecha", "");
        if (dto.getHoraInicio() == null) return new Respuesta(false, "Debe seleccionar hora de inicio", "");
        if (dto.getHoraFin() == null) return new Respuesta(false, "Debe seleccionar hora de fin", "");

        LocalTime ini = dto.getHoraInicio();
        LocalTime fin = dto.getHoraFin();
        if (!ini.isBefore(fin)) {
            return new Respuesta(false, "La hora de inicio debe ser menor que la hora de fin", "");
        }
        return new Respuesta(true, "", "");
    }

    private boolean existeTraslapeHorario(Long idMedico, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Long idCitaExcluir) {
        TypedQuery<Long> q = em.createQuery(
                "SELECT COUNT(c) "
                + "FROM CitaEntity c "
                + "WHERE c.medico.idMedico = :idMedico "
                + "  AND c.fecha = :fecha "
                + "  AND (c.estado IS NULL OR c.estado <> 'C') "
                + "  AND (:idCitaExcluir IS NULL OR c.idCita <> :idCitaExcluir) "
                + "  AND (:horaInicio < c.horaFin AND :horaFin > c.horaInicio)",
                Long.class
        );

        q.setParameter("idMedico", idMedico);
        q.setParameter("fecha", fecha);
        q.setParameter("idCitaExcluir", idCitaExcluir);
        q.setParameter("horaInicio", horaInicio);
        q.setParameter("horaFin", horaFin);

        Long count = q.getSingleResult();
        return count != null && count > 0;
    }

    private List<CitaDTO> toDTOList(List<CitaEntity> entities) {
        List<CitaDTO> dtos = new ArrayList<>();
        if (entities == null) return dtos;

        for (CitaEntity e : entities) {
            dtos.add(new CitaDTO(e));
        }
        return dtos;
    }

    private void rollbackSeguro() {
        try {
            if (em != null && em.getTransaction() != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (Exception ignore) {
        }
    }

    private void cerrarEntityManager() {
        try {
            if (em != null && em.isOpen()) em.close();
        } catch (Exception ignore) {
        }
    }
}
