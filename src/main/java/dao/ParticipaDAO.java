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
     * 🚀 ESTA ES LA VERSIÓN DEFINITIVA (SQL NATIVO)
     * Al usar SQL puro, nos saltamos cualquier problema de nombres de variables en Java.
     */
    public List<Equipo> getEquiposPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        List<Equipo> equipos = new ArrayList<>();
        try {
            session.beginTransaction();
            
            // Usamos los nombres reales de las tablas y columnas de tu DB
            String sql = "SELECT e.* FROM equipo e " +
                         "INNER JOIN participa p ON e.id_equipo = p.id_equipo " +
                         "WHERE p.id_torneo = :id";
            
            equipos = session.createNativeQuery(sql, Equipo.class)
                    .setParameter("id", idTorneo)
                    .getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            System.err.println("ERROR CRÍTICO EN DAO: " + e.getMessage());
        }
        return equipos;
    }

    /**
     * También actualizamos este para que sea consistente
     */
    public List<Participa> getParticipacionesPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        List<Participa> lista = new ArrayList<>();
        try {
            session.beginTransaction();
            // Aquí usamos p.id.idTorneo que es la forma más directa en HQL para claves compuestas
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
