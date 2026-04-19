package dao;

import modelo.Torneo;
import util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.query.Query;

public class TorneoDAO extends DAO<Torneo> {
    
    public TorneoDAO() {
        super(Torneo.class);
    }

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
            // MUY IMPORTANTE: Si la tabla no existe, capturamos el error 
            // y devolvemos 0 para que el buscador SIGA FUNCIONANDO.
            System.err.println("Aviso: La tabla inscripcion no existe aún.");
            return 0; 
        }
    
    }
}