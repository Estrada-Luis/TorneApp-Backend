package dao;

import java.util.List;
import java.util.ArrayList;
import org.hibernate.Session;
import modelo.Torneo;
import modelo.Partido;
import util.HibernateUtil;

public class TorneoDAO extends DAO<Torneo> {

    public TorneoDAO() {
        super(Torneo.class);
    }

    /**
     * 🚀 NUEVO: Obtener un partido por su ID
     * Esto soluciona el error en ServicioTorneo
     */
    public Partido getPartidoById(int id) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            Partido partido = session.get(Partido.class, id);
            session.getTransaction().commit();
            return partido;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 🚀 NUEVO: Actualizar los datos de un partido
     * Se usa para guardar el "String resultado" (ej: 2-1)
     */
    public void updatePartido(Partido partido) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            session.update(partido);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    /**
     * ✅ TORNEOS INSCRITOS (SQL Nativo)
     */
    public List<Torneo> findInscritosByClub(int idClub) {
        Session session = HibernateUtil.getCurrentSession();
        List<Torneo> lista = new ArrayList<>();
        try {
            session.beginTransaction();
            
            String sql = "SELECT t.* FROM torneo t " +
                         "INNER JOIN participa p ON t.id_torneo = p.id_torneo " +
                         "INNER JOIN equipo e ON p.id_equipo = e.id_equipo " +
                         "WHERE e.id_club = :idClub";
            
            lista = session.createNativeQuery(sql, Torneo.class)
                    .setParameter("idClub", idClub)
                    .getResultList();
            
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * ✅ TORNEOS ORGANIZADOS (SQL Nativo)
     */
    public List<Torneo> findOrganizadosByClub(int idClub) {
        Session session = HibernateUtil.getCurrentSession();
        List<Torneo> lista = new ArrayList<>();
        try {
            session.beginTransaction();
            
            String sql = "SELECT * FROM torneo WHERE id_club = :idClub";
            
            lista = session.createNativeQuery(sql, Torneo.class)
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