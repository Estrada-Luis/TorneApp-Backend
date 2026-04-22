package dao;

import java.util.List;
import java.util.ArrayList;
import org.hibernate.Session;
import org.hibernate.query.Query;
import modelo.*;
import util.HibernateUtil;

public class ParticipaDAO extends DAO<Participa> {

    public ParticipaDAO() {
        super(Participa.class);
    }

    /**
     * Busca una inscripción específica usando la clave compuesta.
     * Vital para actualizar grupos o estados de una inscripción concreta.
     */
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
     * Obtiene solo los objetos Equipo inscritos en un torneo.
     * 🚀 CORRECCIÓN: Se usa JOIN FETCH para evitar que los equipos lleguen vacíos a Android.
     */
    public List<Equipo> getEquiposPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        List<Equipo> equipos = new ArrayList<>();
        try {
            session.beginTransaction();
            
            // Usamos 'join fetch' para que Hibernate cargue los datos del equipo de una vez
            String hql = "select e from Participa p join fetch p.equipo e where p.torneo.idTorneo = :id";
            
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
     * Obtiene los objetos Participa completos (Inscripción + Equipo + Torneo).
     * Útil para la pestaña de "Gestión Activa" y clasificación.
     */
    public List<Participa> getParticipacionesPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        List<Participa> lista = new ArrayList<>();
        try {
            session.beginTransaction();

            // Forzamos la carga de equipo y torneo para evitar errores de Lazy Loading en el JSON
            String hql = "from Participa p join fetch p.equipo join fetch p.torneo where p.torneo.idTorneo = :id";
            
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

    /**
     * Busca participaciones filtrando por el ID del club (Para el historial del club).
     */
    public List<Participa> findByClub(int idClub) {
        Session session = HibernateUtil.getCurrentSession();
        List<Participa> lista = new ArrayList<>();
        try {
            session.beginTransaction();
            
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