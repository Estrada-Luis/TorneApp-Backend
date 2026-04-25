package dao;

import modelo.Juega;
import modelo.Partido;
import util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class JuegaDAO extends DAO<Juega> {

    public JuegaDAO() {
        super(Juega.class);
    }

    // Marca el partido como finalizado
    public void cerrarActa(int idPartido) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            String sql = "UPDATE partido SET acta_cerrada = 1 WHERE id_partido = :idP";
            session.createNativeQuery(sql)
                    .setParameter("idP", idPartido)
                    .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    public void actualizarResultado(int idPartido, int golesLocal, int golesVisitante) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();

            // SQL Local
            String sqlLocal = "UPDATE juega SET goles = :goles, puntos = CASE " +
                    "WHEN :gLocal > :gVis THEN 3 WHEN :gLocal = :gVis THEN 1 ELSE 0 END " +
                    "WHERE id_partido = :idP AND id_equipo = (SELECT id_equipo_local FROM partido WHERE id_partido = :idP)";
            
            session.createNativeQuery(sqlLocal)
                    .setParameter("goles", golesLocal)
                    .setParameter("gLocal", golesLocal)
                    .setParameter("gVis", golesVisitante)
                    .setParameter("idP", idPartido).executeUpdate();

            // SQL Visitante
            String sqlVisitante = "UPDATE juega SET goles = :goles, puntos = CASE " +
                    "WHEN :gVis > :gLocal THEN 3 WHEN :gVis = :gLocal THEN 1 ELSE 0 END " +
                    "WHERE id_partido = :idP AND id_equipo = (SELECT id_equipo_visitante FROM partido WHERE id_partido = :idP)";
            
            session.createNativeQuery(sqlVisitante)
                    .setParameter("goles", golesVisitante)
                    .setParameter("gLocal", golesLocal)
                    .setParameter("gVis", golesVisitante)
                    .setParameter("idP", idPartido).executeUpdate();

            // Actualizar String resultado en partido
            String sqlPartido = "UPDATE partido SET resultado = :res WHERE id_partido = :idP";
            session.createNativeQuery(sqlPartido)
                    .setParameter("res", golesLocal + "-" + golesVisitante)
                    .setParameter("idP", idPartido).executeUpdate();

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    public List<Partido> getPartidosPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            String sql = "SELECT * FROM partido WHERE id_torneo = :id ORDER BY id_partido ASC";
            List<Partido> lista = session.createNativeQuery(sql, Partido.class)
                    .setParameter("id", idTorneo).getResultList();
            session.getTransaction().commit();
            return lista;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            return null;
        }
    }
    
    // Método para crear el partido físico y sus 2 entradas en 'juega'
    public void crearPartidoFaseGrupos(int idTorneo, int idLocal, int idVisitante, String grupo) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            String sqlP = "INSERT INTO partido (id_torneo, id_equipo_local, id_equipo_visitante, resultado, acta_cerrada, nombre_fase) " +
                         "VALUES (:idT, :idL, :idV, '0-0', 0, :fase)";
            session.createNativeQuery(sqlP)
                    .setParameter("idT", idTorneo).setParameter("idL", idLocal)
                    .setParameter("idV", idVisitante).setParameter("fase", "Grupo " + grupo)
                    .executeUpdate();

            long idGen = ((Number) session.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).longValue();

            String sqlJ = "INSERT INTO juega (id_partido, id_equipo, goles, puntos) VALUES (:idP, :idE, 0, 0)";
            session.createNativeQuery(sqlJ).setParameter("idP", idGen).setParameter("idE", idLocal).executeUpdate();
            session.createNativeQuery(sqlJ).setParameter("idP", idGen).setParameter("idE", idVisitante).executeUpdate();

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
        }
    }
}