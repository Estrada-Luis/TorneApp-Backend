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
    /**
     * ⚽ CREA UN ENFRENTAMIENTO COMPLETO EN LA DB
     * Crea el 'partido' y las dos entradas en 'juega' (local y visitante)
     */
    public void crearPartidoFaseGrupos(int idTorneo, int idLocal, int idVisitante, String grupo) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();

            // 1. Insertamos el partido físico (poniendo 'grupo' en la columna correspondiente si la tienes)
            // Si tu tabla partido no tiene columna grupo, quítalo del SQL
            String sqlPartido = "INSERT INTO partido (id_torneo, id_equipo_local, id_equipo_visitante, resultado, acta_cerrada, fase) " +
                                "VALUES (:idT, :idL, :idV, '0-0', 0, :fase)";
            
            var query = session.createNativeQuery(sqlPartido)
                    .setParameter("idT", idTorneo)
                    .setParameter("idL", idLocal)
                    .setParameter("idV", idVisitante)
                    .setParameter("fase", "GRUPO " + grupo); // Usamos fase para guardar el nombre del grupo
            
            query.executeUpdate();

            // 2. Obtenemos el ID del partido que acabamos de crear
            // Esto es necesario para vincular la tabla 'juega'
            long idPartidoGenerado = ((Number) session.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult()).longValue();

            // 3. Insertamos en 'juega' para el Local
            String sqlJuegaLocal = "INSERT INTO juega (id_partido, id_equipo, goles, puntos) VALUES (:idP, :idE, 0, 0)";
            session.createNativeQuery(sqlJuegaLocal)
                    .setParameter("idP", idPartidoGenerado)
                    .setParameter("idE", idLocal)
                    .executeUpdate();

            // 4. Insertamos en 'juega' para el Visitante
            session.createNativeQuery(sqlJuegaLocal)
                    .setParameter("idP", idPartidoGenerado)
                    .setParameter("idE", idVisitante)
                    .executeUpdate();

            session.getTransaction().commit();
            System.out.println("✅ Partido generado: " + idLocal + " vs " + idVisitante);
            
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}