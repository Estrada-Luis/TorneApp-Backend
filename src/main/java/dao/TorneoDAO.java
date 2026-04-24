package dao;

import java.util.List;
import java.util.ArrayList;
import org.hibernate.Session;
import modelo.Torneo;
import modelo.Partido;
import modelo.Equipo;
import util.HibernateUtil;

public class TorneoDAO extends DAO<Torneo> {

    public TorneoDAO() {
        super(Torneo.class);
    }

    /**
     * 🔍 LEER TORNEO FORZANDO REFRESCO
     * Esto asegura que el escritorio vea los cambios hechos por el móvil
     */
    public Torneo read(int id) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            Torneo t = session.get(Torneo.class, id);
            if (t != null) {
                // Forzamos la carga de la colección de equipos para que no salga vacía
                t.getEquipos().size(); 
            }
            session.getTransaction().commit();
            return t;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            return null;
        }
    }

    /**
     * 🏁 GENERAR FASE FINAL (Escritorio)
     */
    public void generarFaseFinal(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            // Llamada al procedimiento o lógica de generación
            session.createNativeQuery("CALL generar_fase_final(:id)")
                   .setParameter("id", idTorneo)
                   .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
        }
    }

    /**
     * ✅ LISTAR TORNEOS (Escritorio/Móvil)
     * He usado DISTINCT y un JOIN para asegurar que traiga los equipos 
     */
    @Override
    public List<Torneo> readAll() {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            // HQL que trae el torneo y sus equipos de un tirón para evitar el "vacío"
            List<Torneo> lista = session.createQuery("SELECT DISTINCT t FROM Torneo t LEFT JOIN FETCH t.equipos", Torneo.class)
                                        .getResultList();
            session.getTransaction().commit();
            return lista;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            return new ArrayList<>();
        }
    }

    // --- MÉTODOS DE FILTRADO (Móvil) ---

    public List<Torneo> listarInscritosPorClub(int idClub) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            // Buscamos en 'inscripcion' que es donde guarda el móvil ahora
            String sql = "SELECT DISTINCT t.* FROM torneo t " +
                         "INNER JOIN inscripcion i ON t.id_torneo = i.id_torneo " +
                         "INNER JOIN equipo e ON i.id_equipo = e.id_equipo " +
                         "WHERE e.id_club = :idClub";
            List<Torneo> lista = session.createNativeQuery(sql, Torneo.class)
                                        .setParameter("idClub", idClub)
                                        .getResultList();
            session.getTransaction().commit();
            return lista;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            return new ArrayList<>();
        }
    }

    public List<Torneo> listarOrganizadosPorClub(int idClub) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            List<Torneo> lista = session.createNativeQuery("SELECT * FROM torneo WHERE id_club = :idClub", Torneo.class)
                                        .setParameter("idClub", idClub)
                                        .getResultList();
            session.getTransaction().commit();
            return lista;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            return new ArrayList<>();
        }
    }

    public Partido getPartidoById(int id) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            Partido p = session.get(Partido.class, id);
            session.getTransaction().commit();
            return p;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            return null;
        }
    }

    public void updatePartido(Partido p) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            session.update(p);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
        }
    }
}