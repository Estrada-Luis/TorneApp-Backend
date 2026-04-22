package dao;

import modelo.Torneo;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.ArrayList;
import java.util.List;

public class TorneoDAO extends DAO<Torneo> {
    
    public TorneoDAO() {
        super(Torneo.class);
    }

    /**
     * Cuenta cuántos equipos hay inscritos en un torneo específico.
     */
    public int countInscripciones(int idTorneo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql = "SELECT COUNT(*) FROM inscripcion WHERE id_torneo = :id";
            Object result = session.createNativeQuery(sql)
                                   .setParameter("id", idTorneo)
                                   .uniqueResult();
            
            if (result != null) {
                return ((Number) result).intValue();
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Aviso: La tabla inscripcion no existe aún o hay un error en la consulta.");
            return 0; 
        }
    }

    /**
     * Busca los torneos que han sido organizados por un club concreto.
     */
    public List<Torneo> findOrganizadosByClub(int idClub) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Usamos HQL para filtrar por el ID del club organizador
            String hql = "FROM Torneo t WHERE t.clubOrganizador.id = :idClub";
            Query<Torneo> query = session.createQuery(hql, Torneo.class);
            query.setParameter("idClub", idClub);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Busca los torneos donde algún equipo del club está inscrito.
     */
    public List<Torneo> findInscritosByClub(int idClub) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL: Navega por la relación torneos -> equipos -> club
            String hql = "SELECT DISTINCT t FROM Torneo t JOIN t.equipos e WHERE e.club.id = :idClub";
            Query<Torneo> query = session.createQuery(hql, Torneo.class);
            query.setParameter("idClub", idClub);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}