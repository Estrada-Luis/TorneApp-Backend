package dao;

import java.util.List;
import java.util.ArrayList;
import org.hibernate.Session;
import modelo.Torneo;
import util.HibernateUtil;

public class TorneoDAO extends DAO<Torneo> {

    public TorneoDAO() {
        super(Torneo.class);
    }

    /**
     * ✅ TERCERA FOTO (Inscritos)
     */
    public List<Torneo> findInscritosByClub(int idClub) {
        Session session = HibernateUtil.getCurrentSession();
        List<Torneo> lista = new ArrayList<>();
        try {
            session.beginTransaction();
            
            // SQL Nativo usando nombres de columnas reales de tus fotos
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
     * ✅ ORGANIZADOS
     */
    public List<Torneo> findOrganizadosByClub(int idClub) {
        Session session = HibernateUtil.getCurrentSession();
        List<Torneo> lista = new ArrayList<>();
        try {
            session.beginTransaction();
            
            // Basado en tu primera foto, la columna es 'id_club'
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