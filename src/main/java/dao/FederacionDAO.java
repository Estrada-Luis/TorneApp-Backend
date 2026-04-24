package dao;

import modelo.Federacion;
import util.HibernateUtil;

import org.hibernate.Session;

public class FederacionDAO extends DAO<Federacion> {

    public FederacionDAO() {
        super(Federacion.class);
    }

    public void create(Federacion federacion) {
        Session session = HibernateUtil.getCurrentSession();
        session.beginTransaction();
        session.save(federacion);
        session.getTransaction().commit();
    }

    public Federacion read(int idFederacion) {
        Session session = HibernateUtil.getCurrentSession();
        session.beginTransaction();
        Federacion federacion = session.get(Federacion.class, idFederacion);
        session.getTransaction().commit();
        return federacion;
    }
}
