package dao;

import java.util.List;
import java.util.ArrayList;
import org.hibernate.Session;
import org.hibernate.Transaction;
import modelo.*;
import util.HibernateUtil;

public class ParticipaDAO extends DAO<Participa> {

    public ParticipaDAO() {
        super(Participa.class);
    }

    public Participa read(ParticipaId id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Participa participa = session.get(Participa.class, id);
            tx.commit();
            return participa;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return null;
        } finally {
            session.close();
        }
    }

    /**
     * ✅ INSCRIBIR EQUIPO
     */
    public void inscribir(int idTorneo, int idEquipo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String sql = "INSERT INTO inscripcion (id_torneo, id_equipo) VALUES (:idTor, :idEq)";
            session.createNativeQuery(sql)
                    .setParameter("idTor", idTorneo)
                    .setParameter("idEq", idEquipo)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            reintentarConParticipa(idTorneo, idEquipo);
        } finally {
            session.close();
        }
    }

    private void reintentarConParticipa(int idTorneo, int idEquipo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String sql = "INSERT INTO participa (id_torneo, id_equipo) VALUES (:idTor, :idEq)";
            session.createNativeQuery(sql)
                    .setParameter("idTor", idTorneo)
                    .setParameter("idEq", idEquipo)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        } finally {
            session.close();
        }
    }

    /**
     * 🚀 ASIGNAR GRUPO
     */
    public void actualizarGrupo(int idEquipo, int idTorneo, String grupo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String sql = "UPDATE inscripcion SET grupo = :grupo WHERE id_equipo = :idEq AND id_torneo = :idTor";
            session.createNativeQuery(sql)
                    .setParameter("grupo", grupo)
                    .setParameter("idEq", idEquipo)
                    .setParameter("idTor", idTorneo)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            actualizarGrupoEnParticipa(idEquipo, idTorneo, grupo);
        } finally {
            session.close();
        }
    }

    private void actualizarGrupoEnParticipa(int idEquipo, int idTorneo, String grupo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String sql = "UPDATE participa SET grupo = :grupo WHERE id_equipo = :idEq AND id_torneo = :idTor";
            session.createNativeQuery(sql)
                    .setParameter("grupo", grupo)
                    .setParameter("idEq", idEquipo)
                    .setParameter("idTor", idTorneo)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        } finally {
            session.close();
        }
    }

    /**
     * 🚀 LISTAR EQUIPOS
     */
    public List<Equipo> getEquiposPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String sql = "SELECT e.* FROM equipo e INNER JOIN inscripcion i ON e.id_equipo = i.id_equipo WHERE i.id_torneo = :id";
            return session.createNativeQuery(sql, Equipo.class).setParameter("id", idTorneo).getResultList();
        } catch (Exception e) {
            return getEquiposPorTorneoParticipa(idTorneo);
        } finally {
            session.close();
        }
    }

    private List<Equipo> getEquiposPorTorneoParticipa(int idTorneo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String sql = "SELECT e.* FROM equipo e INNER JOIN participa p ON e.id_equipo = p.id_equipo WHERE p.id_torneo = :id";
            return session.createNativeQuery(sql, Equipo.class).setParameter("id", idTorneo).getResultList();
        } finally {
            session.close();
        }
    }

    /**
     * 🚀 LISTAR PARTICIPACIONES (CORREGIDO PARA GENERAR CALENDARIO)
     */
    public List<Participa> getParticipacionesPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Participa> lista = new ArrayList<>();
        try {
            // Seleccionamos por nombre de columna para evitar líos con el orden en la DB
            String sql = "SELECT id_equipo, grupo FROM inscripcion WHERE id_torneo = :id";
            
            List<Object[]> resultados = session.createNativeQuery(sql)
                    .setParameter("id", idTorneo)
                    .getResultList();

            EquipoDAO eqDao = new EquipoDAO();

            for (Object[] fila : resultados) {
                if (fila[0] == null) continue;
                
                int idEq = ((Number) fila[0]).intValue(); 
                String letraGrupo = (fila[1] != null) ? fila[1].toString() : "SIN_GRUPO";
                
                // IMPORTANTE: El generador de calendario necesita que el grupo no esté vacío
                if (!letraGrupo.equals("SIN_GRUPO") && !letraGrupo.equals("-") && !letraGrupo.isEmpty()) {
                    Participa p = new Participa();
                    p.setEquipo(eqDao.read(idEq));
                    p.setGrupo(letraGrupo);
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return lista;
    }

    private List<Participa> getParticipacionesPorTorneoAlternativo(int idTorneo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "FROM Participa p WHERE p.torneo.idTorneo = :id";
            return session.createQuery(hql, Participa.class).setParameter("id", idTorneo).getResultList();
        } finally {
            session.close();
        }
    }

    /**
     * 🚀 OBTENER GRUPO (PARA LA TABLA)
     */
    public static String obtenerGrupo(int idEquipo, int idTorneo) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            String sql = "SELECT grupo FROM inscripcion WHERE id_equipo = :idEq AND id_torneo = :idTor";
            Object resultado = session.createNativeQuery(sql)
                    .setParameter("idEq", idEquipo)
                    .setParameter("idTor", idTorneo)
                    .uniqueResult();

            tx.commit();
            return (resultado != null) ? resultado.toString() : "-";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return "-"; 
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public List<Participa> readAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return session.createQuery("FROM Participa", Participa.class).getResultList();
        } finally {
            session.close();
        }
    }
}