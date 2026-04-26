package dao;

import modelo.Juega;
import modelo.Partido;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.ArrayList;

public class JuegaDAO extends DAO<Juega> {

    public JuegaDAO() {
        super(Juega.class);
    }

    public void cerrarActa(int idPartido) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String sql = "UPDATE partido SET actaCerrada = 1 WHERE id_partido = :idP";
            session.createNativeQuery(sql)
                    .setParameter("idP", idPartido)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void actualizarResultado(int idPartido, int golesLocal, int golesVisitante) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            // 1. Obtenemos los dos registros de 'juega' para este partido
            String hql = "FROM Juega j WHERE j.partido.idPartido = :idP";
            List<Juega> participaciones = session.createQuery(hql, Juega.class)
                    .setParameter("idP", idPartido)
                    .getResultList();

            if (participaciones.size() == 2) {
                Juega local = participaciones.get(0);
                Juega visitante = participaciones.get(1);

                local.setGoles(golesLocal);
                visitante.setGoles(golesVisitante);

                if (golesLocal > golesVisitante) {
                    local.setPuntos(3); visitante.setPuntos(0);
                } else if (golesLocal < golesVisitante) {
                    local.setPuntos(0); visitante.setPuntos(3);
                } else {
                    local.setPuntos(1); visitante.setPuntos(1);
                }

                session.update(local);
                session.update(visitante);

                String sqlPartido = "UPDATE partido SET resultado = :res WHERE id_partido = :idP";
                session.createNativeQuery(sqlPartido)
                        .setParameter("res", golesLocal + "-" + golesVisitante)
                        .setParameter("idP", idPartido)
                        .executeUpdate();
            }

            tx.commit();
            System.out.println("✅ Resultado guardado en DB: " + golesLocal + "-" + golesVisitante);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<Partido> getPartidosPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String sql = "SELECT * FROM partido WHERE id_torneo = :id ORDER BY id_partido ASC";
            return session.createNativeQuery(sql, Partido.class)
                    .setParameter("id", idTorneo).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            session.close();
        }
    }
    
    public void crearPartidoFaseGrupos(int idTorneo, int idLocal, int idVisitante, String grupo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            String sqlP = "INSERT INTO partido (id_torneo, resultado, actaCerrada, nombreFase, tipoFase) " +
                         "VALUES (:idT, '0-0', 0, :nomFase, 'GRUPOS')";
            
            session.createNativeQuery(sqlP)
                    .setParameter("idT", idTorneo)
                    .setParameter("nomFase", grupo)
                    .executeUpdate();

            Object resId = session.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult();
            long idGen = ((Number) resId).longValue();

            String sqlJ = "INSERT INTO juega (id_partido, id_equipo, goles, puntos, grupo) VALUES (:idP, :idE, 0, 0, :gr)";
            
            session.createNativeQuery(sqlJ).setParameter("idP", idGen).setParameter("idE", idLocal).setParameter("gr", grupo).executeUpdate();
            session.createNativeQuery(sqlJ).setParameter("idP", idGen).setParameter("idE", idVisitante).setParameter("gr", grupo).executeUpdate();

            tx.commit();
            System.out.println("✅ CALENDARIO: Partido creado.");
            
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * 📊 OBTENER RESULTADOS FILTRADOS (Corregido con SQL Nativo)
     */
    public List<Juega> getResultadosPorTorneo(int idTorneo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            // 🚩 Usamos SQL Nativo para evitar el error "could not resolve property idTorneo"
            String sql = "SELECT j.* FROM juega j " +
                         "INNER JOIN partido p ON j.id_partido = p.id_partido " +
                         "WHERE p.id_torneo = :id";
            
            return session.createNativeQuery(sql, Juega.class)
                    .setParameter("id", idTorneo)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("❌ Error en getResultadosPorTorneo: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            session.close();
        }
    }
}