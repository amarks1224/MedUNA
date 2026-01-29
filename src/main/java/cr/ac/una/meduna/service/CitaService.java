/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
 *
 * @author juans
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
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Respuesta getCitas() {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            TypedQuery<CitaEntity> q = em.createQuery("SELECT c FROM CitaEntity c ORDER BY c.fecha DESC, c.horaInicio DESC", CitaEntity.class);
            List<CitaEntity> entities = q.getResultList();

            List<CitaDTO> dtos = new ArrayList<>();
            for (CitaEntity e : entities) {
                dtos.add(new CitaDTO(e));
            }

            return new Respuesta(true, "", "", "Citas", dtos);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo lista de citas", e);
            return new Respuesta(false, "Error consultando citas", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Respuesta getCitasByMedico(Long idMedico) {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            TypedQuery<CitaEntity> q = em.createNamedQuery("Cita.findByMedico", CitaEntity.class);
            q.setParameter("idMedico", idMedico);

            List<CitaEntity> entities = q.getResultList();
            List<CitaDTO> dtos = new ArrayList<>();
            for (CitaEntity e : entities) {
                dtos.add(new CitaDTO(e));
            }

            return new Respuesta(true, "", "", "Citas", dtos);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo citas por médico", e);
            return new Respuesta(false, "Error consultando citas del médico", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
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

            List<CitaEntity> entities = q.getResultList();
            List<CitaDTO> dtos = new ArrayList<>();
            for (CitaEntity e : entities) {
                dtos.add(new CitaDTO(e));
            }

            return new Respuesta(true, "", "", "Citas", dtos);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error obteniendo citas por paciente", e);
            return new Respuesta(false, "Error consultando citas del paciente", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Respuesta guardarCita(CitaDTO citaDTO) {
        try {

            Respuesta validacion = validarDatosBasicos(citaDTO);
            if (!validacion.getEstado()) {
                return validacion;
            }

            em = EntityManagerHelper.getInstance().getManager();
            em.getTransaction().begin();

            MedicoEntity medico = em.find(MedicoEntity.class, citaDTO.getIdMedico());
            if (medico == null) {
                em.getTransaction().rollback();
                return new Respuesta(false, "El médico no existe", "");
            }

            PacienteEntity paciente = em.find(PacienteEntity.class, citaDTO.getIdPaciente());
            if (paciente == null) {
                em.getTransaction().rollback();
                return new Respuesta(false, "El paciente no existe", "");
            }

            boolean traslapa = existeTraslapeHorario(
                    medico.getIdMedico(),
                    citaDTO.getFecha(),
                    citaDTO.getHoraInicio(),
                    citaDTO.getHoraFin(),
                    citaDTO.getIdCita()
            );

            if (traslapa) {
                em.getTransaction().rollback();
                return new Respuesta(false, "El médico ya tiene una cita en ese horario", "");
            }

            CitaEntity entity;

            if (citaDTO.getIdCita() != null && citaDTO.getIdCita() > 0) {
                entity = em.find(CitaEntity.class, citaDTO.getIdCita());
                if (entity == null) {
                    em.getTransaction().rollback();
                    return new Respuesta(false, "Cita no encontrada para editar", "");
                }
            } else {
                entity = new CitaEntity();

                if (citaDTO.getEstado() == null || citaDTO.getEstado().isBlank()) {
                    citaDTO.setEstado("P");
                }
            }

            entity.setMedico(medico);
            entity.setPaciente(paciente);
            entity.setFecha(citaDTO.getFecha());
            entity.setHoraInicio(citaDTO.getHoraInicio());
            entity.setHoraFin(citaDTO.getHoraFin());
            entity.setMotivo(citaDTO.getMotivo());
            entity.setEstado(citaDTO.getEstado());
            entity.setObservaciones(citaDTO.getObservaciones());
            entity.setDiagnostico(citaDTO.getDiagnostico());
            entity.setCancelacion(citaDTO.getCancelacion());

            if (entity.getIdCita() != null) {
                entity = em.merge(entity);
            } else {
                em.persist(entity);
            }

            em.getTransaction().commit();
            return new Respuesta(true, "Cita guardada exitosamente", "", "Cita", new CitaDTO(entity));

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error guardando cita", e);
            return new Respuesta(false, "Error guardando cita", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Respuesta cancelarCita(Long idCita, String motivoCancelacion) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            em.getTransaction().begin();

            CitaEntity entity = em.find(CitaEntity.class, idCita);
            if (entity == null) {
                em.getTransaction().rollback();
                return new Respuesta(false, "Cita no encontrada", "");
            }

            entity.setEstado("C");
            entity.setCancelacion(motivoCancelacion != null ? motivoCancelacion : "");

            entity = em.merge(entity);

            em.getTransaction().commit();
            return new Respuesta(true, "Cita cancelada exitosamente", "", "Cita", new CitaDTO(entity));

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error cancelando cita", e);
            return new Respuesta(false, "Error cancelando cita", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
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

            List<CitaEntity> entities = q.getResultList();
            List<CitaDTO> dtos = new ArrayList<>();
            for (CitaEntity e : entities) {
                dtos.add(new CitaDTO(e));
            }

            return new Respuesta(true, "", "", "Citas", dtos);

        } catch (Exception e) {
            return new Respuesta(false, "Error consultando citas", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Respuesta cambiarEstadoCita(Long idCita, String nuevoEstado, String motivoCancelacion) {
        try {
            em = EntityManagerHelper.getInstance().getManager();
            em.getTransaction().begin();

            CitaEntity entity = em.find(CitaEntity.class, idCita);

            if (entity == null) {
                em.getTransaction().rollback();
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
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error cambiando estado de cita", e);
            return new Respuesta(false, "Error cambiando estado de cita", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    
    
    private Respuesta validarDatosBasicos(CitaDTO dto) {
        if (dto == null) {
            return new Respuesta(false, "Datos de cita vacíos", "");
        }
        if (dto.getIdMedico() == null) {
            return new Respuesta(false, "Debe seleccionar un médico", "");
        }
        if (dto.getIdPaciente() == null) {
            return new Respuesta(false, "Debe seleccionar un paciente", "");
        }
        if (dto.getFecha() == null) {
            return new Respuesta(false, "Debe seleccionar una fecha", "");
        }
        if (dto.getHoraInicio() == null) {
            return new Respuesta(false, "Debe seleccionar hora de inicio", "");
        }
        if (dto.getHoraFin() == null) {
            return new Respuesta(false, "Debe seleccionar hora de fin", "");
        }

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
}
