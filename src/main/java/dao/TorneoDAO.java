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
     * ✅ TORNEOS INSCRITOS (Tercera foto)
     * Busca torneos donde el club tiene equipos participando.
     */
    public List<Torneo> findInscritosByClub(int idClub) {
        Session session = HibernateUtil.getCurrentSession();
        List<Torneo> lista = new ArrayList<>();
        try {
            session.beginTransaction();
            
            // SQL Nativo: Une Torneo -> Participa -> Equipo y filtra por id_club
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
     * ✅ TORNEOS ORGANIZADOS
     * Busca torneos donde el ID del club organizador coincide.
     */
    public List<Torneo> findOrganizadosByClub(int idClub) {
        Session session = HibernateUtil.getCurrentSession();
        List<Torneo> lista = new ArrayList<>();
        try {
            session.beginTransaction();
            
            // SQL Nativo: Filtra directamente en la tabla torneo por la columna id_club
            // Ajusta "id_club_organizador" si el nombre de tu columna es distinto (ej: id_club)
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
