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
     * 🚀 CORRECCIÓN CLAVE: Accedemos a p.id.idTorneo para que el filtro funcione con la clave compuesta.
     */
    public List<Equipo> getEquiposPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        List<Equipo> equipos = new ArrayList<>();
        try {
            session.beginTransaction();
            
            // Cambiamos p.torneo.idTorneo por p.id.idTorneo
            String hql = "select e from Participa p join fetch p.equipo e where p.id.idTorneo = :id";
            
            equipos = session.createQuery(hql, Equipo.class)
                    .setParameter("id", idTorneo)
                    .getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            e.printStackTrace();
        }
        return equipos;
    }

    /**
     * 🚀 CORRECCIÓN: Filtro por clave compuesta.
     */
    public List<Participa> getParticipacionesPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        List<Participa> lista = new ArrayList<>();
        try {
            session.beginTransaction();

            // Cambiamos p.torneo.idTorneo por p.id.idTorneo
            String hql = "from Participa p join fetch p.equipo join fetch p.torneo where p.id.idTorneo = :id";
            
            lista = session.createQuery(hql, Participa.class)
                    .setParameter("id", idTorneo)
                    .getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            e.printStackTrace();
        }
        return lista;
    }

    public List<Participa> findByClub(int idClub) {
        Session session = HibernateUtil.getCurrentSession();
        List<Participa> lista = new ArrayList<>();
        try {
            session.beginTransaction();
            
            // Aquí p.equipo.club.idClub suele funcionar bien porque no es parte de la PK
            String hql = "from Participa p join fetch p.torneo join fetch p.equipo " +
                         "where p.equipo.club.idClub = :idClub";
            
            lista = session.createQuery(hql, Participa.class)
                    .setParameter("idClub", idClub)
                    .getResultList();
            
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            e.printStackTrace();
        }
        return lista;
    }
}