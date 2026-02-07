package cr.ac.una.meduna.service;

import cr.ac.una.meduna.model.EstadisticaDTO;
import cr.ac.una.meduna.util.EntityManagerHelper;
import cr.ac.una.meduna.util.Respuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Servicio de estadisticas
 *
 * @author Angie Marks
 * @author Juan Calderón
 */
public class EstadisticaService {

    private static final Logger logger = Logger.getLogger(EstadisticaService.class.getName());
    private EntityManager em;

    public Respuesta getCitasPorEstado(LocalDate desde, LocalDate hasta, Long idMedico, Long idEspecialidad) {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            String jpql = """
                    SELECT c.estado, COUNT(c)
                    FROM CitaEntity c
                    WHERE c.fecha BETWEEN :desde AND :hasta
                    """;

            if (idMedico != null) {
                jpql += " AND c.medico.idMedico = :idMedico";
            }
            if (idEspecialidad != null) {
                jpql += " AND c.medico.especialidadEntity.idEspecialidad = :idEspecialidad";
            }

            jpql += " GROUP BY c.estado ORDER BY c.estado";

            Query q = em.createQuery(jpql);
            q.setParameter("desde", desde);
            q.setParameter("hasta", hasta);

            if (idMedico != null) {
                q.setParameter("idMedico", idMedico);
            }
            if (idEspecialidad != null) {
                q.setParameter("idEspecialidad", idEspecialidad);
            }

            List<Object[]> rows = q.getResultList();

            List<EstadisticaDTO> out = new ArrayList<>();
            for (Object[] r : rows) {
                out.add(new EstadisticaDTO(String.valueOf(r[0]), (Long) r[1]));
            }

            return new Respuesta(true, "", "", "Estadisticas", out);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getCitasPorEstado", e);
            return new Respuesta(false, "Error consultando estadísticas por estado", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Respuesta getCitasAtendidasPorMedico(LocalDate desde, LocalDate hasta, Long idEspecialidad, List<String> estadosAtendidos) {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            String jpql = """
                    SELECT CONCAT(m.nombre, ' ', m.apellido), COUNT(c)
                    FROM CitaEntity c
                    JOIN c.medico m
                    WHERE c.fecha BETWEEN :desde AND :hasta
                    AND c.estado IN :estados
                    """;

            if (idEspecialidad != null) {
                jpql += " AND m.especialidadEntity.idEspecialidad = :idEspecialidad";
            }

            jpql += " GROUP BY m.nombre, m.apellido ORDER BY COUNT(c) DESC";

            Query q = em.createQuery(jpql);
            q.setParameter("desde", desde);
            q.setParameter("hasta", hasta);
            q.setParameter("estados", estadosAtendidos);

            if (idEspecialidad != null) {
                q.setParameter("idEspecialidad", idEspecialidad);
            }

            List<Object[]> rows = q.getResultList();

            List<EstadisticaDTO> out = new ArrayList<>();
            for (Object[] r : rows) {
                out.add(new EstadisticaDTO(String.valueOf(r[0]), (Long) r[1]));
            }

            return new Respuesta(true, "", "", "Estadisticas", out);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getCitasAtendidasPorMedico", e);
            return new Respuesta(false, "Error, estadísticas por médico", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Respuesta getCitasPorEspecialidad(LocalDate desde, LocalDate hasta, Long idMedico) {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            String jpql = """
                    SELECT e.nombre, COUNT(c)
                    FROM CitaEntity c
                    JOIN c.medico m
                    JOIN m.especialidadEntity e
                    WHERE c.fecha BETWEEN :desde AND :hasta
                    """;

            if (idMedico != null) {
                jpql += " AND m.idMedico = :idMedico";
            }

            jpql += " GROUP BY e.nombre ORDER BY COUNT(c) DESC";

            Query q = em.createQuery(jpql);
            q.setParameter("desde", desde);
            q.setParameter("hasta", hasta);

            if (idMedico != null) {
                q.setParameter("idMedico", idMedico);
            }

            List<Object[]> rows = q.getResultList();

            List<EstadisticaDTO> out = new ArrayList<>();
            for (Object[] r : rows) {
                out.add(new EstadisticaDTO(String.valueOf(r[0]), (Long) r[1]));
            }

            return new Respuesta(true, "", "", "Estadisticas", out);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getCitasPorEspecialidad", e);
            return new Respuesta(false, "Error, estadísticas por especialidad", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Respuesta getCitasPorMes(LocalDate desde, LocalDate hasta, Long idMedico, Long idEspecialidad) {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            String sql = """
            SELECT TO_CHAR(FECHA_CITA, 'YYYY-MM') AS MES, COUNT(*) AS CANT
            FROM CITA c
            JOIN MEDICO m ON m.ID_MEDICO = c.ID_MEDICO
            WHERE c.FECHA_CITA BETWEEN ? AND ?
        """;

            if (idMedico != null) {
                sql += " AND c.ID_MEDICO = ? ";
            }
            if (idEspecialidad != null) {
                sql += " AND m.ID_ESPECIALIDAD = ? ";
            }

            sql += " GROUP BY TO_CHAR(FECHA_CITA, 'YYYY-MM') ORDER BY MES ";

            Query q = em.createNativeQuery(sql);

            int idx = 1;
            q.setParameter(idx++, java.sql.Date.valueOf(desde));
            q.setParameter(idx++, java.sql.Date.valueOf(hasta));
            if (idMedico != null) {
                q.setParameter(idx++, idMedico);
            }
            if (idEspecialidad != null) {
                q.setParameter(idx++, idEspecialidad);
            }

            List<Object[]> rows = q.getResultList();

            List<EstadisticaDTO> out = new ArrayList<>();
            for (Object[] r : rows) {
                String mes = String.valueOf(r[0]);
                Number n = (Number) r[1]; // en Oracle suele venir BigDecimal
                out.add(new EstadisticaDTO(mes, n.longValue()));
            }

            return new Respuesta(true, "", "", "Estadisticas", out);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getCitasPorMes", e);
            return new Respuesta(false, "Error, distribución por mes", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Respuesta getPacientesMasFrecuentes(LocalDate desde, LocalDate hasta, Long idMedico, Long idEspecialidad, int topN) {
        try {
            em = EntityManagerHelper.getInstance().getManager();

            String jpql = """
                    SELECT CONCAT(p.nombre, ' ', p.apellido), COUNT(c)
                    FROM CitaEntity c
                    JOIN c.paciente p
                    WHERE c.fecha BETWEEN :desde AND :hasta
                    """;

            if (idMedico != null) {
                jpql += " AND c.medico.idMedico = :idMedico";
            }
            if (idEspecialidad != null) {
                jpql += " AND c.medico.especialidadEntity.idEspecialidad = :idEspecialidad";
            }

            jpql += " GROUP BY p.nombre, p.apellido ORDER BY COUNT(c) DESC";

            Query q = em.createQuery(jpql);
            q.setParameter("desde", desde);
            q.setParameter("hasta", hasta);

            if (idMedico != null) {
                q.setParameter("idMedico", idMedico);
            }
            if (idEspecialidad != null) {
                q.setParameter("idEspecialidad", idEspecialidad);
            }

            q.setMaxResults(topN);

            List<Object[]> rows = q.getResultList();

            List<EstadisticaDTO> out = new ArrayList<>();
            for (Object[] r : rows) {
                out.add(new EstadisticaDTO(String.valueOf(r[0]), (Long) r[1]));
            }

            return new Respuesta(true, "", "", "Estadisticas", out);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getPacientesMasFrecuentes", e);
            return new Respuesta(false, "Error, pacientes más frecuentes", e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
