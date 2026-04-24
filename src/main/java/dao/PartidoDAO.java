package dao;

import modelo.Partido;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.ArrayList;
import java.util.List;

public class PartidoDAO extends DAO<Partido> {

    public PartidoDAO() {
        super(Partido.class);
    }

    /**
     * Busca los partidos asociados a un torneo específico mediante su ID.
     * Este método es mucho más eficiente que filtrar en memoria.
     */
    public List<Partido> findPartidosByTorneo(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            
            // Usamos HQL para filtrar por la propiedad 'torneo.idTorneo' de la entidad Partido
            String hql = "FROM Partido p WHERE p.torneo.idTorneo = :idTorneo";
            Query<Partido> query = session.createQuery(hql, Partido.class);
            query.setParameter("idTorneo", idTorneo);
            
            List<Partido> resultados = query.list();
            
            session.getTransaction().commit();
            return resultados;
        } catch (Exception e) {
            if (session.getTransaction() != null) session.getTransaction().rollback();
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void create(Partido partido) {
        Session session = HibernateUtil.getCurrentSession();
        session.beginTransaction();
        session.save(partido);
        session.getTransaction().commit();
    }


}