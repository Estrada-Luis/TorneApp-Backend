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
     * Obtiene los equipos inscritos. 
     * He cambiado la consulta para que sea más robusta con Hibernate.
     */
    public List<Equipo> getEquiposPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        List<Equipo> equipos = new ArrayList<>();
        try {
            session.beginTransaction();
            
            // Usamos una consulta que busca por el objeto Torneo completo
            // Esto obliga a Hibernate a encontrar la relación sí o sí
            String hql = "SELECT p.equipo FROM Participa p " +
                         "JOIN FETCH p.equipo " +
                         "WHERE p.torneo.idTorneo = :id";
            
            equipos = session.createQuery(hql, Equipo.class)
                    .setParameter("id", idTorneo)
                    .getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            System.err.println("ERROR en ParticipaDAO: " + e.getMessage());
        }
        return equipos;
    }

    public List<Participa> getParticipacionesPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        List<Participa> lista = new ArrayList<>();
        try {
            session.beginTransaction();
            String hql = "FROM Participa p JOIN FETCH p.equipo JOIN FETCH p.torneo WHERE p.torneo.idTorneo = :id";
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
