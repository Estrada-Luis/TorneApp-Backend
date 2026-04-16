package dao;

import modelo.Partido;
import util.HibernateUtil;

import org.hibernate.Session;

public class PartidoDAO extends DAO<Partido> {

    public PartidoDAO() {
        super(Partido.class);
    }

    public void create(Partido partido) {
        Session session = HibernateUtil.getCurrentSession();
        session.beginTransaction();
        session.save(partido);
        session.getTransaction().commit();
    }

    public Partido read(int idPartido) {
        Session session = HibernateUtil.getCurrentSession();
        session.beginTransaction();
        Partido partido = session.get(Partido.class, idPartido);
        session.getTransaction().commit();
        return partido;
    }
}