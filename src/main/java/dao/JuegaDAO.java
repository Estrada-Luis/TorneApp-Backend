package dao;

import modelo.Juega;
import modelo.Partido;
import util.HibernateUtil;
import modelo.Equipo;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class JuegaDAO extends DAO<Juega> {

    public JuegaDAO() {
        super(Juega.class);
    }

    // Obtener los equipos de un partido específico
    public List<Juega> findByPartido(Partido partido) {
        Session session = HibernateUtil.getCurrentSession();
        session.beginTransaction();

        String hql = "FROM Juega j WHERE j.partido = :partido";
        Query<Juega> query = session.createQuery(hql, Juega.class);
        query.setParameter("partido", partido);

        List<Juega> juegos = query.list();

        session.getTransaction().commit();
        return juegos;
    }

    public void create(Juega juega) {
        Session session = HibernateUtil.getCurrentSession();
        session.beginTransaction();
        session.save(juega);
        session.getTransaction().commit();
    }
}