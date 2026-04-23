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

    // Actualiza los goles de local y visitante en la tabla 'juega'
    public void actualizarResultado(int idPartido, int golesLocal, int golesVisitante) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();

            // SQL para el local
            String sqlLocal = "UPDATE juega SET goles = :goles WHERE id_partido = :idP AND id_equipo = " +
                              "(SELECT id_equipo_local FROM partido WHERE id_partido = :idP)";
            session.createNativeQuery(sqlLocal).setParameter("goles", golesLocal).setParameter("idP", idPartido).executeUpdate();

            // SQL para el visitante
            String sqlVisitante = "UPDATE juega SET goles = :goles WHERE id_partido = :idP AND id_equipo = " +
                                 "(SELECT id_equipo_visitante FROM partido WHERE id_partido = :idP)";
            session.createNativeQuery(sqlVisitante).setParameter("goles", golesVisitante).setParameter("idP", idPartido).executeUpdate();

            // También actualizamos el String 'resultado' en la tabla partido para la TableView
            String sqlPartido = "UPDATE partido SET resultado = :res WHERE id_partido = :idP";
            session.createNativeQuery(sqlPartido).setParameter("res", golesLocal + "-" + golesVisitante).setParameter("idP", idPartido).executeUpdate();

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
            List<Partido> lista = session.createNativeQuery(sql, Partido.class).setParameter("id", idTorneo).getResultList();
            session.getTransaction().commit();
            return lista;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            return null;
        }
    }
}