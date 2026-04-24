package dao;

import java.util.List;
import java.util.ArrayList;
import org.hibernate.Session;
import modelo.*;
import util.HibernateUtil;

public class ParticipaDAO extends DAO<Participa> {

    public ParticipaDAO() {
        super(Participa.class);
    }

    public Participa read(ParticipaId id) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            Participa participa = session.get(Participa.class, id);
            session.getTransaction().commit();
            return participa;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 🚀 ASIGNAR GRUPO (SQL NATIVO)
     * Este es el método que necesita el Botón 1 del escritorio.
     */
    public void actualizarGrupo(int idEquipo, int idTorneo, String grupo) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            
            // Usamos SQL nativo para asegurar que el cambio impacta en la tabla intermedia
            String sql = "UPDATE participa SET grupo = :grupo WHERE id_equipo = :idEq AND id_torneo = :idTor";
            
            session.createNativeQuery(sql)
                    .setParameter("grupo", grupo)
                    .setParameter("idEq", idEquipo)
                    .setParameter("idTor", idTorneo)
                    .executeUpdate();

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            System.err.println("ERROR AL ACTUALIZAR GRUPO: " + e.getMessage());
        }
    }

    /**
     * 🚀 OBTENER EQUIPOS POR TORNEO (SQL NATIVO)
     */
    public List<Equipo> getEquiposPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        List<Equipo> equipos = new ArrayList<>();
        try {
            session.beginTransaction();
            
            String sql = "SELECT e.* FROM equipo e " +
                         "INNER JOIN participa p ON e.id_equipo = p.id_equipo " +
                         "WHERE p.id_torneo = :id";
            
            equipos = session.createNativeQuery(sql, Equipo.class)
                    .setParameter("id", idTorneo)
                    .getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            System.err.println("ERROR CRÍTICO EN DAO (getEquiposPorTorneo): " + e.getMessage());
        }
        return equipos;
    }

    /**
     * OBTENER PARTICIPACIONES (HQL)
     */
    public List<Participa> getParticipacionesPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        List<Participa> lista = new ArrayList<>();
        try {
            session.beginTransaction();
            String hql = "FROM Participa p JOIN FETCH p.equipo JOIN FETCH p.torneo WHERE p.id.idTorneo = :id";
            lista = session.createQuery(hql, Participa.class)
                    .setParameter("id", idTorneo)
                    .getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
        }
        return lista;
    }
}